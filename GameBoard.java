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

import java.util.Random;

/**
 * The model for the game board.
 * Manages the grid of cells, bomb placement, reveal logic, and win/loss conditions.
 * It supports both standard rectangular boards and irregular noise-based maps.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public class GameBoard {
    // --- Member Variables ---
    /** The 2D array representing the grid of cells on the game board. */
    private final Cell[][] grid;
    /** The number of rows in the game board. */
    private final int rows;
    /** The number of columns in the game board. */
    private final int cols;
    /** The total number of bombs on the board. */
    private int bombCount;
    /** The count of non-bomb cells that have been revealed. Used for win condition. */
    private int revealedCellsCount;
    /** The number of flags currently placed by the player. */
    private int flagsPlaced;
    /** The total number of playable (non-water) cells on the board. */
    private int playableCellCount;
    /** The current state of the game (PLAYING, WIN, LOSS). */
    private GameState gameState;
    /** A flag indicating if it's the very first click of the game, used for initial board setup. */
    private boolean firstClick = true;
    /** The difficulty level of the current game. */
    private Difficulty difficulty;

    /** Enum representing the current state of the game. */
    public enum GameState {
        /** The game is currently in progress. */
        PLAYING,
        /** The player has won the game. */
        WIN,
        /** The player has lost the game. */
        LOSS
    }

    /**
     * Constructor for standard rectangular boards.
     * Initializes a new game board with a uniform grid of playable cells.
     *
     * @param rows The number of rows for the game board.
     * @param cols The number of columns for the game board.
     * @param bombCount The number of bombs to be placed on the board.
     * @param difficulty The difficulty level, used for balancing game mechanics.
     */
    public GameBoard(int rows, int cols, int bombCount, Difficulty difficulty) {
        // Delegate to the main constructor, passing null for playableMap and calculating playableCellCount
        this(rows, cols, bombCount, null, rows * cols, difficulty);
    }
    
    /**
     * Main constructor for all board types, including irregular noise-based maps.
     * Initializes the grid and sets up the initial state of the game.
     *
     * @param rows The total number of rows in the grid.
     * @param cols The total number of columns in the grid.
     * @param bombCount The number of bombs to be placed.
     * @param playableMap A 2D boolean array where 'true' indicates a playable (land) cell.
     *                    Can be null for standard rectangular maps where all cells are playable.
     * @param playableCellCount The total number of playable cells on the board.
     * @param difficulty The difficulty level for the game.
     */
    public GameBoard(int rows, int cols, int bombCount, boolean[][] playableMap, int playableCellCount, Difficulty difficulty) {
        this.rows = rows;
        this.cols = cols;
        this.bombCount = bombCount;
        this.grid = new Cell[rows][cols];
        this.playableCellCount = playableCellCount;
        this.difficulty = difficulty;
        // Reset the board to its initial state based on the provided playable map
        reset(playableMap);
    }
    
    /**
     * Initializes or re-initializes the grid, setting cells to playable or non-playable
     * based on the provided map. This method is called during board construction or reset.
     *
     * @param playableMap The 2D boolean array defining the playable area. If null, all cells are considered playable.
     */
    private void reset(boolean[][] playableMap) {
        // Reset game state variables
        gameState = GameState.PLAYING;
        revealedCellsCount = 0;
        flagsPlaced = 0;
        firstClick = true;

        // Initialize each cell in the grid
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(); // Create a new Cell object for each grid position
                // If a playable map is provided, set cell's playable status accordingly
                if (playableMap != null && !playableMap[r][c]) {
                    grid[r][c].setPlayable(false); // Mark cell as non-playable (e.g., water)
                }
            }
        }
    }

    /**
     * Sets up the board after the first click.
     * This involves placing bombs, power-ups, and calculating adjacent bomb counts.
     * This ensures the first clicked cell is always safe.
     *
     * @param safeRow The row of the first click, guaranteed to be safe (no bomb).
     * @param safeCol The column of the first click, guaranteed to be safe (no bomb).
     */
    public void setupBoard(int safeRow, int safeCol) {
        placeBombs(safeRow, safeCol); // Place bombs, avoiding the first click
        placePowerUps(); // Place power-ups on non-bomb cells
        calculateAllAdjacentBombs(); // Calculate adjacent bomb counts for all cells
        firstClick = false; // Mark that the first click has occurred
    }
    
    /**
     * Randomly places bombs on playable cells, ensuring no bomb is placed
     * at the first-click location.
     *
     * @param safeRow The row of the cell that was first clicked (must be bomb-free).
     * @param safeCol The column of the cell that was first clicked (must be bomb-free).
     */
    private void placeBombs(int safeRow, int safeCol) {
        Random rand = new Random();
        int bombsPlaced = 0;
        // Failsafe to prevent an infinite loop on very small/dense custom maps
        // Ensures there's at least one non-bomb playable cell if bombCount is too high
        if (playableCellCount <= bombCount) {
            this.bombCount = Math.max(1, playableCellCount - 1);
        }

        // Continue placing bombs until the desired count is reached
        while (bombsPlaced < bombCount) {
            int r = rand.nextInt(rows); // Get a random row
            int c = rand.nextInt(cols); // Get a random column
            // Check if the cell is playable, not already a bomb, and not the safe first-click cell
            if (grid[r][c].isPlayable() && !grid[r][c].isBomb() && !(r == safeRow && c == safeCol)) {
                grid[r][c].setBomb(true); // Set the cell as a bomb
                bombsPlaced++; // Increment bomb count
            }
        }
    }

    /**
     * Places power-ups randomly on non-bomb, playable cells.
     * The number of power-ups is determined by the playable cell count.
     */
    private void placePowerUps() {
        Random rand = new Random();
        // Calculate the number of power-ups based on playable cells, with a minimum of 2
        int powerUpCount = 2 + (playableCellCount) / 75;

        // Attempt to place each power-up
        for (int i = 0; i < powerUpCount; i++) {
            int r = rand.nextInt(rows); // Get a random row
            int c = rand.nextInt(cols); // Get a random column
            // Check if the cell is playable, not a bomb, and doesn't already have a power-up
            if (grid[r][c].isPlayable() && !grid[r][c].isBomb() && grid[r][c].getPowerUp() == null) {
                int powerUpType = rand.nextInt(3); // Randomly select power-up type (Heal, Reveal, Bomb)
                if (powerUpType == 0) grid[r][c].setPowerUp(new HealPowerUp());
                else if (powerUpType == 1) grid[r][c].setPowerUp(new RevealPowerUp());
                else grid[r][c].setPowerUp(new BombPowerUp());
            }
        }
    }
    
    /**
     * Main interaction method for when a user left-clicks a cell.
     * Handles initial board setup, power-up collection, and recursive cell revelation.
     *
     * @param row The row of the clicked cell.
     * @param col The column of the clicked cell.
     * @param player The current player object, used for health and power-up management.
     * @return The PowerUp object collected (if any), or null.
     */
    public PowerUp clickCell(int row, int col, Player player) {
        // Do nothing if game is not playing, coordinates are invalid, or cell is not playable
        if (gameState != GameState.PLAYING || !isValid(row, col) || !grid[row][col].isPlayable()) {
            return null;
        }
        
        // Logic for collecting an already-revealed power-up icon (after a previous reveal)
        if(grid[row][col].isRevealed() && grid[row][col].getPowerUpTypeOnReveal() != null) {
            PowerUp p = grid[row][col].collectPowerUpOnRevealed(this);
            if (p != null) {
                player.addPowerUp(p.getType()); // Add the collected power-up to the player's inventory
                return p; // Return the collected power-up
            }
            return null; // No power-up collected
        }

        // If it's the first click, set up the board (place bombs and power-ups)
        if (firstClick) {
            setupBoard(row, col);
        }
        
        // Determine cascade limit based on difficulty for balanced gameplay
        // This limits how many cells can be revealed in a single chain reaction
        int cascadeLimit = 300; // High limit for Easy difficulty
        if (difficulty == Difficulty.MEDIUM) cascadeLimit = 30; // Medium limit for Medium difficulty
        if (difficulty == Difficulty.HARD || difficulty == Difficulty.CUSTOM) cascadeLimit = 15; // Low limit for Hard/Custom

        // Start the recursive revelation process
        return revealCellRecursive(row, col, player, cascadeLimit);
    }

    /**
     * The recursive part of the reveal logic, handling chain reactions when an empty cell is clicked.
     * It reveals adjacent cells until a cell with adjacent bombs or a flagged/revealed cell is encountered.
     *
     * @param row The current row to reveal.
     * @param col The current column to reveal.
     * @param player The current player object.
     * @param cascadeLimit A counter to prevent excessively large (or infinite) chain reactions,
     *                     especially on large boards or low bomb counts.
     * @return The PowerUp found in the current cell, or null.
     */
    private PowerUp revealCellRecursive(int row, int col, Player player, int cascadeLimit) {
        // Base cases for recursion:
        // 1. Cascade limit reached (prevents infinite loops/excessive reveals)
        // 2. Invalid coordinates
        // 3. Cell is not playable (e.g., water)
        // 4. Cell is already revealed
        // 5. Cell is flagged
        if (cascadeLimit <= 0 || !isValid(row, col) || !grid[row][col].isPlayable() || grid[row][col].isRevealed() || grid[row][col].isFlagged()) {
            return null;
        }
        grid[row][col].reveal(); // Mark the current cell as revealed

        // If the revealed cell is a bomb
        if (grid[row][col].isBomb()) {
            player.takeDamage(1); // Player takes damage
            AudioManager.playSoundEffect("assets/kaboom.wav"); // Play bomb sound effect
            if (player.getHealth() <= 0) {
                gameState = GameState.LOSS; // Game over if health drops to 0 or below
                revealAllBombs(); // Show all bombs on loss
            }
            return null; // No power-up found on a bomb cell
        }
        
        // Attempt to collect a power-up if present in this cell (uncovers its icon)
        PowerUp collectedPowerUp = grid[row][col].collectPowerUp();
        // All revealed non-bomb cells count towards winning, regardless of power-up presence
        if (!grid[row][col].isBomb()) {
            revealedCellsCount++; // Increment count of revealed non-bomb cells
        }

        // If the cell has no adjacent bombs and no power-up was collected,
        // recursively reveal its neighbors (cascade effect)
        if (grid[row][col].getAdjacentBombs() == 0 && collectedPowerUp == null) {
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    // Exclude the current cell itself from recursive calls
                    if (!(r == row && c == col)) {
                        revealCellRecursive(r, c, player, cascadeLimit - 1); // Decrement cascade limit
                    }
                }
            }
        }
        checkWinCondition(); // Check if the game has been won after this reveal
        return collectedPowerUp; // Return any power-up found
    }
    
    /**
     * Reveals a 3x3 area around the specified cell without triggering bombs
     * or collecting power-ups directly (they are just uncovered).
     * This is typically used by the Reveal Power-Up.
     *
     * @param row The center row of the area to reveal.
     * @param col The center column of the area to reveal.
     */
    public void revealArea(int row, int col) {
        // Iterate through the 3x3 area centered at (row, col)
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                // Check if coordinates are valid, cell is playable, not already revealed, and not flagged
                if(isValid(r, c) && grid[r][c].isPlayable() && !grid[r][c].isRevealed() && !grid[r][c].isFlagged()) {
                    grid[r][c].reveal(); // Reveal the cell
                    grid[r][c].collectPowerUp(); // Uncover power-up icon if present (but don't add to player)
                    // If a non-bomb cell is revealed, increment the revealed count for win condition
                    if (!grid[r][c].isBomb()) {
                        revealedCellsCount++;
                    }
                }
            }
        }
        checkWinCondition(); // Check win condition after revealing the area
    }

    /**
     * Destroys a 3x3 area around the specified cell.
     * Destroyed cells become void craters. On Hard difficulty, destroying a bomb
     * will cause the player to take damage.
     * This is typically used by the Bomb Power-Up.
     *
     * @param row The center row of the area to clear.
     * @param col The center column of the area to clear.
     * @param player The current player object, used for damage calculation on Hard difficulty.
     */
    public void clearArea(int row, int col, Player player) {
        // Iterate through the 3x3 area centered at (row, col)
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                // Check if coordinates are valid, cell is playable, and not already revealed
                if(isValid(r, c) && grid[r][c].isPlayable() && !grid[r][c].isRevealed()) {
                    // On Hard difficulty, if a bomb is destroyed, player takes damage
                    if (this.difficulty == Difficulty.HARD && grid[r][c].isBomb()) {
                        player.takeDamage(1);
                        if (player.getHealth() <= 0) {
                            gameState = GameState.LOSS; // Game over if health drops to 0 or below
                        }
                    }
                    grid[r][c].destroy(); // Mark the cell as destroyed
                    // If a non-bomb playable cell is destroyed, it counts towards revealedCellsCount
                    if (!grid[r][c].isBomb()) {
                        revealedCellsCount++;
                    }
                }
            }
        }
        // If game state changed to LOSS during area clear, reveal all bombs
        if (gameState == GameState.LOSS) {
            revealAllBombs();
        }
        checkWinCondition(); // Check win condition after clearing the area
    }
    
    /**
     * Toggles a flag on an un-revealed cell.
     * Flags are used to mark suspected bomb locations.
     *
     * @param row The row of the cell to toggle flag.
     * @param col The column of the cell to toggle flag.
     */
    public void toggleFlag(int row, int col) {
        // Only allow flagging if game is playing, coordinates are valid, cell is playable, and not revealed
        if (gameState == GameState.PLAYING && isValid(row, col) && grid[row][col].isPlayable() && !grid[row][col].isRevealed()) {
            grid[row][col].toggleFlag(); // Toggle the cell's flag state
            // Update the total count of flags placed
            flagsPlaced += grid[row][col].isFlagged() ? 1 : -1;
        }
    }
    
    /**
     * Called by a Cell when a revealed power-up icon is clicked to be collected.
     * This method is no longer directly used for win condition counting, as that logic
     * has been moved to `collectPowerUpOnRevealed` in `Cell.java` and `revealCellRecursive`.
     */
    void incrementRevealedCount() { /* This method is no longer directly used for win condition counting */ }

    /**
     * Checks if the number of cleared cells matches the win condition.
     * The game is won when all non-bomb playable cells have been revealed.
     */
    void checkWinCondition() {
        // If the count of revealed non-bomb cells is equal to or greater than
        // the total playable cells minus the bomb count, the game is won.
        if (revealedCellsCount >= playableCellCount - bombCount) {
            gameState = GameState.WIN;
        }
    }
    
    // --- Helper and Getter Methods ---
    /**
     * Calculates and sets the number of adjacent bombs for all non-bomb playable cells on the board.
     * This is typically called once after bombs are placed.
     */
    private void calculateAllAdjacentBombs() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Only calculate for playable cells that are not bombs
                if (grid[r][c].isPlayable() && !grid[r][c].isBomb()) {
                    grid[r][c].setAdjacentBombs(countNeighbors(r, c));
                }
            }
        }
    }

    /**
     * Counts the number of bombs in the 8 adjacent cells (including diagonals)
     * around a given cell.
     *
     * @param row The row of the center cell.
     * @param col The column of the center cell.
     * @return The count of adjacent bombs.
     */
    private int countNeighbors(int row, int col) {
        int count = 0;
        // Iterate through the 3x3 area around the cell
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                // Check if the neighbor is valid and contains a bomb
                if (isValid(r, c) && grid[r][c].isBomb()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Reveals all bomb cells on the board.
     * This is typically called when the game is lost.
     */
    private void revealAllBombs() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c].isBomb()) {
                    grid[r][c].reveal(); // Reveal bomb cells
                }
            }
        }
    }

    /**
     * Automatically flags all unflagged bomb cells when the game is won.
     * This provides a visual confirmation of all bomb locations upon victory.
     */
    public void autoFlagBombsOnWin() {
        // Only perform this action if the game has been won
        if (gameState != GameState.WIN) return;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // If a cell is playable, a bomb, and not already flagged, flag it
                if (grid[r][c].isPlayable() && grid[r][c].isBomb() && !grid[r][c].isFlagged()) {
                    grid[r][c].toggleFlag(); // Toggle flag to set it
                    flagsPlaced++; // Increment flag count
                }
            }
        }
    }

    /**
     * Checks if the given row and column coordinates are within the bounds of the grid.
     * @param r The row index.
     * @param c The column index.
     * @return True if the coordinates are valid, false otherwise.
     */
    private boolean isValid(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    /**
     * Returns the Cell object at the specified coordinates.
     * @param r The row index.
     * @param c The column index.
     * @return The Cell object.
     */
    public Cell getCell(int r, int c) { return grid[r][c]; }
    /**
     * Returns the number of rows in the game board.
     * @return The number of rows.
     */
    public int getRows() { return rows; }
    /**
     * Returns the number of columns in the game board.
     * @return The number of columns.
     */
    public int getCols() { return cols; }
    /**
     * Returns the total number of bombs on the board.
     * @return The bomb count.
     */
    public int getBombCount() { return bombCount; }
    /**
     * Returns the number of flags currently placed by the player.
     * @return The number of flags.
     */
    public int getFlagsPlaced() { return flagsPlaced; }
    /**
     * Returns the current state of the game.
     * @return The current GameState (PLAYING, WIN, or LOSS).
     */
    public GameState getGameState() { return gameState; }

    /**
     * Sets the current game state.
     * This method can be used to explicitly change the game's state, e.g., to WIN or LOSS.
     * @param newState The new GameState to set.
     */
    public void setGameState(GameState newState) {
        this.gameState = newState;
    }
}
