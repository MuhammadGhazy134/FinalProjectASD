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
        // Always set end node to 100 points first
        endNode.setPoints(100);

        // Change these numbers to control how many point boxes appear
        // Format: minimum + random.nextInt(range)
        // Example: 20 + random.nextInt(11) = 20 to 30 point boxes
        int numberOfPointBoxes = 20 + random.nextInt(11);  // 20-30 point boxes

        List<Integer> availableIndices = new ArrayList<>();

        // Exclude the end node (node 42) from getting random points
        for (int i = 1; i < nodes.size(); i++) {
            if (nodes.get(i) != endNode) {  // Skip the end node
                availableIndices.add(i);
            }
        }

        for (int i = 0; i < numberOfPointBoxes && !availableIndices.isEmpty(); i++) {
            int index = random.nextInt(availableIndices.size());
            int nodeIndex = availableIndices.remove(index);
            Node node = nodes.get(nodeIndex);

            // Change this to control the range of points per box
            // Format: minimum + random.nextInt(range)
            // Example: 1 + random.nextInt(15) = 1 to 15 points per box
            int points = 1 + random.nextInt(15);  // 1-15 points per box

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

        //sfx
        SoundManager.getInstance().playSound("button_click");
        SoundManager.getInstance().playSound("dice_roll");

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
        SoundManager.getInstance().playSound("dijkstra");
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
        SoundManager.getInstance().playSound("exact_landing");
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
        SoundManager.getInstance().playSound("perfect_landing");
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
                //sfx
                SoundManager.getInstance().playSound("move");
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
                //sfx
                SoundManager.getInstance().playSound("move");
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

            SoundManager.getInstance().playSound("collect_points");
            System.out.println(player.getName() + " collected " + points + " points!");
        }

        // Check for double turn (multiples of 5, but NOT node 40)
        doubleTurn = (currentNode.getId() % 5 == 0 && currentNode.getId() > 0 && currentNode.getId() != 40);

        if (doubleTurn) {
            SoundManager.getInstance().playSound("double_turn");
            SwingUtilities.invokeLater(() -> {
                // Create a custom JWindow for auto-dismissing notification
                JWindow notification = new JWindow();
                notification.setAlwaysOnTop(true);

                JPanel panel = new JPanel();
                panel.setBackground(new Color(80, 60, 0));
                panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 215, 0), 3),
                        BorderFactory.createEmptyBorder(20, 40, 20, 40)
                ));

                JLabel message = new JLabel(
                        "<html><center><b style='font-size: 24px; color: rgb(255, 215, 0);'>" +
                                "⭐ you get a DOUBLE TURN! ⭐<br><br>" +
                                "<span style='font-size: 16px; color: rgb(255, 255, 255);'>" +
                                "You landed on a<br>mystery box!</span>" +
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

        if (currentNode == endNode) {
            gameOver = true;
            showFinalScoreboard();
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

    private void showFinalScoreboard() {
        SoundManager.getInstance().stopBackgroundMusic();
        SoundManager.getInstance().playSound("game_over");

        SwingUtilities.invokeLater(() -> {
            // Create custom dialog
            JDialog scoreboard = new JDialog();
            scoreboard.setTitle("Game Over");
            scoreboard.setModal(true);
            scoreboard.setSize(600, 700);
            scoreboard.setLocationRelativeTo(null);
            scoreboard.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBackground(new Color(20, 20, 20));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

            // Sort players by score (highest first)
            List<Player> sortedPlayers = new ArrayList<>(players);
            sortedPlayers.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));

            Player winner = sortedPlayers.get(0);

            // Title - LEADERBOARD
            JLabel titleLabel = new JLabel("LEADERBOARD");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 56));
            titleLabel.setForeground(new Color(255, 180, 0));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Winner announcement
            JLabel winnerLabel = new JLabel("🏆 WINNER 🏆");
            winnerLabel.setFont(new Font("Arial", Font.BOLD, 32));
            winnerLabel.setForeground(new Color(255, 215, 0));
            winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Winner name with color
            String winnerColorHex = String.format("#%02x%02x%02x",
                    winner.getColor().getRed(),
                    winner.getColor().getGreen(),
                    winner.getColor().getBlue());
            JLabel winnerNameLabel = new JLabel(
                    "<html><center><b style='font-size: 26px; color: " + winnerColorHex + ";'>" +
                            winner.getName() + "</b><br>" +
                            "<span style='font-size: 18px; color: rgb(200, 200, 200);'>Final Score: " +
                            winner.getScore() + " points</span></center></html>");
            winnerNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            winnerNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Rankings panel with clean design
            JPanel rankingsPanel = new JPanel();
            rankingsPanel.setLayout(new BoxLayout(rankingsPanel, BoxLayout.Y_AXIS));
            rankingsPanel.setBackground(new Color(20, 20, 20));
            rankingsPanel.setMaximumSize(new Dimension(500, 300));
            rankingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

            // Header row
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(20, 20, 20));
            headerPanel.setMaximumSize(new Dimension(500, 40));

            JLabel playerHeader = new JLabel("PLAYER");
            playerHeader.setFont(new Font("Arial", Font.BOLD, 18));
            playerHeader.setForeground(new Color(255, 180, 0));
            playerHeader.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 0));

            JLabel scoreHeader = new JLabel("SCORE");
            scoreHeader.setFont(new Font("Arial", Font.BOLD, 18));
            scoreHeader.setForeground(new Color(255, 180, 0));
            scoreHeader.setHorizontalAlignment(SwingConstants.RIGHT);
            scoreHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 20));

            headerPanel.add(playerHeader, BorderLayout.WEST);
            headerPanel.add(scoreHeader, BorderLayout.EAST);

            rankingsPanel.add(headerPanel);

            // Add each player's ranking
            for (int i = 0; i < sortedPlayers.size(); i++) {
                Player p = sortedPlayers.get(i);

                JPanel playerPanel = new JPanel(new BorderLayout());
                playerPanel.setBackground(new Color(255, 165, 0));
                playerPanel.setMaximumSize(new Dimension(500, 60));
                playerPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

                // Left side - Player icon and name
                JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                leftPanel.setBackground(new Color(255, 165, 0));

                // Player icon
                JPanel iconPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setColor(Color.WHITE);
                        g2d.fillOval(0, 0, 40, 40);
                        g2d.setColor(p.getColor());
                        g2d.fillOval(12, 8, 16, 16);
                        g2d.fillOval(8, 20, 24, 20);
                    }
                };
                iconPanel.setPreferredSize(new Dimension(40, 40));
                iconPanel.setBackground(new Color(255, 165, 0));

                JLabel nameLabel = new JLabel(p.getName());
                nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
                nameLabel.setForeground(Color.BLACK);

                leftPanel.add(iconPanel);
                leftPanel.add(nameLabel);

                // Right side - Score
                JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
                rightPanel.setBackground(new Color(255, 140, 0));
                rightPanel.setPreferredSize(new Dimension(120, 44));

                JLabel scoreLabel = new JLabel(p.getScore() + " pts");
                scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
                scoreLabel.setForeground(Color.BLACK);
                scoreLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

                rightPanel.add(scoreLabel);

                playerPanel.add(leftPanel, BorderLayout.CENTER);
                playerPanel.add(rightPanel, BorderLayout.EAST);

                rankingsPanel.add(playerPanel);

                if (i < sortedPlayers.size() - 1) {
                    rankingsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
                }
            }

            // Buttons panel
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            buttonsPanel.setBackground(new Color(20, 20, 20));
            buttonsPanel.setMaximumSize(new Dimension(550, 70));

            JButton playAgainButton = new JButton("PLAY AGAIN");
            playAgainButton.setFont(new Font("Arial", Font.BOLD, 16));
            playAgainButton.setPreferredSize(new Dimension(200, 50));
            playAgainButton.setBackground(new Color(0, 180, 0));
            playAgainButton.setForeground(Color.WHITE);
            playAgainButton.setFocusPainted(false);
            playAgainButton.setBorderPainted(false);

            JButton restartButton = new JButton("RESTART");
            restartButton.setFont(new Font("Arial", Font.BOLD, 16));
            restartButton.setPreferredSize(new Dimension(200, 50));
            restartButton.setBackground(new Color(255, 165, 0));
            restartButton.setForeground(Color.WHITE);
            restartButton.setFocusPainted(false);
            restartButton.setBorderPainted(false);

            JButton mainMenuButton = new JButton("EXIT");
            mainMenuButton.setFont(new Font("Arial", Font.BOLD, 16));
            mainMenuButton.setPreferredSize(new Dimension(200, 50));
            mainMenuButton.setBackground(new Color(200, 0, 0));
            mainMenuButton.setForeground(Color.WHITE);
            mainMenuButton.setFocusPainted(false);
            mainMenuButton.setBorderPainted(false);

// Button actions
            playAgainButton.addActionListener(e -> {
                SoundManager.getInstance().playSound("button_click");
                // Restart the main background music
                SoundManager.getInstance().playBackgroundMusic("bgm_main");
                scoreboard.dispose();
                continueGame();
            });

            restartButton.addActionListener(e -> {
                SoundManager.getInstance().playSound("button_click");
                // Restart the main background music
                SoundManager.getInstance().playBackgroundMusic("bgm_main");
                scoreboard.dispose();
                resetGame();
            });

            mainMenuButton.addActionListener(e -> {
                SoundManager.getInstance().playSound("button_click");
                SoundManager.getInstance().stopBackgroundMusic();
                scoreboard.dispose();
                returnToMainMenu();
            });
            buttonsPanel.add(playAgainButton);
            buttonsPanel.add(restartButton);

            JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            exitPanel.setBackground(new Color(20, 20, 20));
            exitPanel.add(mainMenuButton);

            // Add all components to main panel
            mainPanel.add(titleLabel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            mainPanel.add(winnerLabel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            mainPanel.add(winnerNameLabel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            mainPanel.add(rankingsPanel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainPanel.add(buttonsPanel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            mainPanel.add(exitPanel);

            scoreboard.add(mainPanel);
            scoreboard.setVisible(true);
        });
    }

    public void continueGame() {
        // Continue with current scores - just reset positions
        gameOver = false;
        doubleTurn = false;
        currentPlayerIndex = 0;

        // Reset player positions only (keep scores)
        for (Player player : players) {
            player.setCurrentNode(startNode);
            player.setDisplayNode(startNode);  // Also reset display node
        }

        // Regenerate random points
        for (Node node : nodes) {
            node.setPoints(0);
        }
        generateRandomPoints();

        System.out.println("Continuing game with current scores!");
    }

    public void resetGame() {
        // Reset all game state INCLUDING scores
        gameOver = false;
        doubleTurn = false;
        currentPlayerIndex = 0;

        // Reset all players (positions AND scores)
        for (Player player : players) {
            player.setCurrentNode(startNode);
            player.setDisplayNode(startNode);  // Also reset display node
            player.setScore(0);
        }

        // Regenerate random points
        for (Node node : nodes) {
            node.setPoints(0);
        }
        generateRandomPoints();

        System.out.println("Game restarted! All scores reset to 0.");
    }

    public void returnToMainMenu() {
        // This will be connected to your game launcher later
        System.out.println("Returning to main menu...");
        System.out.println("(Game launcher integration will be added later)");

        // For now, just close the game window
        SwingUtilities.invokeLater(() -> {
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                if (window instanceof JFrame) {
                    window.dispose();
                }
            }
        });
    }
}