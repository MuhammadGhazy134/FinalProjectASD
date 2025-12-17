// ============= GamePanel.java =============
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

class GamePanel extends JPanel {
    private BoardGame game;
    private BufferedImage mapImage;
    private static final int PLAYER_SIZE = 20;
    private static final int NODE_SIZE = 30;

    public GamePanel(BoardGame game, String imagePath) {
        this.game = game;
        loadMapImage(imagePath);

        setPreferredSize(new Dimension(1152, 864)); // Adjust to your image size
        setBackground(Color.WHITE);

        // Add mouse listener for clicking nodes (for debugging/setup)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Clicked at: (" + e.getX() + ", " + e.getY() + ")");
            }
        });
    }

    private void loadMapImage(String imagePath) {
        try {
            mapImage = ImageIO.read(new File(imagePath));
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw map image
        if (mapImage != null) {
            g2d.drawImage(mapImage, 0, 0, getWidth(), getHeight(), null);
        }

        // Draw nodes
        drawNodes(g2d);

        // Draw players
        drawPlayers(g2d);
    }

    private void drawNodes(Graphics2D g2d) {
        for (Node node : game.getNodes()) {
            Point pos = node.getPosition();

            // Draw node circle
            if (node.getType() == Node.NodeType.START) {
                g2d.setColor(new Color(0, 255, 0, 150));
            } else if (node.getType() == Node.NodeType.END) {
                g2d.setColor(new Color(255, 0, 0, 150));
            } else if (node.getType() == Node.NodeType.PRIME) {
                g2d.setColor(new Color(255, 255, 0, 150));
            } else {
                g2d.setColor(new Color(100, 100, 100, 100));
            }

            g2d.fillOval(pos.x - NODE_SIZE/2, pos.y - NODE_SIZE/2, NODE_SIZE, NODE_SIZE);

            // Draw node ID
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String id = String.valueOf(node.getId());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(id);
            g2d.drawString(id, pos.x - textWidth/2, pos.y + 5);
        }
    }

    private void drawPlayers(Graphics2D g2d) {
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player player = game.getPlayers().get(i);
            Node currentNode = player.getCurrentNode();

            if (currentNode != null) {
                Point pos = currentNode.getPosition();

                // Offset players so they don't overlap
                int offsetX = (i % 2) * 15 - 7;
                int offsetY = (i / 2) * 15 - 7;

                g2d.setColor(player.getColor());
                g2d.fillOval(pos.x + offsetX - PLAYER_SIZE/2,
                        pos.y + offsetY - PLAYER_SIZE/2,
                        PLAYER_SIZE, PLAYER_SIZE);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                g2d.drawString(player.getName().substring(0, 1),
                        pos.x + offsetX - 4,
                        pos.y + offsetY + 4);
            }
        }
    }
}