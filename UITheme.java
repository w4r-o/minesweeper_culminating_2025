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

public class UITheme {
    public static final Color BACKGROUND = new Color(45, 45, 50);
    public static final Color PANEL_BACKGROUND = new Color(55, 55, 60);
    public static final Color WATER = new Color(0, 50, 20);
    public static final Color BUTTON_IDLE = new Color(80, 80, 85);
    public static final Color BUTTON_HOVER = new Color(100, 100, 105);
    public static final Color BUTTON_PRESSED = new Color(70, 70, 75);
    public static final Color TEXT_PRIMARY = new Color(230, 230, 230);
    public static final Color TEXT_TITLE = Color.WHITE;
    public static final Color TEXT_DISABLED = new Color(150, 150, 150);

    public static final Font FONT_PRIMARY = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 48);
    public static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 14);
}
