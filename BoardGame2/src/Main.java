import javax.swing.*;
import java.awt.*;

// ============= Main.java =============
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Turn-Based Board Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // IMPORTANT: Set these to your ORIGINAL image dimensions
            int originalImageWidth = 1152;
            int originalImageHeight = 864;

            // Initialize game with original image dimensions
            BoardGame game = new BoardGame(originalImageWidth, originalImageHeight);
            game.initializeBoard();

            // Add players
            game.addPlayer(new Player("Player 1", Color.RED));
            game.addPlayer(new Player("Player 2", Color.BLUE));
            game.addPlayer(new Player("Player 3", Color.GREEN));
            game.addPlayer(new Player("Player 4", Color.YELLOW));

            // Create game panel with your map image
            GamePanel gamePanel = new GamePanel(game, "C:\\Users\\GHAZY\\IdeaProjects\\ASD Class\\BoardGame2\\map.png");
            frame.add(gamePanel, BorderLayout.CENTER);

            // Create control panel
            JPanel controlPanel = new JPanel();
            JButton rollButton = new JButton("Roll Dice");
            JLabel statusLabel = new JLabel("Click 'Roll Dice' to start!");
            JButton fullscreenButton = new JButton("Toggle Fullscreen");

            rollButton.addActionListener(e -> {
                game.playTurn();
                gamePanel.repaint();
                statusLabel.setText("Current player: " + game.getCurrentPlayer().getName());
            });

            fullscreenButton.addActionListener(e -> {
                GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                if (gd.getFullScreenWindow() == null) {
                    frame.dispose();
                    frame.setUndecorated(true);
                    gd.setFullScreenWindow(frame);
                    frame.setVisible(true);
                } else {
                    gd.setFullScreenWindow(null);
                    frame.dispose();
                    frame.setUndecorated(false);
                    frame.setVisible(true);
                }
                gamePanel.repaint();
            });

            controlPanel.add(rollButton);
            controlPanel.add(statusLabel);
            controlPanel.add(fullscreenButton);
            frame.add(controlPanel, BorderLayout.SOUTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            System.out.println("Game started! Click anywhere on the map to see ORIGINAL coordinates.");
            System.out.println("Use these coordinates to place your nodes.");
        });
    }
}