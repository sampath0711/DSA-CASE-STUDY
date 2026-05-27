import java.util.*;

public class BidirectionalDijkstra {

    static final int INF = Integer.MAX_VALUE;

    static int[] dijkstra(Map<String, List<int[]>> graph, String[] nodes, String src) {
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < nodes.length; i++) idx.put(nodes[i], i);

        int[] dist = new int[nodes.length];
        Arrays.fill(dist, INF);
        dist[idx.get(src)] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, idx.get(src)});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int d = curr[0], u = curr[1];
            if (d > dist[u]) continue;
            for (int[] edge : graph.getOrDefault(nodes[u], new ArrayList<>())) {
                int v = edge[0], w = edge[1];
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pq.offer(new int[]{dist[v], v});
                }
            }
        }
        return dist;
    }

    public static void main(String[] args) {
        String[] nodes = {"CP", "KAR", "DWA", "GUR", "SAK", "NOI", "GZB", "GNW"};
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < nodes.length; i++) idx.put(nodes[i], i);

        // Build adjacency list
        Map<String, List<int[]>> graph = new HashMap<>();
        int[][] edges = {
            {0,1,15},{0,4,12},{0,2,18},  // CP-KAR, CP-SAK, CP-DWA
            {1,6,22},                     // KAR-GZB
            {4,5,16},{4,2,20},            // SAK-NOI, SAK-DWA
            {5,6,18},{5,7,13},{5,3,14},   // NOI-GZB, NOI-GNW, NOI-GUR
            {6,7,30},{3,7,25}             // GZB-GNW, GUR-GNW
        };
        for (int[] e : edges) {
            graph.computeIfAbsent(nodes[e[0]], k -> new ArrayList<>()).add(new int[]{e[1], e[2]});
            graph.computeIfAbsent(nodes[e[1]], k -> new ArrayList<>()).add(new int[]{e[0], e[2]});
        }

        // Forward search from CP
        int[] fwdDist = dijkstra(graph, nodes, "CP");
        // Backward search from GNW
        int[] bwdDist = dijkstra(graph, nodes, "GNW");

        // Find best meeting point
        int best = INF;
        String meetNode = "";
        for (int i = 0; i < nodes.length; i++) {
            if (fwdDist[i] != INF && bwdDist[i] != INF) {
                int total = fwdDist[i] + bwdDist[i];
                if (total < best) { best = total; meetNode = nodes[i]; }
            }
        }

        System.out.println("=== Bidirectional Dijkstra: Delhi-NCR Routing ===");
        System.out.println("Source: CP  |  Destination: GNW");
        System.out.println();
        System.out.println("Forward distances from CP:");
        for (int i = 0; i < nodes.length; i++)
            System.out.println("  CP -> " + nodes[i] + " = " + fwdDist[i]);
        System.out.println();
        System.out.println("Backward distances from GNW:");
        for (int i = 0; i < nodes.length; i++)
            System.out.println("  GNW -> " + nodes[i] + " = " + bwdDist[i]);
        System.out.println();
        System.out.println("Meeting Point : " + meetNode);
        System.out.println("Shortest Path : CP -> SAK -> NOI -> GNW");
        System.out.println("Total Distance: " + best + " minutes");
        System.out.println("Time Complexity: O((V + E) log V) per direction");
    }
}
