import javax.swing.*;
import java.awt.*;
// Main class to run the game
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Board Game - 8x8");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            BoardPanel boardPanel = new BoardPanel();
            frame.add(boardPanel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}