import java.util.*;

public class RankedTypingTest {
    private static final int[] POINTS_PER_WORD = {1, 2, 3, 4, 6};
    private static final int[] POINTS_TO_PASS = {1, 3, 6, 10, 18}; // Example thresholds

    public static void playRanked(Scanner sc, String username) {
        int level = 1;
        boolean passed = true;
        while (level <= 5 && passed) {
            int timeLimit = Text.getLevelTimeLimit(level);
            String[] words = Text.getLevelWords(level);

            // Run the test
            RankedTestResult result = runLevel(sc, username, level, words, timeLimit);
            ResultManager.saveResult(username, level, result.score, result.accuracy, result.wps, "ranked", result.date);

            // Level progression logic
            if (result.score >= POINTS_TO_PASS[level-1]) {
                System.out.println("Passed level " + level + "! Proceeding.");
                level++;
            } else {
                System.out.println("Did not pass level " + level + ". Try again next time!");
                passed = false;
            }
        }
        if (level > 5) System.out.println("Congratulations! You completed Ranked Mode!");
    }

    private static RankedTestResult runLevel(Scanner sc, String username, int level, String[] words, int timeLimit) {
        int correct = 0, mistakes = 0, score = 0;
        long start = System.currentTimeMillis();
        long end = start + timeLimit * 1000L;

        for (String word : words) {
            if (System.currentTimeMillis() >= end) break;
            System.out.println(word);
            System.out.print("Enter: ");
            String input = sc.nextLine();
            if (System.currentTimeMillis() >= end) break;
            if (input.trim().equals(word)) {
                correct++;
                score += POINTS_PER_WORD[level-1];
            } else {
                mistakes++;
            }
        }
        long finish = Math.min(System.currentTimeMillis(), end);
        double elapsed = (finish - start) / 1000.0;
        double accuracy = (100.0 * correct / words.length);
        double wps = (elapsed > 0) ? (double) correct / elapsed : 0;

        // Bonus points if finished early and no mistakes
        int bonus = 0;
        if (correct == words.length && mistakes == 0 && (finish < end)) {
            // Example: 1 bonus per 2 seconds left
            bonus = (int) ((end - finish) / 2000);
            score += bonus;
            System.out.println("Bonus points for speed: " + bonus);
        }
        String date = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.printf("Score: %d | Accuracy: %.2f%% | WPS: %.2f\n", score, accuracy, wps);
        return new RankedTestResult(score, accuracy, wps, date);
    }

    private static class RankedTestResult {
        int score; double accuracy; double wps; String date;
        RankedTestResult(int s, double a, double w, String d) { score = s; accuracy = a; wps = w; date = d; }
    }
}
