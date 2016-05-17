
public class SubString implements Comparable<SubString> {
  private final String text;
  private final int start;
  private final int end; // not included

  public SubString(String s, int start, int end) {
    this.text = s;
    this.start = start;
    this.end = end;
  }

  public int length() {
    return end - start;
  }

  public char charAt(int i) {
    return text.charAt(start + i);
  }

  public int compareTo(SubString that) {
    int s1 = this.start, s2 = that.start, e1 = this.end, e2 = that.end;
    String text1 = this.text, text2 = that.text;
    while (true) {
      if (s1 >= e1) return -1;
      if (s2 >= e2) return 1;
      char c1 = text1.charAt(s1++), c2 = text2.charAt(s2++);
      if (c1 > c2) return 1;
      else if (c2 > c1) return -1;
    }
  }
}