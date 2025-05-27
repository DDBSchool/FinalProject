package s1finalproject;

import java.util.*;

public class ProfileManager {
    public static void showProfile(Scanner sc, String username) {
        while (true) {
            System.out.println("\nView profile for:");
            System.out.println("1. Ranked");
            System.out.println("2. Unranked");
            System.out.println("3. Back");
            System.out.print("Select: ");
            String inp = sc.nextLine();
            if (inp.equals("1") || inp.equals("2")) {
                String type = inp.equals("1") ? "ranked" : "unranked";
                List<ResultManager.GameResult> games = ResultManager.getUserGames(username, type);
                ResultManager.Stats stats = ResultManager.getStats(username, type);
                System.out.println("\nLast 10 " + type + " games:");
                for (ResultManager.GameResult g : games) {
                    System.out.printf("Date: %s | Level: %d | Score: %d | Acc: %.2f%% | WPS: %.2f\n",
                        g.date, g.level, g.score, g.accuracy, g.wps);
                }
                if (type.equals("ranked")) {
                    System.out.printf("Highest Score: %d | Avg Acc: %.2f%% | Avg WPS: %.2f\n",
                        stats.maxScore, stats.avgAcc, stats.avgWps);
                } else {
                    System.out.printf("Avg Acc: %.2f%% | Avg WPS: %.2f\n", stats.avgAcc, stats.avgWps);
                }
            } else if (inp.equals("3")) {
                break;
            } else {
                System.out.println("Invalid input.");
            }
        }
    }
}