/**
 * A utility class for generating game boards.
 * It handles the creation of unique daily challenges and general noise-based island maps.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
import java.time.LocalDate;
import java.util.Random;
import java.util.prefs.Preferences;

/**
 * A utility class for generating game boards.
 * It handles the creation of unique daily challenges and general noise-based island maps.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public class DailyChallengeGenerator {
    // Preference keys for storing the daily challenge date and seed
    private static final String PREF_KEY_DATE = "daily_challenge_date_minesweeper_enhanced";
    private static final String PREF_KEY_SEED = "daily_challenge_seed_minesweeper_enhanced";
    
    /**
     * Cheat method to advance the stored date by one day.
     * This is primarily for testing daily challenge generation without waiting a full day.
     *
     * @return A string indicating the new internal date.
     */
    public static String advanceDay() {
        // Get the user preferences node for this package
        Preferences prefs = Preferences.userNodeForPackage(DailyChallengeGenerator.class);
        // Retrieve the stored date string, defaulting to today's date if not found
        String storedDateStr = prefs.get(PREF_KEY_DATE, LocalDate.now().toString());
        // Parse the stored date string into a LocalDate object
        LocalDate storedDate = LocalDate.parse(storedDateStr);
        // Advance the date by one day
        LocalDate newDate = storedDate.plusDays(1);
        // Store the new date back into preferences
        prefs.put(PREF_KEY_DATE, newDate.toString());
        return "Internal date advanced to: " + newDate;
    }

    /**
     * Generates the fixed daily challenge game board.
     * The board configuration (seed) is determined by the current date, ensuring a unique challenge each day.
     *
     * @return A GameBoard instance representing the daily challenge.
     */
    public static GameBoard generate() {
        // Get the user preferences node for this package
        Preferences prefs = Preferences.userNodeForPackage(DailyChallengeGenerator.class);
        // Get today's date string, defaulting to current date
        String today = prefs.get(PREF_KEY_DATE, LocalDate.now().toString());
        // Get the date for which the seed was last stored
        String storedSeedDate = prefs.get("daily_challenge_seed_date_enhanced", "");
        long seed;
        // If the stored seed date matches today's date, use the existing seed
        if (today.equals(storedSeedDate)) {
            seed = prefs.getLong(PREF_KEY_SEED, new Random().nextLong());
        } else {
            // Otherwise, generate a new seed for today's challenge
            seed = new Random().nextLong();
            // Store today's date and the new seed in preferences
            prefs.put("daily_challenge_seed_date_enhanced", today);
            prefs.putLong(PREF_KEY_SEED, seed);
        }
        // Generate a noise-based board with fixed dimensions and hard difficulty using the determined seed
        return generateNoiseBoard(22, 22, seed, Difficulty.HARD);
    }
    
    /**
     * General-purpose function to generate a noise-based island map.
     * This method uses Perlin noise to create a 'playable map' (land vs. water)
     * and then places bombs based on the playable cells.
     *
     * @param rows The number of rows for the game board.
     * @param cols The number of columns for the game board.
     * @param seed The seed for the Perlin noise generator, ensuring reproducible maps.
     * @param difficulty The difficulty setting, influencing bomb count.
     * @return A GameBoard instance generated using Perlin noise.
     */
    public static GameBoard generateNoiseBoard(int rows, int cols, long seed, Difficulty difficulty) {
        // Initialize Perlin noise generator with the given seed
        PerlinNoise noise = new PerlinNoise(seed);
        // Noise scale determines the "zoom" level of the noise; higher values mean finer patterns
        double noiseScale = 0.3; // Increased from 0.1 for finer, more random patterns
        // Land threshold determines which noise values become playable cells (land)
        double landThreshold = 0.45; // Keep threshold, but it will now result in more scattered bombs
        boolean[][] playableMap = new boolean[rows][cols];
        int playableCellCount = 0;

        // Iterate through each cell to determine if it's playable based on noise
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Generate noise value, normalize it to a 0-1 range
                double n = (noise.noise(r * noiseScale, c * noiseScale) + 1) / 2.0;
                // If noise value is above threshold, mark as playable (land)
                if (n > landThreshold) {
                    playableMap[r][c] = true;
                }
                // Count playable cells directly after initial noise application
                if(playableMap[r][c]) {
                    playableCellCount++;
                }
            }
        }
        // Removed the smoothing loops that created "islands" - this means the map will be more scattered

        // Calculate bomb count based on the number of playable cells, with a minimum of 10 bombs
        int bombCount = Math.max(10, playableCellCount / 6);
        // Return a new GameBoard with the generated playable map and bomb count
        return new GameBoard(rows, cols, bombCount, playableMap, playableCellCount, difficulty);
    }

    /**
     * A self-contained implementation of the Perlin Noise algorithm for map generation.
     * This class generates smooth, natural-looking random values.
     */
    private static class PerlinNoise {
        // Permutation array, doubled to avoid modulo operations in noise calculation
        private final int[] p = new int[512];

        /**
         * Constructs a PerlinNoise generator with a given seed.
         * Initializes the permutation table based on the seed for reproducible noise.
         * @param seed The seed for the random number generator.
         */
        public PerlinNoise(long seed) {
            Random rand = new Random(seed);
            int[] permutation = new int[256];
            // Initialize permutation array with values 0-255
            for (int i = 0; i < 256; i++) {
                permutation[i] = i;
            }
            // Shuffle the permutation array using Fisher-Yates algorithm
            for (int i = 255; i > 0; i--) {
                int index = rand.nextInt(i + 1);
                int temp = permutation[index];
                permutation[index] = permutation[i];
                permutation[i] = temp;
            }
            // Duplicate the permutation array to avoid boundary checks
            for (int i = 0; i < 256; i++) {
                p[i] = p[i + 256] = permutation[i];
            }
        }

        /**
         * Generates a 2D Perlin noise value for the given coordinates.
         * @param x The x-coordinate.
         * @param y The y-coordinate.
         * @return The noise value, typically between -1 and 1.
         */
        public double noise(double x, double y) {
            // Find the unit cube containing the point
            int X = (int) Math.floor(x) & 255;
            int Y = (int) Math.floor(y) & 255;

            // Relative x, y coordinates of the point within the cube
            x -= Math.floor(x);
            y -= Math.floor(y);

            // Compute fade curves for x, y
            double u = fade(x);
            double v = fade(y);

            // Hash coordinates of the 4 corners
            int a = p[X] + Y;
            int b = p[X + 1] + Y;

            // Interpolate results along x and then y
            return lerp(v,
                        lerp(u, grad(p[a], x, y),
                               grad(p[b], x - 1, y)),
                        lerp(u, grad(p[a + 1], x, y - 1),
                               grad(p[b + 1], x - 1, y - 1)));
        }

        /**
         * Fade function for Perlin noise, a 6t^5 - 15t^4 + 10t^3 curve.
         * @param t Input value.
         * @return Faded value.
         */
        private static double fade(double t) {
            return t * t * t * (t * (t * 6 - 15) + 10);
        }

        /**
         * Linear interpolation function.
         * @param t Interpolation factor.
         * @param a Start value.
         * @param b End value.
         * @return Interpolated value.
         */
        private static double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }

        /**
         * Gradient function for Perlin noise.
         * @param hash Hash value from the permutation table.
         * @param x X component of the vector from the corner to the point.
         * @param y Y component of the vector from the corner to the point.
         * @return Dot product of the pseudorandom gradient vector and the input vector.
         */
        private static double grad(int hash, double x, double y) {
            int h = hash & 15;      // Convert lower 4 bits of hash into 12 gradient directions.
            double u = h < 8 ? x : y; // Select x or y for u
            double v = h < 4 ? y : h == 12 || h == 14 ? x : 0; // Select y, x, or 0 for v
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v); // Compute dot product
        }
    }
}
