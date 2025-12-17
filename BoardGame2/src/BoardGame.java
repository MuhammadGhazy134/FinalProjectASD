import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

// ============= BoardGame.java =============
class BoardGame {
    private List<Node> nodes;
    private List<Player> players;
    private int currentPlayerIndex;
    private Random random;
    private Dice dice;
    private Node startNode;
    private Node endNode;
    private int originalImageWidth;
    private int originalImageHeight;
    private boolean gameOver;
    private boolean doubleTurn;

    public BoardGame(int imageWidth, int imageHeight) {
        this.nodes = new ArrayList<>();
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.random = new Random();
        this.dice = new Dice();
        this.originalImageWidth = imageWidth;
        this.originalImageHeight = imageHeight;
        this.gameOver = false;
        this.doubleTurn = false;
    }

    public void initializeBoard() {
        createNodesFromMap();
        connectNodes();
        generateRandomPoints();
    }

    private void createNodesFromMap() {
        // REPLACE THESE WITH YOUR ACTUAL MAP COORDINATES
        // Click on your map to get coordinates, then add them here
        nodes.add(new Node(0, 640, 805));  // START
        nodes.add(new Node(1, 640, 750));
        nodes.add(new Node(2, 600, 720));
        nodes.add(new Node(3, 560, 690));  // Prime
        nodes.add(new Node(4, 520, 660));
        nodes.add(new Node(5, 480, 630));  // Multiple of 5
        nodes.add(new Node(6, 440, 600));

        // Add more nodes here...

        startNode = nodes.get(0);
        startNode.setType(Node.NodeType.START);

        endNode = nodes.get(nodes.size() - 1);
        endNode.setType(Node.NodeType.END);
    }

    private void connectNodes() {
        // Connect nodes based on your map paths
        for (int i = 0; i < nodes.size() - 1; i++) {
            nodes.get(i).addNeighbor(nodes.get(i + 1));
        }

        // Add branching paths/intersections here
        // Example: nodes.get(5).addNeighbor(nodes.get(15)); // Shortcut
    }

    private void generateRandomPoints() {
        // Set end node to 100 points
        endNode.setPoints(100);

        // Random points on other nodes
        int numberOfPointBoxes = 10 + random.nextInt(6);
        List<Integer> availableIndices = new ArrayList<>();

        for (int i = 1; i < nodes.size() - 1; i++) {
            availableIndices.add(i);
        }

        for (int i = 0; i < numberOfPointBoxes && !availableIndices.isEmpty(); i++) {
            int index = random.nextInt(availableIndices.size());
            int nodeIndex = availableIndices.remove(index);
            Node node = nodes.get(nodeIndex);
            int points = 1 + random.nextInt(10);
            node.setPoints(points);
            System.out.println("Points: Node " + node.getId() + " = " + points);
        }
    }

    public void addPlayer(Player player) {
        player.setCurrentNode(startNode);
        players.add(player);
    }

    public void playTurn(Runnable onComplete) {
        if (gameOver) return;

        Player currentPlayer = players.get(currentPlayerIndex);
        int roll = dice.roll();
        boolean movingForward = dice.isGreen();

        Node startNode = currentPlayer.getCurrentNode();
        boolean isPrimeStart = startNode.getType() == Node.NodeType.PRIME;

        if (isPrimeStart) {
            System.out.println("Dijkstra activated! Starting from prime node " + startNode.getId());
            DijkstraPathfinder.enableIntersectionPaths(startNode);
        }

        // Simple movement for now
        Node targetNode = findTargetNode(currentPlayer.getCurrentNode(), roll, movingForward);

        // Animate movement
        animateMovement(currentPlayer, targetNode, () -> {
            finishTurn(currentPlayer, onComplete);
        });
    }

    private Node findTargetNode(Node start, int steps, boolean forward) {
        Node current = start;

        for (int i = 0; i < steps; i++) {
            if (current.getNeighbors().isEmpty()) break;

            if (forward) {
                current = current.getNeighbors().get(0);
            } else {
                // For backward, try to go to previous node (simplified)
                boolean found = false;
                for (Node node : nodes) {
                    if (node.getNeighbors().contains(current)) {
                        current = node;
                        found = true;
                        break;
                    }
                }
                if (!found) break;
            }

            if (current == endNode) break;
        }

        return current;
    }

    private void animateMovement(Player player, Node targetNode, Runnable onComplete) {
        // Simple animation - just move directly
        javax.swing.Timer moveTimer = new javax.swing.Timer(300, new java.awt.event.ActionListener() {
            private Node currentTarget = player.getCurrentNode();
            private int step = 0;
            private List<Node> path = getPathToTarget(player.getCurrentNode(), targetNode);

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (step >= path.size()) {
                    ((javax.swing.Timer)e.getSource()).stop();
                    player.moveTo(targetNode);
                    if (onComplete != null) onComplete.run();
                    return;
                }

                player.setDisplayNode(path.get(step));
                step++;
            }
        });
        moveTimer.start();
    }

    private List<Node> getPathToTarget(Node start, Node target) {
        List<Node> path = new ArrayList<>();
        Node current = start;
        path.add(current);

        while (current != target && !current.getNeighbors().isEmpty()) {
            Node next = current.getNeighbors().get(0);
            path.add(next);
            current = next;

            if (path.size() > 100) break; // Safety check
        }

        return path;
    }

    private void finishTurn(Player player, Runnable onComplete) {
        // Collect points
        Node currentNode = player.getCurrentNode();
        if (currentNode.getPoints() > 0) {
            int points = currentNode.getPoints();
            player.addScore(points);
            currentNode.setPoints(0);
            System.out.println(player.getName() + " collected " + points + " points!");
        }

        // Check for double turn
        doubleTurn = (player.getTotalMoves() % 5 == 0);

        if (doubleTurn) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JLabel message = new javax.swing.JLabel(
                        "<html><center><b style='font-size: 24px; color: rgb(255, 215, 0);'>" +
                                "CONGRATULATIONS!<br>You get a<br>DOUBLE TURN!</b></center></html>");
                message.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                javax.swing.UIManager.put("OptionPane.background", new Color(40, 40, 40));
                javax.swing.UIManager.put("Panel.background", new Color(40, 40, 40));
                javax.swing.JOptionPane.showMessageDialog(null, message, "Double Turn!",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
            });
        }

        // Check win condition
        if (currentNode == endNode) {
            gameOver = true;
        }

        // Next player
        if (!gameOver && !doubleTurn) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }

        if (onComplete != null) onComplete.run();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<Node> getNodes() { return nodes; }
    public List<Player> getPlayers() { return players; }
    public Node getStartNode() { return startNode; }
    public Node getEndNode() { return endNode; }
    public int getOriginalImageWidth() { return originalImageWidth; }
    public int getOriginalImageHeight() { return originalImageHeight; }
    public boolean isGameOver() { return gameOver; }
    public boolean isDoubleTurn() { return doubleTurn; }
    public void clearDoubleTurn() { doubleTurn = false; }
    public Dice getDice() { return dice; }

    public boolean isAnyPlayerAnimating() {
        for (Player player : players) {
            if (player.isAnimating()) return true;
        }
        return false;
    }
}