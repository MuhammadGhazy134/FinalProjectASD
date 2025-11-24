import javax.swing.*;
import java.awt.*;
// BoardPanel class for rendering
class BoardPanel extends JPanel {
    private Board board;

    public BoardPanel() {
        board = new Board();
        setPreferredSize(new Dimension(520, 520));
        setBackground(new Color(245, 222, 179));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        board.draw(g);
    }

    public Board getBoard() {
        return board;
    }
}