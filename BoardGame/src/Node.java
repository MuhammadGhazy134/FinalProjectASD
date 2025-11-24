import javax.swing.*;
import java.awt.*;

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
        // Draw the box
        g.setColor(Color.WHITE);
        g.fillRect(x, y, SIZE, SIZE);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, SIZE, SIZE);

        // Draw the number at top-right corner
        g.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        String numStr = String.valueOf(number);
        int textWidth = fm.stringWidth(numStr);
        g.drawString(numStr, x + SIZE - textWidth - 5, y + 15);
    }
}