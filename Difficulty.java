/**
 * An enum to manage the preset difficulty levels of the game.
 * Also includes a CUSTOM type for user-defined games.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public enum Difficulty {
    EASY(9, 9, 10),
    MEDIUM(16, 16, 40),
    HARD(16, 30, 99),
    CUSTOM(0, 0, 0); // Placeholder for custom games

    private final int rows;
    private final int cols;
    private final int bombCount;

    Difficulty(int rows, int cols, int bombCount) {
        this.rows = rows;
        this.cols = cols;
        this.bombCount = bombCount;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getBombCount() { return bombCount; }
}
