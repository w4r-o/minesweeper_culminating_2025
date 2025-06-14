/**
 * The UI panel displayed at the end of a game to show the result (Win/Loss).
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ResultPanel extends JPanel {
    /**
     * Constructs the result screen.
     * @param message The message to display (e.g., "You Won!").
     * @param isWin True if the player won, used for coloring the text.
     */
    public ResultPanel(String message, boolean isWin) {
        setLayout(new GridBagLayout());
        setBackground(UITheme.BACKGROUND);
        setPreferredSize(new Dimension(450, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(20, 0, 20, 0);

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(UITheme.FONT_TITLE);
        messageLabel.setForeground(isWin ? new Color(0, 200, 100) : Color.RED);

        JButton returnToMenuButton = new StyledButton("Return to Menu");
        
        add(messageLabel, gbc);
        add(returnToMenuButton, gbc);
    }

    public void addReturnToMenuListener(ActionListener listener) {
        ((JButton)getComponent(1)).addActionListener(listener);
    }
}
