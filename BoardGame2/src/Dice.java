import java.awt.*;
import java.util.*;
import javax.swing.*;

// ============= Dice.java =============
class Dice {
    private Random random;
    private int lastRoll;
    private boolean isGreen;

    public Dice() {
        random = new Random();
        lastRoll = 0;
        isGreen = true;
    }

    public int roll() {
        lastRoll = random.nextInt(6) + 1;
        double probability = random.nextDouble();
        isGreen = probability < 1; // 90% green (forward), 10% red (backward)
        return lastRoll;
    }

    public int getLastRoll() {
        return lastRoll;
    }

    public boolean isGreen() {
        return isGreen;
    }

    public String getColorText() {
        return isGreen ? "GREEN (Forward)" : "RED (Backward)";
    }

    public Color getColor() {
        return isGreen ? new Color(34, 139, 34) : new Color(220, 20, 60);
    }
}