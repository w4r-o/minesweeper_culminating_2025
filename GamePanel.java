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
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel {
    private GameBoard board;
    private boolean cheatMode=false, revealModeActive=false, bombModeActive=false;
    private Point mouseHoverPoint = null;
    private final Map<Integer, Color> numberColors = new HashMap<>();

    public GamePanel(GameBoard board) {
        this.board = board;
        setBackground(UITheme.BACKGROUND);
        numberColors.put(1, new Color(0, 0, 255)); numberColors.put(2, new Color(0, 128, 0)); numberColors.put(3, new Color(255, 0, 0));
        numberColors.put(4, new Color(0, 0, 128)); numberColors.put(5, new Color(128, 0, 0)); numberColors.put(6, new Color(0, 128, 128));
        numberColors.put(7, new Color(0, 0, 0)); numberColors.put(8, new Color(128, 128, 128));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cellSize = Math.min(getWidth() / board.getCols(), getHeight() / board.getRows());
        int gridWidth = cellSize * board.getCols(); int gridHeight = cellSize * board.getRows();
        int xOffset = (getWidth() - gridWidth) / 2; int yOffset = (getHeight() - gridHeight) / 2;

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                drawCell(g2d, board.getCell(r, c), xOffset + c * cellSize, yOffset + r * cellSize, cellSize);
            }
        }
        
        if (mouseHoverPoint != null) {
            Color previewColor = null; int radius = 1;
            if (revealModeActive || bombModeActive) {
                previewColor = bombModeActive ? new Color(255, 100, 0, 100) : new Color(0, 255, 255, 100);
                g.setColor(previewColor);
                int c = (mouseHoverPoint.x - xOffset) / cellSize; int r = (mouseHoverPoint.y - yOffset) / cellSize;
                g.fillRect(xOffset + (c - radius) * cellSize, yOffset + (r - radius) * cellSize, cellSize * (2 * radius + 1), cellSize * (2 * radius + 1));
            }
        }
    }

    private void drawCell(Graphics2D g2d, Cell cell, int x, int y, int size) {
        if (!cell.isPlayable()) { g2d.setColor(UITheme.WATER); g2d.fillRect(x,y,size,size); return; }
        if (cell.isDestroyed()) { g2d.setColor(new Color(60, 60, 60)); g2d.fillRect(x, y, size, size); g2d.setColor(Color.BLACK); g2d.drawRect(x,y,size,size); return; }
        if (cell.isRevealed()) { g2d.setColor(cell.isBomb() ? new Color(255, 180, 180) : new Color(220, 220, 220)); } else { g2d.setColor(new Color(170, 170, 170)); }
        g2d.fillRect(x, y, size, size); g2d.setColor(Color.DARK_GRAY); g2d.drawRect(x, y, size, size);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, size * 2 / 3));
        if (cell.isRevealed()) {
            if (cell.isBomb()) { g2d.setColor(Color.BLACK); g2d.fillOval(x + size / 4, y + size / 4, size / 2, size / 2); }
            else if (cell.getPowerUpTypeOnReveal() != null) { String icon = ""; Color iconColor = Color.BLACK; switch (cell.getPowerUpTypeOnReveal()) { case HEAL: icon = "\u2665"; iconColor = Color.RED; break; case REVEAL: icon = "\u263C"; iconColor = Color.BLUE; break; case BOMB: icon = "\u25CF"; iconColor = Color.BLACK; break; } g2d.setColor(iconColor); drawCenteredString(g2d, icon, new Rectangle(x, y, size, size)); }
            else if (cell.getAdjacentBombs() > 0) { g2d.setColor(numberColors.getOrDefault(cell.getAdjacentBombs(), Color.BLACK)); drawCenteredString(g2d, String.valueOf(cell.getAdjacentBombs()), new Rectangle(x, y, size, size)); }
        } else {
            if (cell.isFlagged()) { g2d.setColor(Color.RED); drawCenteredString(g2d, "P", new Rectangle(x, y, size, size)); }
            if (cheatMode && cell.isBomb()) { g2d.setColor(new Color(255, 0, 0, 150)); drawCenteredString(g2d, "B", new Rectangle(x, y, size, size)); }
        }
    }
    
    private void drawCenteredString(Graphics g, String text, Rectangle rect) { FontMetrics metrics = g.getFontMetrics(g.getFont()); int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2; int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent(); g.drawString(text, x, y); }
    public void setBombMode(boolean active) { this.bombModeActive = active; if (!active) mouseHoverPoint = null; repaint(); } public void setCheatMode(boolean cheatMode) { this.cheatMode = cheatMode; repaint(); } public void setRevealMode(boolean active) { this.revealModeActive = active; if (!active) mouseHoverPoint = null; repaint(); }
    public void setMouseHoverPoint(Point p) { this.mouseHoverPoint = p; repaint(); } public void addGridMouseListener(MouseListener listener) { addMouseListener(listener); } public void addGridMouseMotionListener(MouseMotionListener listener) { addMouseMotionListener(listener); }
}
