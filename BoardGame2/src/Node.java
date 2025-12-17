// ============= Node.java =============
import java.awt.*;
import java.util.*;
import java.util.List;

class Node {
    private int id;
    private Point originalPosition;  // Store original coordinates
    private List<Node> neighbors;
    private NodeType type;

    public enum NodeType {
        NORMAL, START, END, PRIME, SPECIAL
    }

    public Node(int id, int x, int y) {
        this.id = id;
        this.originalPosition = new Point(x, y);
        this.neighbors = new ArrayList<>();
        this.type = isPrime(id) ? NodeType.PRIME : NodeType.NORMAL;
    }

    public void addNeighbor(Node neighbor) {
        if (!neighbors.contains(neighbor)) {
            neighbors.add(neighbor);
        }
    }

    public void removeNeighbor(Node neighbor) {
        neighbors.remove(neighbor);
    }

    private boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    // Calculate scaled position based on current panel size
    public Point getScaledPosition(int originalImageWidth, int originalImageHeight,
                                   int currentWidth, int currentHeight) {
        double scaleX = (double) currentWidth / originalImageWidth;
        double scaleY = (double) currentHeight / originalImageHeight;

        int scaledX = (int) (originalPosition.x * scaleX);
        int scaledY = (int) (originalPosition.y * scaleY);

        return new Point(scaledX, scaledY);
    }

    // Getters and setters
    public int getId() { return id; }
    public Point getOriginalPosition() { return originalPosition; }
    public List<Node> getNeighbors() { return neighbors; }
    public NodeType getType() { return type; }
    public void setType(NodeType type) { this.type = type; }
}