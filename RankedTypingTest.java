import java.util.*;

public class RankedTypingTest {
    private static final int[] POINTS_PER_WORD = {1, 2, 3, 4, 6};
    private static final int[] POINTS_TO_PASS = {1, 3, 6, 10, 18}; // Example thresholds
    private static final int WORDS_PER_LEVEL = 5; // Always pick 5 words per level

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
            String[] allWords = Text.getLevelWords(level);
            String[] words = pickRandomWords(allWords, WORDS_PER_LEVEL);

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
        // Clamp finalLevel to at least 1 to prevent DB constraint error
        int finalLevel = passed ? 5 : Math.max(1, level - 1);

        // Only save if at least one word was presented
        if (totalWords > 0) {
            ResultManager.saveResult(username, finalLevel, totalScore, finalAccuracy, finalWps, "ranked", sessionDate);
        } else {
            System.out.println("No words attempted, result will not be saved.");
        }

        if (passed) {
            System.out.println("Congratulations! You completed Ranked Mode!");
        }
    }

    // Helper: picks up to 'count' random words from a source array, never modifies the original array
    private static String[] pickRandomWords(String[] sourceWords, int count) {
        List<String> wordList = new ArrayList<>(Arrays.asList(sourceWords));
        Collections.shuffle(wordList);
        return wordList.subList(0, Math.min(count, wordList.size())).toArray(new String[0]);
    }

    // Helper class to store per-level test results
    private static class RankedTestResult {
        int score, correct;
        double timeTaken;
        RankedTestResult(int s, int c, double t) {
            score = s; correct = c; timeTaken = t;
        }
    }

    // Returns per-level test result, prints correct in green and incorrect in red
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
                // Print correct input in green
                System.out.println("\u001B[32mCorrect! You typed: \"" + input + "\"\u001B[0m");
                correct++;
                score += POINTS_PER_WORD[level - 1];
            } else {
                // Print the incorrect word in red
                System.out.println("\u001B[31mIncorrect! You typed: \"" + input + "\" | Expected: \"" + word + "\"\u001B[0m");
            }
        }
        long finish = Math.min(System.currentTimeMillis(), end);
        double elapsed = (finish - start) / 1000.0;
        double accuracy = (words.length > 0) ? (100.0 * correct / words.length) : 0;
        double wps = (elapsed > 0) ? (double) correct / elapsed : 0;

        // Bonus points: +1 if 4 correct, +2 if all correct AND finished early (still time left)
        int bonus = 0;
        if (correct == 4) {
            bonus = 1;
            System.out.println("Bonus: +1 point for getting 4 correct!");
        } else if (correct == words.length && finish < end) {
            bonus = 2;
            System.out.println("Bonus: +2 points for getting all correct and finishing early!");
        }
        score += bonus;

        System.out.printf("Score: %d | Accuracy: %.2f%% | WPS: %.2f\n", score, accuracy, wps);
        return new RankedTestResult(score, correct, elapsed);
    }
}
