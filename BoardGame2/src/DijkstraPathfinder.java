import java.util.*;

// ============= Dijkstra.java =============
class DijkstraPathfinder {

    public static List<Node> findShortestPath(Node start, Node end, List<Node> allNodes) {
        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(
                Comparator.comparingInt(distances::get)
        );

        // Initialize distances
        for (Node node : allNodes) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.equals(end)) {
                break;
            }

            for (Node neighbor : current.getNeighbors()) {
                int newDist = distances.get(current) + 1;
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, current);
                    queue.remove(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        // Reconstruct path
        List<Node> path = new ArrayList<>();
        Node current = end;
        while (current != null && previous.containsKey(current)) {
            path.add(0, current);
            current = previous.get(current);
        }
        if (!path.isEmpty()) {
            path.add(0, start);
        }

        return path;
    }

    public static void enableIntersectionPaths(Node primeNode) {
        System.out.println("Dijkstra activated! Intersection paths enabled at node " + primeNode.getId());
    }
}