/**
 * An interface defining the basic contract for all PowerUp types.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public interface PowerUp {
    PowerUpType getType();
    String getDescription();
}
