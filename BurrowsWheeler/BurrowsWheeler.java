
import edu.princeton.cs.algs4.*;
import java.util.*;

public class BurrowsWheeler {
  
  // apply Burrows-Wheeler encoding, reading from standard input and writing to
  // standard output
  public static void encode() {
    StringBuilder sb = new StringBuilder();
    while (!BinaryStdIn.isEmpty())
      sb.append(BinaryStdIn.readChar());
    String s = sb.toString();
    CircularSuffixArray csa = new CircularSuffixArray(s);
    int length = s.length();
    for (int i = 0; i < length; i++) {
      if (csa.index(i) == 0)
        BinaryStdOut.write(i);
    }
    for (int i = 0; i < length; i++) {
      int index = csa.index(i);
      if (index == 0)
        BinaryStdOut.write(s.charAt(length-1));
      else
        BinaryStdOut.write(s.charAt(index-1));
    }
    BinaryStdIn.close();
    BinaryStdOut.close();
  }

  // apply Burrows-Wheeler decoding, reading from standard input and writing to
  // standard output
  public static void decode() {
    int first = BinaryStdIn.readInt();
    StringBuilder sb = new StringBuilder();
    while (!BinaryStdIn.isEmpty())
      sb.append(BinaryStdIn.readChar());
    String s = sb.toString();
    int length = s.length();
    char[] t = s.toCharArray();
    char[] sortedT = Arrays.copyOf(t, t.length);
    Arrays.sort(sortedT);
    int[] next = calIndex(sortedT, t);
    int cur = first;
    for (int i = 0; i < length; i++) {
      BinaryStdOut.write(sortedT[cur]);
      cur = next[cur];
    }
    BinaryStdIn.close();
    BinaryStdOut.close();
  }

  private static int[] calIndex(char[] firsts, char[] lasts) {
    assert(firsts.length == lasts.length);
    int length = firsts.length;
    int[] index = new int[length];
    Map<Character, ArrayList<Integer>> map = new HashMap<>();
    for (int i = 0; i < length; i++) {
      if (!map.containsKey(lasts[i]))
        map.put(lasts[i], new ArrayList<>());
      map.get(lasts[i]).add(i);
    }
    for (int i = length-1; i >= 0; i--) {
      ArrayList<Integer> tmp = map.get(firsts[i]);
      index[i] = tmp.remove(tmp.size()-1);
    }
    return index;
  }
  
  // if args[0] is '-', apply Burrows-Wheeler encoding
  // if args[0] is '+', apply Burrows-Wheeler decoding
  public static void main(String[] args) {
    if (args[0].equals("-"))
      encode();
    else if (args[0].equals("+"))
      decode();
    else
      throw new IllegalArgumentException();
  }
}