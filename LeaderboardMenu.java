package s1finalproject;

	import java.util.Scanner;
	
	public class LeaderboardMenu {
	    public static void showLeaderboardMenu(Scanner sc) {
	        while (true) {
	            System.out.println("\n=== Leaderboard Menu ===");
	            System.out.println("1. Show the Top 10 Fastest (Unique Players)");
	            System.out.println("2. Show the Top 10 by Accuracy (Unique Players)");
	            System.out.println("3. Show the Top 10 Most Points (Unique Players)");
	            System.out.println("4. Show Top 10 Attempts (All Attempts)");
	            System.out.println("5. Back");
	            System.out.print("Enter choice: ");
	            String choice = sc.nextLine().trim();
	            switch (choice) {
	                case "1":
	                    Leaderboard.showTop10Unique("wps");
	                    break;
	                case "2":
	                    Leaderboard.showTop10Unique("accuracy");
	                    break;
	                case "3":
	                    Leaderboard.showTop10Unique("score");
	                    break;
	                case "4":
	                    Leaderboard.showTop10Attempts();
	                    break;
	                case "5":
	                    return;
	                default:
	                    System.out.println("Invalid choice. Try again.");
	            }
	        }
	    }
	}