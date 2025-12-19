import javax.swing.*;
import java.awt.*;

// ============= Main.java =============
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Party Board Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Get number of players
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

            // Set your original image dimensions
            int originalImageWidth = 1152;
            int originalImageHeight = 864;

            // Initialize game
            BoardGame game = new BoardGame(originalImageWidth, originalImageHeight);
            game.initializeBoard();
            SoundManager.getInstance().playBackgroundMusic("bgm_main");

            // Add players with same colors as ladder game
            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
            for (int i = 0; i < numPlayers; i++) {
                game.addPlayer(new Player("Player " + (i + 1), colors[i % colors.length]));
            }

            // Create game panel
            GamePanel gamePanel = new GamePanel(game, "C:\\Users\\GHAZY\\IdeaProjects\\ASD Class\\BoardGame2\\map.png");

            // Create right panel (matching ladder game UI)
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setPreferredSize(new Dimension(280, originalImageHeight));
            rightPanel.setBackground(new Color(60, 60, 60));
            rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Title
            JLabel titleLabel = new JLabel("Party Board Game");
            titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Rules
            JLabel rulesLabel = new JLabel(
                    "<html><center><b>💡 Hint:</b><br>" +
                            "Start from PRIME box<br>to use intersections!<br><br>" +
                            "<b>❓ Mystery Box:</b><br>Land on the box to get the surprise!</center></html>");
            rulesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            rulesLabel.setForeground(new Color(173, 216, 230));
            rulesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            rulesLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Dice panel
            DicePanel dicePanel = new DicePanel();
            dicePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Status label
            JLabel statusLabel = new JLabel();
            statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            statusLabel.setForeground(Color.WHITE);
            statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Roll button
            JButton rollButton = new JButton("Roll Dice");
            rollButton.setFont(new Font("Arial", Font.BOLD, 16));
            rollButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            rollButton.setMaximumSize(new Dimension(150, 40));
            rollButton.setBackground(new Color(0, 120, 215));
            rollButton.setForeground(Color.WHITE);
            rollButton.setFocusPainted(false);

            // Scoreboard
            JLabel scoreboardLabel = new JLabel();
            scoreboardLabel.setForeground(Color.WHITE);
            scoreboardLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            scoreboardLabel.setVerticalAlignment(SwingConstants.TOP);
            scoreboardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel scoreboardPanel = new JPanel();
            scoreboardPanel.setBackground(new Color(80, 80, 80));
            scoreboardPanel.setPreferredSize(new Dimension(240, 150));
            scoreboardPanel.setMaximumSize(new Dimension(240, 150));
            scoreboardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            scoreboardPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
            scoreboardPanel.add(scoreboardLabel);

            //sound ui
// Volume slider: 0 = 0%, 100 = 100%
            JLabel volumeLabel = new JLabel("Volume: 80%");
            volumeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            volumeLabel.setForeground(Color.WHITE);
            volumeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JSlider volumeSlider = new JSlider(0, 100, 80); // 0 to 100, default 80
            volumeSlider.setMaximumSize(new Dimension(200, 40));
            volumeSlider.setBackground(new Color(60, 60, 60));
            volumeSlider.setMajorTickSpacing(25);
            volumeSlider.setPaintTicks(true);
            volumeSlider.setPaintLabels(true);

// Custom labels
            java.util.Hashtable<Integer, JLabel> labelTable = new java.util.Hashtable<>();
            JLabel label0 = new JLabel("0%");
            label0.setFont(new Font("Arial", Font.PLAIN, 10));
            label0.setForeground(Color.WHITE);

            JLabel label100 = new JLabel("100%");
            label100.setFont(new Font("Arial", Font.PLAIN, 10));
            label100.setForeground(Color.WHITE);

            labelTable.put(0, label0);
            labelTable.put(100, label100);
            volumeSlider.setLabelTable(labelTable);

            volumeSlider.addChangeListener(e -> {
                float volume = volumeSlider.getValue() / 100f;
                SoundManager.getInstance().setVolume(volume);
                volumeLabel.setText("Volume: " + volumeSlider.getValue() + "%");
            });

            // Update functions
            Runnable updateStatusLabel = () -> {
                Player currentPlayer = game.getCurrentPlayer();
                String colorHex = String.format("#%02x%02x%02x",
                        currentPlayer.getColor().getRed(),
                        currentPlayer.getColor().getGreen(),
                        currentPlayer.getColor().getBlue());
                String statusText = "<html><center>Current Player:<br><b><font color='" + colorHex + "'>" +
                        currentPlayer.getName() + "</font></b>";
                if (game.isDoubleTurn()) {
                    statusText += "<br><font color='rgb(255, 215, 0)'>DOUBLE TURN!</font>";
                }
                statusText += "</center></html>";
                statusLabel.setText(statusText);
            };

            Runnable updateScoreboard = () -> {
                StringBuilder scoreText = new StringBuilder("<html><div style='text-align: center;'>");
                scoreText.append("<b style='font-size: 14px;'>SCOREBOARD</b><br><br>");

                for (Player player : game.getPlayers()) {
                    String colorHex = String.format("#%02x%02x%02x",
                            player.getColor().getRed(),
                            player.getColor().getGreen(),
                            player.getColor().getBlue());
                    scoreText.append("<font color='").append(colorHex).append("'><b>")
                            .append(player.getName()).append("</b></font>: ")
                            .append(player.getScore()).append(" pts<br>");
                }

                scoreText.append("</div></html>");
                scoreboardLabel.setText(scoreText.toString());
            };

            updateStatusLabel.run();
            updateScoreboard.run();

            // Roll button action
            rollButton.addActionListener(e -> {
                if (!game.isGameOver() && !game.isAnyPlayerAnimating()) {
                    rollButton.setEnabled(false);

                    game.playTurn(() -> {
                        Dice dice = game.getDice();

                        if (game.isGameOver()) {
                            Player winner = game.getCurrentPlayer();
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

                    // Animate dice
                    Dice dice = game.getDice();
                    dicePanel.animateDiceRoll(dice.getLastRoll(), dice.getColor());
                }
            });
// Add a timer to continuously check if game was restarted
            Timer gameStateChecker = new Timer(500, e -> {
                if (!game.isGameOver() && !rollButton.isEnabled() && !game.isAnyPlayerAnimating()) {
                    rollButton.setEnabled(true);
                    updateStatusLabel.run();
                    updateScoreboard.run();
                }
            });
            gameStateChecker.start();
            // Add components to right panel
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
            rightPanel.add(scoreboardPanel);
            rightPanel.add(volumeLabel);
            rightPanel.add(volumeSlider);

            frame.add(gamePanel, BorderLayout.CENTER);
            frame.add(rightPanel, BorderLayout.EAST);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            System.out.println("Game started! Click on map to get node coordinates.");
        });
    }
}