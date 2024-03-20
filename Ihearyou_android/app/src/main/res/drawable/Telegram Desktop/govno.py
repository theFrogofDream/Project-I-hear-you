input_str = str(input())
square = [["A", "B", "C", "D", "E"],
          ["F", "G", "H", ["I", "J"], "K"],
          ["L", "M", "N", "O", "P"],
          ["Q", "R", "S", "T", "U"],
          ["V", "W", "X", "Y", "Z"]]
crypt = ""
for word in input_str:
    if word == "I" or word == "J":
        crypt = crypt + "24"
    i = 0
    for i in range(5):
        j = 0
        for j in range(5):
            if square[i][j] == word:
                crypt = crypt + str(i + 1) + str(j + 1)
            j += 1
        i += 1

print(crypt)
