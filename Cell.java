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
    private boolean isBomb;
    private int adjacentBombs;
    private boolean isRevealed;
    private boolean isFlagged;
    private boolean isDestroyed;
    private PowerUp powerUp;                // The hidden power-up object, null if none
    private PowerUpType powerUpTypeOnReveal;// The type of power-up to display after being revealed
    private boolean isPlayable = true;      // False for 'water' cells on noise maps

    /**
     * Constructs a new Cell and resets its state to default values.
     */
    public Cell() {
        reset();
    }

    /**
     * Resets all properties of the cell to their initial state for a new game.
     */
    public void reset() {
        isBomb = false; adjacentBombs = 0; isRevealed = false; isFlagged = false;
        isDestroyed = false; powerUp = null; powerUpTypeOnReveal = null; isPlayable = true;
    }

    /**
     * Called when the user clicks a revealed power-up icon to collect it.
     * @param board The GameBoard, needed to update the game's win condition.
     * @return The collected PowerUp object.
     */
    public PowerUp collectPowerUpOnRevealed(GameBoard board) {
        if(powerUpTypeOnReveal == null) return null;
        
        PowerUp p = null;
        if(powerUpTypeOnReveal == PowerUpType.HEAL) p = new HealPowerUp();
        if(powerUpTypeOnReveal == PowerUpType.REVEAL) p = new RevealPowerUp();
        if(powerUpTypeOnReveal == PowerUpType.BOMB) p = new BombPowerUp();
        
        this.powerUpTypeOnReveal = null; // Remove the icon
        board.incrementRevealedCount();  // Now that the icon is gone, this cell counts towards the win
        board.checkWinCondition();       // Check if this was the last move needed to win
        return p;
    }
    
    /** Reveals the cell if it is not flagged. */
    public void reveal() { if (!isFlagged) this.isRevealed = true; }
    /** Toggles the flagged state of the cell if it is not revealed. */
    public void toggleFlag() { if (!isRevealed) this.isFlagged = !this.isFlagged; }
    /** Destroys the cell, making it a void crater. */
    public void destroy() { this.isDestroyed = true; this.isRevealed = true; this.powerUp = null; this.powerUpTypeOnReveal = null; }

    // --- Package-Private Setters (for GameBoard to control) ---
    void setPlayable(boolean playable) { this.isPlayable = playable; }
    void setBomb(boolean isBomb) { this.isBomb = isBomb; }
    void setAdjacentBombs(int count) { this.adjacentBombs = count; }
    void setPowerUp(PowerUp powerUp) { this.powerUp = powerUp; }
    /** Called when a cell is first revealed to uncover the power-up icon. */
    PowerUp collectPowerUp() { PowerUp p = this.powerUp; if (p != null) this.powerUpTypeOnReveal = p.getType(); this.powerUp = null; return p; }
    
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
