package pentapulse.ihearyou

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.SpeechStreamService
import org.vosk.android.StorageService
import pentapulse.ihearyou.ui.theme.IHearYouTheme
import java.lang.Exception
import java.util.LinkedList


class MainViewmodel(context: Context) : ViewModel(), RecognitionListener {
    private val _messagesQueue = MutableStateFlow(LinkedList<String>())
    val messagesQueue: StateFlow<LinkedList<String>> = _messagesQueue
    val partialResult = mutableStateOf("")

    var shouldRecognize = mutableStateOf(false)

    fun addMessage(message: String) {
        val currentQueue = _messagesQueue.value.toMutableList()

        if (currentQueue.size > 100) {
            currentQueue.removeAt(currentQueue.size - 1)
        }

        currentQueue.add(message)
        _messagesQueue.value = LinkedList(currentQueue)
    }

    var model: Model = Model()
    private var speechService: SpeechService? = null
    private lateinit var recognizer: Recognizer
    private lateinit var speechStreamService: SpeechStreamService

//    private val backgroundThread = Thread {
//        while (shouldRecognize.value) {
//            recognizeAudio()
//        }
//    }

    fun startListening() {
        recognizeAudio()
    }

    fun endListening() {
        speechService?.stop()
    }

    init {
        LibVosk.setLogLevel(LogLevel.DEBUG)
        StorageService.unpack(context, "model-ru-rus", "model", { model ->
            this.model = model
        }, {
            Log.e("VOSK INIT ERROR", it.toString())
        })


    }

    fun recognizeAudio() {
        if (speechService != null) {
            speechService!!.stop();
            speechService = null;
        } else {
            recognizer = Recognizer(this.model, 16000f)
            speechService = SpeechService(recognizer, 16000f)
            speechService!!.startListening(this)
        }
    }


    override fun onTimeout() {

    }

    override fun onPartialResult(hypothesis: String?) {
        hypothesis?.let {
            if (JSONObject(it).getString("partial").isEmpty()) {
                return
            }
            partialResult.value = JSONObject(it).getString("partial")
        }

    }

    override fun onResult(hypothesis: String?) {
        hypothesis?.let {
            val text = JSONObject(it).getString("text")
            if (text.isNotEmpty()) {
                addMessage("- $text")
            }
            partialResult.value = ""
        }

    }

    override fun onFinalResult(hypothesis: String?) {
        hypothesis?.let {
            val text = JSONObject(it).getString("text")
            addMessage("- $text")
        }
    }

    override fun onError(exception: Exception?) {
        TODO("Not yet implemented")
    }

}


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewmodel = MainViewmodel(this)
            IHearYouTheme {
                val messages by viewmodel.messagesQueue.collectAsState()
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Я слышу тебя!") }, actions = {
                            IconButton(onClick = {
//                                val intent = Intent()
                            }) {
                                Icon(Icons.Outlined.Settings, "")
                            }
                        })

                    },
                    bottomBar = {

                        Button(modifier = Modifier.fillMaxWidth(), onClick = {
                            if (viewmodel.shouldRecognize.value) {
                                viewmodel.shouldRecognize.value = false
                                viewmodel.endListening()
                            } else {
                                viewmodel.shouldRecognize.value = true

                                viewmodel.startListening()
                            }
                        }) {
                            Text("Старт")
                        }

                    },
                    content = { paddingValues ->
                        Column(
                            Modifier.padding(
                                top = paddingValues.calculateTopPadding(),
                                start = 12.dp,
                                end = 12.dp,
                            )
                        ) {
                            LazyColumn(modifier = Modifier
                                .fillMaxSize()
                                .animateContentSize(),
                                content = {
                                    itemsIndexed(messages) { index, message ->
                                        if (message.lastIndex == index && message != "") {
                                            Text(
                                                message,
                                                fontSize = 24.sp,
                                                color = Color.White,
                                                modifier = Modifier.animateItemPlacement()
                                            )

                                        } else {
                                            Text(
                                                message,
                                                fontSize = 24.sp,
                                                color = Color.Gray,
                                                modifier = Modifier.animateItemPlacement()
                                            )
                                        }

                                    }
                                    item {
                                        if (viewmodel.partialResult.value.isNotEmpty()) {
                                            Text(viewmodel.partialResult.value, fontSize = 18.sp)
                                        }
                                    }

                                })
                        }
                    }
                )
            }
        }
    }


}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IHearYouTheme {
        Greeting("Android")
    }
}