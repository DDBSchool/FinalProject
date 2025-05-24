import java.util.*;

public class RankedTypingTest {
    private static final int[] POINTS_PER_WORD = {1, 2, 3, 4, 6};
    private static final int[] POINTS_TO_PASS = {1, 3, 6, 10, 18}; // Example thresholds

    public static void playRanked(Scanner sc, String username) {
        int level = 1;
        boolean passed = true;

        // Accumulated stats for the whole session
        int totalScore = 0;
        int totalCorrect = 0;
        int totalWords = 0;
        double totalTime = 0.0;

        String sessionDate = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        while (level <= 5 && passed) {
            int timeLimit = Text.getLevelTimeLimit(level);
            String[] words = Text.getLevelWords(level);

            // Run the test for this level
            RankedTestResult result = runLevel(sc, level, words, timeLimit);

            // Accumulate results
            totalScore += result.score;
            totalCorrect += result.correct;
            totalWords += words.length;
            totalTime += result.timeTaken;

            // Level progression logic
            if (result.score >= POINTS_TO_PASS[level - 1]) {
                System.out.println("Passed level " + level + "! Proceeding.");
                level++;
            } else {
                System.out.println("Did not pass level " + level + ". Try again next time!");
                passed = false;
            }
        }

        // Compute final stats
        double finalAccuracy = (totalWords > 0) ? (100.0 * totalCorrect / totalWords) : 0;
        double finalWps = (totalTime > 0) ? (double) totalCorrect / totalTime : 0;
        int finalLevel = passed ? 5 : (level - 1);

        // Save total result (only once, after all levels are played)
        ResultManager.saveResult(username, finalLevel, totalScore, finalAccuracy, finalWps, "ranked", sessionDate);

        if (passed) {
            System.out.println("Congratulations! You completed Ranked Mode!");
        }
    }

    // Helper class to store per-level test results
    private static class RankedTestResult {
        int score, correct;
        double timeTaken;
        RankedTestResult(int s, int c, double t) {
            score = s; correct = c; timeTaken = t;
        }
    }

    // Returns per-level test result
    private static RankedTestResult runLevel(Scanner sc, int level, String[] words, int timeLimit) {
        int correct = 0, score = 0;
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
                score += POINTS_PER_WORD[level - 1];
            }
        }
        long finish = Math.min(System.currentTimeMillis(), end);
        double elapsed = (finish - start) / 1000.0;
        double accuracy = (100.0 * correct / words.length);
        double wps = (elapsed > 0) ? (double) correct / elapsed : 0;

        // Bonus points if finished early and no mistakes
        int bonus = 0;
        if (correct == words.length && (finish < end)) {
            bonus = (int) ((end - finish) / 2000);
            score += bonus;
            System.out.println("Bonus points for speed: " + bonus);
        }
        System.out.printf("Score: %d | Accuracy: %.2f%% | WPS: %.2f\n", score, accuracy, wps);
        return new RankedTestResult(score, correct, elapsed);
    }
}
