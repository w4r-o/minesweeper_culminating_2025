/**
 * The UI panel for displaying game instructions and rules.
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

public class TutorialPanel extends JPanel {
    /**
     * Constructs a TutorialPanel.
     * This panel displays instructions and rules for playing Minesweeper,
     * including details on gameplay mechanics, power-ups, and win/loss conditions.
     *
     * @param returnToMenuListener An ActionListener to be triggered when the return button is pressed,
     *                             typically used to navigate back to the main menu.
     */
    public TutorialPanel(ActionListener returnToMenuListener) {
        // Set the layout manager for the panel to BorderLayout
        setLayout(new BorderLayout());
        // Set the background color of the panel using the UITheme
        setBackground(UITheme.BACKGROUND);
        // Add an empty border for spacing
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create a JTextArea to display the tutorial text
        JTextArea tutorialText = new JTextArea();
        // Set the text content for the tutorial
        tutorialText.setText(
            "How to Play Minesweeper:\n\n" +
            "Objective: Clear a rectangular board containing hidden \"mines\" or bombs without detonating any of them, " +
            "with help from clues about the number of neighboring mines in each field.\n\n" +
            "Gameplay:\n" +
            "1. Left-click a square to reveal it. If it's a mine, you lose!\n" +
            "2. If it's not a mine, the square will show a number indicating how many mines are adjacent to it (including diagonals).\n" +
            "3. If the revealed square is blank (0 adjacent mines), it will automatically clear adjacent squares until numbers are reached.\n" +
            "4. Right-click a square to place a flag on it, marking it as a suspected mine. Right-click again to remove the flag.\n" +
            "5. Collect power-ups by revealing cells that contain them. Power-ups include:\n" +
            "   - Heal: Restores one health point.\n" +
            "   - Reveal: Temporarily reveals a 3x3 area without triggering mines.\n" +
            "   - Bomb: Clears a 3x3 area. Be careful on Hard mode, as this can damage you!\n\n" +
            "Winning: You win when all non-mine squares have been revealed.\n" +
            "Losing: You lose if you click on a mine and run out of health. \n" + 
            "6. T to skip 24h and refresh daily challenge and C to reveal bomb cheat"
        );
        // Set the font for the text area
        tutorialText.setFont(UITheme.FONT_PRIMARY);
        // Set the foreground (text) color
        tutorialText.setForeground(UITheme.TEXT_PRIMARY);
        // Set the background color of the text area
        tutorialText.setBackground(UITheme.PANEL_BACKGROUND);
        // Make the text area non-editable
        tutorialText.setEditable(false);
        // Enable line wrapping
        tutorialText.setLineWrap(true);
        // Set wrap style to word boundaries
        tutorialText.setWrapStyleWord(true);

        // Create a JScrollPane to make the text area scrollable if content exceeds visible area
        JScrollPane scrollPane = new JScrollPane(tutorialText);
        // Remove the default border of the scroll pane
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        // Set vertical scroll bar policy to show as needed
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Create a styled button for returning to the main menu
        JButton returnButton = new StyledButton("Return to Main Menu");
        // Add the provided ActionListener to the return button
        returnButton.addActionListener(returnToMenuListener);

        // Add the scroll pane to the center of the panel
        add(scrollPane, BorderLayout.CENTER);
        // Add the return button to the south (bottom) of the panel
        add(returnButton, BorderLayout.SOUTH);
    }
}
