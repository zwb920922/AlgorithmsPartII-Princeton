import java.util.*;

import edu.princeton.cs.algs4.*;


public class BoggleSolver {

  private Node root;
  
  private int N; // total number of words in the dictionary
  
  private BoggleBoard curBoard;
  
  private int curRows;
  
  private int curCols;
  
  private Set<String> curWords;
  
  private final int[][] dirs = new int[][]{{-1,-1}, {-1,0}, {-1,1}, {0,-1},
                                           {0, 1}, {1,-1}, {1, 0}, {1, 1}};

  private int getScore(String word) {
    int score;
    switch (word.length()) {
      case 0: 
      case 1:
      case 2: score = 0;
              break;
      case 3: 
      case 4: score = 1;
              break;
      case 5: score = 2;
              break;
      case 6: score = 3;
              break;
      case 7: score = 5;
              break;
      default: score = 11;
               break;
    }
    return score;
  }
  
  private class Node {
    private Integer score;
    private Node[] next = new Node[26];
  }

  private Node get(String word) {
    Node ret = get(root, word, 0);
    return ret;
  }
  
  private Node get(Node cur, String word, int d) {
    if (cur == null) return null;
    if (d == word.length())
      return cur;
    return get(cur.next[word.charAt(d)-'A'], word, d+1);
  }

  private void put(String word) {
    root = put(root, word, 0);
  }
  
  private Node put(Node cur, String word, int d) {
    if (cur == null)
      cur = new Node();
    if (d == word.length()) {
      if (cur.score != null) N++;
      cur.score = getScore(word);
      return cur;
    }
    cur.next[word.charAt(d)-'A'] = put(cur.next[word.charAt(d)-'A'], word, d+1);
    return cur;
  }

  // Initializes the data structure using the given array of strings as the
  // dictionary.
  // (You can assume each word in the dictionary contains only the uppercase
  // letters A through Z.)
  public BoggleSolver(String[] dictionary) {
    N = 0;
    for (String word : dictionary) {
      put(word);
    }
  }

  // Returns the set of all valid words in the given Boggle board, as an
  // Iterable.
  public Iterable<String> getAllValidWords(BoggleBoard board) {
    curWords = new HashSet<>();
    curBoard = board;
    curRows = board.rows();
    curCols = board.cols();
    boolean[][] visited = new boolean[curRows][curCols];
    StringBuilder strb = new StringBuilder();
    for (int i = 0; i < curRows; i++) {
      for (int j = 0; j < curCols; j++) {
        find(root, i, j, strb, visited);
      }
    }
    return curWords;
  }
  
  private void find(Node curN, int row, int col, StringBuilder curS, boolean[][] visited) {
    // base case
    if (curN == null || row >= curRows || row < 0 || 
        col >= curCols || col < 0 || visited[row][col])
      return;
    // check whether the cur letter is 'Q' and update cur node
    char c = curBoard.getLetter(row, col);
    if (c == 'Q') {
      curN = curN.next['Q' - 'A'];
      if (curN == null) return;
      curN = curN.next['U'-'A'];
      if (curN == null) return;
      curS.append("QU");
    } else {
      curN = curN.next[c-'A'];
      if (curN == null) return;
      curS.append(c);
    }
    visited[row][col] = true;
    // if the word is valid
    if (curN.score != null && curN.score > 0) {
      curWords.add(curS.toString());
    }
    // keep looking for vaid words
    for (int[] dir : dirs) {
      find(curN, row+dir[0], col+dir[1], curS, visited);
    }
    if (c == 'Q')
      curS.delete(curS.length()-2, curS.length());
    else
      curS.deleteCharAt(curS.length()-1);
    visited[row][col] = false;
  }

  // Returns the score of the given word if it is in the dictionary, zero
  // otherwise.
  // (You can assume the word contains only the uppercase letters A through Z.)
  public int scoreOf(String word) {
    if (word.length() < 3) return 0;
    Node n = get(word);
    if (n == null || n.score == null) return 0;
    return  n.score;
  }
  
  public static void main(String[] args) {
    In in = new In("dictionary-algs4.txt");
    String[] dictionary = in.readAllStrings();
    BoggleSolver solver = new BoggleSolver(dictionary);
    BoggleBoard board = new BoggleBoard("board-q.txt");
    int score = 0;
    for (String word : solver.getAllValidWords(board)) {
      StdOut.println(word);
      score += solver.scoreOf(word);
    }
    StdOut.println("Score = " + score);
  }
  
}