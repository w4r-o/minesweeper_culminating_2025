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

public class DailyChallengeGenerator {
    private static final String PREF_KEY_DATE = "daily_challenge_date_minesweeper_enhanced";
    private static final String PREF_KEY_SEED = "daily_challenge_seed_minesweeper_enhanced";
    
    /** Cheat method to advance the stored date by one day. */
    public static String advanceDay() {
        Preferences prefs = Preferences.userNodeForPackage(DailyChallengeGenerator.class);
        String storedDateStr = prefs.get(PREF_KEY_DATE, LocalDate.now().toString());
        LocalDate storedDate = LocalDate.parse(storedDateStr);
        LocalDate newDate = storedDate.plusDays(1);
        prefs.put(PREF_KEY_DATE, newDate.toString());
        return "Internal date advanced to: " + newDate;
    }

    /** Generates the fixed daily challenge for the "Daily Challenge" button. */
    public static GameBoard generate() {
        Preferences prefs = Preferences.userNodeForPackage(DailyChallengeGenerator.class);
        String today = prefs.get(PREF_KEY_DATE, LocalDate.now().toString());
        String storedSeedDate = prefs.get("daily_challenge_seed_date_enhanced", "");
        long seed;
        if (today.equals(storedSeedDate)) {
            seed = prefs.getLong(PREF_KEY_SEED, new Random().nextLong());
        } else {
            seed = new Random().nextLong();
            prefs.put("daily_challenge_seed_date_enhanced", today);
            prefs.putLong(PREF_KEY_SEED, seed);
        }
        return generateNoiseBoard(22, 22, seed, Difficulty.HARD);
    }
    
    /** General-purpose function to generate a noise-based island map. */
    public static GameBoard generateNoiseBoard(int rows, int cols, long seed, Difficulty difficulty) {
        PerlinNoise noise = new PerlinNoise(seed);
        double noiseScale = 0.1; double landThreshold = 0.45;
        boolean[][] playableMap = new boolean[rows][cols];
        int playableCellCount = 0;

        for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) {
            double n = (noise.noise(r * noiseScale, c * noiseScale) + 1) / 2.0;
            if (n > landThreshold) playableMap[r][c] = true;
        }
        for (int r = 1; r < rows - 1; r++) for (int c = 1; c < cols - 1; c++) {
            int landNeighbors = 0;
            for (int dr = -1; dr <= 1; dr++) for (int dc = -1; dc <= 1; dc++) if (playableMap[r+dr][c+dc]) landNeighbors++;
            if (landNeighbors < 3) playableMap[r][c] = false;
            if (landNeighbors > 7) playableMap[r][c] = true;
        }
        for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) if(playableMap[r][c]) playableCellCount++;
        
        int bombCount = Math.max(10, playableCellCount / 6);
        return new GameBoard(rows, cols, bombCount, playableMap, playableCellCount, difficulty);
    }

    /** A self-contained implementation of the Perlin Noise algorithm for map generation. */
    private static class PerlinNoise {
        private final int[] p = new int[512];
        public PerlinNoise(long seed) { Random rand = new Random(seed); int[] permutation = new int[256]; for (int i = 0; i < 256; i++) permutation[i] = i; for (int i = 255; i > 0; i--) { int index = rand.nextInt(i + 1); int temp = permutation[index]; permutation[index] = permutation[i]; permutation[i] = temp; } for (int i = 0; i < 256; i++) p[i] = p[i + 256] = permutation[i]; }
        public double noise(double x, double y) { int X = (int) Math.floor(x) & 255; int Y = (int) Math.floor(y) & 255; x -= Math.floor(x); y -= Math.floor(y); double u = fade(x); double v = fade(y); int a = p[X] + Y, b = p[X + 1] + Y; return lerp(v, lerp(u, grad(p[a], x, y), grad(p[b], x, y)), lerp(u, grad(p[a + 1], x, y - 1), grad(p[b + 1], x, y - 1))); }
        private static double fade(double t) { return t * t * t * (t * (t * 6 - 15) + 10); }
        private static double lerp(double t, double a, double b) { return a + t * (b - a); }
        private static double grad(int hash, double x, double y) { int h = hash & 15; double u = h < 8 ? x : y; double v = h < 4 ? y : h == 12 || h == 14 ? x : 0; return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v); }
    }
}
