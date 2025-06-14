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
    public TutorialPanel(ActionListener returnToMenuListener) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea tutorialText = new JTextArea();
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
        tutorialText.setFont(UITheme.FONT_PRIMARY);
        tutorialText.setForeground(UITheme.TEXT_PRIMARY);
        tutorialText.setBackground(UITheme.PANEL_BACKGROUND);
        tutorialText.setEditable(false);
        tutorialText.setLineWrap(true);
        tutorialText.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(tutorialText);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JButton returnButton = new StyledButton("Return to Main Menu");
        returnButton.addActionListener(returnToMenuListener);

        add(scrollPane, BorderLayout.CENTER);
        add(returnButton, BorderLayout.SOUTH);
    }
}
