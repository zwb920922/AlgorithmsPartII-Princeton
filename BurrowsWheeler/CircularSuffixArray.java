
import edu.princeton.cs.algs4.*;
import java.util.*;

public class CircularSuffixArray {
  
//  private SubString[] suffixes;
//  
//  private SubString[] sorted;
  
  private int length;
  
  private int[] index;
  
  private int first;

  public CircularSuffixArray(String s) {
    // circular suffix array of s
    if (s == null) throw new NullPointerException();
    length = s.length();
    String source = s + s;
    SubString[] suffixes = new SubString[length];
    index = new int[length];
    Map<SubString, ArrayList<Integer>> indexMap = new HashMap<>();
    for (int i = 0; i < length; i++) {
      SubString suffix = new SubString(source, i, length+i);
      suffixes[i] = suffix;
      if (!indexMap.containsKey(suffix)) 
        indexMap.put(suffix, new ArrayList<>());
      indexMap.get(suffix).add(i);
    }
    SubString[] sorted = Arrays.copyOf(suffixes, length);
    Arrays.sort(sorted);
    for (int i = 0; i < length; i++) {
      index[i] = indexMap.get(sorted[i]).remove(0);
      if (index[i] == 0) first = i;
    }
  }

  public int length() {
    // length of s
    return length;
  }

  public int index(int i) {
    // returns index of ith sorted suffix
    return index[i];
  }
  
  private static<T> void printArray(T[] a) {
    for (T t : a)
      System.out.println(t);
  }

  public static void main(String[] args) {
    // unit testing of the methods (optional)
    String s = "*************";
    CircularSuffixArray csa = new CircularSuffixArray(s);
//    printArray(csa.suffixes);
//    System.out.println();
//    printArray(csa.sorted);
    System.out.println(Arrays.toString(csa.index));
    System.out.println(csa.first);
  }
}