import java.io.*;

import java.util.*;

import edu.princeton.cs.algs4.*;

public class BaseballElimination {

  private String[] teams;

  private Map<String, Integer> teamsMap;

  private int numOfTeams;

  private int[] wins;

  private int[] loss;

  private int[] remains;

  private int[][] againsts;

//  private int[] insideRemains; // matches other teams

  private boolean[] isEliminated;

  private Set<String>[] certificates;

  @SuppressWarnings("unchecked")
  public BaseballElimination(String filename) {
    // create a baseball division from given filename in format specified below
    Scanner s = null;
    try {
      s = new Scanner(new File(filename));
      numOfTeams = Integer.parseInt(s.nextLine()); // total teams
      teams = new String[numOfTeams];
      teamsMap = new HashMap<>();
      wins = new int[numOfTeams];
      loss = new int[numOfTeams];
      remains = new int[numOfTeams];
      againsts = new int[numOfTeams][numOfTeams];
//      insideRemains = new int[numOfTeams];
      isEliminated = new boolean[numOfTeams];
      certificates = (HashSet<String>[]) new HashSet[numOfTeams];
      int index = 0;
      while (s.hasNextLine()) {
        String[] tokens = s.nextLine().trim().split("\\s+");
        teams[index] = tokens[0];
        teamsMap.put(tokens[0], index);
        wins[index] = Integer.parseInt(tokens[1]);
        loss[index] = Integer.parseInt(tokens[2]);
        remains[index] = Integer.parseInt(tokens[3]);
//        insideRemains[index] = 0;
        for (int i = 4 + index; i < tokens.length; i++) {
          int num = Integer.parseInt(tokens[i]); // number of matches to "other"
                                                 // team
          againsts[index][i - 4] = num;
          againsts[i - 4][index] = num;
//          insideRemains[index] += num;
          // int matchIndex = 1 + (n - 1 + n - 1 - index + 1) * index / 2 +
          // other - index + 1;
          // division.addEdge(new FlowEdge(0, matchIndex, num));
          // division.addEdge(new FlowEdge(matchIndex, N - n + index,
          // Double.MAX_VALUE));
          // division.addEdge(new FlowEdge(matchIndex, N - n + other,
          // Double.MAX_VALUE));
        }
        index++;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (s != null)
        s.close();
    }
    // calculate eliminations
    int N = numOfTeams + 2 + numOfTeams * (numOfTeams - 1) / 2; // total
                                                                // vertices in
                                                                // the network
//    int insideSum = 0;
//    for (int i : insideRemains)
//      insideSum += i;
    for (int k = 0; k < numOfTeams; k++) {
      int[][] opponents = new int[numOfTeams * (numOfTeams - 1) / 2 + 1][2];
//      ArrayList<FlowEdge> remainsEdge = new ArrayList<>();
      FlowNetwork division = new FlowNetwork(N);
      for (int i = 0; i < numOfTeams; i++) {
        for (int j = i+1; j < numOfTeams; j++) {
          int matchIndex = (numOfTeams * 2 - 1 - i) * i / 2 + j - i;
          opponents[matchIndex][0] = i;
          opponents[matchIndex][1] = j;
          FlowEdge tmp = new FlowEdge(0, matchIndex, againsts[i][j]);
//          remainsEdge.add(tmp);
          division.addEdge(tmp);
          division.addEdge(new FlowEdge(matchIndex, N - numOfTeams + i - 1, Double.MAX_VALUE));
          division.addEdge(new FlowEdge(matchIndex, N - numOfTeams + j - 1, Double.MAX_VALUE));
        }
      }
      certificates[k] = new HashSet<>();
      int maxWins = wins[k] + remains[k];
      for (int l = 0; l < numOfTeams; l++) {
        if (l != k) {
          if (maxWins - wins[l] < 0) certificates[k].add(teams[l]);
          division.addEdge(new FlowEdge(N - numOfTeams + l - 1, N-1, Math.max(maxWins - wins[l], 0)));
        }
        else
          division.addEdge(new FlowEdge(N - numOfTeams + l - 1, N-1, remains[k]));
      }
      FordFulkerson maxFlow = new FordFulkerson(division, 0, N-1);
      for (int i = 0; i < numOfTeams; i++) {
        if (i != k && maxFlow.inCut(N - numOfTeams - 1 + i))
          certificates[k].add(teams[i]);
      }
//      System.out.println("max flow: " + maxFlow.value());
//      System.out.println(division.toString());
//      for (FlowEdge f: remainsEdge) {
//        if (f.flow() != f.capacity()) {
//          certificates[k].add(teams[opponents[f.to()][0]]);
//          certificates[k].add(teams[opponents[f.to()][1]]);
//        }
//      }
//      
      if (!certificates[k].isEmpty())
        isEliminated[k] = true;
    }
//    System.out.println(Arrays.deepToString(againsts));
  }

  public int numberOfTeams() {
    // number of teams
    return numOfTeams;
  }

  public Iterable<String> teams() {
    // all teams
    return Arrays.asList(teams);
  }

  public int wins(String team) {
    // number of wins for given team
    Integer index = teamsMap.get(team);
    if (index == null) throw new IllegalArgumentException();
    return wins[index];
  }

  public int losses(String team) {
    // number of losses for given team
    Integer index = teamsMap.get(team);
    if (index == null) throw new IllegalArgumentException();
    return loss[index];
  }

  public int remaining(String team) {
    // number of remaining games for given team
    Integer index = teamsMap.get(team);
    if (index == null) throw new IllegalArgumentException();
    return remains[index];
  }

  public int against(String team1, String team2) {
    // number of remaining games between team1 and team2
    Integer index1 = teamsMap.get(team1);
    Integer index2 = teamsMap.get(team2);
    if (index1 == null || index2 == null) throw new IllegalArgumentException();
    return againsts[index1][index2];
  }

  public boolean isEliminated(String team) {
    // is given team eliminated?
    Integer index = teamsMap.get(team);
    if (index == null) throw new IllegalArgumentException();
    return isEliminated[index];
  }

  public Iterable<String> certificateOfElimination(String team) {
    // subset R of teams that eliminates given team; null if not eliminated
    Integer index = teamsMap.get(team);
    if (index == null) throw new IllegalArgumentException();
    return certificates[index].isEmpty() ? null : certificates[index];
  }

  public static void main(String[] args) {
    BaseballElimination division = new BaseballElimination(args[0]);
    for (String team : division.teams()) {
      if (division.isEliminated(team)) {
        StdOut.print(team + " is eliminated by the subset R = { ");
        for (String t : division.certificateOfElimination(team)) {
          StdOut.print(t + " ");
        }
        StdOut.println("}");
      } else {
        StdOut.println(team + " is not eliminated");
      }
    }
  }

}
