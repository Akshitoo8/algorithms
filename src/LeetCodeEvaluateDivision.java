package src;

import java.util.*;

//Problem, my solution and explanation: https://leetcode.com/problems/evaluate-division/solutions/4383747/java-solution-beats-100/
public class LeetCodeEvaluateDivision {
    static class Pair {
        String parent;
        Double ratio;

        Pair(String parent, Double ratio) {
            this.parent = parent;
            this.ratio = ratio;
        }

        @Override
        public String toString() {
            return "{ parent: " + parent + ", ratio: " + ratio + "}";
        }
    }

    //test
    public static void main(String[] args) {
        List<List<String>> equations = new ArrayList<>();
        equations.add(List.of("a", "b"));
        equations.add(List.of("c", "b"));
        equations.add(List.of("d", "b"));
        equations.add(List.of("w", "x"));
        equations.add(List.of("y", "x"));
        equations.add(List.of("z", "x"));
        equations.add(List.of("w", "d"));

        double[] values = new double[equations.size()];
        values[0] = 2.0;
        values[1] = 3.0;
        values[2] = 4.0;
        values[3] = 5.0;
        values[4] = 6.0;
        values[5] = 7.0;
        values[6] = 8.0;

        List<List<String>> queries = new ArrayList<>();
        queries.add(List.of("a", "z"));
        queries.add(List.of("a", "c"));

        double[] results = calcEquation2(equations, values, queries);
        Arrays.stream(results).forEach(System.out::println);
    }

    static void union(Map<String, Pair> map, String key1, String key2, Double linkingRatio) {
        if (!map.get(key1).parent.equals(map.get(key2).parent)) {
            List<Pair> pair = find(map, key1, key2);
            Pair pair1 = pair.get(0);
            Pair pair2 = pair.get(1);

            map.get(pair2.parent).parent = pair1.parent;
            map.get(pair2.parent).ratio = pair1.ratio / (linkingRatio * pair2.ratio);
        }
    }

    public static double[] calcEquation2(List<List<String>> equations, double[] values, List<List<String>> queries) {
        Map<String, Pair> map = new HashMap<>();

        //union of the two values in a single equation.
        int i = 0;
        for (List<String> equation : equations) {
            String key1 = equation.get(0);
            String key2 = equation.get(1);
            if (!map.containsKey(key1) && !map.containsKey(key2)) {
                map.put(key1, new Pair(key2, values[i]));
                map.put(key2, new Pair(key2, 1.0));
            } else if (map.containsKey(key1) && !map.containsKey(key2)) {
                map.put(key2, new Pair(map.get(key1).parent, (1.0 / values[i]) * map.get(key1).ratio));
            } else if (!map.containsKey(key1) && map.containsKey(key2)) {
                map.put(key1, new Pair(map.get(key2).parent, values[i] * map.get(key2).ratio));
            } else {
                union(map, key1, key2, values[i]);
            }
            i++;
        }

        return getResult(queries, map);
    }

    private static double[] getResult(List<List<String>> queries, Map<String, Pair> map) {
        double[] results = new double[queries.size()];
        int j = 0;
        for (List<String> query : queries) {
            if (!map.containsKey(query.get(0)) || !map.containsKey(query.get(1))) {
                results[j] = -1;
                j++;
                continue;
            }

            List<Pair> pairList = find(map, query.get(0), query.get(1));
            Pair pair1 = pairList.get(0);
            Pair pair2 = pairList.get(1);

            if (pair1.parent.equals(pair2.parent)) {
                results[j] = pair1.ratio / pair2.ratio;
            } else {
                results[j] = -1;
            }
            j++;
        }
        return results;
    }

    private static List<Pair> find(Map<String, Pair> map, String key1, String key2) {
        String parent1 = map.get(key1).parent;
        Double ratio1 = map.get(key1).ratio;
        String parent2 = map.get(key2).parent;
        Double ratio2 = map.get(key2).ratio;

        while (!parent1.equals(map.get(parent1).parent)) {
            ratio1 = ratio1 * map.get(parent1).ratio;
            parent1 = map.get(parent1).parent;
        }

        while (!parent2.equals(map.get(parent2).parent)) {
            ratio2 = ratio2 * map.get(parent2).ratio;
            parent2 = map.get(parent2).parent;
        }

        List<Pair> pairList = new ArrayList<>();
        pairList.add(new Pair(parent1, ratio1));
        pairList.add(new Pair(parent2, ratio2));
        return pairList;
    }
}
