import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MazePanel extends JPanel {
    private static final int ROWS = 15;
    private static final int COLS = 20;
    private static final int CELL_SIZE = 30;

    private Maze maze;
    private MazeGenerator generator;
    private SoundManager soundManager;

    private Position currentStep;
    private Set<String> visitedDuringGeneration;
    private Set<Position> exploredCells;
    private List<Position> finalPath;
    private List<Position> queueState;
    private int pathCost;
    private int nodesExplored;

    private boolean isGenerating;
    private boolean isSolving;
    private int lastStepSoundTime;

    public MazePanel() {
        maze = new Maze(ROWS, COLS);
        generator = new MazeGenerator(maze);
        soundManager = new SoundManager(); // Initialize sound manager

        visitedDuringGeneration = new HashSet<>();
        exploredCells = new HashSet<>();
        finalPath = new ArrayList<>();
        queueState = new ArrayList<>();

        setPreferredSize(new Dimension(
                COLS * CELL_SIZE + 200,
                ROWS * CELL_SIZE + 100
        ));
        setBackground(new Color(15, 23, 42));

        lastStepSoundTime = 0;
    }

    public void setSoundManager(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    public void generateMaze(int speed) {
        if (isGenerating || isSolving) return;

        isGenerating = true;
        maze.reset();
        visitedDuringGeneration.clear();
        exploredCells.clear();
        finalPath.clear();
        queueState.clear();
        pathCost = 0;
        nodesExplored = 0;

        // Play start sound
        soundManager.playSound("button_click");

        Thread generatorThread = new Thread(() -> {
            generator.generateWithPrim(new MazeGenerator.MazeGenerationListener() {
                private long lastSoundTime = 0;

                @Override
                public void onStepComplete(Position current, Set<String> visited, int frontierSize) {
                    currentStep = current;
                    visitedDuringGeneration = new HashSet<>(visited);

                    // Play step sound with cooldown to avoid overwhelming
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastSoundTime > 50) { // 50ms cooldown
                        soundManager.playSound("wall_remove");
                        lastSoundTime = currentTime;
                    }

                    repaint();
                    try {
                        Thread.sleep(speed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onGenerationComplete() {
                    currentStep = null;
                    isGenerating = false;
                    // Play completion sound
                    soundManager.playSound("maze_generated");
                    repaint();
                }
            });
        });
        generatorThread.start();
    }

    public void solveMaze(String algorithm, int speed) {
        if (isGenerating || isSolving) return;

        isSolving = true;
        exploredCells.clear();
        finalPath.clear();
        queueState.clear();
        pathCost = 0;
        nodesExplored = 0;

        // Play start sound
        soundManager.playSound("button_click");

        MazeSolver solver;
        switch (algorithm) {
            case "BFS": solver = new BFSSolver(maze); break;
            case "DFS": solver = new DFSSolver(maze); break;
            case "Dijkstra": solver = new DijkstraSolver(maze); break;
            case "A*": solver = new AStarSolver(maze); break;
            default: return;
        }

        Thread solverThread = new Thread(() -> {
            final long[] lastSoundTime = {0};

            solver.solve(new MazeSolver.SolverListener() {
                @Override
                public void onStepComplete(Position current, Set<Position> explored,
                                           List<Position> queue, int nodes) {
                    currentStep = current;
                    exploredCells = new HashSet<>(explored);
                    queueState = new ArrayList<>(queue);
                    nodesExplored = nodes;

                    // Play step sound with cooldown
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastSoundTime[0] > Math.max(30, speed/2)) {
                        soundManager.playSound("step");
                        lastSoundTime[0] = currentTime;
                    }

                    repaint();
                    try {
                        Thread.sleep(speed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSolutionFound(List<Position> path, int cost, int nodes) {
                    finalPath = new ArrayList<>(path);
                    pathCost = cost;
                    nodesExplored = nodes;
                    currentStep = null;
                    isSolving = false;

                    // Play success sound
                    soundManager.playSound("solution_found");
                    repaint();
                }

                @Override
                public void onNoSolution() {
                    currentStep = null;
                    isSolving = false;
                    // Play error sound
                    soundManager.playSound("error");
                    JOptionPane.showMessageDialog(MazePanel.this,
                            "No solution found!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    repaint();
                }
            });
        });
        solverThread.start();
    }

    public void reset() {
        if (isGenerating || isSolving) return;

        maze.reset();
        visitedDuringGeneration.clear();
        exploredCells.clear();
        finalPath.clear();
        queueState.clear();
        currentStep = null;
        pathCost = 0;
        nodesExplored = 0;

        // Play reset sound
        soundManager.playSound("reset");
        repaint();
    }

    // ... rest of the existing methods remain the same ...
    public boolean isGenerating() { return isGenerating; }
    public boolean isSolving() { return isSolving; }
    public int getPathCost() { return pathCost; }
    public int getNodesExplored() { return nodesExplored; }
    public int getQueueSize() { return queueState.size(); }
    public int getPathLength() { return finalPath.size(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int offsetX = 50;
        int offsetY = 50;

        // Draw maze
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Cell cell = maze.getCell(i, j);
                int x = offsetX + j * CELL_SIZE;
                int y = offsetY + i * CELL_SIZE;

                // Determine cell color
                Color cellColor = cell.getTerrain().getColor();
                Position pos = new Position(i, j);

                if (finalPath.contains(pos)) {
                    cellColor = new Color(251, 191, 36); // Yellow - path
                } else if (currentStep != null && currentStep.equals(pos) && isSolving) {
                    cellColor = new Color(139, 92, 246); // Purple - current
                } else if (exploredCells.contains(pos)) {
                    cellColor = new Color(252, 165, 165); // Light red - explored
                } else if (queueState.contains(pos)) {
                    cellColor = new Color(196, 181, 253); // Light purple - in queue
                } else if (currentStep != null && currentStep.equals(pos) && isGenerating) {
                    cellColor = new Color(16, 185, 129); // Green - generating
                } else if (visitedDuringGeneration.contains(pos.toString())) {
                    cellColor = new Color(224, 242, 254); // Light blue - visited
                }

                g2d.setColor(cellColor);
                g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);

                // Draw weight
                if (!finalPath.contains(pos) && !exploredCells.contains(pos) &&
                        (currentStep == null || !currentStep.equals(pos))) {
                    int weight = cell.getTerrain().getWeight();
                    if (weight > 1) {
                        g2d.setColor(new Color(51, 65, 85));
                        g2d.setFont(new Font("Arial", Font.BOLD, 10));
                        String weightStr = String.valueOf(weight);
                        FontMetrics fm = g2d.getFontMetrics();
                        int textX = x + (CELL_SIZE - fm.stringWidth(weightStr)) / 2;
                        int textY = y + (CELL_SIZE + fm.getAscent()) / 2 - 2;
                        g2d.drawString(weightStr, textX, textY);
                    }
                }

                // Draw walls
                g2d.setColor(new Color(30, 41, 59));
                g2d.setStroke(new BasicStroke(2));

                if (cell.hasTopWall()) {
                    g2d.drawLine(x, y, x + CELL_SIZE, y);
                }
                if (cell.hasRightWall()) {
                    g2d.drawLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE);
                }
                if (cell.hasBottomWall()) {
                    g2d.drawLine(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE);
                }
                if (cell.hasLeftWall()) {
                    g2d.drawLine(x, y, x, y + CELL_SIZE);
                }
            }
        }

        // Draw START marker
        int startX = offsetX + CELL_SIZE / 2;
        int startY = offsetY + CELL_SIZE / 2;
        g2d.setColor(new Color(34, 197, 94));
        g2d.fillOval(startX - 10, startY - 10, 20, 20);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("S", startX - 5, startY + 5);

        // START label
        g2d.setColor(new Color(34, 197, 94));
        g2d.fillRoundRect(startX - 15, offsetY - 25, 30, 18, 3, 3);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.drawString("START", startX - 14, offsetY - 12);

        // Draw END marker
        int endX = offsetX + (COLS - 1) * CELL_SIZE + CELL_SIZE / 2;
        int endY = offsetY + (ROWS - 1) * CELL_SIZE + CELL_SIZE / 2;
        g2d.setColor(new Color(239, 68, 68));
        g2d.fillOval(endX - 10, endY - 10, 20, 20);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("E", endX - 5, endY + 5);

        // END label
        g2d.setColor(new Color(239, 68, 68));
        int endLabelY = offsetY + ROWS * CELL_SIZE + 8;
        g2d.fillRoundRect(endX - 12, endLabelY, 24, 18, 3, 3);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.drawString("END", endX - 10, endLabelY + 13);

        // Draw legend
        drawLegend(g2d, offsetX + COLS * CELL_SIZE + 20, offsetY);
    }

    private void drawLegend(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(30, 41, 59));
        g2d.fillRoundRect(x, y, 150, 230, 10, 10); // Increased height for sound info

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Terrain Types", x + 10, y + 25);

        int yOffset = y + 45;
        for (TerrainType terrain : TerrainType.values()) {
            g2d.setColor(terrain.getColor());
            g2d.fillRect(x + 10, yOffset, 20, 20);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString(terrain.getName(), x + 35, yOffset + 15);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString("Cost: " + terrain.getWeight(), x + 95, yOffset + 15);
            yOffset += 30;
        }

        // Add sound status
        yOffset += 10;
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.drawString("Sound: " + (soundManager.isEnabled() ? "ON 🔊" : "OFF 🔇"),
                x + 10, yOffset);
        yOffset += 20;
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("Volume: " + (int)(soundManager.getVolume() * 100) + "%",
                x + 10, yOffset);
    }
}