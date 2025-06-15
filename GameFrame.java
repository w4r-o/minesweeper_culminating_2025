/**
 * The main application window (JFrame). It holds all other UI panels.
 * It is configured to start maximized to fill the screen.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    /** The CardLayout manager used to switch between different panels. */
    private CardLayout cardLayout;
    /** The main JPanel that holds all other panels and uses CardLayout. */
    private JPanel mainPanel;

    /**
     * Constructs the main game frame.
     * Sets up the window title, default close operation, and maximizes the window.
     */
    public GameFrame() {
        setTitle("Minesweeper"); // Set the title of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure the application exits when the frame is closed
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the window to fill the screen
        setMinimumSize(new Dimension(800, 600)); // Set a minimum size for the window

        cardLayout = new CardLayout(); // Initialize the CardLayout
        mainPanel = new JPanel(cardLayout); // Create the main panel with the CardLayout
        add(mainPanel); // Add the main panel to the frame
    }

    /**
     * Adds a new JPanel to the main panel, making it available for display.
     * Each panel is associated with a unique name for switching.
     *
     * @param panel The JPanel to add.
     * @param name The unique name to associate with this panel.
     */
    public void addPanel(JPanel panel, String name) {
        mainPanel.add(panel, name);
    }

    /**
     * Displays the panel associated with the given name.
     * This method switches the visible panel in the main frame.
     *
     * @param name The name of the panel to show.
     */
    public void showPanel(String name) {
        cardLayout.show(mainPanel, name); // Show the panel using CardLayout
        revalidate(); // Revalidate the container to ensure proper layout
        repaint(); // Repaint the container to update the display
    }
}
