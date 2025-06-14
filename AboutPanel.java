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

public class AboutPanel extends JPanel {
    public AboutPanel(ActionListener returnToMenuListener) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea aboutText = new JTextArea();
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
        aboutText.setFont(UITheme.FONT_PRIMARY);
        aboutText.setForeground(UITheme.TEXT_PRIMARY);
        aboutText.setBackground(UITheme.PANEL_BACKGROUND);
        aboutText.setEditable(false);
        aboutText.setLineWrap(true);
        aboutText.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(aboutText);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JButton returnButton = new StyledButton("Return to Main Menu");
        returnButton.addActionListener(returnToMenuListener);

        add(scrollPane, BorderLayout.CENTER);
        add(returnButton, BorderLayout.SOUTH);
    }
}
