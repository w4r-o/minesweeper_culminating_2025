/**
 * A concrete implementation of the PowerUp interface for the Reveal ability.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public class RevealPowerUp implements PowerUp {
    @Override public PowerUpType getType() { return PowerUpType.REVEAL; }
    @Override public String getDescription() { return "Reveals a 3x3 area."; }
}
