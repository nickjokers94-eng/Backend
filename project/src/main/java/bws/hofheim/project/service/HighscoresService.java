package bws.hofheim.project.service;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HighscoresService {

    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";
        return DriverManager.getConnection(url, user, dbPassword);
    }

    public List<Map<String, Object>> getHighscores() {
        List<Map<String, Object>> highscores = new ArrayList<>();
        String sql = "SELECT h.score, h.userid, h.username, h.timestamp " +
                "FROM highscores h " +
                "ORDER BY h.score DESC, h.timestamp ASC " +
                "LIMIT 20";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> highscore = new HashMap<>();
                highscore.put("score", rs.getInt("score"));
                highscore.put("userid", rs.getInt("userid"));
                highscore.put("username", rs.getString("username"));

                // Timestamp formatieren
                Timestamp timestamp = rs.getTimestamp("timestamp");
                if (timestamp != null) {
                    LocalDateTime dateTime = timestamp.toLocalDateTime();
                    highscore.put("timestamp", dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                } else {
                    highscore.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                }

                highscores.add(highscore);
            }

        } catch (SQLException ex) {
            System.err.println("Fehler beim Abrufen der Highscores: " + ex.getMessage());
        }

        return highscores;
    }

    public boolean saveScore(String username, int score) {
        // Zuerst User-ID abrufen
        Integer userId = getUserId(username);
        if (userId == null) {
            System.err.println("User nicht gefunden: " + username);
            return false;
        }

        String sql = "INSERT INTO highscores (userid, username, score, timestamp) VALUES (?, ?, ?, NOW())";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, username);
            stmt.setInt(3, score);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Score gespeichert für " + username + ": " + score + " Punkte");
            return rowsAffected > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler beim Speichern des Scores: " + ex.getMessage());
            return false;
        }
    }

    public List<Map<String, Object>> getUserHighscores(String username) {
        List<Map<String, Object>> userHighscores = new ArrayList<>();
        String sql = "SELECT score, timestamp FROM highscores WHERE username = ? ORDER BY score DESC, timestamp DESC LIMIT 10";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> highscore = new HashMap<>();
                highscore.put("score", rs.getInt("score"));

                Timestamp timestamp = rs.getTimestamp("timestamp");
                if (timestamp != null) {
                    LocalDateTime dateTime = timestamp.toLocalDateTime();
                    highscore.put("timestamp", dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                } else {
                    highscore.put("timestamp", "Unbekannt");
                }

                userHighscores.add(highscore);
            }

        } catch (SQLException ex) {
            System.err.println("Fehler beim Abrufen der User-Highscores: " + ex.getMessage());
        }

        return userHighscores;
    }

    public Map<String, Object> getPlayerStats(String username) {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT " +
                "COUNT(*) as total_games, " +
                "MAX(score) as best_score, " +
                "AVG(score) as avg_score, " +
                "SUM(score) as total_score " +
                "FROM highscores WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                stats.put("totalGames", rs.getInt("total_games"));
                stats.put("bestScore", rs.getInt("best_score"));
                stats.put("avgScore", Math.round(rs.getDouble("avg_score")));
                stats.put("totalScore", rs.getLong("total_score"));
            } else {
                stats.put("totalGames", 0);
                stats.put("bestScore", 0);
                stats.put("avgScore", 0);
                stats.put("totalScore", 0);
            }

        } catch (SQLException ex) {
            System.err.println("Fehler beim Abrufen der Spielerstatistiken: " + ex.getMessage());
            // Fallback-Werte
            stats.put("totalGames", 0);
            stats.put("bestScore", 0);
            stats.put("avgScore", 0);
            stats.put("totalScore", 0);
        }

        return stats;
    }

    public boolean clearAllHighscores() {
        String sql = "DELETE FROM highscores";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Alle Highscores gelöscht. Anzahl Datensätze: " + rowsAffected);
            return true;

        } catch (SQLException ex) {
            System.err.println("Fehler beim Löschen aller Highscores: " + ex.getMessage());
            return false;
        }
    }

    public boolean clearUserHighscores(String username) {
        String sql = "DELETE FROM highscores WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Highscores für " + username + " gelöscht. Anzahl: " + rowsAffected);
            return rowsAffected > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler beim Löschen der User-Highscores: " + ex.getMessage());
            return false;
        }
    }

    private Integer getUserId(String username) {
        String sql = "SELECT userid FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("userid");
            }

        } catch (SQLException ex) {
            System.err.println("Fehler beim Abrufen der User-ID: " + ex.getMessage());
        }

        return null;
    }

    // Methode für Leaderboard mit Ranking
    public List<Map<String, Object>> getTopPlayers(int limit) {
        List<Map<String, Object>> topPlayers = new ArrayList<>();
        String sql = "SELECT username, MAX(score) as best_score, COUNT(*) as games_played, SUM(score) as total_score " +
                "FROM highscores " +
                "GROUP BY username " +
                "ORDER BY best_score DESC, total_score DESC " +
                "LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            int rank = 1;
            while (rs.next()) {
                Map<String, Object> player = new HashMap<>();
                player.put("rank", rank++);
                player.put("username", rs.getString("username"));
                player.put("bestScore", rs.getInt("best_score"));
                player.put("gamesPlayed", rs.getInt("games_played"));
                player.put("totalScore", rs.getLong("total_score"));

                topPlayers.add(player);
            }

        } catch (SQLException ex) {
            System.err.println("Fehler beim Abrufen der Top-Spieler: " + ex.getMessage());
        }

        return topPlayers;
    }
}