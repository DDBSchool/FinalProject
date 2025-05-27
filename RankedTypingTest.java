package s1finalproject;

import java.util.*;

public class RankedTypingTest {
    private static final int[] POINTS_PER_WORD = {1, 2, 3, 4, 6};
    private static final int[] POINTS_TO_PASS = {1, 3, 6, 10, 18}; // thresholds
    private static final int WORDS_PER_LEVEL = 5; // Always 5 words per level

    private static String repeatSpace(int count) {
        char[] chars = new char[count];
        Arrays.fill(chars, ' ');
        return new String(chars);
    }
    private static String stripAnsi(String str) {
        return str.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    public static void playRanked(Scanner sc, String username) {
        int level = 1;
        boolean passed = true;

        int totalScore = 0;
        int totalCorrect = 0;
        int totalWords = 0;
        double totalTime = 0.0;

        String sessionDate = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        // First level: Start menu
        while (level <= 5 && passed) {
            if (level == 1) {
                System.out.println("===========================================");
                System.out.println("1. Start test");
                System.out.println("2.back");
                System.out.println();
            } else {
                System.out.println("===========================================");
                System.out.println("1. Continue test");
                System.out.println("2.back");
                System.out.println();
            }
            System.out.println("=== Level " + level + " ===");
            System.out.println();

            String menu = sc.nextLine().trim();
            while (!(menu.equals("1") || menu.equals("2"))) {
                System.out.print("Select 1 or 2: ");
                menu = sc.nextLine().trim();
            }
            if (menu.equals("2")) {
                break;
            }

            int timeLimit = Text.getLevelTimeLimit(level);
            String[] allWords = Text.getLevelWords(level);
            String[] words = pickRandomWords(allWords, WORDS_PER_LEVEL);

            RankedTestResult result = runLevel(sc, level, words, timeLimit);

            // Show bonus points gained this level
            System.out.println("Level " + level + " finished! Bonus points gained this level: " + result.score);

            totalScore += result.score;
            totalCorrect += result.correct;
            totalWords += words.length;
            totalTime += result.timeTaken;

            if (result.score >= POINTS_TO_PASS[level - 1]) {
                System.out.println();
                System.out.println("=== Passed level " + level + "! ===");
                System.out.println();
                level++;
            } else {
                System.out.println();
                System.out.println("Did not pass level " + level + ". Try again next time!");
                System.out.println();
                passed = false;
            }
            System.out.println("===========================================");
            System.out.println();
        }

        double finalAccuracy = (totalWords > 0) ? (100.0 * totalCorrect / totalWords) : 0;
        double finalWps = (totalTime > 0) ? (double) totalCorrect / totalTime : 0;
        int finalLevel;
        if (passed && level > 5) {
            finalLevel = 5;
        } else {
            finalLevel = Math.max(1, level - 1);
        }

        if (totalWords > 0) {
            ResultManager.saveResult(username, finalLevel, totalScore, finalAccuracy, finalWps, "ranked", sessionDate);
            // Date is saved, do not print here; show it in the leaderboard as needed
        } else {
            System.out.println("No words attempted, result will not be saved.");
        }

        // Show total stats after the game
        System.out.println("=== Game Summary ===");
        System.out.println("Total Score: " + totalScore);
        System.out.printf("Total Accuracy: %.2f%%\n", finalAccuracy);
        System.out.printf("Total WPS: %.2f\n", finalWps);

        if (passed) {
            System.out.println("===  Test is finished! ===");
            System.out.println();
        }
    }

    private static String[] pickRandomWords(String[] sourceWords, int count) {
        List<String> wordList = new ArrayList<>(Arrays.asList(sourceWords));
        Collections.shuffle(wordList);
        return wordList.subList(0, Math.min(count, wordList.size())).toArray(new String[0]);
    }

    private static class RankedTestResult {
        int score, correct;
        double timeTaken;
        RankedTestResult(int s, int c, double t) {
            score = s; correct = c; timeTaken = t;
        }
    }

    private static RankedTestResult runLevel(Scanner sc, int level, String[] words, int timeLimit) {
        int correct = 0;
        int score = 0;
        long start = System.currentTimeMillis();
        long end = start + timeLimit * 1000L;
        String pink = "\u001B[95m";
        String reset = "\u001B[0m";
        int width = 45;

        for (String word : words) {
            if (System.currentTimeMillis() >= end) break;

            System.out.println();
            int pad = Math.max(0, (width - word.length()) / 2);
            String centered = repeatSpace(pad) + word;
            System.out.println(pink + centered + reset);
            System.out.println();

            System.out.print("Enter: ");
            String input = sc.nextLine();

            if (System.currentTimeMillis() >= end) break;

            System.out.println();
            pad = Math.max(0, (width - input.length()) / 2);
            String centeredInput = repeatSpace(pad) + input;
            System.out.println(centeredInput);
            System.out.println();

            String resultMsg;
            if (input.trim().equals(word)) {
                resultMsg = "\u001B[32mCorrect!\u001B[0m";
                correct++;
                score += POINTS_PER_WORD[level - 1];
            } else {
                resultMsg = "\u001B[31mIncorrect!\u001B[0m";
            }
            pad = Math.max(0, (width - stripAnsi(resultMsg).length()) / 2);
            String centeredResult = repeatSpace(pad) + resultMsg;
            System.out.println(centeredResult);
            System.out.println();

            System.out.println("===========================================");
        }

        long finish = Math.min(System.currentTimeMillis(), end);
        double elapsed = (finish - start) / 1000.0;

        System.out.println("Score:  " + score + " | Accuracy: " + String.format("%.2f", (100.0 * correct / words.length)) + "% | WPS: " + String.format("%.2f", (elapsed > 0 ? (double) correct / elapsed : 0)));
        System.out.println("===========================================");
        System.out.println();

        return new RankedTestResult(score, correct, elapsed);
    }
}