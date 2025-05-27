package s1finalproject;

import java.sql.*;
import java.util.*;

public class Leaderboard {

    // Top 10 unique players by selected metric
    public static void showTop10Unique(String metric) {
        List<ResultRow> results = getAllResultsSortedBy(metric);
        LinkedHashMap<String, ResultRow> bestPerUser = new LinkedHashMap<>();
        for (ResultRow r : results) {
            if (!bestPerUser.containsKey(r.username)) bestPerUser.put(r.username, r);
            if (bestPerUser.size() == 10) break;
        }
        printLeaderboard(new ArrayList<>(bestPerUser.values()), metric, true);
    }

    // Top 10 attempts (all attempts)
    public static void showTop10Attempts() {
        List<ResultRow> results = getAllResultsSortedBy("wps");
        printLeaderboard(results.subList(0, Math.min(10, results.size())), "wps", false);
    }

    // Fetch all results sorted by given metric (wps, accuracy, score)
    private static List<ResultRow> getAllResultsSortedBy(String metric) {
        String orderBy;
        switch (metric) {
            case "score": orderBy = "score DESC"; break;
            case "accuracy": orderBy = "accuracy DESC"; break;
            case "wps":
            default: orderBy = "words_per_second DESC"; break;
        }
        // Join users and typing_results to get username and result metrics
        String sql = "SELECT u.username, tr.score, tr.accuracy, tr.words_per_second, tr.test_time " +
                     "FROM typing_results tr JOIN users u ON tr.user_id = u.id " +
                     "ORDER BY " + orderBy;
        List<ResultRow> results = new ArrayList<>();
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(new ResultRow(
                    rs.getString("username"),
                    rs.getInt("score"),
                    rs.getDouble("accuracy"),
                    rs.getDouble("words_per_second"),
                    rs.getString("test_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Print formatted leaderboard
    private static void printLeaderboard(List<ResultRow> list, String metric, boolean unique) {
        String title = "Top 10 " + (metric.equals("wps") ? "Fastest" : metric.equals("accuracy") ? "by Accuracy" : "Most Points");
        if (unique) title += " (Unique Players)";
        else title += " (All Attempts)";
        System.out.println("\n=== " + title + " ===");
        System.out.printf("%-5s %-15s %-10s %-10s %-10s %-20s\n", "Rank", "Username", "Score", "Accuracy", "WPS", "Date");
        int rank = 1;
        for (ResultRow r : list) {
            System.out.printf("%-5d %-15s %-10d %-10.2f %-10.2f %-20s\n",
                rank++, r.username, r.score, r.accuracy, r.wps, r.date);
        }
        if (list.isEmpty()) {
            System.out.println("No data.");
        }
        System.out.println();
    }

    // Row object for leaderboard
    private static class ResultRow {
        String username, date;
        int score;
        double accuracy, wps;
        ResultRow(String u, int s, double a, double w, String d) {
            username = u; score = s; accuracy = a; wps = w; date = d;
        }
    }
}
