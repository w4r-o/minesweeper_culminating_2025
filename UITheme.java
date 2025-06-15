/**
 * A central place for all UI colors and fonts for a consistent, modern theme.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * A central place for all UI colors and fonts for a consistent, modern theme.
 * This class provides static final fields for various color schemes and fonts
 * used throughout the Minesweeper application, ensuring a unified look and feel.
 * It also manages the selection and loading of custom mine display icons.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public class UITheme {
    // --- Color Palette ---
    /** The primary background color for the application. */
    public static final Color BACKGROUND = new Color(45, 45, 50);
    /** The background color for panels and containers. */
    public static final Color PANEL_BACKGROUND = new Color(55, 55, 60);
    /** Color used for non-playable 'water' cells on noise-generated maps. */
    public static final Color WATER = new Color(0, 50, 20);
    /** Default background color for buttons when idle. */
    public static final Color BUTTON_IDLE = new Color(80, 80, 85);
    /** Background color for buttons when hovered over. */
    public static final Color BUTTON_HOVER = new Color(100, 100, 105);
    /** Background color for buttons when pressed. */
    public static final Color BUTTON_PRESSED = new Color(70, 70, 75);
    /** Primary text color for most UI elements. */
    public static final Color TEXT_PRIMARY = new Color(230, 230, 230);
    /** Text color for titles and prominent headings. */
    public static final Color TEXT_TITLE = Color.WHITE;
    /** Text color for disabled UI elements. */
    public static final Color TEXT_DISABLED = new Color(150, 150, 150);

    // --- Mine Display Settings ---
    /** Stores the path to the currently selected custom mine image, or "default" for the default icon. */
    public static String currentMineImagePath = "default";
    /** The ImageIcon object for the currently selected custom mine display. Null if using default. */
    public static ImageIcon currentMineIcon;
    /** Flag indicating whether the default black circle mine icon should be used. */
    public static boolean useDefaultMineIcon = true;

    /**
     * Sets the mine display option.
     * This method loads a custom image for mines or reverts to the default black circle.
     * It attempts to load images from the classpath first (for JARs) and then from the file system.
     *
     * @param option "default" for the black circle, or the file path (e.g., "assets/banana.gif") to a GIF asset.
     */
    public static void setMineDisplay(String option) {
        if ("default".equals(option)) {
            useDefaultMineIcon = true; // Use default icon
            currentMineIcon = null; // Clear custom icon
            currentMineImagePath = "default"; // Reset path
        } else {
            useDefaultMineIcon = false; // Use custom icon
            currentMineImagePath = option; // Store custom image path
            try {
                BufferedImage img = null;
                // Attempt to load image as a resource stream (robust for packaged JARs)
                URL imageUrl = UITheme.class.getClassLoader().getResource(option);
                if (imageUrl != null) {
                    img = ImageIO.read(imageUrl);
                } else {
                    // Fallback: try loading from direct file path (useful during IDE development)
                    File imageFile = new File(option);
                    if (imageFile.exists()) {
                        img = ImageIO.read(imageFile);
                    } else {
                        // If image not found, print error and revert to default
                        System.err.println("Image not found (classpath or file system): " + option);
                        useDefaultMineIcon = true;
                    }
                }

                if (img != null) {
                    currentMineIcon = new ImageIcon(img); // Create ImageIcon from loaded image
                } else {
                    currentMineIcon = null; // No image loaded
                    useDefaultMineIcon = true; // Fallback to default
                }

            } catch (IOException e) {
                // Catch specific IOException during image reading
                System.err.println("Error loading image: " + option + " - " + e.getMessage());
                currentMineIcon = null; // Clear icon on error
                useDefaultMineIcon = true; // Fallback to default on error
            } catch (Exception e) {
                // Catch any other general exceptions during image loading
                e.printStackTrace();
                currentMineIcon = null; // Clear icon on error
                useDefaultMineIcon = true; // Fallback to default on error
            }
        }
    }

    // --- Font Definitions ---
    /** Primary font used for most text in the application. */
    public static final Font FONT_PRIMARY = new Font("Segoe UI", Font.BOLD, 14);
    /** Font used for main titles and prominent text. */
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 48);
    /** Monospaced font, potentially for debug information or specific text displays. */
    public static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 14);
}
