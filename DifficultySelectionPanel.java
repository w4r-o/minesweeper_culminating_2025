/**
 * The UI panel for selecting a game difficulty.
 * Provides buttons for preset difficulties (Easy, Medium, Hard), a custom option,
 * and a checkbox to enable noise-based map generation.
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

public class DifficultySelectionPanel extends JPanel {
    private JButton easyButton, mediumButton, hardButton, customButton;
    private JCheckBox noiseGenCheckbox;

    public DifficultySelectionPanel() {
        setLayout(new GridBagLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel title = new JLabel("Select Difficulty", SwingConstants.CENTER);
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_TITLE);

        easyButton = new StyledButton("Easy (9x9, 10 Mines)");
        mediumButton = new StyledButton("Medium (16x16, 40 Mines)");
        hardButton = new StyledButton("Hard (16x30, 99 Mines)");
        customButton = new StyledButton("Custom...");

        noiseGenCheckbox = new JCheckBox("Use Noise-Based Map Generation");
        noiseGenCheckbox.setHorizontalAlignment(SwingConstants.CENTER);
        noiseGenCheckbox.setFont(UITheme.FONT_PRIMARY);
        noiseGenCheckbox.setForeground(UITheme.TEXT_PRIMARY);
        noiseGenCheckbox.setOpaque(false);

        add(title, gbc);
        add(easyButton, gbc);
        add(mediumButton, gbc);
        add(hardButton, gbc);
        add(customButton, gbc);
        gbc.insets = new Insets(20, 0, 10, 0); // Add extra space before checkbox
        add(noiseGenCheckbox, gbc);
    }

    /**
     * Checks if the user wants to generate an island-style map.
     * @return true if the checkbox is selected, false otherwise.
     */
    public boolean isNoiseGenSelected() {
        return noiseGenCheckbox.isSelected();
    }

    // --- Action Listener Attachments ---
    public void addEasyListener(ActionListener listener) { easyButton.addActionListener(listener); }
    public void addMediumListener(ActionListener listener) { mediumButton.addActionListener(listener); }
    public void addHardListener(ActionListener listener) { hardButton.addActionListener(listener); }
    public void addCustomListener(ActionListener listener) { customButton.addActionListener(listener); }
}
