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

public class MainMenuPanel extends JPanel {
    public MainMenuPanel() {
        setLayout(new GridBagLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(10, 50, 10, 50);
        
        JLabel title = new JLabel("Minesweeper", SwingConstants.CENTER);
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_TITLE);
        
        add(title, gbc);
        add(new StyledButton("New Game"), gbc);
        add(new StyledButton("Daily Challenge"), gbc);
        add(new StyledButton("How to Play"), gbc);
        add(new StyledButton("About"), gbc);

        // KEY BINDING - The correct, modern way to handle keyboard shortcuts in Swing.
        // It works regardless of which component has focus within the window.
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke('t'), "timeSkip");
        im.put(KeyStroke.getKeyStroke('T'), "timeSkip");
        am.put("timeSkip", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = DailyChallengeGenerator.advanceDay();
                // Use a custom-styled JOptionPane
                JLabel messageLabel = new JLabel(msg);
                messageLabel.setFont(UITheme.FONT_PRIMARY);
                JOptionPane.showMessageDialog(MainMenuPanel.this, messageLabel, "Time Cheat", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    
    // --- Action Listener Attachments ---
    public void addNewGameListener(ActionListener listener) { ((JButton)getComponent(1)).addActionListener(listener); }
    public void addDailyChallengeListener(ActionListener listener) { ((JButton)getComponent(2)).addActionListener(listener); }
    public void addTutorialListener(ActionListener listener) { ((JButton)getComponent(3)).addActionListener(listener); }
    public void addAboutListener(ActionListener listener) { ((JButton)getComponent(4)).addActionListener(listener); }
}
