import java.awt.*;

// ============= Player.java =============
class Player {
    private String name;
    private Color color;
    private Node currentNode;
    private int totalMoves;
    private boolean hasExtraTurn;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.totalMoves = 0;
        this.hasExtraTurn = false;
    }

    public void moveTo(Node node) {
        this.currentNode = node;
        this.totalMoves++;

        // Check for extra turn (every 5th move)
        if (totalMoves % 5 == 0) {
            hasExtraTurn = true;
        }
    }

    // Getters and setters
    public String getName() { return name; }
    public Color getColor() { return color; }
    public Node getCurrentNode() { return currentNode; }
    public void setCurrentNode(Node node) { this.currentNode = node; }
    public int getTotalMoves() { return totalMoves; }
    public boolean hasExtraTurn() { return hasExtraTurn; }
    public void useExtraTurn() { this.hasExtraTurn = false; }
}