/**
 * A fully custom JDialog for entering custom game settings.
 * It replaces the default JOptionPane to match the modern application theme.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
import javax.swing.*;
import java.awt.*;

public class CustomGameDialog extends JDialog {
    /** Text field for entering the number of rows. */
    private JTextField rowsField;
    /** Text field for entering the number of columns. */
    private JTextField colsField;
    /** Text field for entering the number of bombs. */
    private JTextField bombsField;
    /** Flag to indicate if the user confirmed the settings (clicked "Start Game"). */
    private boolean isConfirmed = false;

    /**
     * Constructs the custom game settings dialog.
     * This dialog allows the user to specify custom dimensions (rows, columns)
     * and bomb count for a new game. The bomb count field can be hidden
     * if noise-based generation is used.
     *
     * @param owner The Frame from which the dialog is displayed, used for modal behavior and positioning.
     * @param useNoiseGen If true, the bomb count field is hidden, as bomb placement is handled by noise generation.
     */
    public CustomGameDialog(Frame owner, boolean useNoiseGen) {
        // Call the superclass constructor to create a modal dialog with a title
        super(owner, "Custom Game Settings", true); // true for modal

        // Create the main panel with a GridBagLayout for flexible component arrangement
        JPanel mainPanel = new JPanel(new GridBagLayout());
        // Set the background color of the main panel
        mainPanel.setBackground(UITheme.BACKGROUND);
        // Add an empty border for padding around the content
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // Initialize GridBagConstraints for layout control
        GridBagConstraints gbc = new GridBagConstraints();
        // Set insets (padding) for components
        gbc.insets = new Insets(5, 5, 5, 5);
        // Make components fill their display area horizontally
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create labels for input fields
        JLabel rowsLabel = new JLabel("Rows (9-24):");
        JLabel colsLabel = new JLabel("Columns (9-40):");
        JLabel bombsLabel = new JLabel("Bombs:");
        // Set foreground color for labels
        rowsLabel.setForeground(UITheme.TEXT_PRIMARY);
        colsLabel.setForeground(UITheme.TEXT_PRIMARY);
        bombsLabel.setForeground(UITheme.TEXT_PRIMARY);

        // Initialize text fields with default values and preferred column width
        rowsField = new JTextField("16", 5);
        colsField = new JTextField("30", 5);
        bombsField = new JTextField("99", 5);

        // Add rows label and text field to the main panel
        gbc.gridx = 0; gbc.gridy = 0; mainPanel.add(rowsLabel, gbc);
        gbc.gridx = 1; mainPanel.add(rowsField, gbc);
        // Add columns label and text field to the main panel
        gbc.gridx = 0; gbc.gridy = 1; mainPanel.add(colsLabel, gbc);
        gbc.gridx = 1; mainPanel.add(colsField, gbc);

        // Conditionally add bombs label and text field based on noise generation setting
        if (!useNoiseGen) {
            gbc.gridx = 0; gbc.gridy = 2; mainPanel.add(bombsLabel, gbc);
            gbc.gridx = 1; mainPanel.add(bombsField, gbc);
        }

        // Create "Start Game" and "Cancel" buttons using StyledButton for consistent theme
        StyledButton okButton = new StyledButton("Start Game");
        StyledButton cancelButton = new StyledButton("Cancel");
        // Add action listeners to buttons to set confirmation status and dispose the dialog
        okButton.addActionListener(e -> { isConfirmed = true; dispose(); });
        cancelButton.addActionListener(e -> { isConfirmed = false; dispose(); });

        // Create a panel for buttons with right alignment
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // Make button panel transparent
        buttonPanel.setOpaque(false);
        // Add buttons to the button panel
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        
        // Add the button panel to the main panel, spanning two columns and aligned to the east
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(buttonPanel, gbc);
        
        // Set the main panel as the content pane of the dialog
        setContentPane(mainPanel);
        // Pack the dialog to fit its components
        pack();
        // Center the dialog relative to its owner frame
        setLocationRelativeTo(owner);
    }

    // --- Getters for the controller to retrieve user input ---
    /**
     * Returns whether the user confirmed the custom game settings.
     * @return True if "Start Game" was clicked, false otherwise.
     */
    public boolean isConfirmed() { return isConfirmed; }
    /**
     * Retrieves the number of rows entered by the user.
     * @return The parsed integer value for rows.
     */
    public int getRows() { return Integer.parseInt(rowsField.getText()); }
    /**
     * Retrieves the number of columns entered by the user.
     * @return The parsed integer value for columns.
     */
    public int getCols() { return Integer.parseInt(colsField.getText()); }
    /**
     * Retrieves the number of bombs entered by the user.
     * @return The parsed integer value for bombs.
     */
    public int getBombs() { return Integer.parseInt(bombsField.getText()); }
}
