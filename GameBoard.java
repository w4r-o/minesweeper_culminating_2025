/**
 * The model for the game board.
 * Manages the grid of cells, bomb placement, reveal logic, and win/loss conditions.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
import java.util.Random;

public class GameBoard {
    // --- Member Variables ---
    private final Cell[][] grid;
    private final int rows, cols;
    private int bombCount, revealedCellsCount, flagsPlaced;
    private int playableCellCount;
    private GameState gameState;
    private boolean firstClick = true;
    private Difficulty difficulty;

    /** Enum representing the current state of the game. */
    public enum GameState { PLAYING, WIN, LOSS }

    /**
     * Constructor for standard rectangular boards.
     * @param rows The number of rows.
     * @param cols The number of columns.
     * @param bombCount The number of bombs.
     * @param difficulty The difficulty level, used for balancing.
     */
    public GameBoard(int rows, int cols, int bombCount, Difficulty difficulty) {
        this(rows, cols, bombCount, null, rows * cols, difficulty);
    }
    
    /**
     * Main constructor for all board types, including irregular noise-based maps.
     * @param rows The total number of rows in the grid.
     * @param cols The total number of columns in the grid.
     * @param bombCount The number of bombs.
     * @param playableMap A 2D boolean array where 'true' indicates a playable (land) cell. Can be null for rectangular maps.
     * @param playableCellCount The total number of playable cells.
     * @param difficulty The difficulty level.
     */
    public GameBoard(int rows, int cols, int bombCount, boolean[][] playableMap, int playableCellCount, Difficulty difficulty) {
        this.rows = rows; this.cols = cols; this.bombCount = bombCount;
        this.grid = new Cell[rows][cols]; this.playableCellCount = playableCellCount;
        this.difficulty = difficulty;
        reset(playableMap);
    }
    
    /**
     * Initializes the grid, setting cells to playable or non-playable based on the map.
     * @param playableMap The map defining the playable area.
     */
    private void reset(boolean[][] playableMap) {
        gameState = GameState.PLAYING; revealedCellsCount = 0; flagsPlaced = 0; firstClick = true;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell();
                if (playableMap != null && !playableMap[r][c]) {
                    grid[r][c].setPlayable(false);
                }
            }
        }
    }

    /**
     * Sets up the board after the first click, placing bombs and power-ups.
     * @param safeRow The row of the first click, guaranteed to be safe.
     * @param safeCol The column of the first click.
     */
    public void setupBoard(int safeRow, int safeCol) {
        placeBombs(safeRow, safeCol);
        placePowerUps();
        calculateAllAdjacentBombs();
        firstClick = false;
    }
    
    /**
     * Randomly places bombs on playable cells, avoiding the first-click location.
     * @param safeRow The safe row.
     * @param safeCol The safe column.
     */
    private void placeBombs(int safeRow, int safeCol) {
        Random rand = new Random();
        int bombsPlaced = 0;
        // Failsafe to prevent an infinite loop on very small/dense custom maps
        if (playableCellCount <= bombCount) {
            this.bombCount = Math.max(1, playableCellCount - 1);
        }
        while (bombsPlaced < bombCount) {
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);
            if (grid[r][c].isPlayable() && !grid[r][c].isBomb() && !(r == safeRow && c == safeCol)) {
                grid[r][c].setBomb(true);
                bombsPlaced++;
            }
        }
    }

    /** Places power-ups randomly on non-bomb cells. */
    private void placePowerUps() {
        Random rand = new Random();
        int powerUpCount = 2 + (playableCellCount) / 75;
        for (int i = 0; i < powerUpCount; i++) {
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);
            if (grid[r][c].isPlayable() && !grid[r][c].isBomb() && grid[r][c].getPowerUp() == null) {
                int powerUpType = rand.nextInt(3);
                if (powerUpType == 0) grid[r][c].setPowerUp(new HealPowerUp());
                else if (powerUpType == 1) grid[r][c].setPowerUp(new RevealPowerUp());
                else grid[r][c].setPowerUp(new BombPowerUp());
            }
        }
    }
    
    /**
     * Main interaction method for when a user left-clicks a cell.
     * @param row The clicked row.
     * @param col The clicked column.
     * @param player The current player.
     * @return The PowerUp found, or null if none was found.
     */
    public PowerUp clickCell(int row, int col, Player player) {
        if (gameState != GameState.PLAYING || !isValid(row, col) || !grid[row][col].isPlayable()) return null;
        
        // Logic for collecting an already-revealed power-up icon
        if(grid[row][col].isRevealed() && grid[row][col].getPowerUpTypeOnReveal() != null) {
            PowerUp p = grid[row][col].collectPowerUpOnRevealed(this);
            if (p != null) { player.addPowerUp(p.getType()); return p; }
            return null;
        }

        if (firstClick) setupBoard(row, col);
        
        // Determine cascade limit based on difficulty for balanced gameplay
        int cascadeLimit = 300; // Easy
        if (difficulty == Difficulty.MEDIUM) cascadeLimit = 30;
        if (difficulty == Difficulty.HARD || difficulty == Difficulty.CUSTOM) cascadeLimit = 15;
        
        return revealCellRecursive(row, col, player, cascadeLimit);
    }

    /**
     * The recursive part of the reveal logic, handling chain reactions.
     * @param row The current row to reveal.
     * @param col The current column to reveal.
     * @param player The current player.
     * @param cascadeLimit A counter to prevent excessively large (or infinite) chain reactions.
     * @return The PowerUp found, or null.
     */
    private PowerUp revealCellRecursive(int row, int col, Player player, int cascadeLimit) {
        if (cascadeLimit <= 0 || !isValid(row, col) || !grid[row][col].isPlayable() || grid[row][col].isRevealed() || grid[row][col].isFlagged()) return null;
        grid[row][col].reveal();

        if (grid[row][col].isBomb()) {
            player.takeDamage(1);
            if (player.getHealth() <= 0) { gameState = GameState.LOSS; revealAllBombs(); }
            return null;
        }
        
        PowerUp collectedPowerUp = grid[row][col].collectPowerUp(); // Uncovers the icon
        if (collectedPowerUp == null) {
             revealedCellsCount++; // Only non-powerup cells count towards winning
        }

        if (grid[row][col].getAdjacentBombs() == 0 && collectedPowerUp == null) {
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    if (!(r == row && c == col)) {
                        revealCellRecursive(r, c, player, cascadeLimit - 1);
                    }
                }
            }
        }
        checkWinCondition();
        return collectedPowerUp;
    }
    
    /** Reveals a 3x3 area without triggering bombs or collecting powerups. */
    public void revealArea(int row, int col) {
        for (int r = row - 1; r <= row + 1; r++) { for (int c = col - 1; c <= col + 1; c++) {
            if(isValid(r, c) && grid[r][c].isPlayable() && !grid[r][c].isRevealed() && !grid[r][c].isFlagged()) {
                grid[r][c].reveal();
                PowerUp p = grid[r][c].collectPowerUp();
                if (p == null) revealedCellsCount++;
            }
        }}
        checkWinCondition();
    }

    /** Destroys a 3x3 area. */
    public void clearArea(int row, int col, Player player) {
        for (int r = row - 1; r <= row + 1; r++) { for (int c = col - 1; c <= col + 1; c++) {
            if(isValid(r, c) && grid[r][c].isPlayable() && !grid[r][c].isRevealed()) {
                if (this.difficulty == Difficulty.HARD && grid[r][c].isBomb()) {
                    player.takeDamage(1); if (player.getHealth() <= 0) gameState = GameState.LOSS;
                }
                grid[r][c].destroy();
            }
        }}
        if (gameState == GameState.LOSS) revealAllBombs();
    }
    
    /** Toggles a flag on an un-revealed cell. */
    public void toggleFlag(int row, int col) {
        if (gameState == GameState.PLAYING && isValid(row, col) && grid[row][col].isPlayable() && !grid[row][col].isRevealed()) {
            grid[row][col].toggleFlag();
            flagsPlaced += grid[row][col].isFlagged() ? 1 : -1;
        }
    }
    
    /** Called by a Cell when a revealed power-up icon is clicked to be collected. */
    void incrementRevealedCount() { this.revealedCellsCount++; }
    /** Checks if the number of cleared cells matches the win condition. */
    void checkWinCondition() { if (revealedCellsCount >= playableCellCount - bombCount) { gameState = GameState.WIN; } }
    
    // --- Helper and Getter Methods ---
    private void calculateAllAdjacentBombs() { for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) if (grid[r][c].isPlayable() && !grid[r][c].isBomb()) grid[r][c].setAdjacentBombs(countNeighbors(r, c)); }
    private int countNeighbors(int row, int col) { int count = 0; for (int r = row - 1; r <= row + 1; r++) for (int c = col - 1; c <= col + 1; c++) if (isValid(r, c) && grid[r][c].isBomb()) count++; return count; }
    private void revealAllBombs() { for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) if (grid[r][c].isBomb()) grid[r][c].reveal(); }
    public void autoFlagBombsOnWin() { if (gameState != GameState.WIN) return; for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) if (grid[r][c].isPlayable() && grid[r][c].isBomb() && !grid[r][c].isFlagged()) { grid[r][c].toggleFlag(); flagsPlaced++; } }
    private boolean isValid(int r, int c) { return r >= 0 && r < rows && c >= 0 && c < cols; }
    public Cell getCell(int r, int c) { return grid[r][c]; }
    public int getRows() { return rows; } public int getCols() { return cols; } public int getBombCount() { return bombCount; } public int getFlagsPlaced() { return flagsPlaced; } public GameState getGameState() { return gameState; }
}
