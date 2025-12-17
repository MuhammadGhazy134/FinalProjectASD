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
    private Timer repaintTimer;

    public GamePanel(BoardGame game, String imagePath) {
        this.game = game;
        loadMapImage(imagePath);

        setPreferredSize(new Dimension(game.getOriginalImageWidth(), game.getOriginalImageHeight()));
        setBackground(new Color(245, 222, 179));

        repaintTimer = new Timer(16, e -> repaint());
        repaintTimer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int originalX = (int) (e.getX() * ((double) game.getOriginalImageWidth() / getWidth()));
                int originalY = (int) (e.getY() * ((double) game.getOriginalImageHeight() / getHeight()));

                System.out.println("Clicked at ORIGINAL coordinates: (" + originalX + ", " + originalY + ")");
            }
        });
    }

    private void loadMapImage(String imagePath) {
        try {
            java.net.URL imageURL = getClass().getClassLoader().getResource(imagePath);

            if (imageURL != null) {
                mapImage = ImageIO.read(imageURL);
                System.out.println("Image loaded from resources!");
            } else {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    mapImage = ImageIO.read(imageFile);
                    System.out.println("Image loaded from file!");
                } else {
                    System.err.println("Image not found: " + imageFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (mapImage != null) {
            g2d.drawImage(mapImage, 0, 0, getWidth(), getHeight(), null);
        }

        drawNodes(g2d);
        drawPlayers(g2d);
    }

    private void drawNodes(Graphics2D g2d) {
        double scale = Math.min(
                (double) getWidth() / game.getOriginalImageWidth(),
                (double) getHeight() / game.getOriginalImageHeight()
        );

        for (Node node : game.getNodes()) {
            Point pos = node.getScaledPosition(
                    game.getOriginalImageWidth(),
                    game.getOriginalImageHeight(),
                    getWidth(),
                    getHeight()
            );

            int scaledNodeSize = (int) (NODE_SIZE * scale);

            // Draw node based on type
            if (node.getType() == Node.NodeType.START) {
                g2d.setColor(new Color(0, 255, 0, 150));
            } else if (node.getType() == Node.NodeType.END) {
                g2d.setColor(new Color(255, 0, 0, 150));
            } else if (node.getType() == Node.NodeType.PRIME) {
                g2d.setColor(new Color(173, 216, 230, 150));
            } else {
                g2d.setColor(new Color(100, 100, 100, 80));
            }

            g2d.fillOval(pos.x - scaledNodeSize/2, pos.y - scaledNodeSize/2, scaledNodeSize, scaledNodeSize);

            // Draw node ID
            g2d.setColor(Color.BLACK);
            int fontSize = Math.max(8, (int) (12 * scale));
            g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
            String id = String.valueOf(node.getId());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(id);
            g2d.drawString(id, pos.x - textWidth/2, pos.y + fontSize/3);

            // Draw points if any
            if (node.getPoints() > 0) {
                g2d.setColor(new Color(255, 140, 0));
                g2d.setFont(new Font("Arial", Font.BOLD, (int)(10 * scale)));
                String pointsText = "+" + node.getPoints();
                int pointsWidth = g2d.getFontMetrics().stringWidth(pointsText);
                g2d.drawString(pointsText, pos.x - pointsWidth/2, pos.y + scaledNodeSize/2 + 10);
            }
        }
    }

    private void drawPlayers(Graphics2D g2d) {
        double scale = Math.min(
                (double) getWidth() / game.getOriginalImageWidth(),
                (double) getHeight() / game.getOriginalImageHeight()
        );
        int scaledPlayerSize = (int) (PLAYER_SIZE * scale);

        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player player = game.getPlayers().get(i);
            Node displayNode = player.getDisplayNode();

            if (displayNode != null) {
                Point pos = displayNode.getScaledPosition(
                        game.getOriginalImageWidth(),
                        game.getOriginalImageHeight(),
                        getWidth(),
                        getHeight()
                );

                int offsetX = (int) ((i % 2) * 15 * scale - 7 * scale);
                int offsetY = (int) ((i / 2) * 15 * scale - 7 * scale);

                g2d.setColor(player.getColor());
                g2d.fillOval(pos.x + offsetX - scaledPlayerSize/2,
                        pos.y + offsetY - scaledPlayerSize/2,
                        scaledPlayerSize, scaledPlayerSize);

                g2d.setColor(Color.BLACK);
                g2d.drawOval(pos.x + offsetX - scaledPlayerSize/2,
                        pos.y + offsetY - scaledPlayerSize/2,
                        scaledPlayerSize, scaledPlayerSize);

                g2d.setColor(Color.WHITE);
                int fontSize = Math.max(8, (int) (10 * scale));
                g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
                String initial = player.getName().substring(0, 1);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(initial);
                g2d.drawString(initial,
                        pos.x + offsetX - textWidth/2,
                        pos.y + offsetY + fontSize/3);
            }
        }
    }
}