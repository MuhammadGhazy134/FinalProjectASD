import javax.swing.*;
import java.awt.*;

// ============= Main.java =============
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Turn-Based Board Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Initialize game
            BoardGame game = new BoardGame();
            game.initializeBoard();

            // Add players
            game.addPlayer(new Player("Player 1", Color.RED));
            game.addPlayer(new Player("Player 2", Color.BLUE));
            game.addPlayer(new Player("Player 3", Color.GREEN));
            game.addPlayer(new Player("Player 4", Color.YELLOW));

            // Create game panel with your map image
            // REPLACE "map.png" with your actual image path
            GamePanel gamePanel = new GamePanel(game, "C:\\Users\\GHAZY\\IdeaProjects\\ASD Class\\BoardGame2\\map.png");
            frame.add(gamePanel, BorderLayout.CENTER);

            // Create control panel
            JPanel controlPanel = new JPanel();
            JButton rollButton = new JButton("Roll Dice");
            JLabel statusLabel = new JLabel("Click 'Roll Dice' to start!");

            rollButton.addActionListener(e -> {
                game.playTurn();
                gamePanel.repaint();
                statusLabel.setText("Current player: " + game.getCurrentPlayer().getName());
            });

            controlPanel.add(rollButton);
            controlPanel.add(statusLabel);
            frame.add(controlPanel, BorderLayout.SOUTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            System.out.println("Game started! Click anywhere on the map to see coordinates.");
        });
    }
}