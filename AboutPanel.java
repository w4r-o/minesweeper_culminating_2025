/**
 * The UI panel for displaying information about the game and its creators.
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

/**
 * The UI panel for displaying information about the game and its creators.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public class AboutPanel extends JPanel {

    /**
     * Constructs an AboutPanel.
     * This panel displays information about the game, its version, release date, and developers.
     * It also provides a button to return to the main menu.
     *
     * @param returnToMenuListener An ActionListener to be triggered when the return button is pressed,
     *                             typically used to navigate back to the main menu.
     */
    public AboutPanel(ActionListener returnToMenuListener) {
        // Set the layout manager for the panel to BorderLayout
        setLayout(new BorderLayout());
        // Set the background color of the panel using the UITheme
        setBackground(UITheme.BACKGROUND);
        // Add an empty border for spacing
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create a JTextArea to display the about text
        JTextArea aboutText = new JTextArea();
        // Set the text content for the about section
        aboutText.setText(
            "Minesweeper\n\n" +
            "Version: 2.1.0\n" +
            "Release Date: June 14, 2025\n\n" +
            "Developed by:\n" +
            "Aaron Jiang\n" +
            "Leo Tan\n\n" +
            "This project was created as a culminating project for ICS3U1.\n" +
            "It features a modern UI, custom game difficulties, noise-based map generation" +
            "and unique power-ups to enhance the classic Minesweeper experience.\n\n"
        );
        // Set the font for the text area
        aboutText.setFont(UITheme.FONT_PRIMARY);
        // Set the foreground (text) color
        aboutText.setForeground(UITheme.TEXT_PRIMARY);
        // Set the background color of the text area
        aboutText.setBackground(UITheme.PANEL_BACKGROUND);
        // Make the text area non-editable
        aboutText.setEditable(false);
        // Enable line wrapping
        aboutText.setLineWrap(true);
        // Set wrap style to word boundaries
        aboutText.setWrapStyleWord(true);

        // Create a JScrollPane to make the text area scrollable if content exceeds visible area
        JScrollPane scrollPane = new JScrollPane(aboutText);
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
