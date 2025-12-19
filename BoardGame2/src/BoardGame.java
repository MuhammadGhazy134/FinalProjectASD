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
        // MAIN PATH - Straight from start to end (0-42)
        nodes.add(new Node(0, 637, 803));   // START
        nodes.add(new Node(1, 629, 752));
        nodes.add(new Node(2, 589, 748));   // PRIME
        nodes.add(new Node(3, 542, 738));   // PRIME
        nodes.add(new Node(4, 492, 711));
        nodes.add(new Node(5, 444, 682));   // PRIME & Multiple of 5 - Double turn
        nodes.add(new Node(6, 391, 644));
        nodes.add(new Node(7, 340, 620));   // PRIME
        nodes.add(new Node(8, 299, 615));
        nodes.add(new Node(9, 210, 543));
        nodes.add(new Node(10, 168, 523));  // Multiple of 5 - Double turn
        nodes.add(new Node(11, 157, 474));  // PRIME
        nodes.add(new Node(12, 78, 446));
        nodes.add(new Node(13, 101, 394));  // PRIME
        nodes.add(new Node(14, 80, 337));
        nodes.add(new Node(15, 114, 318));  // Multiple of 5 - Double turn
        nodes.add(new Node(16, 165, 313));
        nodes.add(new Node(17, 205, 352));  // PRIME
        nodes.add(new Node(18, 221, 384));
        nodes.add(new Node(19, 259, 384));  // PRIME
        nodes.add(new Node(20, 276, 331));  // Multiple of 5 - Double turn
        nodes.add(new Node(21, 318, 243));
        nodes.add(new Node(22, 353, 251));
        nodes.add(new Node(23, 383, 247));  // PRIME
        nodes.add(new Node(24, 471, 241));
        nodes.add(new Node(25, 500, 248));  // Multiple of 5 - Double turn
        nodes.add(new Node(26, 541, 258));
        nodes.add(new Node(27, 579, 262));
        nodes.add(new Node(28, 617, 257));
        nodes.add(new Node(29, 657, 246));  // PRIME
        nodes.add(new Node(30, 709, 237));  // Multiple of 5 - Double turn
        nodes.add(new Node(31, 731, 266));  // PRIME
        nodes.add(new Node(32, 762, 273));
        nodes.add(new Node(33, 798, 276));
        nodes.add(new Node(34, 838, 335));
        nodes.add(new Node(35, 899, 407));  // Multiple of 5 - Double turn
        nodes.add(new Node(36, 937, 409));
        nodes.add(new Node(37, 975, 413));  // PRIME
        nodes.add(new Node(38, 1031, 403));
        nodes.add(new Node(39, 1054, 347));
        nodes.add(new Node(40, 1031, 300));  // Multiple of 5 - Double turn
        nodes.add(new Node(41, 991, 257));   // PRIME
        nodes.add(new Node(42, 957, 232));   // END

        // SHORTCUT 1 - Branches from Node 8, merges at Node 35 (43-56)
        nodes.add(new Node(43, 301, 543));   // PRIME
        nodes.add(new Node(44, 336, 523));
        nodes.add(new Node(45, 376, 518));   // Multiple of 5 - Double turn
        nodes.add(new Node(46, 409, 514));
        nodes.add(new Node(47, 455, 495));   // PRIME
        nodes.add(new Node(48, 494, 483));
        nodes.add(new Node(49, 533, 476));
        nodes.add(new Node(50, 573, 476));   // Multiple of 5 - Double turn
        nodes.add(new Node(51, 609, 477));
        nodes.add(new Node(52, 647, 474));
        nodes.add(new Node(53, 688, 481));   // PRIME
        nodes.add(new Node(54, 761, 471));
        nodes.add(new Node(55, 830, 514));   // Multiple of 5 - Double turn
        nodes.add(new Node(56, 872, 426));

        // SHORTCUT 2 - Branches from Node 19, merges at Node 49 (57-61)
        nodes.add(new Node(57, 311, 361));
        nodes.add(new Node(58, 363, 397));
        nodes.add(new Node(59, 423, 403));   // PRIME
        nodes.add(new Node(60, 492, 420));   // Multiple of 5 - Double turn
        nodes.add(new Node(61, 531, 424));   // PRIME

        // SHORTCUT 3 - Branches from Node 23, merges at Node 33 (62-72)
        nodes.add(new Node(62, 412, 207));
        nodes.add(new Node(63, 433, 139));   // PRIME
        nodes.add(new Node(64, 481, 108));
        nodes.add(new Node(65, 497, 140));   // Multiple of 5 - Double turn
        nodes.add(new Node(66, 529, 147));
        nodes.add(new Node(67, 563, 145));   // PRIME
        nodes.add(new Node(68, 604, 143));
        nodes.add(new Node(69, 635, 129));
        nodes.add(new Node(70, 644, 80));    // Multiple of 5 - Double turn
        nodes.add(new Node(71, 826, 214));   // PRIME
        nodes.add(new Node(72, 825, 256));

        startNode = nodes.get(0);
        startNode.setType(Node.NodeType.START);

        endNode = nodes.get(42);
        endNode.setType(Node.NodeType.END);
    }

    private void connectNodes() {
        // Connect main path
        for (int i = 0; i < 42; i++) {
            nodes.get(i).addNeighbor(nodes.get(i + 1));
        }

        // SHORTCUT 1: Node 8 → Node 35
        nodes.get(8).addNeighbor(nodes.get(43));
        for (int i = 43; i < 56; i++) {
            nodes.get(i).addNeighbor(nodes.get(i + 1));
        }
        nodes.get(56).addNeighbor(nodes.get(35));

        // SHORTCUT 2: Node 19 → Node 49
        nodes.get(19).addNeighbor(nodes.get(57));
        for (int i = 57; i < 61; i++) {
            nodes.get(i).addNeighbor(nodes.get(i + 1));
        }
        nodes.get(61).addNeighbor(nodes.get(49));

        // SHORTCUT 3: Node 23 → Node 33
        nodes.get(23).addNeighbor(nodes.get(62));
        for (int i = 62; i < 72; i++) {
            nodes.get(i).addNeighbor(nodes.get(i + 1));
        }
        nodes.get(72).addNeighbor(nodes.get(33));
    }

    private void generateRandomPoints() {
        endNode.setPoints(100);
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

        Node startingNode = currentPlayer.getCurrentNode();
        boolean isPrimeStart = startingNode.getType() == Node.NodeType.PRIME;

        // Check distance to end node
        int distanceToEnd = calculateDistanceToEnd(startingNode);

        // If near the end and moving forward, check for exact landing rule
        if (movingForward && distanceToEnd > 0 && distanceToEnd < 10) {
            if (roll > distanceToEnd) {
                // Roll is too high! Cannot move
                showExactLandingMessage(distanceToEnd, roll);
                finishTurn(currentPlayer, onComplete);
                return;
            } else if (roll == distanceToEnd) {
                // Perfect! Exact landing on end node
                showPerfectLandingMessage();
            }
        }

        // Find target node with Dijkstra if starting from prime
        Node targetNode;
        boolean usedShortcut = false;
        List<Node> customPath = null;

        if (isPrimeStart && movingForward) {
            PathResult pathResult = findTargetNodeWithDijkstra(startingNode, roll);
            targetNode = pathResult.targetNode;
            usedShortcut = pathResult.usedShortcut;
            customPath = pathResult.pathTaken;
        } else {
            targetNode = findTargetNode(startingNode, roll, movingForward);
        }

        // Show notification ONLY if shortcut was used
        if (usedShortcut) {
            showDijkstraNotification();
        }

        // Animate movement with custom path if available
        if (customPath != null) {
            animateMovementWithPath(currentPlayer, customPath, () -> {
                finishTurn(currentPlayer, onComplete);
            });
        } else {
            animateMovement(currentPlayer, targetNode, () -> {
                finishTurn(currentPlayer, onComplete);
            });
        }
    }

    private static class PathResult {
        Node targetNode;
        boolean usedShortcut;
        List<Node> pathTaken;

        PathResult(Node targetNode, boolean usedShortcut, List<Node> pathTaken) {
            this.targetNode = targetNode;
            this.usedShortcut = usedShortcut;
            this.pathTaken = pathTaken;
        }
    }

    private PathResult findTargetNodeWithDijkstra(Node start, int steps) {
        // Check if we can reach an intersection with the current roll
        Node current = start;
        List<Node> pathTaken = new ArrayList<>();
        pathTaken.add(current);

        for (int i = 0; i < steps; i++) {
            if (current.getNeighbors().isEmpty()) break;

            // Check if current node is an intersection (has multiple paths)
            if (current.getNeighbors().size() > 1) {
                // We reached an intersection! Use Dijkstra to find shortest path
                Node shortestPathNode = findShortestPathAtIntersection(current);

                // Continue moving from the chosen path
                current = shortestPathNode;
                pathTaken.add(current);

                // Continue for remaining steps
                for (int j = i + 1; j < steps; j++) {
                    if (current.getNeighbors().isEmpty()) break;
                    current = current.getNeighbors().get(0);
                    pathTaken.add(current);
                    if (current == endNode) break;
                }

                // We used a shortcut! Return the custom path
                return new PathResult(current, true, pathTaken);
            } else {
                // Normal movement, no intersection yet
                current = current.getNeighbors().get(0);
                pathTaken.add(current);
                if (current == endNode) break;
            }
        }

        // No intersection was reached, normal movement
        return new PathResult(current, false, null);
    }

    private Node findShortestPathAtIntersection(Node intersection) {
        // Get all possible paths from this intersection
        List<Node> neighbors = intersection.getNeighbors();

        Node bestPath = neighbors.get(0);
        int shortestDistance = Integer.MAX_VALUE;

        for (Node neighbor : neighbors) {
            // Calculate distance from this neighbor to the end node
            List<Node> pathToEnd = DijkstraPathfinder.findShortestPath(neighbor, endNode, nodes);
            int distance = pathToEnd.isEmpty() ? Integer.MAX_VALUE : pathToEnd.size() - 1;

            if (distance < shortestDistance) {
                shortestDistance = distance;
                bestPath = neighbor;
            }
        }

        System.out.println("Dijkstra chose shortest path at intersection: Node " + bestPath.getId() +
                " (Distance to end: " + shortestDistance + ")");

        return bestPath;
    }

    private void showDijkstraNotification() {
        SwingUtilities.invokeLater(() -> {
            // Create a custom JWindow for auto-dismissing notification
            JWindow notification = new JWindow();
            notification.setAlwaysOnTop(true);

            JPanel panel = new JPanel();
            panel.setBackground(new Color(40, 40, 100));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 200, 255), 3),
                    BorderFactory.createEmptyBorder(20, 40, 20, 40)
            ));

            JLabel message = new JLabel(
                    "<html><center><b style='font-size: 24px; color: rgb(100, 200, 255);'>" +
                            "🌟 DIJKSTRA ACTIVATED! 🌟<br><br>" +
                            "<span style='font-size: 16px; color: rgb(255, 255, 255);'>" +
                            "Shortest path taken<br>at intersection!</span>" +
                            "</b></center></html>");
            message.setHorizontalAlignment(SwingConstants.CENTER);

            panel.add(message);
            notification.add(panel);
            notification.pack();
            notification.setLocationRelativeTo(null);
            notification.setVisible(true);

            // Auto-dismiss after 2 seconds
            javax.swing.Timer dismissTimer = new javax.swing.Timer(2000, e -> {
                notification.dispose();
            });
            dismissTimer.setRepeats(false);
            dismissTimer.start();
        });
    }

    private void showExactLandingMessage(int needed, int rolled) {
        SwingUtilities.invokeLater(() -> {
            JWindow notification = new JWindow();
            notification.setAlwaysOnTop(true);

            JPanel panel = new JPanel();
            panel.setBackground(new Color(100, 40, 40));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 100, 100), 3),
                    BorderFactory.createEmptyBorder(20, 40, 20, 40)
            ));

            JLabel message = new JLabel(
                    "<html><center><b style='font-size: 20px; color: rgb(255, 150, 150);'>" +
                            "⚠️ EXACT LANDING REQUIRED! ⚠️<br><br>" +
                            "<span style='font-size: 16px; color: rgb(255, 255, 255);'>" +
                            "You need exactly <b>" + needed + "</b> to win!<br>" +
                            "You rolled <b>" + rolled + "</b>. No movement!</span>" +
                            "</b></center></html>");
            message.setHorizontalAlignment(SwingConstants.CENTER);

            panel.add(message);
            notification.add(panel);
            notification.pack();
            notification.setLocationRelativeTo(null);
            notification.setVisible(true);

            javax.swing.Timer dismissTimer = new javax.swing.Timer(2500, e -> {
                notification.dispose();
            });
            dismissTimer.setRepeats(false);
            dismissTimer.start();
        });
    }

    private void showPerfectLandingMessage() {
        SwingUtilities.invokeLater(() -> {
            JWindow notification = new JWindow();
            notification.setAlwaysOnTop(true);

            JPanel panel = new JPanel();
            panel.setBackground(new Color(40, 100, 40));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 255, 100), 3),
                    BorderFactory.createEmptyBorder(20, 40, 20, 40)
            ));

            JLabel message = new JLabel(
                    "<html><center><b style='font-size: 24px; color: rgb(150, 255, 150);'>" +
                            "🎯 PERFECT LANDING! 🎯<br><br>" +
                            "<span style='font-size: 16px; color: rgb(255, 255, 255);'>" +
                            "Exact roll to reach the end!</span>" +
                            "</b></center></html>");
            message.setHorizontalAlignment(SwingConstants.CENTER);

            panel.add(message);
            notification.add(panel);
            notification.pack();
            notification.setLocationRelativeTo(null);
            notification.setVisible(true);

            javax.swing.Timer dismissTimer = new javax.swing.Timer(2000, e -> {
                notification.dispose();
            });
            dismissTimer.setRepeats(false);
            dismissTimer.start();
        });
    }

    private int calculateDistanceToEnd(Node currentNode) {
        if (currentNode == endNode) return 0;

        // Use BFS to find shortest distance to end
        Queue<Node> queue = new LinkedList<>();
        Map<Node, Integer> distances = new HashMap<>();

        queue.add(currentNode);
        distances.put(currentNode, 0);

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            int dist = distances.get(node);

            if (node == endNode) {
                return dist;
            }

            for (Node neighbor : node.getNeighbors()) {
                if (!distances.containsKey(neighbor)) {
                    distances.put(neighbor, dist + 1);
                    queue.add(neighbor);
                }
            }
        }

        return Integer.MAX_VALUE; // End not reachable
    }

    private Node findTargetNode(Node start, int steps, boolean forward) {
        Node current = start;
        for (int i = 0; i < steps; i++) {
            if (current.getNeighbors().isEmpty()) break;
            if (forward) {
                current = current.getNeighbors().get(0);
            } else {
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
        javax.swing.Timer moveTimer = new javax.swing.Timer(300, new java.awt.event.ActionListener() {
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

    private void animateMovementWithPath(Player player, List<Node> customPath, Runnable onComplete) {
        javax.swing.Timer moveTimer = new javax.swing.Timer(300, new java.awt.event.ActionListener() {
            private int step = 0;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (step >= customPath.size()) {
                    ((javax.swing.Timer)e.getSource()).stop();
                    player.moveTo(customPath.get(customPath.size() - 1));
                    if (onComplete != null) onComplete.run();
                    return;
                }
                player.setDisplayNode(customPath.get(step));
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
            if (path.size() > 100) break;
        }
        return path;
    }

    private void finishTurn(Player player, Runnable onComplete) {
        Node currentNode = player.getCurrentNode();
        if (currentNode.getPoints() > 0) {
            int points = currentNode.getPoints();
            player.addScore(points);
            currentNode.setPoints(0);
            System.out.println(player.getName() + " collected " + points + " points!");
        }

        doubleTurn = (currentNode.getId() % 5 == 0 && currentNode.getId() > 0);

        if (doubleTurn) {
            SwingUtilities.invokeLater(() -> {
                JLabel message = new JLabel(
                        "<html><center><b style='font-size: 24px; color: rgb(255, 215, 0);'>" +
                                "CONGRATULATIONS!<br>You get a<br>DOUBLE TURN!</b></center></html>");
                message.setHorizontalAlignment(SwingConstants.CENTER);
                UIManager.put("OptionPane.background", new Color(40, 40, 40));
                UIManager.put("Panel.background", new Color(40, 40, 40));
                JOptionPane.showMessageDialog(null, message, "Double Turn!",
                        JOptionPane.INFORMATION_MESSAGE);
            });
        }

        if (currentNode == endNode) {
            gameOver = true;
        }

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