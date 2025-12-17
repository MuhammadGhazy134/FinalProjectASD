import java.util.*;

// ============= BoardGame.java =============
class BoardGame {
    private List<Node> nodes;
    private List<Player> players;
    private int currentPlayerIndex;
    private Random random;
    private Node startNode;
    private Node endNode;
    private int originalImageWidth;
    private int originalImageHeight;

    public BoardGame(int imageWidth, int imageHeight) {
        this.nodes = new ArrayList<>();
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.random = new Random();
        this.originalImageWidth = imageWidth;
        this.originalImageHeight = imageHeight;
    }

    public void initializeBoard() {
        // Create nodes based on your map - you'll need to set these coordinates
        // by looking at your image and noting the x,y positions of each circle
        createNodesFromMap();
        connectNodes();
    }

    private void createNodesFromMap() {
        // IMPORTANT: These coordinates should be based on your ORIGINAL image size
        // Example: If your image is 1152x864, use coordinates for that size

        nodes.add(new Node(0, 639, 809));  // START position (bottom center)
        nodes.add(new Node(1, 699, 597));
        nodes.add(new Node(2, 656, 596));
        nodes.add(new Node(3, 604, 585));  // Prime number - will trigger Dijkstra
        nodes.add(new Node(4, 549, 564));
        nodes.add(new Node(5, 493, 540));  // Multiple of 5 - extra turn

        // Continue adding all nodes from your map...
        // You need to map all the circle positions from your image

        // Mark start and end nodes
        startNode = nodes.get(0);
        startNode.setType(Node.NodeType.START);

        // Set the last node as END
        endNode = nodes.get(nodes.size() - 1);
        endNode.setType(Node.NodeType.END);
    }

    private void connectNodes() {
        // Connect nodes based on the paths in your map
        // Example connections - REPLACE WITH YOUR ACTUAL MAP CONNECTIONS

        nodes.get(0).addNeighbor(nodes.get(1));
        nodes.get(1).addNeighbor(nodes.get(2));
        nodes.get(2).addNeighbor(nodes.get(3));
        nodes.get(3).addNeighbor(nodes.get(4));
        nodes.get(4).addNeighbor(nodes.get(5));

        // Add more connections based on your map's paths
        // For intersections, connect nodes that cross paths
    }

    public void addPlayer(Player player) {
        player.setCurrentNode(startNode);
        players.add(player);
    }

    public int rollDice() {
        return random.nextInt(6) + 1; // 1-6
    }

    public void playTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);
        int diceRoll = rollDice();

        System.out.println("\n" + currentPlayer.getName() + "'s turn!");
        System.out.println("Rolled: " + diceRoll);

        movePlayer(currentPlayer, diceRoll);

        // Check for extra turn
        if (currentPlayer.hasExtraTurn()) {
            System.out.println("Extra turn! (Multiple of 5 moves reached)");
            currentPlayer.useExtraTurn();
        } else {
            nextPlayer();
        }
    }

    private void movePlayer(Player player, int spaces) {
        Node currentNode = player.getCurrentNode();

        // Simple linear movement
        for (int i = 0; i < spaces && currentNode != endNode; i++) {
            if (!currentNode.getNeighbors().isEmpty()) {
                // Choose the first neighbor (or implement path selection logic)
                currentNode = currentNode.getNeighbors().get(0);
            }
        }

        player.moveTo(currentNode);

        System.out.println(player.getName() + " moved to node " + currentNode.getId());

        // Check if landed on prime number node
        if (currentNode.getType() == Node.NodeType.PRIME) {
            System.out.println("Landed on prime number node!");
            DijkstraPathfinder.enableIntersectionPaths(currentNode);
        }

        // Check if reached end
        if (currentNode == endNode) {
            System.out.println(player.getName() + " WINS!");
        }
    }

    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public int getOriginalImageWidth() {
        return originalImageWidth;
    }

    public int getOriginalImageHeight() {
        return originalImageHeight;
    }
}