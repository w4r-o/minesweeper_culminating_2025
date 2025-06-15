/**
 * The UI panel at the top of the game screen.
 * Displays the elapsed time and the current flag count.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
import javax.swing.*;
import java.awt.*;

public class TopPanel extends JPanel {
    /** Label to display the elapsed time or remaining time. */
    private JLabel timerLabel;
    /** Label to display the current number of flags placed and total bombs. */
    private JLabel flagLabel;

    /**
     * Constructs a new TopPanel.
     * Sets up the layout, background, and initializes the timer and flag labels.
     */
    public TopPanel() {
        // Set the layout manager to BorderLayout
        setLayout(new BorderLayout());
        // Set the background color from the UITheme
        setBackground(UITheme.PANEL_BACKGROUND);
        // Add an empty border for padding
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Initialize timer and flag labels with default text
        timerLabel = createLabel("Time: 0s");
        flagLabel = createLabel("Flags: 0/0");

        // Add labels to the panel, timer on the left, flags on the right
        add(timerLabel, BorderLayout.WEST);
        add(flagLabel, BorderLayout.EAST);
    }

    /**
     * Helper method to create a JLabel with consistent styling.
     * @param text The text for the label.
     * @return A new JLabel instance.
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(UITheme.TEXT_PRIMARY); // Set foreground color
        label.setFont(UITheme.FONT_PRIMARY); // Set font
        return label;
    }

    /**
     * Updates the timer display.
     * Shows either elapsed time or remaining time if a time limit is set.
     *
     * @param seconds The time in seconds (either elapsed or remaining).
     * @param timeLimitSeconds The total time limit in seconds, or -1 if no limit.
     */
    public void updateTimer(int seconds, int timeLimitSeconds) {
        if (timeLimitSeconds != -1) {
            // Display remaining time if a limit is set
            timerLabel.setText(String.format("Time: %d s (Remaining)", seconds));
        } else {
            // Display elapsed time if no limit
            timerLabel.setText(String.format("Time: %d s", seconds));
        }
    }

    /**
     * Updates the flag count display.
     * @param flagsPlaced The number of flags currently placed by the player.
     * @param totalBombs The total number of bombs on the board.
     */
    public void updateFlags(int flagsPlaced, int totalBombs) {
        flagLabel.setText(String.format("Flags: %d/%d", flagsPlaced, totalBombs));
    }
}
