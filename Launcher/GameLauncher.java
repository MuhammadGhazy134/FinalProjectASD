import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class GameLauncher extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Color scheme
    private final Color BG_COLOR = new Color(45, 52, 70);
    private final Color CARD_BG = new Color(60, 68, 92);
    private final Color ACCENT_COLOR = new Color(88, 166, 255);
    private final Color HOVER_COLOR = new Color(108, 186, 255);
    private final Color TEXT_COLOR = new Color(240, 243, 250);
    private final Color SUBTITLE_COLOR = new Color(180, 190, 210);
    private final Color EXIT_COLOR = new Color(220, 53, 69);
    private final Color EXIT_HOVER_COLOR = new Color(200, 35, 51);

    public GameLauncher() {
        setTitle("Game Launcher");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG_COLOR);

        // Create all screens
        mainPanel.add(createHomeScreen(), "HOME");
        mainPanel.add(createBoardGameScreen(), "BOARDGAME");
        mainPanel.add(createPlayerSelectionScreen("Ladder Board Game", "games/BoardGame1.jar"), "LADDER_PLAYERS");
        mainPanel.add(createPlayerSelectionScreen("Party Board Game Custom", "games/BoardGame2.jar"), "CUSTOM_PLAYERS");

        add(mainPanel);
        setLocationRelativeTo(null);
    }

    private JPanel createHomeScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(40, 60, 40, 60));

        // Title panel
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(BG_COLOR);

        JPanel titleContent = new JPanel();
        titleContent.setBackground(BG_COLOR);
        titleContent.setLayout(new BoxLayout(titleContent, BoxLayout.Y_AXIS));

        // Main title with gradient effect
        JLabel title = new JLabel("<html><div style='text-align: center;'>" +
                "<span style='font-size: 36px; letter-spacing: 3px;'>ARCADE</span><br>" +
                "<span style='font-size: 20px; color: rgb(88, 166, 255); letter-spacing: 8px;'>GAME HUB</span>" +
                "</div></html>");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Choose your adventure");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitle.setForeground(SUBTITLE_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleContent.add(title);
        titleContent.add(Box.createRigidArea(new Dimension(0, 10)));
        titleContent.add(subtitle);

        titlePanel.add(titleContent);

        // Center buttons panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BG_COLOR);

        JButton boardGameBtn = createStyledButton("Party Board Game", 300, 60);
        JButton mazeBtn = createStyledButton("Maze Solver", 300, 60);
        JButton exitBtn = createExitButton("Exit", 300, 60);

        boardGameBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        mazeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        boardGameBtn.addActionListener(e -> cardLayout.show(mainPanel, "BOARDGAME"));
        mazeBtn.addActionListener(e -> runJar("games/MazeSolver.jar"));
        exitBtn.addActionListener(e -> System.exit(0));

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(boardGameBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(mazeBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(exitBtn);
        centerPanel.add(Box.createVerticalGlue());

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBoardGameScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Top panel with back button and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_COLOR);

        JButton backBtn = createBackButton();

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(BG_COLOR);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = createTitle("Choose Board Game");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Select your preferred game mode");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(SUBTITLE_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        titlePanel.add(subtitle);

        topPanel.add(backBtn, BorderLayout.WEST);
        topPanel.add(titlePanel, BorderLayout.CENTER);

        // Center panel with game cards
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Ladder Board Game Card
        JPanel ladderCard = createGameCard(
                "Ladder Board Game",
                "Classic ladder board game with snakes",
                "images/ladder_preview.png",
                e -> cardLayout.show(mainPanel, "LADDER_PLAYERS")
        );
        centerPanel.add(ladderCard, gbc);

        // Custom Map Board Game Card
        gbc.gridx = 1;
        JPanel customCard = createGameCard(
                "Party Board Game",
                "Custom map with special events",
                "images/custom_preview.png",
                e -> cardLayout.show(mainPanel, "CUSTOM_PLAYERS")
        );
        centerPanel.add(customCard, gbc);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPlayerSelectionScreen(String gameName, String jarPath) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 60, 30, 60));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_COLOR);

        JButton backBtn = createBackButton();
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "BOARDGAME"));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(BG_COLOR);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = createTitle(gameName);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Select Number of Players");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(SUBTITLE_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        titlePanel.add(subtitle);

        topPanel.add(backBtn, BorderLayout.WEST);
        topPanel.add(titlePanel, BorderLayout.CENTER);

        // Center panel with player buttons
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        for (int i = 2; i <= 4; i++) {
            final int players = i;

            // Create player icons string
            String playerIcons = "";
            for (int j = 0; j < players; j++) {
                playerIcons += "👤 ";
            }

            JButton playerBtn = createPlayerButtonWithIcon(playerIcons.trim(), players + " Players");
            playerBtn.setPreferredSize(new Dimension(180, 120));
            playerBtn.addActionListener(e -> runJarWithPlayers(jarPath, players));

            gbc.gridx = (i - 2) % 2;
            gbc.gridy = (i - 2) / 2;
            centerPanel.add(playerBtn, gbc);
        }

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createGameCard(String title, String description, String imagePath, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(280, 320));

        // Top: Image preview
        JPanel imagePanel = createImagePanel(imagePath);

        // Bottom: Text info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_BG);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descLabel.setForeground(SUBTITLE_COLOR);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        infoPanel.add(descLabel);

        card.add(imagePanel, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.SOUTH);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(70, 78, 102));
                infoPanel.setBackground(new Color(70, 78, 102));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(HOVER_COLOR, 2, true),
                        new EmptyBorder(15, 15, 15, 15)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BG);
                infoPanel.setBackground(CARD_BG);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT_COLOR, 2, true),
                        new EmptyBorder(15, 15, 15, 15)
                ));
            }
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
        });

        return card;
    }

    private JPanel createImagePanel(String imagePath) {
        JPanel panel = new JPanel() {
            private BufferedImage image;

            {
                try {
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        image = ImageIO.read(imgFile);
                    }
                } catch (Exception e) {
                    System.err.println("Could not load image: " + imagePath);
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (image != null) {
                    // Scale image to fit panel while maintaining aspect ratio
                    int imgWidth = image.getWidth();
                    int imgHeight = image.getHeight();
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();

                    double scale = Math.min((double) panelWidth / imgWidth, (double) panelHeight / imgHeight);
                    int scaledWidth = (int) (imgWidth * scale);
                    int scaledHeight = (int) (imgHeight * scale);

                    int x = (panelWidth - scaledWidth) / 2;
                    int y = (panelHeight - scaledHeight) / 2;

                    g2d.drawImage(image, x, y, scaledWidth, scaledHeight, null);
                } else {
                    // Fallback: show placeholder
                    g2d.setColor(new Color(40, 45, 60));
                    g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 10, 10);
                    g2d.setColor(SUBTITLE_COLOR);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                    String msg = "No Preview";
                    FontMetrics fm = g2d.getFontMetrics();
                    int msgWidth = fm.stringWidth(msg);
                    g2d.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2);
                }
            }
        };

        panel.setBackground(new Color(40, 45, 60));
        panel.setPreferredSize(new Dimension(250, 180));

        return panel;
    }

    private JButton createStyledButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
        });

        return button;
    }

    private JButton createPlayerButton(String text) {
        JButton button = new JButton("<html><center>" + text + "</center></html>");
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
        });

        return button;
    }

    private JButton createPlayerButtonWithIcon(String icons, String text) {
        JButton button = new JButton("<html><center>" +
                "<div style='font-size: 24px; margin-bottom: 10px;'>" + icons + "</div>" +
                "<div style='font-size: 16px;'>" + text + "</div>" +
                "</center></html>");
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
        });

        return button;
    }

    private JButton createExitButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setBackground(EXIT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(EXIT_HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(EXIT_COLOR);
            }
        });

        return button;
    }

    private JButton createBackButton() {
        JButton button = new JButton("← Back");
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(70, 78, 102));
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 35));
        button.setMaximumSize(new Dimension(100, 35));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(80, 88, 112));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 78, 102));
            }
        });

        button.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));

        return button;
    }

    private JLabel createTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 28));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private void runJar(String jarPath) {
        try {
            File jarFile = new File(jarPath);
            if (!jarFile.exists()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Game file not found:\n" + jarPath,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath);
            pb.inheritIO();
            pb.start();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to launch game:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void runJarWithPlayers(String jarPath, int players) {
        try {
            File jarFile = new File(jarPath);
            if (!jarFile.exists()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Game file not found:\n" + jarPath,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath, String.valueOf(players));
            pb.inheritIO();
            pb.start();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to launch game:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            GameLauncher launcher = new GameLauncher();
            launcher.setVisible(true);
        });
    }
}