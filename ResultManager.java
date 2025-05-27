package s1finalproject;

import java.sql.*;
import java.util.*;

public class ResultManager {
    // Save result, now uses user_id instead of username
    public static void saveResult(String username, int level, int score, double accuracy, double wps, String type, String date) {
        UserManager userManager = new UserManager();
        Integer userId = userManager.getUserId(username);
        if (userId == null) {
            System.out.println("User not found, cannot save result.");
            return;
        }
        String sql = "INSERT INTO typing_results (user_id, level, score, accuracy, words_per_second, type, test_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
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

    // Get last 10 games for user and type, joining users to get username if needed
    public static List<GameResult> getUserGames(String username, String type) {
        List<GameResult> results = new ArrayList<>();
        UserManager userManager = new UserManager();
        Integer userId = userManager.getUserId(username);
        if (userId == null) return results;

        String sql = "SELECT tr.test_time, tr.level, tr.score, tr.accuracy, tr.words_per_second " +
                     "FROM typing_results tr WHERE tr.user_id=? AND tr.type=? ORDER BY tr.test_time DESC LIMIT 10";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
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
        UserManager userManager = new UserManager();
        Integer userId = userManager.getUserId(username);
        if (userId == null) return new Stats(0, 0, 0);

        String sql = type.equals("ranked")
            ? "SELECT MAX(score) as max_score, AVG(accuracy) as avg_acc, AVG(words_per_second) as avg_wps FROM typing_results WHERE user_id=? AND type=?"
            : "SELECT AVG(accuracy) as avg_acc, AVG(words_per_second) as avg_wps FROM typing_results WHERE user_id=? AND type=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
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
