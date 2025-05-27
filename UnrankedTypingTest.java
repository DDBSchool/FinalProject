package s1finalproject;

import java.util.*;

public class UnrankedTypingTest {
    private static String repeatSpace(int count) {
        char[] chars = new char[count];
        Arrays.fill(chars, ' ');
        return new String(chars);
    }

    private static String stripAnsi(String str) {
        return str.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    public static void playUnranked(Scanner sc, String username) {
        System.out.print("Select level (1-5): ");
        int level = Integer.parseInt(sc.nextLine());
        System.out.print("Enter time limit in seconds: ");
        int timeLimit = Integer.parseInt(sc.nextLine());
        String[] allWords = Text.getLevelWords(level);

        // Show schema menu before start
        System.out.println("===========================================");
        System.out.println("1. Start test");
        System.out.println("2.back");
        System.out.println();
        System.out.println("=== Level " + level + " ===");
        System.out.println();

        String menu = sc.nextLine().trim();
        while (!(menu.equals("1") || menu.equals("2"))) {
            System.out.print("Select 1 or 2: ");
            menu = sc.nextLine().trim();
        }
        if (menu.equals("2")) {
            return;
        }

        // Shuffle and pick 10 random words
        List<String> wordList = new ArrayList<>(Arrays.asList(allWords));
        Collections.shuffle(wordList);
        List<String> selectedWords = wordList.subList(0, Math.min(10, wordList.size()));

        int correct = 0;
        long start = System.currentTimeMillis();
        long end = start + timeLimit * 1000L;
        String pink = "\u001B[95m"; // bright pink (may appear magenta in some terminals)
        String reset = "\u001B[0m";
        int width = 45; // width for centering

        for (String word : selectedWords) {
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
        double accuracy = (100.0 * correct / selectedWords.size());
        double wps = (elapsed > 0) ? (double) correct / elapsed : 0;

        // Save and inform the player with date/time
        String date = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println("===========================================");
        System.out.println("=== Game Summary ===");
        System.out.println("Score:  " + correct);
        System.out.printf("Total Accuracy: %.2f%%\n", accuracy);
        System.out.printf("Total WPS: %.2f\n", wps);
        System.out.println("===========================================");
        System.out.println();

        ResultManager.saveResult(username, level, 0, accuracy, wps, "unranked", date);

        // Inform player of date and time played
        System.out.println("You played on: " + date);
        System.out.println();
    }
}