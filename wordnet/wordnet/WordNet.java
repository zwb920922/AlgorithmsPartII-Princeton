import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

// wordnet
public class WordNet {

    private Digraph d;
    
    private int V;

    private Map<String, List<Integer>> wordMap;

    private ArrayList<String>[] synsets;

    private SAP sap;

    // constructor takes the name of the two input files
    @SuppressWarnings("unchecked")
    public WordNet(String syns, String hypernyms) {
        // check arguments
        if (syns == null || hypernyms == null)
            throw new NullPointerException();
        // read synsets
        In syn = new In(syns);
        String[] lines = syn.readAllLines();
        V = lines.length;
        d = new Digraph(V);
        synsets = (ArrayList<String>[]) new ArrayList[lines.length];
        for (int i = 0; i < synsets.length; i++) {
            synsets[i] = new ArrayList<>();
        }
        wordMap = new HashMap<>();
        
        for (String line : lines) {
            String[] tokens = line.split(",");
            String[] words = tokens[1].split(" ");
            int index = Integer.parseInt(tokens[0]);
            // System.out.println(index + Arrays.toString(words));
            for (String word : words) {
                if (!wordMap.containsKey(word))
                    wordMap.put(word, new ArrayList<>());
                wordMap.get(word).add(index);
                synsets[index].add(word);
            }
        }
        // read hypernyms
        In hyp = new In(hypernyms);
        while (hyp.hasNextLine()) {
            String line = hyp.readLine();
            String[] tokens = line.split(",");
            int v = Integer.parseInt(tokens[0]);
            for (int i = 1; i < tokens.length; i++) {
                d.addEdge(v, Integer.parseInt(tokens[i]));
            }
        }

        // check whether it is rooted DAG
        DirectedCycle cycle = new DirectedCycle(d);
        if (cycle.hasCycle())
            throw new IllegalArgumentException();
        int root = 0;
        for (int i = 0; i < V; i++)
            if (d.outdegree(i) == 0)
                root++;
        if (root != 1)
            throw new IllegalArgumentException();

        sap = new SAP(d);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return wordMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new NullPointerException();
        return wordMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new NullPointerException();
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException(nounA + " and " + nounB);
        return sap.length(wordMap.get(nounA), wordMap.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of
    // nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new NullPointerException();
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException(nounA + " and " + nounB);
        ArrayList<String> synset = synsets[sap.ancestor(wordMap.get(nounA), wordMap.get(nounB))];
        return String.join(" ", synset);
    }

    // do unit testing of this class
    public static void main(String[] args) {
//        WordNet wordnet = new WordNet("wordnet//synsets.txt", "wordnet//hypernyms.txt");
//        System.out.println(wordnet.distance("Esther", "antimycin"));
//        System.out.println(wordnet.sap("he-huckleberry", "Najas"));
//        System.out.println(wordnet.wordMap.get("he-huckleberry"));
//        System.out.println(wordnet.wordMap.get("Najas"));
    }

}