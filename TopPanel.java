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
    private JLabel timerLabel;
    private JLabel flagLabel;

    public TopPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.PANEL_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        timerLabel = createLabel("Time: 0s");
        flagLabel = createLabel("Flags: 0/0");

        add(timerLabel, BorderLayout.WEST);
        add(flagLabel, BorderLayout.EAST);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(UITheme.TEXT_PRIMARY);
        label.setFont(UITheme.FONT_PRIMARY);
        return label;
    }

    public void updateTimer(int seconds) {
        timerLabel.setText(String.format("Time: %d s", seconds));
    }

    public void updateFlags(int flagsPlaced, int totalBombs) {
        flagLabel.setText(String.format("Flags: %d/%d", flagsPlaced, totalBombs));
    }
}
