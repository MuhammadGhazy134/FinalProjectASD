import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.Timer;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.Timer;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

// Player class representing each player in the game
class Player {
    private String name;
    private Color color;
    private int currentPosition;
    private int displayPosition;
    private int targetPosition;
    private double animationProgress;
    boolean movingForward;
    private boolean isUsingLadder;
    private int score;
    private static final int PLAYER_SIZE = 20;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.currentPosition = 1;
        this.displayPosition = 1;
        this.targetPosition = 1;
        this.animationProgress = 1.0;
        this.movingForward = true;
        this.isUsingLadder = false;
        this.score = 0;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getDisplayPosition() {
        return displayPosition;
    }

    public void setCurrentPosition(int position) {
        this.currentPosition = position;
        this.displayPosition = position;
        this.targetPosition = position;
        this.animationProgress = 1.0;
        this.isUsingLadder = false;
    }

    public void setTargetPosition(int position) {
        this.targetPosition = position;
        this.displayPosition = currentPosition;
        this.animationProgress = 0.0;
        this.movingForward = position > currentPosition;
        this.isUsingLadder = false;
    }

    public void setLadderTarget(int position) {
        this.targetPosition = position;
        this.displayPosition = currentPosition;
        this.animationProgress = 0.0;
        this.isUsingLadder = true;
    }

    public void updateAnimation(double delta) {
        if (displayPosition != targetPosition) {
            animationProgress += delta;
            if (animationProgress >= 1.0) {
                animationProgress = 1.0;
                displayPosition = targetPosition;
                currentPosition = targetPosition;
                isUsingLadder = false;
            }
        }
    }

    public boolean moveToNextBox() {
        if (displayPosition != targetPosition) {
            if (movingForward) {
                displayPosition++;
            } else {
                displayPosition--;
            }
            if (displayPosition == targetPosition) {
                currentPosition = targetPosition;
                return true;
            }
            return false;
        }
        return true;
    }

    public boolean isAnimating() {
        return displayPosition != targetPosition;
    }

    public boolean isUsingLadder() {
        return isUsingLadder;
    }

    public void draw(Graphics g, Node currentNode, int playerIndex, int totalPlayers) {
        int offsetX = (playerIndex % 2) * (PLAYER_SIZE + 5);
        int offsetY = (playerIndex / 2) * (PLAYER_SIZE + 5);
        int x, y;

        if (isUsingLadder && animationProgress < 1.0) {
            Node startNode = getBoard().getNode(currentPosition);
            Node endNode = getBoard().getNode(targetPosition);
            if (startNode != null && endNode != null) {
                int startX = startNode.getX() + (startNode.getSize() / 2) - PLAYER_SIZE + offsetX;
                int startY = startNode.getY() + (startNode.getSize() / 2) - PLAYER_SIZE + offsetY;
                int endX = endNode.getX() + (endNode.getSize() / 2) - PLAYER_SIZE + offsetX;
                int endY = endNode.getY() + (endNode.getSize() / 2) - PLAYER_SIZE + offsetY;

                x = (int)(startX + (endX - startX) * animationProgress);
                y = (int)(startY + (endY - startY) * animationProgress);

                if (animationProgress < 0.5) {
                    y -= (int)(Math.sin(animationProgress * Math.PI) * 10);
                }
            } else {
                x = currentNode.getX() + (currentNode.getSize() / 2) - PLAYER_SIZE + offsetX;
                y = currentNode.getY() + (currentNode.getSize() / 2) - PLAYER_SIZE + offsetY;
            }
        } else {
            x = currentNode.getX() + (currentNode.getSize() / 2) - PLAYER_SIZE + offsetX;
            y = currentNode.getY() + (currentNode.getSize() / 2) - PLAYER_SIZE + offsetY;
        }

        g.setColor(color);
        g.fillOval(x, y, PLAYER_SIZE, PLAYER_SIZE);
        g.setColor(Color.BLACK);
        g.drawOval(x, y, PLAYER_SIZE, PLAYER_SIZE);

        if (isUsingLadder && animationProgress < 1.0) {
            g.setColor(Color.YELLOW);
            g.fillRect(x + PLAYER_SIZE/2 - 2, y - 8, 4, 4);
        }
    }

    private Board getBoard() {
        return GameManager.getCurrentBoard();
    }
}

// Dice class for rolling
class Dice {
    private Random random;
    private int lastRoll;
    private boolean isGreen;

    public Dice() {
        random = new Random();
        lastRoll = 0;
        isGreen = true;
    }

    public int roll() {
        lastRoll = random.nextInt(6) + 1;
        double probability = random.nextDouble();
        isGreen = probability < 0.9;
        return lastRoll;
    }

    public int getLastRoll() {
        return lastRoll;
    }

    public boolean isGreen() {
        return isGreen;
    }

    public String getColorText() {
        return isGreen ? "GREEN (Forward)" : "RED (Backward)";
    }

    public Color getColor() {
        return isGreen ? new Color(34, 139, 34) : new Color(220, 20, 60);
    }
}

// Node class representing each box on the board
class Node {
    private int number;
    private int x, y;
    private int points;
    private static final int SIZE = 60;

    public Node(int number, int x, int y) {
        this.number = number;
        this.x = x;
        this.y = y;
        this.points = 0;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public int getNumber() {
        return number;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return SIZE;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean isPrime = isPrimeNumber(number);

        if (isPrime) {
            g2d.setColor(new Color(173, 216, 230, 100));
            g2d.fillRect(x, y, SIZE, SIZE);
        }

        if (number % 5 == 0 && number > 0) {
            g2d.setColor(new Color(255, 215, 0, 100));
            g2d.fillRect(x, y, SIZE, SIZE);
        }

        g2d.setColor(Color.WHITE);
        g2d.fillRect(x, y, SIZE, SIZE);

        if (isPrime) {
            g2d.setColor(new Color(0, 100, 200));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(x, y, SIZE, SIZE);
        }

        if (number % 5 == 0 && number > 0) {
            g2d.setColor(new Color(255, 215, 0));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(x, y, SIZE, SIZE);
        }

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(x, y, SIZE, SIZE);

        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String numStr = String.valueOf(number);
        int textWidth = fm.stringWidth(numStr);

        if (number % 5 == 0 && number > 0) {
            g2d.setColor(new Color(184, 134, 11));
        } else if (isPrime) {
            g2d.setColor(new Color(0, 0, 139));
        } else {
            g2d.setColor(Color.BLACK);
        }
        g2d.drawString(numStr, x + SIZE - textWidth - 5, y + 15);

        if (isPrime) {
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.setColor(new Color(0, 100, 200));
            String primeText = "P";
            FontMetrics fm2 = g2d.getFontMetrics();
            int primeWidth = fm2.stringWidth(primeText);
            g2d.drawString(primeText, x + 5, y + 15);
        }

        if (number % 5 == 0 && number > 0) {
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.setColor(new Color(255, 215, 0));
            String bonus = "2X";
            FontMetrics fm2 = g2d.getFontMetrics();
            int bonusWidth = fm2.stringWidth(bonus);
            int bonusHeight = fm2.getAscent();
            g2d.drawString(bonus, x + (SIZE - bonusWidth) / 2, y + (SIZE + bonusHeight) / 2 - 5);
        }

        if (points > 0) {
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.setColor(new Color(255, 140, 0));
            String pointsText = "+" + points;
            g2d.drawString(pointsText, x + 5, y + SIZE - 5);
        }
    }

    private boolean isPrimeNumber(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }
}

// Dijkstra Algorithm Implementation
class DijkstraAlgorithm {
    private Board board;
    private boolean movingForward;

    public DijkstraAlgorithm(Board board) {
        this.board = board;
    }

    private static class DijkstraNode implements Comparable<DijkstraNode> {
        int position;
        int stepsUsed;
        List<Integer> path;

        public DijkstraNode(int position, int stepsUsed, List<Integer> path) {
            this.position = position;
            this.stepsUsed = stepsUsed;
            this.path = new ArrayList<>(path);
        }

        @Override
        public int compareTo(DijkstraNode other) {
            if (this.stepsUsed != other.stepsUsed) {
                return Integer.compare(this.stepsUsed, other.stepsUsed);
            }
            return Integer.compare(other.position, this.position);
        }
    }

    public List<Integer> findShortestPathWithLadders(int start, int diceRoll, boolean forward) {
        this.movingForward = forward;
        PriorityQueue<DijkstraNode> pq = new PriorityQueue<>();
        Map<Integer, Integer> bestSteps = new HashMap<>();
        Map<Integer, List<Integer>> bestPaths = new HashMap<>();

        List<Integer> initialPath = new ArrayList<>();
        initialPath.add(start);
        pq.offer(new DijkstraNode(start, 0, initialPath));
        bestSteps.put(start, 0);
        bestPaths.put(start, initialPath);

        List<Integer> bestFinalPath = initialPath;
        int bestFinalPosition = start;
        int bestFinalSteps = Integer.MAX_VALUE;

        while (!pq.isEmpty()) {
            DijkstraNode current = pq.poll();

            if (bestSteps.containsKey(current.position) &&
                    bestSteps.get(current.position) < current.stepsUsed) {
                continue;
            }

            if (current.stepsUsed == diceRoll) {
                if (current.position > bestFinalPosition ||
                        (current.position == bestFinalPosition && current.stepsUsed < bestFinalSteps)) {
                    bestFinalPosition = current.position;
                    bestFinalPath = current.path;
                    bestFinalSteps = current.stepsUsed;
                }
                continue;
            }

            if (current.stepsUsed < diceRoll) {
                int nextPos = movingForward ? current.position + 1 : current.position - 1;

                if (nextPos >= 1 && nextPos <= board.getTotalNodes()) {
                    int newSteps = current.stepsUsed + 1;

                    if (!bestSteps.containsKey(nextPos) || newSteps <= bestSteps.get(nextPos)) {
                        List<Integer> newPath = new ArrayList<>(current.path);
                        newPath.add(nextPos);

                        bestSteps.put(nextPos, newSteps);
                        bestPaths.put(nextPos, newPath);
                        pq.offer(new DijkstraNode(nextPos, newSteps, newPath));

                        if (board.isLadderBottom(nextPos)) {
                            int ladderTop = board.checkLadder(nextPos);
                            int ladderSteps = newSteps + 1;

                            if (ladderSteps <= diceRoll) {
                                if (!bestSteps.containsKey(ladderTop) || ladderSteps <= bestSteps.get(ladderTop)) {
                                    List<Integer> ladderPath = new ArrayList<>(newPath);
                                    ladderPath.add(ladderTop);

                                    bestSteps.put(ladderTop, ladderSteps);
                                    bestPaths.put(ladderTop, ladderPath);
                                    pq.offer(new DijkstraNode(ladderTop, ladderSteps, ladderPath));
                                }
                            }
                        }
                    }
                }
            }
        }

        return bestFinalPath;
    }

    public void displayShortestPath(int start, int diceRoll, JLabel statusLabel, Player player, boolean forward) {
        List<Integer> path = findShortestPathWithLadders(start, diceRoll, forward);

        if (path != null && path.size() > 1) {
            StringBuilder pathInfo = new StringBuilder();
            pathInfo.append("<html><center><b style='color: rgb(255, 215, 0);'>DIJKSTRA ACTIVATED!</b><br>");
            pathInfo.append("<font color='white'>").append(player.getName()).append(" from prime ").append(start).append("<br>");
            pathInfo.append("Roll: ").append(diceRoll).append(" | Path: ");

            int laddersUsed = 0;
            for (int i = 0; i < path.size(); i++) {
                if (i > 0) pathInfo.append(" → ");
                int pos = path.get(i);
                pathInfo.append(pos);

                if (i > 0 && board.isLadderBottom(path.get(i-1)) &&
                        board.checkLadder(path.get(i-1)) == pos) {
                    pathInfo.append("⬆");
                    laddersUsed++;
                }
            }

            pathInfo.append("<br>Ladders used: ").append(laddersUsed);
            pathInfo.append(" | Final: ").append(path.get(path.size()-1));
            pathInfo.append("</font></center></html>");

            statusLabel.setText(pathInfo.toString());
        }
    }
}

// Board class managing the game board
class Board {
    private Node[][] nodes;
    private static final int ROWS = 8;
    private static final int COLS = 8;
    private static final int TOTAL_NODES = ROWS * COLS;
    private Map<Integer, Integer> ladders;
    private Random random;
    private DijkstraAlgorithm dijkstra;

    public Board() {
        nodes = new Node[ROWS][COLS];
        ladders = new HashMap<>();
        random = new Random();
        dijkstra = new DijkstraAlgorithm(this);
        initializeBoard();
        generateRandomLadders();
        generateRandomPoints();
    }

    private void initializeBoard() {
        int number = 1;
        int nodeSize = 60;
        int padding = 20;

        for (int row = ROWS - 1; row >= 0; row--) {
            if ((ROWS - 1 - row) % 2 == 0) {
                for (int col = 0; col < COLS; col++) {
                    int x = padding + col * nodeSize;
                    int y = padding + row * nodeSize;
                    nodes[row][col] = new Node(number, x, y);
                    number++;
                }
            } else {
                for (int col = COLS - 1; col >= 0; col--) {
                    int x = padding + col * nodeSize;
                    int y = padding + row * nodeSize;
                    nodes[row][col] = new Node(number, x, y);
                    number++;
                }
            }
        }
    }

    private void generateRandomLadders() {
        int numberOfLadders = 5;
        List<Integer> availablePositions = new ArrayList<>();

        for (int i = 2; i <= TOTAL_NODES - 1; i++) {
            availablePositions.add(i);
        }

        for (int i = 0; i < numberOfLadders; i++) {
            if (availablePositions.size() < 2) break;

            int bottomIndex = random.nextInt(availablePositions.size());
            int bottom = availablePositions.get(bottomIndex);
            availablePositions.remove(bottomIndex);

            List<Integer> possibleTops = new ArrayList<>();
            for (int pos : availablePositions) {
                if (pos > bottom && Math.abs(pos - bottom) >= 5) {
                    possibleTops.add(pos);
                }
            }

            if (!possibleTops.isEmpty()) {
                int topIndex = random.nextInt(possibleTops.size());
                int top = possibleTops.get(topIndex);
                availablePositions.remove(Integer.valueOf(top));
                ladders.put(bottom, top);
                System.out.println("Ladder: " + bottom + " -> " + top);
            }
        }
    }

    private void generateRandomPoints() {
        Node lastNode = getNode(TOTAL_NODES);
        if (lastNode != null) {
            lastNode.setPoints(100);
        }

        int numberOfPointBoxes = 10 + random.nextInt(6);
        List<Integer> availablePositions = new ArrayList<>();

        for (int i = 2; i < TOTAL_NODES; i++) {
            availablePositions.add(i);
        }

        for (int i = 0; i < numberOfPointBoxes && !availablePositions.isEmpty(); i++) {
            int index = random.nextInt(availablePositions.size());
            int position = availablePositions.get(index);
            availablePositions.remove(index);

            Node node = getNode(position);
            if (node != null) {
                int points = 1 + random.nextInt(10);
                node.setPoints(points);
                System.out.println("Points: Box " + position + " = " + points);
            }
        }
    }

    public Node getNode(int number) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (nodes[row][col].getNumber() == number) {
                    return nodes[row][col];
                }
            }
        }
        return null;
    }

    public int checkLadder(int position) {
        return ladders.getOrDefault(position, position);
    }

    public boolean isLadderBottom(int position) {
        return ladders.containsKey(position);
    }

    public Map<Integer, Integer> getLadders() {
        return new HashMap<>(ladders);
    }

    public boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    public DijkstraAlgorithm getDijkstra() {
        return dijkstra;
    }

    public void draw(Graphics g) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                nodes[row][col].draw(g);
            }
        }
        drawLadders(g);
    }

    private void drawLadders(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Map.Entry<Integer, Integer> ladder : ladders.entrySet()) {
            int bottom = ladder.getKey();
            int top = ladder.getValue();
            Node bottomNode = getNode(bottom);
            Node topNode = getNode(top);

            if (bottomNode != null && topNode != null) {
                g2d.setColor(new Color(0, 100, 0));
                g2d.setStroke(new BasicStroke(4));

                int startX = bottomNode.getX() + bottomNode.getSize() / 2;
                int startY = bottomNode.getY() + bottomNode.getSize() / 2;
                int endX = topNode.getX() + topNode.getSize() / 2;
                int endY = topNode.getY() + topNode.getSize() / 2;

                g2d.drawLine(startX - 8, startY, endX - 8, endY);
                g2d.drawLine(startX + 8, startY, endX + 8, endY);

                g2d.setStroke(new BasicStroke(2));
                int steps = 5;
                for (int i = 1; i < steps; i++) {
                    float ratio = (float) i / steps;
                    int x1 = (int) (startX - 8 + (endX - startX) * ratio);
                    int y1 = (int) (startY + (endY - startY) * ratio);
                    int x2 = (int) (startX + 8 + (endX - startX) * ratio);
                    int y2 = y1;
                    g2d.drawLine(x1, y1, x2, y2);
                }

                g2d.setColor(new Color(0, 150, 0, 100));
                g2d.fillRect(bottomNode.getX(), bottomNode.getY(), bottomNode.getSize(), bottomNode.getSize());
                g2d.fillRect(topNode.getX(), topNode.getY(), topNode.getSize(), topNode.getSize());

                g2d.setColor(Color.GREEN);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                String ladderText = "L";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(ladderText);
                g2d.drawString(ladderText,
                        bottomNode.getX() + (bottomNode.getSize() - textWidth) / 2,
                        bottomNode.getY() + bottomNode.getSize() - 10);
                g2d.drawString(ladderText,
                        topNode.getX() + (topNode.getSize() - textWidth) / 2,
                        topNode.getY() + bottomNode.getSize() - 10);
            }
        }
    }

    public int getTotalNodes() {
        return TOTAL_NODES;
    }
}

// GameManager class to handle game logic
class GameManager {
    private Board board;
    private Player[] players;
    private int currentPlayerIndex;
    private Dice dice;
    private boolean gameOver;
    private Timer movementTimer;
    private boolean doubleTurn;
    private JLabel statusLabel;
    private static Board currentBoard;

    public GameManager(Board board, int numPlayers) {
        this.board = board;
        currentBoard = board;
        this.dice = new Dice();
        this.currentPlayerIndex = 0;
        this.gameOver = false;
        this.doubleTurn = false;

        players = new Player[numPlayers];
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player("Player " + (i + 1), colors[i % colors.length]);
        }
    }

    public static Board getCurrentBoard() {
        return currentBoard;
    }

    public void setStatusLabel(JLabel statusLabel) {
        this.statusLabel = statusLabel;
    }

    public void playTurn(Runnable onComplete) {
        if (gameOver) return;

        Player currentPlayer = players[currentPlayerIndex];
        int startPosition = currentPlayer.getCurrentPosition();
        int roll = dice.roll();
        boolean movingForward = dice.isGreen();

        // FIXED: Check if starting from prime number - enables Dijkstra regardless of direction
        boolean canUseLadders = board.isPrime(startPosition);
        List<Integer> optimalPath = null;

        // FIXED: Always try to use Dijkstra when starting from prime, even for backward movement
        if (canUseLadders) {
            optimalPath = board.getDijkstra().findShortestPathWithLadders(startPosition, roll, movingForward);
            board.getDijkstra().displayShortestPath(startPosition, roll, statusLabel, currentPlayer, movingForward);
        }

        final List<Integer> finalPath = optimalPath;

        if (onComplete != null) {
            onComplete.run();
        }

        Timer delayTimer = new Timer(800, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer)e.getSource()).stop();

                if (canUseLadders && finalPath != null && finalPath.size() > 1) {
                    followDijkstraPath(currentPlayer, finalPath, 0, onComplete);
                } else {
                    int newPosition;
                    if (movingForward) {
                        newPosition = startPosition + roll;
                    } else {
                        newPosition = startPosition - roll;
                    }

                    if (newPosition < 1) newPosition = 1;
                    if (newPosition >= board.getTotalNodes()) {
                        newPosition = board.getTotalNodes();
                        gameOver = true;
                    }

                    currentPlayer.setTargetPosition(newPosition);
                    handleNormalMovement(currentPlayer, startPosition, roll, onComplete);
                }
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    private void followDijkstraPath(Player player, List<Integer> path, int currentIndex, Runnable onComplete) {
        if (currentIndex >= path.size() - 1) {
            finishTurn(onComplete);
            return;
        }

        int currentPos = path.get(currentIndex);
        int nextPos = path.get(currentIndex + 1);

        boolean isLadder = board.isLadderBottom(currentPos) && board.checkLadder(currentPos) == nextPos;

        if (isLadder) {
            player.setLadderTarget(nextPos);
            Timer ladderTimer = new Timer(20, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    player.updateAnimation(0.05);
                    if (!player.isAnimating()) {
                        ((Timer)e.getSource()).stop();
                        followDijkstraPath(player, path, currentIndex + 1, onComplete);
                    }
                }
            });
            ladderTimer.start();
        } else {
            player.setTargetPosition(nextPos);
            Timer moveTimer = new Timer(300, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean complete = player.moveToNextBox();
                    if (complete) {
                        ((Timer)e.getSource()).stop();
                        followDijkstraPath(player, path, currentIndex + 1, onComplete);
                    }
                }
            });
            moveTimer.start();
        }
    }

    private void handleNormalMovement(Player currentPlayer, int startPosition, int totalSteps,
                                      Runnable onComplete) {
        final int[] stepsUsed = {0};

        movementTimer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean movementComplete = currentPlayer.moveToNextBox();
                stepsUsed[0]++;

                if (movementComplete) {
                    movementTimer.stop();
                    finishTurn(onComplete);
                }
            }
        });
        movementTimer.start();
    }

    private void finishTurn(Runnable onComplete) {
        Player currentPlayer = players[currentPlayerIndex];

        Node currentNode = board.getNode(currentPlayer.getCurrentPosition());
        if (currentNode != null && currentNode.getPoints() > 0) {
            int points = currentNode.getPoints();
            currentPlayer.addScore(points);
            currentNode.setPoints(0);

            if (statusLabel != null) {
                String colorHex = String.format("#%02x%02x%02x",
                        currentPlayer.getColor().getRed(),
                        currentPlayer.getColor().getGreen(),
                        currentPlayer.getColor().getBlue());
                statusLabel.setText("<html><center><font color='" + colorHex + "'>" +
                        currentPlayer.getName() + "</font><br>collected <b>+" + points + " points!</b><br>" +
                        "Total: " + currentPlayer.getScore() + "</center></html>");
            }
        }

        doubleTurn = (currentPlayer.getCurrentPosition() % 5 == 0 && currentPlayer.getCurrentPosition() > 0);

        if (doubleTurn) {
            SwingUtilities.invokeLater(() -> {
                JLabel message = new JLabel("<html><center><b style='font-size: 24px; color: rgb(255, 215, 0);'>CONGRATULATIONS!<br> you get a <br>DOUBLE TURN!</b></center></html>");
                message.setHorizontalAlignment(SwingConstants.CENTER);
                UIManager.put("OptionPane.background", new Color(40, 40, 40));
                UIManager.put("Panel.background", new Color(40, 40, 40));
                UIManager.put("OptionPane.messageForeground", new Color(255, 215, 0));
                JOptionPane.showMessageDialog(null, message, "Double Turn!", JOptionPane.INFORMATION_MESSAGE);
            });
        }

        if (!gameOver && !doubleTurn) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        }

        if (onComplete != null) {
            onComplete.run();
        }
    }

    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getLastRoll() {
        return dice.getLastRoll();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        if (!gameOver) return null;
        return players[currentPlayerIndex];
    }

    public Dice getDice() {
        return dice;
    }

    public boolean isAnyPlayerAnimating() {
        for (Player player : players) {
            if (player.isAnimating()) return true;
        }
        return false;
    }

    public boolean isDoubleTurn() {
        return doubleTurn;
    }

    public void clearDoubleTurn() {
        doubleTurn = false;
    }

    public Board getBoard() {
        return board;
    }
}

// BoardPanel class for rendering
class BoardPanel extends JPanel {
    private Board board;
    private GameManager gameManager;
    private Timer repaintTimer;

    public BoardPanel(GameManager gameManager) {
        this.board = gameManager.getBoard();
        this.gameManager = gameManager;
        setPreferredSize(new Dimension(520, 520));
        setBackground(new Color(245, 222, 179));

        repaintTimer = new Timer(16, e -> repaint());
        repaintTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        board.draw(g);

        Player[] players = gameManager.getPlayers();
        for (int i = 0; i < players.length; i++) {
            int displayPos = players[i].getDisplayPosition();
            Node currentNode = board.getNode(displayPos);
            if (currentNode != null) {
                players[i].draw(g, currentNode, i, players.length);
            }
        }
    }

    public Board getBoard() {
        return board;
    }
}

// Main class to run the game
public class BoardGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Board Game - Prime Ladder Edition with Dijkstra");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            String input = JOptionPane.showInputDialog(frame,
                    "Enter number of players (2-4):", "2");
            int numPlayers = 2;
            try {
                numPlayers = Integer.parseInt(input);
                if (numPlayers < 2) numPlayers = 2;
                if (numPlayers > 4) numPlayers = 4;
            } catch (Exception e) {
                numPlayers = 2;
            }

            Board board = new Board();
            GameManager gameManager = new GameManager(board, numPlayers);
            BoardPanel boardPanel = new BoardPanel(gameManager);

            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setPreferredSize(new Dimension(280, 520));
            rightPanel.setBackground(new Color(60, 60, 60));
            rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel titleLabel = new JLabel("Prime Ladder Game");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel rulesLabel = new JLabel("<html><center><b>Ladder Rule:</b><br>Start from PRIME box<br>to use ladders!<br><br><b>Dijkstra Feature:</b><br>From prime boxes,<br>find optimal path<br>using ladders!</center></html>");
            rulesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            rulesLabel.setForeground(new Color(173, 216, 230));
            rulesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            rulesLabel.setHorizontalAlignment(SwingConstants.CENTER);

            class DicePanel extends JPanel {
                private int diceValue = 0;
                private Color diceColor = Color.WHITE;
                private double rotationAngle = 0;
                private Timer rotationTimer;

                public DicePanel() {
                    setPreferredSize(new Dimension(100, 100));
                    setMaximumSize(new Dimension(100, 100));
                    setBackground(new Color(80, 80, 80));
                }

                public void animateDiceRoll(int finalValue, Color finalColor) {
                    rotationAngle = 0;
                    rotationTimer = new Timer(30, new ActionListener() {
                        int count = 0;
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            diceValue = (int)(Math.random() * 6) + 1;
                            rotationAngle += 0.3;
                            count++;
                            if (count > 20) {
                                rotationTimer.stop();
                                diceValue = finalValue;
                                diceColor = finalColor;
                                rotationAngle = 0;
                            }
                            repaint();
                        }
                    });
                    rotationTimer.start();
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;

                    g2d.translate(centerX, centerY);
                    g2d.rotate(rotationAngle);

                    g2d.setColor(diceColor);
                    g2d.fillRoundRect(-30, -30, 60, 60, 10, 10);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRoundRect(-30, -30, 60, 60, 10, 10);

                    g2d.setFont(new Font("Arial", Font.BOLD, 32));
                    String val = diceValue > 0 ? String.valueOf(diceValue) : "?";
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(val);
                    int textHeight = fm.getAscent();
                    g2d.drawString(val, -textWidth / 2, textHeight / 2 - 5);
                }
            }

            DicePanel dicePanel = new DicePanel();
            dicePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel statusLabel = new JLabel();
            statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            statusLabel.setForeground(Color.WHITE);
            statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

            Runnable updateStatusLabel = () -> {
                Player currentPlayer = gameManager.getCurrentPlayer();
                String colorHex = String.format("#%02x%02x%02x",
                        currentPlayer.getColor().getRed(),
                        currentPlayer.getColor().getGreen(),
                        currentPlayer.getColor().getBlue());
                String statusText = "<html><center>Current Player:<br><b><font color='" + colorHex + "'>" +
                        currentPlayer.getName() + "</font></b>";
                if (gameManager.isDoubleTurn()) {
                    statusText += "<br><font color='rgb(255, 215, 0)'>DOUBLE TURN!</font>";
                }
                statusText += "</center></html>";
                statusLabel.setText(statusText);
            };

            updateStatusLabel.run();

            gameManager.setStatusLabel(statusLabel);

            JButton rollButton = new JButton("Roll Dice");
            rollButton.setFont(new Font("Arial", Font.BOLD, 16));
            rollButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            rollButton.setMaximumSize(new Dimension(150, 40));
            rollButton.setBackground(new Color(0, 120, 215));
            rollButton.setForeground(Color.WHITE);
            rollButton.setFocusPainted(false);

            JLabel laddersInfoLabel = new JLabel();
            laddersInfoLabel.setForeground(Color.WHITE);
            laddersInfoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            laddersInfoLabel.setVerticalAlignment(SwingConstants.TOP);
            laddersInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            Runnable updateScoreboard = () -> {
                StringBuilder scoreText = new StringBuilder("<html><div style='text-align: center;'>");
                scoreText.append("<b style='font-size: 14px;'>SCOREBOARD</b><br><br>");

                Player[] players = gameManager.getPlayers();
                for (int i = 0; i < players.length; i++) {
                    String colorHex = String.format("#%02x%02x%02x",
                            players[i].getColor().getRed(),
                            players[i].getColor().getGreen(),
                            players[i].getColor().getBlue());
                    scoreText.append("<font color='").append(colorHex).append("'><b>")
                            .append(players[i].getName()).append("</b></font>: ")
                            .append(players[i].getScore()).append(" pts<br>");
                }

                scoreText.append("</div></html>");
                laddersInfoLabel.setText(scoreText.toString());
            };

            updateScoreboard.run();

            JPanel laddersPanel = new JPanel();
            laddersPanel.setBackground(new Color(80, 80, 80));
            laddersPanel.setPreferredSize(new Dimension(240, 150));
            laddersPanel.setMaximumSize(new Dimension(240, 150));
            laddersPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            laddersPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
            laddersPanel.add(laddersInfoLabel);

            rollButton.addActionListener(e -> {
                if (!gameManager.isGameOver() && !gameManager.isAnyPlayerAnimating()) {
                    rollButton.setEnabled(false);
                    gameManager.playTurn(() -> {
                        Dice dice = gameManager.getDice();
                        if (gameManager.isGameOver()) {
                            Player winner = gameManager.getWinner();
                            String colorHex = String.format("#%02x%02x%02x",
                                    winner.getColor().getRed(),
                                    winner.getColor().getGreen(),
                                    winner.getColor().getBlue());
                            statusLabel.setText("<html><center><b>GAME OVER!</b><br>Winner: <font color='" +
                                    colorHex + "'>" + winner.getName() + "</font><br>Final Score: " +
                                    winner.getScore() + " pts</center></html>");
                        } else {
                            updateStatusLabel.run();
                            rollButton.setEnabled(true);
                        }
                        updateScoreboard.run();
                    });

                    Dice dice = gameManager.getDice();
                    dicePanel.animateDiceRoll(dice.getLastRoll(), dice.getColor());
                }
            });

            rightPanel.add(titleLabel);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            rightPanel.add(rulesLabel);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            rightPanel.add(dicePanel);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            rightPanel.add(statusLabel);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            rightPanel.add(rollButton);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            rightPanel.add(laddersPanel);

            frame.add(boardPanel, BorderLayout.CENTER);
            frame.add(rightPanel, BorderLayout.EAST);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}