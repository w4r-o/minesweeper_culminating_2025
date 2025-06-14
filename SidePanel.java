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

public class SidePanel extends JPanel {
    private JLabel healthLabel, scoreLabel;
    private JButton healButton, revealButton, bombButton;

    public SidePanel(GameController controller) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(UITheme.PANEL_BACKGROUND);
        setPreferredSize(new Dimension(180, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridx = 0; gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);

        healthLabel = createLabel("Health: 3/3"); scoreLabel = createLabel("Score: 0");
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridy = 0; add(healthLabel, gbc);
        gbc.gridy = 1; add(scoreLabel, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(15, 0, 15, 0); add(new JSeparator(), gbc);
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridy = 3; add(createLabel("Abilities"), gbc);
        healButton = new StyledButton("Use Heal (1)"); revealButton = new StyledButton("Use Reveal (1)"); bombButton = new StyledButton("Use Bomb (1)");
        gbc.gridy = 4; add(healButton, gbc);
        gbc.gridy = 5; add(revealButton, gbc);
        gbc.gridy = 6; add(bombButton, gbc);

        gbc.gridy = 7; gbc.weighty = 1.0; add(new JPanel(){ { setOpaque(false); } }, gbc);

        gbc.weighty = 0; gbc.gridy = 8;
        JButton menuButton = new StyledButton("Return to Menu");
        add(menuButton, gbc);

        healButton.addActionListener(e -> controller.usePlayerHeal());
        revealButton.addActionListener(e -> controller.activateRevealMode());
        bombButton.addActionListener(e -> controller.activateBombMode());
        menuButton.addActionListener(e -> controller.returnToMainMenu());
    }
    
    private JLabel createLabel(String text) { JLabel label = new JLabel(text); label.setForeground(UITheme.TEXT_PRIMARY); label.setFont(UITheme.FONT_PRIMARY); return label; }
    
    public void update(Player player) {
        if (player != null) {
            healthLabel.setText(String.format("Health: %d/%d", player.getHealth(), Player.MAX_HEALTH));
            scoreLabel.setText("Score: " + player.getScore());
            healButton.setText(String.format("Use Heal (%d)", player.getHealCount()));
            revealButton.setText(String.format("Use Reveal (%d)", player.getRevealCount()));
            bombButton.setText(String.format("Use Bomb (%d)", player.getBombCount()));
            healButton.setEnabled(player.getHealCount() > 0 && player.getHealth() < Player.MAX_HEALTH);
            revealButton.setEnabled(player.getRevealCount() > 0);
            bombButton.setEnabled(player.getBombCount() > 0);
        }
    }
}
