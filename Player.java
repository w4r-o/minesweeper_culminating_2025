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
    /** The current health of the player. */
    private int health;
    /** The current score of the player. */
    private int score;
    /** The number of Heal power-ups the player has. */
    private int healCount;
    /** The number of Reveal power-ups the player has. */
    private int revealCount;
    /** The number of Bomb power-ups the player has. */
    private int bombCount;
    /** The maximum health a player can have. */
    public static final int MAX_HEALTH = 3;

    /**
     * Constructs a new player with default starting values.
     * Initializes health to max, score to 0, and provides one of each power-up.
     */
    public Player() {
        this.health = MAX_HEALTH;
        this.score = 0;
        this.healCount = 1;
        this.revealCount = 1;
        this.bombCount = 1;
    }

    /**
     * Reduces player health by a given amount, ensuring health does not drop below 0.
     * @param amount The amount of damage to take.
     */
    public void takeDamage(int amount) {
        this.health = Math.max(0, this.health - amount);
    }

    /**
     * Increases player health by a given amount, ensuring health does not exceed MAX_HEALTH.
     * @param amount The amount of health to restore.
     */
    public void heal(int amount) {
        this.health = Math.min(MAX_HEALTH, this.health + amount);
    }

    /**
     * Adds a collected power-up to the player's inventory based on its type.
     * @param type The type of PowerUp to add.
     */
    public void addPowerUp(PowerUpType type) {
        if (type == PowerUpType.HEAL) {
            healCount++;
        } else if (type == PowerUpType.REVEAL) {
            revealCount++;
        } else if (type == PowerUpType.BOMB) {
            bombCount++;
        }
    }

    /**
     * Attempts to use a Heal power-up.
     * A heal power-up can only be used if the player has one and is not at max health.
     * @return True if a heal power-up was successfully used, false otherwise.
     */
    public boolean useHeal() {
        if (healCount > 0 && health < MAX_HEALTH) {
            heal(1); // Restore 1 health point
            healCount--; // Consume one heal power-up
            return true;
        }
        return false;
    }

    /**
     * Attempts to use a Reveal power-up.
     * A reveal power-up can only be used if the player has one.
     * @return True if a reveal power-up was successfully used, false otherwise.
     */
    public boolean useReveal() {
        if (revealCount > 0) {
            revealCount--; // Consume one reveal power-up
            return true;
        }
        return false;
    }

    /**
     * Attempts to use a Bomb power-up.
     * A bomb power-up can only be used if the player has one.
     * @return True if a bomb power-up was successfully used, false otherwise.
     */
    public boolean useBomb() {
        if (bombCount > 0) {
            bombCount--; // Consume one bomb power-up
            return true;
        }
        return false;
    }
    
    // --- Getters ---
    /**
     * Returns the player's current health.
     * @return The current health.
     */
    public int getHealth() { return health; }
    /**
     * Returns the player's current score.
     * @return The current score.
     */
    public int getScore() { return score; }
    /**
     * Returns the number of Heal power-ups the player has.
     * @return The count of Heal power-ups.
     */
    public int getHealCount() { return healCount; }
    /**
     * Returns the number of Reveal power-ups the player has.
     * @return The count of Reveal power-ups.
     */
    public int getRevealCount() { return revealCount; }
    /**
     * Returns the number of Bomb power-ups the player has.
     * @return The count of Bomb power-ups.
     */
    public int getBombCount() { return bombCount; }
}
