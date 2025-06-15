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
    /** The panel displayed at the top, showing timer and flag count. */
    private TopPanel topPanel;
    /** The panel displayed on the left side, showing player health and power-ups. */
    private SidePanel sidePanel;
    /** The central panel where the Minesweeper grid is drawn and interacted with. */
    private GamePanel gamePanel;

    /**
     * Constructs the main game screen by assembling its component panels.
     * This panel acts as a container for the TopPanel, SidePanel, and GamePanel,
     * arranging them using a BorderLayout.
     *
     * @param board The game board model to be displayed and interacted with.
     * @param player The player model for displaying health and power-up counts.
     * @param controller The main game controller, used for passing to sub-panels for action handling.
     */
    public GameScreenPanel(GameBoard board, Player player, GameController controller) {
        // Set the layout manager for this panel to BorderLayout
        setLayout(new BorderLayout());
        // Set the background color using the consistent UI theme
        setBackground(UITheme.BACKGROUND);

        // Initialize the sub-panels
        topPanel = new TopPanel();
        // Pass the controller and a reference to this GameScreenPanel to the SidePanel
        sidePanel = new SidePanel(controller, this);
        gamePanel = new GamePanel(board);
        
        // Initial UI state setup for the top and side panels
        // Update flag display: initially 0 flags placed, show total bomb count
        topPanel.updateFlags(0, board.getBombCount());
        // Update player stats display on the side panel
        sidePanel.update(player);

        // Add the sub-panels to their respective positions in the BorderLayout
        add(topPanel, BorderLayout.NORTH); // Top panel at the top
        add(sidePanel, BorderLayout.WEST); // Side panel on the left
        add(gamePanel, BorderLayout.CENTER); // Game grid panel in the center
    }

    // --- Getters for sub-panels ---
    /**
     * Returns the GamePanel instance, which displays the game grid.
     * @return The GamePanel.
     */
    public GamePanel getGamePanel() { return gamePanel; }
    /**
     * Returns the TopPanel instance, which displays the timer and flag count.
     * @return The TopPanel.
     */
    public TopPanel getTopPanel() { return topPanel; }
    /**
     * Returns the SidePanel instance, which displays player health and power-ups.
     * @return The SidePanel.
     */
    public SidePanel getSidePanel() { return sidePanel; }
}
