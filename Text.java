package s1finalproject;

import java.util.*;

public class Text {
    // Expanded word pools for each level with longer, more challenging words
    private static final String[][] LEVEL_WORDS = {
        // Level 1: moderately long, but still introductory
        {"elephant", "computer", "umbrella", "building", "calendar", "hospital", "diamond", "sandwich", "festival", "mountain", "dolphin", "airplane", "notebook", "penguin", "language"},
        // Level 2: longer, multisyllabic words
        {"adventure", "chocolate", "education", "happiness", "reception", "community", "direction", "microscope", "telephone", "recreation", "volunteer", "president", "refrigerator", "basketball", "invisible"},
        // Level 3: abstract and advanced vocabulary
        {"perception", "motivation", "conclusion", "generation", "opposition", "foundation", "regulation", "preference", "convention", "invitation", "population", "resolution", "foundation", "assignment", "occupation"},
        // Level 4: even longer, with more complex structure
        {"responsibility", "communication", "organization", "concentration", "demonstration", "representation", "determination", "collaboration", "architecture", "transformation", "recommendation", "entertainment", "appreciation", "announcement", "achievement"},
        // Level 5: very long, advanced, and uncommon words
        {"encyclopedia", "philanthropist", "misinterpretation", "incompatibility", "internationalization", "uncharacteristically", "counterproductive", "institutionalization", "photosynthesis", "conceptualization", "misunderstanding", "unquestionably", "disproportionately", "oversimplification", "indistinguishable"}
    };

    private static final int[] LEVEL_TIME_LIMITS = {30, 30, 30, 35, 40}; // example: can be adjusted

    public static String[] getLevelWords(int level) {
        return LEVEL_WORDS[level-1];
    }

    public static int getLevelTimeLimit(int level) {
        return LEVEL_TIME_LIMITS[level-1];
    }

    // Helper to pick n unique random words from the word pool
    public static String[] pickRandomWords(String[] wordPool, int n) {
        List<String> pool = new ArrayList<>(Arrays.asList(wordPool));
        Collections.shuffle(pool);
        return pool.subList(0, Math.min(n, pool.size())).toArray(new String[0]);
    }
}