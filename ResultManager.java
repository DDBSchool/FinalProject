import java.sql.*;
import java.util.*;

public class ResultManager {
    // Save result, now includes type and date
    public static void saveResult(String username, int level, int score, double accuracy, double wps, String type, String date) {
        String sql = "INSERT INTO typing_results (username, level, score, accuracy, words_per_second, type, test_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, level);
            stmt.setInt(3, score);
            stmt.setDouble(4, accuracy);
            stmt.setDouble(5, wps);
            stmt.setString(6, type);
            stmt.setString(7, date);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Show leaderboard (top 10 for each category)
    public static void showLeaderboard() {
        System.out.println("\n=== Top 10 Fastest (Words Per Second) ===");
        showTop10("wps");
        System.out.println("\n=== Top 10 Accuracy ===");
        showTop10("accuracy");
        System.out.println("\n=== Top 10 Most Points ===");
        showTop10("score");
    }

    public static void showTop10(String category) {
        String sql;
        String statLabel;
        switch (category) {
            case "wps":
                sql = "SELECT username, words_per_second, score, accuracy, test_time FROM typing_results ORDER BY words_per_second DESC LIMIT 10";
                statLabel = "Words Per Second";
                break;
            case "accuracy":
                sql = "SELECT username, accuracy, score, words_per_second, test_time FROM typing_results ORDER BY accuracy DESC LIMIT 10";
                statLabel = "Accuracy";
                break;
            case "score":
                sql = "SELECT username, score, accuracy, words_per_second, test_time FROM typing_results ORDER BY score DESC LIMIT 10";
                statLabel = "Score";
                break;
            default:
                System.out.println("Unknown ranking category.");
                return;
        }

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int rank = 1;
            System.out.println("\nTop 10 by " + statLabel + ":");
            System.out.printf("%-4s %-15s %-10s %-10s %-10s %-20s\n", "Rank", "Username", "Score", "Accuracy", "WPS", "Date");
            while (rs.next()) {
                String username = rs.getString("username");
                int score = rs.getInt("score");
                double accuracy = rs.getDouble("accuracy");
                double wps = rs.getDouble("words_per_second");
                String date = rs.getString("test_time");
                System.out.printf("%-4d %-15s %-10d %-10.2f %-10.2f %-20s\n",
                        rank++, username, score, accuracy, wps, date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get last 10 games for user and type
    public static List<GameResult> getUserGames(String username, String type) {
        List<GameResult> results = new ArrayList<>();
        String sql = "SELECT * FROM typing_results WHERE username=? AND type=? ORDER BY test_time DESC LIMIT 10";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(new GameResult(
                    rs.getString("test_time"), rs.getInt("level"),
                    rs.getInt("score"), rs.getDouble("accuracy"),
                    rs.getDouble("words_per_second")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }

    // Get stats for user and type
    public static Stats getStats(String username, String type) {
        String sql = type.equals("ranked")
            ? "SELECT MAX(score) as max_score, AVG(accuracy) as avg_acc, AVG(words_per_second) as avg_wps FROM typing_results WHERE username=? AND type=?"
            : "SELECT AVG(accuracy) as avg_acc, AVG(words_per_second) as avg_wps FROM typing_results WHERE username=? AND type=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (type.equals("ranked")) {
                    return new Stats(rs.getInt("max_score"), rs.getDouble("avg_acc"), rs.getDouble("avg_wps"));
                } else {
                    return new Stats(0, rs.getDouble("avg_acc"), rs.getDouble("avg_wps"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return new Stats(0, 0, 0);
    }

    public static class GameResult {
        public String date; public int level, score; public double accuracy, wps;
        public GameResult(String d, int l, int s, double a, double w) { date=d; level=l; score=s; accuracy=a; wps=w; }
    }
    public static class Stats {
        public int maxScore; public double avgAcc, avgWps;
        public Stats(int m, double a, double w) { maxScore=m; avgAcc=a; avgWps=w; }
    }
}
