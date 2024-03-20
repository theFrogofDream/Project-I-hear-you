# Идея: **приложение-помощник для глухонемых**

## Проблема: глухонемные плохо слышат o_O

### Реализация вкратце:
- Приложение имеет постоянный доступ к микрофону
- Приложение всё время записывает голоса с микрофона
- Приложение транскрибирует всё, что присходит рядом с человеком
- В приложении можно включить встроенный переводчик
- Приложение реагирует на имя владельца, выводя ему уведомление

### Техническая реализация:
- Для распознавания речи в реальном времени будем использовать open-source библиотеку VOSK с маленькой моделью для встраиваемых и мобильных устройств.
Выбор библиотеки обусловлен её бесплатностью и открытостью.
- Приложение будет написано на стеке Kotlin и Jetpack Compose

### TODO LIST: 
- [x] Транскрибация речи
- [ ] Возможность realtime перевода
- [ ] Возможность выбора модели в приложении