
import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;


public class Outcast {
    
    private final WordNet wordnet;
    
   public Outcast(WordNet wordnet) {         // constructor takes a WordNet object
       this.wordnet = wordnet;
   }
   
   public String outcast(String[] nouns) {   // given an array of WordNet nouns, return an outcast
       int l = nouns.length;
       int[][] dist = new int[l][l];
       for (int i = 0; i < l; i++) {
           for (int j = i; j < l; j++) {
               dist[j][i] = wordnet.distance(nouns[i], nouns[j]);
               dist[i][j] = wordnet.distance(nouns[i], nouns[j]);
           }
       }
//       for (int[] i: dist)
//           System.out.println(Arrays.toString(i));
       int curDist = -1;
       int index = -1;
       for (int k = 0; k < l; k++) {
           int d = 0;
           for (int i = 0; i < l; i++) {
               d += dist[k][i];
           }
//           System.out.println("--" + d);
           if (d > curDist) {
               index = k;
               curDist = d;
           }
       }
       return nouns[index];
   }
   
   public static void main(String[] args) {
       WordNet wordnet = new WordNet(args[0], args[1]);
//       System.out.println(wordnet.wordMap.get("water"));
//       System.out.println(wordnet.synsets[18840]);
       Outcast outcast = new Outcast(wordnet);
       for (int t = 2; t < args.length; t++) {
           In in = new In(args[t]);
           String[] nouns = in.readAllStrings();
           StdOut.println(args[t] + ": " + outcast.outcast(nouns));
       }
   }
}