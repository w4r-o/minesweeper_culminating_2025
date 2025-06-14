/**
 * The main container panel for the in-game screen.
 * It uses a BorderLayout to organize the top panel (timer/flags), the side panel (abilities),
 * and the central game grid panel.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
import javax.swing.*;
import java.awt.*;

public class GameScreenPanel extends JPanel {
    private TopPanel topPanel;
    private SidePanel sidePanel;
    private GamePanel gamePanel;

    /**
     * Constructs the main game screen by assembling its component panels.
     * @param board The game board model to be displayed.
     * @param player The player model for displaying stats.
     * @param controller The main controller for handling actions.
     */
    public GameScreenPanel(GameBoard board, Player player, GameController controller) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND); // Use consistent theme background

        topPanel = new TopPanel();
        sidePanel = new SidePanel(controller);
        gamePanel = new GamePanel(board);
        
        // Initial UI state setup
        topPanel.updateFlags(0, board.getBombCount());
        sidePanel.update(player);

        add(topPanel, BorderLayout.NORTH);
        add(sidePanel, BorderLayout.WEST);
        add(gamePanel, BorderLayout.CENTER);
    }

    // --- Getters for sub-panels ---
    public GamePanel getGamePanel() { return gamePanel; }
    public TopPanel getTopPanel() { return topPanel; }
    public SidePanel getSidePanel() { return sidePanel; }
}
