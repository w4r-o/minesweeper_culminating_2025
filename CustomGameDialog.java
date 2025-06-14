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
    private JTextField rowsField, colsField, bombsField;
    private boolean isConfirmed = false;

    /**
     * Constructs the custom game settings dialog.
     * @param owner The Frame from which the dialog is displayed.
     * @param useNoiseGen If true, the bomb count field is hidden.
     */
    public CustomGameDialog(Frame owner, boolean useNoiseGen) {
        super(owner, "Custom Game Settings", true); // true for modal

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UITheme.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel rowsLabel = new JLabel("Rows (9-24):");
        JLabel colsLabel = new JLabel("Columns (9-40):");
        JLabel bombsLabel = new JLabel("Bombs:");
        rowsLabel.setForeground(UITheme.TEXT_PRIMARY);
        colsLabel.setForeground(UITheme.TEXT_PRIMARY);
        bombsLabel.setForeground(UITheme.TEXT_PRIMARY);

        rowsField = new JTextField("16", 5);
        colsField = new JTextField("30", 5);
        bombsField = new JTextField("99", 5);

        gbc.gridx = 0; gbc.gridy = 0; mainPanel.add(rowsLabel, gbc);
        gbc.gridx = 1; mainPanel.add(rowsField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; mainPanel.add(colsLabel, gbc);
        gbc.gridx = 1; mainPanel.add(colsField, gbc);

        if (!useNoiseGen) {
            gbc.gridx = 0; gbc.gridy = 2; mainPanel.add(bombsLabel, gbc);
            gbc.gridx = 1; mainPanel.add(bombsField, gbc);
        }

        StyledButton okButton = new StyledButton("Start Game");
        StyledButton cancelButton = new StyledButton("Cancel");
        okButton.addActionListener(e -> { isConfirmed = true; dispose(); });
        cancelButton.addActionListener(e -> { isConfirmed = false; dispose(); });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(buttonPanel, gbc);
        
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(owner);
    }

    // --- Getters for the controller to retrieve user input ---
    public boolean isConfirmed() { return isConfirmed; }
    public int getRows() { return Integer.parseInt(rowsField.getText()); }
    public int getCols() { return Integer.parseInt(colsField.getText()); }
    public int getBombs() { return Integer.parseInt(bombsField.getText()); }
}
