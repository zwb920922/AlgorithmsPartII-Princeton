
import java.util.ArrayList;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;


public class SAP {

    private Digraph graph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        graph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        ArrayList<Integer> i1 = new ArrayList<>();
        i1.add(v);
        ArrayList<Integer> i2 = new ArrayList<>();
        i2.add(w);
        return length(i1, i2);
    }

    // a common ancestor of v and w that participates in a shortest ancestral
    // path; -1 if no such path
    public int ancestor(int v, int w) {
        ArrayList<Integer> i1 = new ArrayList<>();
        i1.add(v);
        ArrayList<Integer> i2 = new ArrayList<>();
        i2.add(w);
        return ancestor(i1, i2);
    }

    // length of shortest ancestral path between any vertex in v and any vertex
    // in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        boolean[] marked1 = new boolean[graph.V()];
        int[] length = new int[graph.V()];
        bfs1(v, marked1, length);
        boolean[] marked2 = new boolean[graph.V()];
        int ret = bfs2(w, marked1, length, marked2)[0];
        // System.out.println(Arrays.toString(marked1));
        // System.out.println(Arrays.toString(marked2));
        return ret;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no
    // such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        boolean[] marked1 = new boolean[graph.V()];
        int[] length = new int[graph.V()];
        bfs1(v, marked1, length);
        boolean[] marked2 = new boolean[graph.V()];
        int ret = bfs2(w, marked1, length, marked2)[1];
        return ret;
    }

    /**
     * use bfs to mark all the vertexes and the distance from the original
     * vertex
     * 
     * @param v
     *            current vertex
     * @param marked
     *            represents the state of the vertex
     * @param length
     *            distance from the original vertex
     * @param curLength
     *            current length
     */
    private void bfs1(Iterable<Integer> itr, boolean[] marked, int[] length) {
        Queue<Integer> q = new Queue<Integer>();
        for (int v: itr) {
          q.enqueue(v);
          marked[v] = true;
          length[v] = 0;
        }
        while (!q.isEmpty()) {
            int cur = q.dequeue();
//            System.out.println("bfs1 " + cur + " " + length[cur]);
            for (int w : graph.adj(cur)) {
                if (!marked[w]) {
                    q.enqueue(w);
                    marked[w] = true;
                    length[w] = length[cur] + 1;
                }
            }
        }
    }

    /**
     * find the shortest ancestral path
     * 
     * @param w
     *            vertex w
     * @param marked1
     *            represents the vertexes bfs1 marked
     * @param length
     *            represents the corresponding distance bfs1 marked
     * @param marked2
     *            represents the vertexes bfs2 marked
     * @param curLength
     *            current distance from original vertex
     * @return a pair of integer, int[0] is the distance, int[1] is the common
     *         ancestor
     */
    private int[] bfs2(Iterable<Integer> itr, boolean[] marked1, int[] length, boolean[] marked2) {
        Queue<Integer> q = new Queue<Integer>();
        int[] distTo = new int[graph.V()];
        for (int w: itr) {
          q.enqueue(w);
          marked2[w] = true;
          distTo[w] = 0;
        }
        int distance = -1;
        int ancestor = -1;
        while (!q.isEmpty()) {
            int cur = q.dequeue();
//            System.out.println("bfs2 " + cur + " " + distTo[cur]);
            if (marked1[cur]) {
                int dist = length[cur] + distTo[cur];
                if (distance == -1 || dist < distance) {
                    distance = dist;
                    ancestor = cur;
                }
            }
            for (int i: graph.adj(cur)) {
                if (marked2[i]) continue;
                q.enqueue(i);
                marked2[i] = true;
                distTo[i] = distTo[cur] + 1;
            }
        }
        return new int[]{distance, ancestor};
    }
        

    // Queue<Integer> q1 = new Queue<Integer>();
    // Queue<Integer> q2 = new Queue<Integer>();
    // q2.enqueue(w);
    // int distance = -1;
    // int anscestor = -1;
    // int curLength = 0;
    // while (!q2.isEmpty()) {
    // Queue<Integer> tmp = q1;
    // q1 = q2;
    // q2 = tmp;
    // while (!q1.isEmpty()) {
    // int curVertex = q1.dequeue();
    // System.out.println("bfs2-startpoint" + w + "-" + curVertex + " " +
    // curLength);
    // if (marked2[curVertex])
    // continue;
    // marked2[curVertex] = true;
    // if (marked1[curVertex]) {
    // int dist = curLength + length[curVertex];
    // if (distance == -1 || dist < distance) {
    // distance = dist;
    // anscestor = curVertex;
    // }
    // }
    // for (int i : graph.adj(curVertex)) {
    // if (!marked2[i]) {
    // q2.enqueue(i);
    // }
    // }
    // }
    // curLength++;
    // }
    // return new int[] { distance, anscestor };
//    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

}