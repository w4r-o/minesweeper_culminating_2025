/**
 * A concrete implementation of the PowerUp interface for the Heal ability.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public class HealPowerUp implements PowerUp {
    @Override public PowerUpType getType() { return PowerUpType.HEAL; }
    @Override public String getDescription() { return "Restores one health point."; }
}
