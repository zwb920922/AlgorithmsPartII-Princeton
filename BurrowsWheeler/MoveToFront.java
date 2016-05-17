import edu.princeton.cs.algs4.*;
import java.util.*;

public class MoveToFront {

  private static int R = 256; // number of extended ASCII character

  // apply move-to-front encoding, reading from standard input and writing to
  // standard output
  public static void encode() {
    char[] chars = charList();
    while (!BinaryStdIn.isEmpty()) {
      char c = BinaryStdIn.readChar();
      char last = chars[0];
      char i;
      for (i = 0; chars[i] != c; i++) {
        char tmp = chars[i];
        chars[i] = last;
        last = tmp;
      }
      chars[i] = last;
      chars[0] = c;
      BinaryStdOut.write(i);
    }
    BinaryStdIn.close();
    BinaryStdOut.close();
  }

  // apply move-to-front decoding, reading from standard input and writing to
  // standard output
  public static void decode() {
    char[] chars = charList();
    while (!BinaryStdIn.isEmpty()) {
      int index = BinaryStdIn.readChar();
      char c = chars[index];
      System.arraycopy(chars, 0, chars, 1, index);
      chars[0] = c;
      BinaryStdOut.write(c);
    }
    BinaryStdIn.close();
    BinaryStdOut.close();
  }

  private static char[] charList() {
    char[] list = new char[R];
    for (int i = 0; i < R; i++) {
      list[i] = (char) i;
    }
    return list;
  }

  // if args[0] is '-', apply move-to-front encoding
  // if args[0] is '+', apply move-to-front decoding
  public static void main(String[] args) {
    if (args[0].equals("-"))
      encode();
    else if (args[0].equals("+"))
      decode();
    else
      throw new IllegalArgumentException();
      
  }
}
