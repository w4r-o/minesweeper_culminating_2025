/**
 * The UI panel on the left side of the game screen.
 * Displays player stats (Health, Score) and provides buttons for using abilities and returning to the menu.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * The UI panel on the left side of the game screen.
 * Displays player stats (Health, Score) and provides buttons for using abilities and returning to the menu.
 * It also includes an option to select different mine display skins.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public class SidePanel extends JPanel {
    /** Label to display player's current health. */
    private JLabel healthLabel;
    /** Label to display player's current score. */
    private JLabel scoreLabel;
    /** Button for using the Heal power-up. */
    private JButton healButton;
    /** Button for using the Reveal power-up. */
    private JButton revealButton;
    /** Button for using the Bomb power-up. */
    private JButton bombButton;
    /** Reference to the parent GameScreenPanel, used for repainting the game grid. */
    private GameScreenPanel gameScreenPanel;
    /** Custom button for opening the mine display selection dialog. */
    private StyledButton mineDisplayButton;

    /**
     * Constructs a new SidePanel.
     * Sets up the layout, displays player stats, ability buttons, and a mine display option.
     *
     * @param controller The main GameController, used for handling button actions.
     * @param gameScreenPanel The parent GameScreenPanel, used to access the GamePanel for repainting.
     */
    public SidePanel(GameController controller, GameScreenPanel gameScreenPanel) {
        this.gameScreenPanel = gameScreenPanel; // Store reference to GameScreenPanel
        setLayout(new GridBagLayout()); // Use GridBagLayout for flexible component arrangement
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        setBackground(UITheme.PANEL_BACKGROUND); // Set background color from theme
        setPreferredSize(new Dimension(220, 0)); // Set preferred width, height will be determined by layout

        // Initialize GridBagConstraints for layout control
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // Components fill their display area horizontally
        gbc.gridx = 0; // All components in this column
        gbc.weightx = 1.0; // Components expand horizontally
        gbc.insets = new Insets(5, 0, 5, 0); // Set insets (padding) for components

        // Create and add health and score labels
        healthLabel = createLabel("Health: 3/3");
        scoreLabel = createLabel("Score: 0");
        gbc.anchor = GridBagConstraints.NORTH; // Anchor components to the top
        gbc.gridy = 0; add(healthLabel, gbc);
        gbc.gridy = 1; add(scoreLabel, gbc);

        // Mine Display Option (Custom Button)
        JLabel mineDisplayLabel = createLabel("Mine Display:");
        mineDisplayButton = new StyledButton("Select Mine Skin"); // Initial placeholder text
        mineDisplayButton.addActionListener(e -> showMineDisplayDialog()); // Attach action listener to show dialog

        gbc.gridy = 2; gbc.insets = new Insets(10, 0, 5, 0); add(mineDisplayLabel, gbc); // Add label
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 15, 0); add(mineDisplayButton, gbc); // Add button

        // Separator line
        gbc.gridy = 4; gbc.insets = new Insets(15, 0, 15, 0); add(new JSeparator(), gbc);
        gbc.insets = new Insets(5, 0, 5, 0); // Reset insets for abilities section

        // Abilities section label and buttons
        gbc.gridy = 5; add(createLabel("Abilities"), gbc);
        healButton = new StyledButton("Use Heal (1)");
        revealButton = new StyledButton("Use Reveal (1)");
        bombButton = new StyledButton("Use Bomb (1)");
        gbc.gridy = 6; add(healButton, gbc);
        gbc.gridy = 7; add(revealButton, gbc);
        gbc.gridy = 8; add(bombButton, gbc);

        // Add a vertical strut/filler to push the "Return to Menu" button to the bottom
        gbc.gridy = 9; gbc.weighty = 1.0; add(new JPanel(){ { setOpaque(false); } }, gbc);

        // "Return to Menu" button at the bottom
        gbc.weighty = 0; gbc.gridy = 10;
        JButton menuButton = new StyledButton("Return to Menu");
        add(menuButton, gbc);

        // Attach action listeners to ability and menu buttons, delegating to the controller
        healButton.addActionListener(e -> controller.usePlayerHeal());
        revealButton.addActionListener(e -> controller.activateRevealMode());
        bombButton.addActionListener(e -> controller.activateBombMode());
        menuButton.addActionListener(e -> controller.returnToMainMenu());
        updateMineDisplayButtonText(); // Set initial text for the mine display button
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
     * Updates the text of the mine display button to reflect the currently selected mine skin.
     * Extracts the filename from the path if a custom skin is selected.
     */
    private void updateMineDisplayButtonText() {
        String currentPath = UITheme.currentMineImagePath;
        if ("default".equals(currentPath)) {
            mineDisplayButton.setText("Mine Skin: Default");
        } else {
            // Extract filename from path for display (e.g., "banana.gif" from "assets/banana.gif")
            String fileName = new File(currentPath).getName();
            mineDisplayButton.setText("Mine Skin: " + fileName);
        }
    }

    /**
     * Displays a dialog for selecting the mine display skin.
     * Provides options for default and various image-based mine skins.
     */
    private void showMineDisplayDialog() {
        // Create a new JDialog, making it modal and undecorated for custom styling
        JDialog mineDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Mine Skin", true);
        mineDialog.setLayout(new GridBagLayout());
        mineDialog.setBackground(UITheme.BACKGROUND);
        mineDialog.setUndecorated(true); // Remove window decorations for a custom look
        mineDialog.getRootPane().setBorder(BorderFactory.createLineBorder(UITheme.BUTTON_IDLE, 2)); // Custom border

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Each button on a new row
        gbc.fill = GridBagConstraints.HORIZONTAL; // Buttons fill horizontally
        gbc.insets = new Insets(5, 5, 5, 5); // Padding for buttons

        // Define available mine display options (text and image paths)
        String[] mineOptions = {
            "Default (Black Circle)",
            "assets/banana.gif",
            "assets/cantoloupe.gif",
            "assets/grape.gif",
            "assets/strawberry.gif",
            "assets/watemelon.gif"
        };

        // Create a button for each mine option
        for (String option : mineOptions) {
            StyledButton optionButton = new StyledButton(option);
            optionButton.addActionListener(e -> {
                String selectedOption = option;
                if ("Default (Black Circle)".equals(selectedOption)) {
                    UITheme.setMineDisplay("default"); // Set to default mine display
                } else {
                    UITheme.setMineDisplay(selectedOption); // Set to custom image path
                }
                // Repaint the game panel to show the new mine skin immediately
                if (gameScreenPanel != null) {
                    gameScreenPanel.getGamePanel().repaint();
                }
                updateMineDisplayButtonText(); // Update the main button's text to reflect selection
                mineDialog.dispose(); // Close the dialog after selection
            });
            mineDialog.add(optionButton, gbc); // Add button to dialog
        }

        // Add a close button to the dialog
        StyledButton closeButton = new StyledButton("Close");
        closeButton.addActionListener(e -> mineDialog.dispose()); // Close dialog on click
        gbc.insets = new Insets(10, 10, 10, 10); // Larger insets for close button
        mineDialog.add(closeButton, gbc);

        mineDialog.pack(); // Size the dialog to fit its components
        // Position the dialog directly below the mineDisplayButton for better UX
        Point buttonLocation = mineDisplayButton.getLocationOnScreen();
        mineDialog.setLocation(buttonLocation.x, buttonLocation.y + mineDisplayButton.getHeight());
        mineDialog.setVisible(true); // Make the dialog visible
    }
    
    /**
     * Updates the display of player stats (health, score) and power-up counts/button states.
     * This method is called whenever player data changes.
     *
     * @param player The current Player object whose stats are to be displayed.
     */
    public void update(Player player) {
        if (player != null) {
            // Update health and score labels
            healthLabel.setText(String.format("Health: %d/%d", player.getHealth(), Player.MAX_HEALTH));
            scoreLabel.setText("Score: " + player.getScore());
            // Update power-up button texts with current counts
            healButton.setText(String.format("Use Heal (%d)", player.getHealCount()));
            revealButton.setText(String.format("Use Reveal (%d)", player.getRevealCount()));
            bombButton.setText(String.format("Use Bomb (%d)", player.getBombCount()));
            // Enable/disable power-up buttons based on availability and health
            healButton.setEnabled(player.getHealCount() > 0 && player.getHealth() < Player.MAX_HEALTH);
            revealButton.setEnabled(player.getRevealCount() > 0);
            bombButton.setEnabled(player.getBombCount() > 0);
        }
        updateMineDisplayButtonText(); // Update the main button's text on player update
    }
}
