public class Text {
    // Harder words/phrases per level (5 per level)
    public static final String[][] LEVELS = {
        // Level 1: Single challenging words
        {"xylophone", "quizzical", "mnemonic", "zephyr", "juxtapose"},
        // Level 2: Two-word tricky phrases
        {"cryptic glyph", "zealous quest", "azure vortex", "lunar eclipse", "sphinx riddle"},
        // Level 3: Three-word advanced phrases
        {"quantum entanglement theory", "symphonic jazz improvisation", "synchronous digital circuit", "fractal geometry puzzle", "recursive lambda calculus"},
        // Level 4: Short, complex sentences
        {"Sphinxes quietly judge zany jesters.",
         "Quixotic quests require zealous effort.",
         "Jovial hackers encrypt quirky messages.",
         "Glyptodon fossils mystify experts.",
         "Mnemonic rhymes expedite learning."},
        // Level 5: Longer, sophisticated sentences
        {"Juxtaposing paradoxical hypotheses, the physicist unraveled quantum mysteries.",
         "Cryptographers analyze seemingly indecipherable ciphers with unwavering focus.",
         "Mnemonic devices, though quirky, greatly enhance cognitive retention.",
         "Meticulous experimentation yields perplexing yet insightful results.",
         "The zephyr’s whimsical journey across continents inspired countless legends."}
    };

    // Time limits for each level, in seconds
    public static final int[] LEVEL_TIME_LIMITS = {10, 15, 20, 25, 30};

    // Returns exactly N words for level N (levels 1-5)
    public static String[] getLevelWords(int level) {
        if (level < 1 || level > LEVELS.length) {
            throw new IllegalArgumentException("Level must be 1-5");
        }
        String[] levelWords = LEVELS[level - 1];
        int numWords = Math.min(level, levelWords.length);
        String[] result = new String[numWords];
        System.arraycopy(levelWords, 0, result, 0, numWords);
        return result;
    }

    // Returns time limit for level (levels 1-5)
    public static int getLevelTimeLimit(int level) {
        if (level < 1 || level > LEVEL_TIME_LIMITS.length) {
            throw new IllegalArgumentException("Level must be 1-5");
        }
        return LEVEL_TIME_LIMITS[level - 1];
    }
}
