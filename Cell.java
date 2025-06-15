/**
 * The model for a single cell on the game board.
 * It holds all state information for one square, such as whether it's a bomb, revealed, flagged, etc.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public class Cell {
    // --- Member Variables ---
    /** Indicates if this cell contains a bomb. */
    private boolean isBomb;
    /** The number of adjacent cells that contain bombs. */
    private int adjacentBombs;
    /** True if the cell has been revealed by the player. */
    private boolean isRevealed;
    /** True if the cell has been flagged by the player. */
    private boolean isFlagged;
    /** True if the cell has been destroyed (e.g., by a bomb power-up). */
    private boolean isDestroyed;
    /** The hidden power-up object contained within this cell, null if none. */
    private PowerUp powerUp;
    /** The type of power-up to display after being revealed, used for rendering the icon. */
    private PowerUpType powerUpTypeOnReveal;
    /** False for 'water' cells on noise maps, meaning they cannot be interacted with. */
    private boolean isPlayable = true;

    /**
     * Constructs a new Cell and resets its state to default values.
     * Initializes all cell properties to their default, unrevealed, unflagged state.
     */
    public Cell() {
        reset();
    }

    /**
     * Resets all properties of the cell to their initial state for a new game.
     * This method is crucial for reinitializing the board without creating new Cell objects.
     */
    public void reset() {
        isBomb = false;
        adjacentBombs = 0;
        isRevealed = false;
        isFlagged = false;
        isDestroyed = false;
        powerUp = null;
        powerUpTypeOnReveal = null;
        isPlayable = true;
    }

    /**
     * Called when the user clicks a revealed power-up icon to collect it.
     * This method handles the logic for collecting a power-up, removing its icon,
     * updating the revealed cell count, and checking for win conditions.
     *
     * @param board The GameBoard, needed to update the game's win condition.
     * @return The collected PowerUp object, or null if no power-up icon was present.
     */
    public PowerUp collectPowerUpOnRevealed(GameBoard board) {
        // If there's no power-up icon to collect, return null
        if(powerUpTypeOnReveal == null) return null;
        
        PowerUp p = null;
        // Instantiate the correct PowerUp type based on the revealed icon
        if(powerUpTypeOnReveal == PowerUpType.HEAL) p = new HealPowerUp();
        if(powerUpTypeOnReveal == PowerUpType.REVEAL) p = new RevealPowerUp();
        if(powerUpTypeOnReveal == PowerUpType.BOMB) p = new BombPowerUp();
        
        // Remove the power-up icon from the cell after collection
        this.powerUpTypeOnReveal = null;
        // Increment the count of revealed cells on the board, as this cell now counts towards the win condition
        board.incrementRevealedCount();
        // Check if this collection action led to a win
        board.checkWinCondition();
        return p;
    }
    
    /**
     * Reveals the cell if it is not flagged.
     * This method changes the cell's state to revealed, allowing its content (bomb, number, or power-up icon) to be displayed.
     */
    public void reveal() {
        // A cell cannot be revealed if it is flagged
        if (!isFlagged) {
            this.isRevealed = true;
        }
    }

    /**
     * Toggles the flagged state of the cell if it is not revealed.
     * Players use flags to mark potential bomb locations.
     */
    public void toggleFlag() {
        // A cell's flag state can only be toggled if it has not yet been revealed
        if (!isRevealed) {
            this.isFlagged = !this.isFlagged;
        }
    }

    /**
     * Destroys the cell, making it a void crater.
     * This typically happens when a bomb power-up is used. A destroyed cell is also revealed,
     * cannot contain a power-up, and does not count towards the win condition.
     */
    public void destroy() {
        this.isDestroyed = true;
        this.isRevealed = true; // Destroyed cells are visually revealed
        this.powerUp = null; // A destroyed cell cannot have a power-up
        this.powerUpTypeOnReveal = null; // No power-up icon to display
    }

    // --- Package-Private Setters (for GameBoard to control) ---
    /**
     * Sets whether this cell is playable (i.e., not a 'water' cell on noise maps).
     * @param playable True if the cell can be interacted with, false otherwise.
     */
    void setPlayable(boolean playable) {
        this.isPlayable = playable;
    }

    /**
     * Sets whether this cell contains a bomb.
     * @param isBomb True if the cell should be a bomb, false otherwise.
     */
    void setBomb(boolean isBomb) {
        this.isBomb = isBomb;
    }

    /**
     * Sets the number of adjacent bombs for this cell.
     * @param count The number of bombs in neighboring cells.
     */
    void setAdjacentBombs(int count) {
        this.adjacentBombs = count;
    }

    /**
     * Assigns a PowerUp object to this cell.
     * @param powerUp The PowerUp object to be hidden in this cell.
     */
    void setPowerUp(PowerUp powerUp) {
        this.powerUp = powerUp;
    }

    /**
     * Called when a cell is first revealed to uncover the hidden power-up.
     * This method transfers the power-up from its hidden state to a state where its icon can be displayed.
     *
     * @return The PowerUp object that was hidden in this cell, or null if none.
     */
    PowerUp collectPowerUp() {
        PowerUp p = this.powerUp;
        if (p != null) {
            this.powerUpTypeOnReveal = p.getType(); // Set the type for display
        }
        this.powerUp = null; // Remove the hidden power-up after it's "collected" for display
        return p;
    }
    
    // --- Public Getters ---
    public boolean isBomb() { return isBomb; }
    public int getAdjacentBombs() { return adjacentBombs; }
    public boolean isRevealed() { return isRevealed; }
    public boolean isFlagged() { return isFlagged; }
    public boolean isDestroyed() { return isDestroyed; }
    public PowerUpType getPowerUpTypeOnReveal() { return powerUpTypeOnReveal; }
    public boolean isPlayable() { return isPlayable; }
    public PowerUp getPowerUp() { return this.powerUp; }
}
