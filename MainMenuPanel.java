/**
 * The first UI panel the user sees, containing the main menu options.
 * It uses Key Bindings to reliably listen for the 'T' key cheat.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The first UI panel the user sees, containing the main menu options.
 * It uses Key Bindings to reliably listen for the 'T' key cheat, which advances the daily challenge date.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public class MainMenuPanel extends JPanel {

    /**
     * Constructs a new MainMenuPanel.
     * Sets up the layout, background, title, and main menu buttons.
     * Also configures a key binding for a 'time skip' cheat.
     */
    public MainMenuPanel() {
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
        gbc.insets = new Insets(10, 50, 10, 50);
        
        // Create and style the main title label
        JLabel title = new JLabel("Minesweeper", SwingConstants.CENTER);
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_TITLE);
        
        // Add the title and main menu buttons to the panel
        add(title, gbc);
        add(new StyledButton("New Game"), gbc);
        add(new StyledButton("Daily Challenge"), gbc);
        add(new StyledButton("How to Play"), gbc);
        add(new StyledButton("About"), gbc);

        // KEY BINDING - The correct, modern way to handle keyboard shortcuts in Swing.
        // It works regardless of which component has focus within the window.
        // Get the InputMap and ActionMap for the panel when it's in a focused window
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        // Bind the 't' and 'T' keys to the "timeSkip" action
        im.put(KeyStroke.getKeyStroke('t'), "timeSkip");
        im.put(KeyStroke.getKeyStroke('T'), "timeSkip");
        // Define the action to be performed when "timeSkip" is triggered
        am.put("timeSkip", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Advance the daily challenge date using the generator
                String msg = DailyChallengeGenerator.advanceDay();
                // Use a custom-styled JOptionPane to display the message
                JLabel messageLabel = new JLabel(msg);
                messageLabel.setFont(UITheme.FONT_PRIMARY);
                JOptionPane.showMessageDialog(MainMenuPanel.this, messageLabel, "Time Cheat", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    
    // --- Action Listener Attachments ---
    /**
     * Attaches an ActionListener to the "New Game" button.
     * @param listener The ActionListener to be added.
     */
    public void addNewGameListener(ActionListener listener) {
        // Buttons are added sequentially, so getComponent(1) refers to "New Game"
        ((JButton)getComponent(1)).addActionListener(listener);
    }

    /**
     * Attaches an ActionListener to the "Daily Challenge" button.
     * @param listener The ActionListener to be added.
     */
    public void addDailyChallengeListener(ActionListener listener) {
        // getComponent(2) refers to "Daily Challenge"
        ((JButton)getComponent(2)).addActionListener(listener);
    }

    /**
     * Attaches an ActionListener to the "How to Play" (Tutorial) button.
     * @param listener The ActionListener to be added.
     */
    public void addTutorialListener(ActionListener listener) {
        // getComponent(3) refers to "How to Play"
        ((JButton)getComponent(3)).addActionListener(listener);
    }

    /**
     * Attaches an ActionListener to the "About" button.
     * @param listener The ActionListener to be added.
     */
    public void addAboutListener(ActionListener listener) {
        // getComponent(4) refers to "About"
        ((JButton)getComponent(4)).addActionListener(listener);
    }
}
