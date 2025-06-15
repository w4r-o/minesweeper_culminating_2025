/**
 * The central UI component where the game grid is drawn and interacted with.
 * It is responsible for all visual representation of the game board.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.Image;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * The central UI component where the game grid is drawn and interacted with.
 * It is responsible for all visual representation of the game board, including
 * cells, bombs, numbers, flags, power-up icons, and cheat mode indicators.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public class GamePanel extends JPanel {
    /** The GameBoard model that this panel visualizes. */
    private GameBoard board;
    /** Flag indicating if cheat mode is active (shows bomb locations). */
    private boolean cheatMode = false;
    /** Flag indicating if reveal power-up mode is active (shows 3x3 reveal preview). */
    private boolean revealModeActive = false;
    /** Flag indicating if bomb power-up mode is active (shows 3x3 destroy preview). */
    private boolean bombModeActive = false;
    /** The current mouse hover point, used for drawing power-up preview areas. */
    private Point mouseHoverPoint = null;
    /** A map storing colors for displaying adjacent bomb counts (numbers 1-8). */
    private final Map<Integer, Color> numberColors = new HashMap<>();

    /**
     * Constructs a new GamePanel.
     *
     * @param board The GameBoard instance to be displayed.
     */
    public GamePanel(GameBoard board) {
        this.board = board;
        setBackground(UITheme.BACKGROUND); // Set the background color of the panel
        // Initialize the map with specific colors for numbers 1 through 8
        numberColors.put(1, new Color(0, 0, 255)); // Blue
        numberColors.put(2, new Color(0, 128, 0)); // Green
        numberColors.put(3, new Color(255, 0, 0)); // Red
        numberColors.put(4, new Color(0, 0, 128)); // Dark Blue
        numberColors.put(5, new Color(128, 0, 0)); // Dark Red
        numberColors.put(6, new Color(0, 128, 128)); // Teal
        numberColors.put(7, new Color(0, 0, 0)); // Black
        numberColors.put(8, new Color(128, 128, 128)); // Gray
    }

    /**
     * Overrides the paintComponent method to draw the game board and its elements.
     * This method is called automatically by Swing when the component needs to be redrawn.
     *
     * @param g The Graphics object used for drawing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call superclass method to ensure proper rendering
        if (board == null) return; // Do nothing if the board is not set

        Graphics2D g2d = (Graphics2D) g;
        // Enable anti-aliasing for smoother drawing of shapes and text
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Calculate cell size to fit the board within the panel, maintaining aspect ratio
        int cellSize = Math.min(getWidth() / board.getCols(), getHeight() / board.getRows());
        int gridWidth = cellSize * board.getCols();
        int gridHeight = cellSize * board.getRows();
        // Calculate offsets to center the grid within the panel
        int xOffset = (getWidth() - gridWidth) / 2;
        int yOffset = (getHeight() - gridHeight) / 2;

        // Iterate through each cell in the board and draw it
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                drawCell(g2d, board.getCell(r, c), xOffset + c * cellSize, yOffset + r * cellSize, cellSize);
            }
        }
        
        // Draw power-up preview area if a power-up mode is active and mouse is hovering
        if (mouseHoverPoint != null) {
            Color previewColor = null;
            int radius = 1; // For a 3x3 area (center + 1 cell in each direction)
            if (revealModeActive || bombModeActive) {
                // Set preview color based on active mode (bomb: orange, reveal: cyan)
                previewColor = bombModeActive ? new Color(255, 100, 0, 100) : new Color(0, 255, 255, 100);
                g.setColor(previewColor);
                // Calculate the center cell of the hover area
                int c = (mouseHoverPoint.x - xOffset) / cellSize;
                int r = (mouseHoverPoint.y - yOffset) / cellSize;
                // Draw a semi-transparent rectangle representing the 3x3 area
                g.fillRect(xOffset + (c - radius) * cellSize, yOffset + (r - radius) * cellSize, cellSize * (2 * radius + 1), cellSize * (2 * radius + 1));
            }
        }
    }

    /**
     * Draws a single cell on the game board.
     * Handles different cell states: playable/water, destroyed, revealed/unrevealed,
     * bombs, numbers, flags, and power-up icons.
     *
     * @param g2d The Graphics2D object for drawing.
     * @param cell The Cell object to draw.
     * @param x The x-coordinate of the top-left corner of the cell.
     * @param y The y-coordinate of the top-left corner of the cell.
     * @param size The size (width and height) of the cell in pixels.
     */
    private void drawCell(Graphics2D g2d, Cell cell, int x, int y, int size) {
        // If the cell is not playable (e.g., water on noise maps), draw water background
        if (!cell.isPlayable()) {
            g2d.setColor(UITheme.WATER);
            g2d.fillRect(x, y, size, size);
            return; // Skip further drawing for non-playable cells
        }
        // If the cell is destroyed (e.g., by bomb power-up), draw a dark crater
        if (cell.isDestroyed()) {
            g2d.setColor(new Color(60, 60, 60)); // Dark gray for destroyed cells
            g2d.fillRect(x, y, size, size);
            g2d.setColor(Color.BLACK); // Black border
            g2d.drawRect(x, y, size, size);
            return; // Skip further drawing for destroyed cells
        }

        // Determine background color based on cell state (revealed or unrevealed)
        if (cell.isRevealed()) {
            // Special handling for bomb cells if custom mine icon is not used (pink background)
            if (cell.isBomb() && !UITheme.useDefaultMineIcon) {
                g2d.setColor(new Color(220, 220, 220)); // Normal revealed background for custom mine
            } else if (cell.isBomb()) {
                g2d.setColor(new Color(255, 180, 180)); // Pink background for default mine
            } else {
                g2d.setColor(new Color(220, 220, 220)); // Light gray for normal revealed cells
            }
        } else {
            g2d.setColor(new Color(170, 170, 170)); // Medium gray for unrevealed cells
        }
        g2d.fillRect(x, y, size, size); // Fill cell background
        g2d.setColor(Color.DARK_GRAY); // Set border color
        g2d.drawRect(x, y, size, size); // Draw cell border
        
        // Set font for numbers and icons
        g2d.setFont(new Font("Segoe UI", Font.BOLD, size * 2 / 3));

        // Draw content for revealed cells
        if (cell.isRevealed()) {
            if (cell.isBomb()) {
                // Draw bomb icon
                if (UITheme.useDefaultMineIcon || UITheme.currentMineIcon == null) {
                    g2d.setColor(Color.BLACK);
                    g2d.fillOval(x + size / 4, y + size / 4, size / 2, size / 2); // Draw a black circle for default mine
                } else {
                    Image img = UITheme.currentMineIcon.getImage();
                    // Scale image to fit exactly within the cell
                    Image scaledImg = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    g2d.drawImage(scaledImg, x, y, null); // Draw custom mine image
                }
            }
            // Draw power-up icon if present on revealed cell
            else if (cell.getPowerUpTypeOnReveal() != null) {
                String icon = "";
                Color iconColor = Color.BLACK;
                // Determine icon and color based on power-up type
                switch (cell.getPowerUpTypeOnReveal()) {
                    case HEAL: icon = "\u2665"; iconColor = Color.RED; break; // Heart symbol for Heal
                    case REVEAL: icon = "\u263C"; iconColor = Color.BLUE; break; // Sun symbol for Reveal
                    case BOMB: icon = "\u25CF"; iconColor = Color.BLACK; break; // Circle symbol for Bomb
                }
                g2d.setColor(iconColor);
                drawCenteredString(g2d, icon, new Rectangle(x, y, size, size)); // Draw icon centered
            }
            // Draw adjacent bomb count if greater than 0
            else if (cell.getAdjacentBombs() > 0) {
                g2d.setColor(numberColors.getOrDefault(cell.getAdjacentBombs(), Color.BLACK)); // Get color for the number
                drawCenteredString(g2d, String.valueOf(cell.getAdjacentBombs()), new Rectangle(x, y, size, size)); // Draw number centered
            }
        } else {
            // Draw flag if cell is unrevealed and flagged
            if (cell.isFlagged()) {
                g2d.setColor(Color.RED);
                drawCenteredString(g2d, "P", new Rectangle(x, y, size, size)); // Draw 'P' for flag (placeholder)
            }
        }
        // Draw cheat mode 'B' on top of everything else if cheat mode is active and cell is a bomb
        if (cheatMode && cell.isBomb()) {
            g2d.setColor(new Color(255, 0, 0, 150)); // Semi-transparent red
            drawCenteredString(g2d, "B", new Rectangle(x, y, size, size)); // Draw 'B' for bomb
        }
    }
    
    /**
     * Draws a string centered within a given rectangle.
     * @param g The Graphics object to draw on.
     * @param text The string to draw.
     * @param rect The rectangle within which to center the string.
     */
    private void drawCenteredString(Graphics g, String text, Rectangle rect) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        // Calculate the x coordinate for centering text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Calculate the y coordinate for centering text (adjusting for font ascent)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.drawString(text, x, y); // Draw the string
    }

    /**
     * Sets the bomb power-up mode active or inactive.
     * When active, a preview area is shown on hover.
     * @param active True to activate, false to deactivate.
     */
    public void setBombMode(boolean active) {
        this.bombModeActive = active;
        if (!active) mouseHoverPoint = null; // Clear hover point when mode is deactivated
        repaint(); // Redraw the panel to reflect mode change
    }

    /**
     * Sets cheat mode active or inactive.
     * When active, bomb locations are revealed.
     * @param cheatMode True to activate, false to deactivate.
     */
    public void setCheatMode(boolean cheatMode) {
        this.cheatMode = cheatMode;
        repaint(); // Redraw the panel to show/hide bomb indicators
    }

    /**
     * Sets the reveal power-up mode active or inactive.
     * When active, a preview area is shown on hover.
     * @param active True to activate, false to deactivate.
     */
    public void setRevealMode(boolean active) {
        this.revealModeActive = active;
        if (!active) mouseHoverPoint = null; // Clear hover point when mode is deactivated
        repaint(); // Redraw the panel to reflect mode change
    }

    /**
     * Sets the current mouse hover point.
     * Used to draw power-up preview areas.
     * @param p The Point representing the mouse coordinates.
     */
    public void setMouseHoverPoint(Point p) {
        this.mouseHoverPoint = p;
        repaint(); // Redraw the panel to update hover effect
    }

    /**
     * Adds a MouseListener to the game grid.
     * @param listener The MouseListener to add.
     */
    public void addGridMouseListener(MouseListener listener) {
        addMouseListener(listener);
    }

    /**
     * Adds a MouseMotionListener to the game grid.
     * @param listener The MouseMotionListener to add.
     */
    public void addGridMouseMotionListener(MouseMotionListener listener) {
        addMouseMotionListener(listener);
    }
}
