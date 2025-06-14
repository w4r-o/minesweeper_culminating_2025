/**
 * The main entry point for the Minesweeper game application.
 * This class is responsible for initializing and running the GameController on the AWT Event Dispatch Thread.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
import javax.swing.SwingUtilities;

public class Main {
    /**
     * The main method that starts the application.
     * It uses SwingUtilities.invokeLater to ensure that the GUI is created and updated on the Event Dispatch Thread (EDT),
     * which is crucial for thread safety in Swing applications.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            controller.start();
        });
    }
}
