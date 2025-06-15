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
     * This panel displays a message indicating whether the player won or lost,
     * and provides a button to return to the main menu.
     *
     * @param message The message to display (e.g., "You Won!", "Game Over").
     * @param isWin True if the player won the game, which determines the color of the message text.
     */
    public ResultPanel(String message, boolean isWin) {
        // Set the layout manager to GridBagLayout for centering components
        setLayout(new GridBagLayout());
        // Set the background color from the UITheme
        setBackground(UITheme.BACKGROUND);
        // Set a preferred size for the panel
        setPreferredSize(new Dimension(450, 300));

        // Initialize GridBagConstraints for layout control
        GridBagConstraints gbc = new GridBagConstraints();
        // Components span the remaining width of the row
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        // Set insets (padding) for components
        gbc.insets = new Insets(20, 0, 20, 0);

        // Create and style the message label
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(UITheme.FONT_TITLE);
        // Set text color based on win/loss status
        messageLabel.setForeground(isWin ? new Color(0, 200, 100) : Color.RED);

        // Create the "Return to Menu" button using StyledButton for consistent theme
        JButton returnToMenuButton = new StyledButton("Return to Menu");
        
        // Add the message label and button to the panel
        add(messageLabel, gbc);
        add(returnToMenuButton, gbc);
    }

    /**
     * Adds an ActionListener to the "Return to Menu" button.
     * @param listener The ActionListener to be added.
     */
    public void addReturnToMenuListener(ActionListener listener) {
        // The "Return to Menu" button is the second component added (index 1)
        ((JButton)getComponent(1)).addActionListener(listener);
    }
}
