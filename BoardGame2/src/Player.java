import java.awt.*;

// ============= Player.java =============
class Player {
    private String name;
    private Color color;
    private Node currentNode;
    private Node displayNode;
    private int totalMoves;
    private boolean hasExtraTurn;
    private int score;
    private static final int PLAYER_SIZE = 20;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.totalMoves = 0;
        this.hasExtraTurn = false;
        this.score = 0;
    }

    public void moveTo(Node node) {
        this.currentNode = node;
        this.displayNode = node;
        this.totalMoves++;

        // Check for extra turn (every 5th move)
        if (totalMoves % 5 == 0) {
            hasExtraTurn = true;
        }
    }

    public void setDisplayNode(Node node) {
        this.displayNode = node;
    }

    public boolean isAnimating() {
        return displayNode != currentNode;
    }

    // Getters and setters
    public String getName() { return name; }
    public Color getColor() { return color; }
    public Node getCurrentNode() { return currentNode; }
    public Node getDisplayNode() { return displayNode; }
    public void setCurrentNode(Node node) { this.currentNode = node; this.displayNode = node; }
    public int getTotalMoves() { return totalMoves; }
    public boolean hasExtraTurn() { return hasExtraTurn; }
    public void useExtraTurn() { this.hasExtraTurn = false; }
    public int getScore() { return score; }
    public void addScore(int points) { this.score += points; }
    public void setScore(int score) { this.score = score; }  // <-- ADD THIS LINE
}
