/**
 * The model for the player.
 * Manages health, score, and the inventory of collected power-ups.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public class Player {
    private int health;
    private int score;
    private int healCount;
    private int revealCount;
    private int bombCount;
    public static final int MAX_HEALTH = 3;

    /** Constructs a new player with default starting values. */
    public Player() {
        this.health = MAX_HEALTH; this.score = 0;
        this.healCount = 1; this.revealCount = 1; this.bombCount = 1;
    }

    /** Reduces player health by a given amount, capping at 0. */
    public void takeDamage(int amount) { this.health = Math.max(0, this.health - amount); }
    /** Increases player health by a given amount, capping at MAX_HEALTH. */
    public void heal(int amount) { this.health = Math.min(MAX_HEALTH, this.health + amount); }
    /** Adds a collected power-up to the player's inventory. */
    public void addPowerUp(PowerUpType type) {
        if (type == PowerUpType.HEAL) healCount++;
        else if (type == PowerUpType.REVEAL) revealCount++;
        else if (type == PowerUpType.BOMB) bombCount++;
    }
    /** Uses a Heal power-up if available and not at max health. */
    public boolean useHeal() { if (healCount > 0 && health < MAX_HEALTH) { heal(1); healCount--; return true; } return false; }
    /** Uses a Reveal power-up if available. */
    public boolean useReveal() { if (revealCount > 0) { revealCount--; return true; } return false; }
    /** Uses a Bomb power-up if available. */
    public boolean useBomb() { if (bombCount > 0) { bombCount--; return true; } return false; }
    
    // --- Getters ---
    public int getHealth() { return health; }
    public int getScore() { return score; }
    public int getHealCount() { return healCount; }
    public int getRevealCount() { return revealCount; }
    public int getBombCount() { return bombCount; }
}
