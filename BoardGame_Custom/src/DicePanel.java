import java.awt.*;
import javax.swing.*;

// ============= DicePanel.java =============
class DicePanel extends JPanel {
    private int diceValue = 0;
    private Color diceColor = Color.WHITE;
    private double rotationAngle = 0;
    private Timer rotationTimer;

    public DicePanel() {
        setPreferredSize(new Dimension(100, 100));
        setMaximumSize(new Dimension(100, 100));
        setBackground(new Color(80, 80, 80));
    }

    public void animateDiceRoll(int finalValue, Color finalColor) {
        rotationAngle = 0;
        rotationTimer = new Timer(30, new java.awt.event.ActionListener() {
            int count = 0;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                diceValue = (int)(Math.random() * 6) + 1;
                rotationAngle += 0.3;
                count++;
                if (count > 20) {
                    rotationTimer.stop();
                    diceValue = finalValue;
                    diceColor = finalColor;
                    rotationAngle = 0;
                }
                repaint();
            }
        });
        rotationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        g2d.translate(centerX, centerY);
        g2d.rotate(rotationAngle);

        g2d.setColor(diceColor);
        g2d.fillRoundRect(-30, -30, 60, 60, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(-30, -30, 60, 60, 10, 10);

        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        String val = diceValue > 0 ? String.valueOf(diceValue) : "?";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(val);
        int textHeight = fm.getAscent();
        g2d.drawString(val, -textWidth / 2, textHeight / 2 - 5);
    }
}
