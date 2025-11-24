import javax.swing.*;
import java.awt.*;
// Board class managing the game board
class Board {
    private Node[][] nodes;
    private static final int ROWS = 8;
    private static final int COLS = 8;
    private static final int TOTAL_NODES = ROWS * COLS;

    public Board() {
        nodes = new Node[ROWS][COLS];
        initializeBoard();
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

    public void draw(Graphics g) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                nodes[row][col].draw(g);
            }
        }
    }

    public int getTotalNodes() {
        return TOTAL_NODES;
    }
}