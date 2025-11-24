import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.Timer;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

// Player class representing each player in the game
class Player {
    private String name;
    private Color color;
    private int currentPosition;
    private int displayPosition; // Position currently shown during animation
    private int targetPosition;
    private double animationProgress;
    boolean movingForward;
    private boolean isUsingLadder;
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

    // New method for box-by-box movement
    public boolean moveToNextBox() {
        if (displayPosition != targetPosition) {
            if (movingForward) {
                displayPosition++;
            } else {
                displayPosition--;
            }

            // Check if reached target
            if (displayPosition == targetPosition) {
                currentPosition = targetPosition;
                return true; // Movement complete
            }
            return false; // Still moving
        }
        return true; // Already at target
    }

    public boolean isAnimating() {
        return displayPosition != targetPosition;
    }

    public boolean isUsingLadder() {
        return isUsingLadder;
    }

    public void draw(Graphics g, Node currentNode, int playerIndex, int totalPlayers) {
        // Calculate offset for multiple players on same node
        int offsetX = (playerIndex % 2) * (PLAYER_SIZE + 5);
        int offsetY = (playerIndex / 2) * (PLAYER_SIZE + 5);

        int x, y;

        // If using ladder, animate directly from start to end node
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

                // Add a little bounce effect for ladder animation
                if (animationProgress < 0.5) {
                    y -= (int)(Math.sin(animationProgress * Math.PI) * 10);
                }
            } else {
                x = currentNode.getX() + (currentNode.getSize() / 2) - PLAYER_SIZE + offsetX;
                y = currentNode.getY() + (currentNode.getSize() / 2) - PLAYER_SIZE + offsetY;
            }
        } else {
            // For normal movement, just draw at current display position
            x = currentNode.getX() + (currentNode.getSize() / 2) - PLAYER_SIZE + offsetX;
            y = currentNode.getY() + (currentNode.getSize() / 2) - PLAYER_SIZE + offsetY;
        }

        g.setColor(color);
        g.fillOval(x, y, PLAYER_SIZE, PLAYER_SIZE);
        g.setColor(Color.BLACK);
        g.drawOval(x, y, PLAYER_SIZE, PLAYER_SIZE);

        // Draw a little star when using ladder
        if (isUsingLadder && animationProgress < 1.0) {
            g.setColor(Color.YELLOW);
            g.fillRect(x + PLAYER_SIZE/2 - 2, y - 8, 4, 4);
        }
    }

    // Helper method to get board reference (will be set by GameManager)
    private Board getBoard() {
        return GameManager.getCurrentBoard();
    }
}

// Dice class for rolling
class Dice {
    private Random random;
    private int lastRoll;
    private boolean isGreen; // true = green (forward), false = red (backward)

    public Dice() {
        random = new Random();
        lastRoll = 0;
        isGreen = true;
    }

    public int roll() {
        lastRoll = random.nextInt(6) + 1; // 1 to 6

        // 70% chance for green (forward), 30% chance for red (backward)
        double probability = random.nextDouble();
        isGreen = probability < 0.7;

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
    private static final int SIZE = 60;

    public Node(int number, int x, int y) {
        this.number = number;
        this.x = x;
        this.y = y;
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

        // Highlight if multiple of 5
        if (number % 5 == 0 && number > 0) {
            g2d.setColor(new Color(255, 215, 0, 100)); // Gold with transparency
            g2d.fillRect(x, y, SIZE, SIZE);
        }

        // Draw the box
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x, y, SIZE, SIZE);

        // Highlight border for multiples of 5
        if (number % 5 == 0 && number > 0) {
            g2d.setColor(new Color(255, 215, 0)); // Gold
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(x, y, SIZE, SIZE);
        }

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(x, y, SIZE, SIZE);

        // Draw the number at top-right corner
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String numStr = String.valueOf(number);
        int textWidth = fm.stringWidth(numStr);

        // Color the number gold if multiple of 5
        if (number % 5 == 0 && number > 0) {
            g2d.setColor(new Color(184, 134, 11)); // Dark gold
        } else {
            g2d.setColor(Color.BLACK);
        }
        g2d.drawString(numStr, x + SIZE - textWidth - 5, y + 15);

        // Draw "2X" text in the middle for multiples of 5
        if (number % 5 == 0 && number > 0) {
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.setColor(new Color(255, 215, 0)); // Gold
            String bonus = "2X";
            FontMetrics fm2 = g2d.getFontMetrics();
            int bonusWidth = fm2.stringWidth(bonus);
            int bonusHeight = fm2.getAscent();
            g2d.drawString(bonus, x + (SIZE - bonusWidth) / 2, y + (SIZE + bonusHeight) / 2 - 5);
        }
    }
}

// Board class managing the game board
class Board {
    private Node[][] nodes;
    private static final int ROWS = 8;
    private static final int COLS = 8;
    private static final int TOTAL_NODES = ROWS * COLS;
    private Map<Integer, Integer> ladders; // bottom -> top
    private Random random;

    public Board() {
        nodes = new Node[ROWS][COLS];
        ladders = new HashMap<>();
        random = new Random();
        initializeBoard();
        generateRandomLadders();
    }

    private void initializeBoard() {
        int number = 1;
        int nodeSize = 60;
        int padding = 20;

        // Start from bottom-left, zigzag pattern
        for (int row = ROWS - 1; row >= 0; row--) {
            if ((ROWS - 1 - row) % 2 == 0) {
                // Even rows (from bottom): go left to right
                for (int col = 0; col < COLS; col++) {
                    int x = padding + col * nodeSize;
                    int y = padding + row * nodeSize;
                    nodes[row][col] = new Node(number, x, y);
                    number++;
                }
            } else {
                // Odd rows (from bottom): go right to left
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

        // Add all positions except first and last
        for (int i = 2; i <= TOTAL_NODES - 1; i++) {
            availablePositions.add(i);
        }

        for (int i = 0; i < numberOfLadders; i++) {
            if (availablePositions.size() < 2) break;

            // Get random bottom position
            int bottomIndex = random.nextInt(availablePositions.size());
            int bottom = availablePositions.get(bottomIndex);
            availablePositions.remove(bottomIndex);

            // Find available top positions (must be higher than bottom)
            List<Integer> possibleTops = new ArrayList<>();
            for (int pos : availablePositions) {
                if (pos > bottom && Math.abs(pos - bottom) >= 5) { // Minimum ladder length of 5
                    possibleTops.add(pos);
                }
            }

            if (!possibleTops.isEmpty()) {
                int topIndex = random.nextInt(possibleTops.size());
                int top = possibleTops.get(topIndex);
                availablePositions.remove(Integer.valueOf(top));

                ladders.put(bottom, top);
                System.out.println("Ladder: " + bottom + " -> " + top); // For debugging
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
                // Draw ladder with green color
                g2d.setColor(new Color(0, 100, 0)); // Dark green
                g2d.setStroke(new BasicStroke(4));

                int startX = bottomNode.getX() + bottomNode.getSize() / 2;
                int startY = bottomNode.getY() + bottomNode.getSize() / 2;
                int endX = topNode.getX() + topNode.getSize() / 2;
                int endY = topNode.getY() + topNode.getSize() / 2;

                // Draw ladder sides
                g2d.drawLine(startX - 8, startY, endX - 8, endY);
                g2d.drawLine(startX + 8, startY, endX + 8, endY);

                // Draw ladder rungs
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

                // Draw ladder indicator on nodes
                g2d.setColor(new Color(0, 150, 0, 100));
                g2d.fillRect(bottomNode.getX(), bottomNode.getY(), bottomNode.getSize(), bottomNode.getSize());
                g2d.fillRect(topNode.getX(), topNode.getY(), topNode.getSize(), topNode.getSize());

                // Draw "L" on ladder nodes
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
    private static Board currentBoard; // Static reference for Player class

    public GameManager(Board board, int numPlayers) {
        this.board = board;
        currentBoard = board; // Set static reference
        this.dice = new Dice();
        this.currentPlayerIndex = 0;
        this.gameOver = false;
        this.doubleTurn = false;

        // Initialize players with different colors
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
        int roll = dice.roll();

        int newPosition;
        if (dice.isGreen()) {
            // Green: move forward
            newPosition = currentPlayer.getCurrentPosition() + roll;
        } else {
            // Red: move backward
            newPosition = currentPlayer.getCurrentPosition() - roll;
        }

        // Ensure position stays within bounds
        if (newPosition < 1) {
            newPosition = 1; // Can't go below position 1
        }

        // Check if player reaches or exceeds the final position
        if (newPosition >= board.getTotalNodes()) {
            newPosition = board.getTotalNodes();
            gameOver = true;
        }

        final int finalPosition = newPosition;

        // Call dice complete callback first (to finish dice animation)
        if (onComplete != null) {
            onComplete.run();
        }

        // Wait a bit before starting piece movement
        Timer delayTimer = new Timer(600, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer)e.getSource()).stop();

                // Now start piece movement with box-by-box animation
                currentPlayer.setTargetPosition(finalPosition);

                // Start box-by-box movement timer
                movementTimer = new Timer(300, new ActionListener() { // 300ms delay between boxes
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        boolean movementComplete = currentPlayer.moveToNextBox();

                        if (movementComplete) {
                            movementTimer.stop();

                            // Check for ladder after normal movement
                            int landedPosition = currentPlayer.getCurrentPosition();
                            if (board.isLadderBottom(landedPosition)) {
                                int ladderTop = board.checkLadder(landedPosition);

                                // Show ladder message
                                if (statusLabel != null) {
                                    statusLabel.setText("<html><center>LADDER!<br>" +
                                            currentPlayer.getName() + " climbs from " +
                                            landedPosition + " to " + ladderTop + "!</center></html>");
                                }

                                // Use ladder animation (direct to destination)
                                currentPlayer.setLadderTarget(ladderTop);

                                // Start ladder animation
                                Timer ladderTimer = new Timer(20, new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        currentPlayer.updateAnimation(0.05); // Slower for ladder animation
                                        if (!currentPlayer.isAnimating()) {
                                            ((Timer)e.getSource()).stop();
                                            finishTurn(onComplete);
                                        }
                                    }
                                });
                                ladderTimer.start();
                            } else {
                                finishTurn(onComplete);
                            }
                        }
                    }
                });
                movementTimer.start();
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    private void finishTurn(Runnable onComplete) {
        Player currentPlayer = players[currentPlayerIndex];

        // Check if landed on multiple of 5
        doubleTurn = (currentPlayer.getCurrentPosition() % 5 == 0 && currentPlayer.getCurrentPosition() > 0);

        // Move to next player only if not double turn
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

        // Animation repaint timer
        repaintTimer = new Timer(16, e -> repaint());
        repaintTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        board.draw(g);

        // Draw all players
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
            JFrame frame = new JFrame("Board Game - 8x8 with Ladders");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Ask for number of players
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

            // Initialize game components
            Board board = new Board();
            GameManager gameManager = new GameManager(board, numPlayers);
            BoardPanel boardPanel = new BoardPanel(gameManager);

            // Create right side panel with controls
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setPreferredSize(new Dimension(250, 520));
            rightPanel.setBackground(new Color(60, 60, 60));
            rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Title label
            JLabel titleLabel = new JLabel("Board Game");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Dice display panel
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

                    // Draw dice
                    g2d.setColor(diceColor);
                    g2d.fillRoundRect(-30, -30, 60, 60, 10, 10);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRoundRect(-30, -30, 60, 60, 10, 10);

                    // Draw dice value
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

            // Status label
            JLabel statusLabel = new JLabel("<html><center>Current Player:<br><b>" +
                    gameManager.getCurrentPlayer().getName() + "</b></center></html>");
            statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            statusLabel.setForeground(Color.WHITE);
            statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Set status label in game manager
            gameManager.setStatusLabel(statusLabel);

            // Roll button
            JButton rollButton = new JButton("Roll Dice");
            rollButton.setFont(new Font("Arial", Font.BOLD, 16));
            rollButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            rollButton.setMaximumSize(new Dimension(150, 40));
            rollButton.setBackground(new Color(0, 120, 215));
            rollButton.setForeground(Color.WHITE);
            rollButton.setFocusPainted(false);

            // Ladders info panel
            JTextArea laddersInfo = new JTextArea();
            laddersInfo.setEditable(false);
            laddersInfo.setBackground(new Color(80, 80, 80));
            laddersInfo.setForeground(Color.WHITE);
            laddersInfo.setFont(new Font("Arial", Font.PLAIN, 12));
            laddersInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            laddersInfo.setLineWrap(true);
            laddersInfo.setWrapStyleWord(true);

            // Build ladders info text
            StringBuilder laddersText = new StringBuilder("Ladders in this game:\n\n");
            Map<Integer, Integer> ladders = board.getLadders();
            for (Map.Entry<Integer, Integer> ladder : ladders.entrySet()) {
                laddersText.append("Ladder: ").append(ladder.getKey())
                        .append(" → ").append(ladder.getValue()).append("\n");
            }
            laddersInfo.setText(laddersText.toString());

            JScrollPane laddersScroll = new JScrollPane(laddersInfo);
            laddersScroll.setPreferredSize(new Dimension(200, 150));
            laddersScroll.setMaximumSize(new Dimension(200, 150));
            laddersScroll.setAlignmentX(Component.CENTER_ALIGNMENT);

            rollButton.addActionListener(e -> {
                if (!gameManager.isGameOver() && !gameManager.isAnyPlayerAnimating()) {
                    rollButton.setEnabled(false);

                    gameManager.playTurn(() -> {
                        Dice dice = gameManager.getDice();
                        String direction = dice.isGreen() ? "Forward →" : "Backward ←";
                        Color textColor = dice.isGreen() ? new Color(34, 139, 34) : new Color(220, 20, 60);

                        if (gameManager.isGameOver()) {
                            Player winner = gameManager.getWinner();
                            statusLabel.setText("<html><center><b>GAME OVER!</b><br>Winner: " +
                                    winner.getName() + "</center></html>");
                        } else {
                            String statusText = "<html><center>Current Player:<br><b>" +
                                    gameManager.getCurrentPlayer().getName() + "</b>";
                            if (gameManager.isDoubleTurn()) {
                                statusText += "<br><font color='gold'>DOUBLE TURN!</font>";
                            }
                            statusText += "</center></html>";
                            statusLabel.setText(statusText);
                            rollButton.setEnabled(true);
                        }
                    });

                    // Animate dice
                    Dice dice = gameManager.getDice();
                    dicePanel.animateDiceRoll(dice.getLastRoll(), dice.getColor());
                }
            });

            // Add components to right panel
            rightPanel.add(titleLabel);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            rightPanel.add(dicePanel);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            rightPanel.add(statusLabel);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            rightPanel.add(rollButton);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            rightPanel.add(laddersScroll);

            frame.add(boardPanel, BorderLayout.CENTER);
            frame.add(rightPanel, BorderLayout.EAST);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}