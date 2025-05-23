import java.util.*;

public class UnrankedTypingTest {
    public static void playUnranked(Scanner sc, String username) {
        System.out.print("Select level (1-5): ");
        int level = Integer.parseInt(sc.nextLine());
        System.out.print("Enter time limit in seconds: ");
        int timeLimit = Integer.parseInt(sc.nextLine());
        String[] words = Text.getLevelWords(level);

        int correct = 0;
        long start = System.currentTimeMillis();
        long end = start + timeLimit * 1000L;
        for (String word : words) {
            if (System.currentTimeMillis() >= end) break;
            System.out.println(word);
            System.out.print("Enter: ");
            String input = sc.nextLine();
            if (System.currentTimeMillis() >= end) break;
            if (input.trim().equals(word)) {
                // Print correct answers in green
                System.out.println("\u001B[32mCorrect! You typed: \"" + input + "\"\u001B[0m");
                correct++;
            } else {
                // Print incorrect answers in red
                System.out.println("\u001B[31mIncorrect! You typed: \"" + input + "\" | Expected: \"" + word + "\"\u001B[0m");
            }
        }
        long finish = Math.min(System.currentTimeMillis(), end);
        double elapsed = (finish - start) / 1000.0;
        double accuracy = (100.0 * correct / words.length);
        double wps = (elapsed > 0) ? (double) correct / elapsed : 0;
        String date = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.printf("Accuracy: %.2f%% | WPS: %.2f\n", accuracy, wps);
        ResultManager.saveResult(username, level, 0, accuracy, wps, "unranked", date);
    }
}
