import javax.swing.*;
import java.awt.*;

public class MazeFrame extends JFrame {
    private MazePanel mazePanel;
    private ControlPanel controlPanel;

    public MazeFrame() {
        setTitle("Weighted Graph Maze Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        mazePanel = new MazePanel();
        controlPanel = new ControlPanel(mazePanel);

        // Connect the sound manager
        mazePanel.setSoundManager(controlPanel.getSoundManager());

        add(controlPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(mazePanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        setSize(1000, 750); // Slightly increased height
        setMinimumSize(new Dimension(800, 650));
        setLocationRelativeTo(null);
        setResizable(true);
    }
}
