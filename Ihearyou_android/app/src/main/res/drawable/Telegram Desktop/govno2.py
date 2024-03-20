squareg = [["A", "B", "C", "D", "E"],
          ["F", "G", "H", ["I", "J"], "K"],
          ["L", "M", "N", "O", "P"],
          ["Q", "R", "S", "T", "U"],
          ["V", "W", "X", "Y", "Z"]]
def decrypt(input_str, square):
  decrypt = ""
  m1 = []
  m2 = []

  # Separate characters based on even/odd positions
  for num in input_str:
    if input_str.index(num) % 2 == 0:  # Use modulo (%) for even check
      m1.append(num)
    else:
      m2.append(num)

  # Decryption loop
  for i in range(len(m1)):
    row_index = int(m1[i])
    col_index = int(m2[i])

    # Handle nested lists gracefully
    if isinstance(square[row_index][col_index], list):
      decrypt += square[row_index][col_index][0]  # Access first element of nested list
    else:
      decrypt += square[row_index][col_index]

  return decrypt

# Example usage
input_str = str(input("Enter the encrypted message: "))
decrypted_message = decrypt(input_str, squareg)
print("Decrypted message:", decrypted_message)