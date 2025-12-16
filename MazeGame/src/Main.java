import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MazeFrame frame = new MazeFrame();
            frame.setVisible(true);
        });
    }

}