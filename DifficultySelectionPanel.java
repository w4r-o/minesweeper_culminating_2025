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
import java.util.ArrayList;
import java.util.List;

/**
 * The UI panel for selecting a game difficulty.
 * Provides buttons for preset difficulties (Easy, Medium, Hard), a custom option,
 * and a checkbox to enable noise-based map generation. It also includes options
 * for setting a time limit for the game.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public class DifficultySelectionPanel extends JPanel {
    // Buttons for selecting preset and custom difficulties
    private JButton easyButton, mediumButton, hardButton, customButton;
    // Checkbox to enable or disable noise-based map generation
    private JCheckBox noiseGenCheckbox;
    // Stores the selected time limit in seconds; -1 indicates no time limit
    private int selectedTimeLimitSeconds = -1;
    // List to manage the state of time limit selection buttons
    private List<StyledButton> timerButtons;

    /**
     * Constructs a new DifficultySelectionPanel.
     * This panel sets up the UI for difficulty selection, including preset buttons,
     * a custom game option, a noise generation checkbox, and time limit buttons.
     */
    public DifficultySelectionPanel() {
        // Set the layout manager to GridBagLayout for flexible component positioning
        setLayout(new GridBagLayout());
        // Set the background color from the UITheme
        setBackground(UITheme.BACKGROUND);
        // Add an empty border for padding
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Initialize GridBagConstraints for layout control
        GridBagConstraints gbc = new GridBagConstraints();
        // Components span the remaining width of the row
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        // Components fill their display area horizontally
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Set insets (padding) for components
        gbc.insets = new Insets(10, 0, 10, 0);

        // Create and style the title label
        JLabel title = new JLabel("Select Difficulty", SwingConstants.CENTER);
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_TITLE);

        // Initialize difficulty selection buttons with their respective texts
        easyButton = new StyledButton("Easy (9x9, 10 Mines)");
        mediumButton = new StyledButton("Medium (16x16, 40 Mines)");
        hardButton = new StyledButton("Hard (16x30, 99 Mines)");
        customButton = new StyledButton("Custom...");

        // Initialize and style the noise generation checkbox
        noiseGenCheckbox = new JCheckBox("Use Noise-Based Map Generation");
        noiseGenCheckbox.setHorizontalAlignment(SwingConstants.CENTER);
        noiseGenCheckbox.setFont(UITheme.FONT_PRIMARY);
        noiseGenCheckbox.setForeground(UITheme.TEXT_PRIMARY);
        noiseGenCheckbox.setOpaque(false); // Make it transparent to show background color

        // Add components to the panel
        add(title, gbc);
        add(easyButton, gbc);
        add(mediumButton, gbc);
        add(hardButton, gbc);
        add(customButton, gbc);
        
        // Add extra space before the noise generation checkbox
        gbc.insets = new Insets(20, 0, 10, 0);
        add(noiseGenCheckbox, gbc);

        // --- Time Limit Options ---
        // Create and style the time limit label
        JLabel timeLimitLabel = new JLabel("Time Limit:", SwingConstants.CENTER);
        timeLimitLabel.setFont(UITheme.FONT_PRIMARY);
        timeLimitLabel.setForeground(UITheme.TEXT_PRIMARY);
        // Add extra space before the time limit section
        gbc.insets = new Insets(20, 0, 5, 0);
        add(timeLimitLabel, gbc);

        // Initialize list to hold timer buttons
        timerButtons = new ArrayList<>();
        // Create time limit buttons
        StyledButton noLimitButton = new StyledButton("No Limit");
        StyledButton threeMinButton = new StyledButton("3 Minutes");
        StyledButton fiveMinButton = new StyledButton("5 Minutes");

        // Add buttons to the list
        timerButtons.add(noLimitButton);
        timerButtons.add(threeMinButton);
        timerButtons.add(fiveMinButton);

        // Set initial selection to "No Limit"
        updateTimerButtonSelection(noLimitButton);

        // Add action listeners to time limit buttons to update selected time and button appearance
        noLimitButton.addActionListener(e -> { selectedTimeLimitSeconds = -1; updateTimerButtonSelection(noLimitButton); });
        threeMinButton.addActionListener(e -> { selectedTimeLimitSeconds = 3 * 60; updateTimerButtonSelection(threeMinButton); });
        fiveMinButton.addActionListener(e -> { selectedTimeLimitSeconds = 5 * 60; updateTimerButtonSelection(fiveMinButton); });

        // Add time limit buttons to the panel
        add(noLimitButton, gbc);
        add(threeMinButton, gbc);
        add(fiveMinButton, gbc);
    }

    /**
     * Updates the visual selection state of the timer buttons.
     * Resets all timer buttons to their idle background color and highlights the selected button.
     *
     * @param selectedButton The StyledButton that was just selected and should be highlighted.
     */
    private void updateTimerButtonSelection(StyledButton selectedButton) {
        // Iterate through all timer buttons
        for (StyledButton button : timerButtons) {
            button.setBackground(UITheme.BUTTON_IDLE); // Reset all to idle state color
        }
        selectedButton.setBackground(UITheme.BUTTON_PRESSED); // Highlight the selected button
    }

    /**
     * Checks if the user wants to generate an island-style map using Perlin noise.
     * @return true if the noise generation checkbox is selected, false otherwise.
     */
    public boolean isNoiseGenSelected() {
        return noiseGenCheckbox.isSelected();
    }

    /**
     * Returns the selected time limit in seconds.
     * @return The time limit in seconds, or -1 if no limit is selected.
     */
    public int getSelectedTimeLimitSeconds() {
        return selectedTimeLimitSeconds;
    }

    // --- Action Listener Attachments ---
    /**
     * Adds an ActionListener to the Easy difficulty button.
     * @param listener The ActionListener to be added.
     */
    public void addEasyListener(ActionListener listener) { easyButton.addActionListener(listener); }
    /**
     * Adds an ActionListener to the Medium difficulty button.
     * @param listener The ActionListener to be added.
     */
    public void addMediumListener(ActionListener listener) { mediumButton.addActionListener(listener); }
    /**
     * Adds an ActionListener to the Hard difficulty button.
     * @param listener The ActionListener to be added.
     */
    public void addHardListener(ActionListener listener) { hardButton.addActionListener(listener); }
    /**
     * Adds an ActionListener to the Custom difficulty button.
     * @param listener The ActionListener to be added.
     */
    public void addCustomListener(ActionListener listener) { customButton.addActionListener(listener); }
}
