/**
 * A concrete implementation of the PowerUp interface for the Bomb ability.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public class BombPowerUp implements PowerUp {
    @Override public PowerUpType getType() { return PowerUpType.BOMB; }
    @Override public String getDescription() { return "Destroys a 3x3 area. Be careful on Hard mode!"; }
}
