package s1finalproject;

import java.util.Scanner;

public class LeaderboardMenu {
    public static void showLeaderboardMenu(Scanner sc) {
        while (true) {
            System.out.println("\nSelect Level for Leaderboard:");
            System.out.println("1. Level 1");
            System.out.println("2. Level 2");
            System.out.println("3. Level 3");
            System.out.println("4. Level 4");
            System.out.println("5. Level 5");
            System.out.println("6. Back");
            System.out.print("Select option: ");
            String levelInput = sc.nextLine().trim();

            int selectedLevel = -1;
            switch (levelInput) {
                case "1": case "2": case "3": case "4": case "5":
                    selectedLevel = Integer.parseInt(levelInput);
                    showLevelLeaderboard(sc, selectedLevel);
                    break;
                case "6":
                    return; // Back to main menu
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

    private static void showLevelLeaderboard(Scanner sc, int level) {
        while (true) {
            System.out.println("\n=== Leaderboard Menu ===");
            System.out.println("Level " + level);
            System.out.println("1. Show the Top 10 Fastest (Unique Players)");
            System.out.println("2. Show the Top 10 by Accuracy (Unique Players)");
            System.out.println("3. Show the Top 10 Most Points (Unique Players)");
            System.out.println("4. Show Top 10 Attempts (All Attempts)");
            System.out.println("5. Back");
            System.out.print("Select option: ");
            String lbInput = sc.nextLine().trim();

            switch (lbInput) {
                case "1":
                    Leaderboard.showTop10Unique("wps", level);
                    break;
                case "2":
                    Leaderboard.showTop10Unique("accuracy", level);
                    break;
                case "3":
                    Leaderboard.showTop10Unique("score", level);
                    break;
                case "4":
                    Leaderboard.showTop10Attempts(level);
                    break;
                case "5":
                    return; // Back to level select
                default:
                    System.out.println("Invalid input.");
            }
        }
    }
}
