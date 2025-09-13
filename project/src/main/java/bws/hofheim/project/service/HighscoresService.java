package bws.hofheim.project.service;

// import bws.hofheim.project.api.model.User;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HighscoresService {

    public List<Map<String, Object>> getHighscores() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";
        List<Map<String, Object>> highscores = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT score, userid, username FROM highscores ORDER BY score DESC LIMIT 10");

            while (rs.next()) {
                Map<String, Object> highscore = new HashMap<>();
                highscore.put("score", rs.getInt("score"));
                highscore.put("userid", rs.getInt("userid"));
                highscore.put("username", rs.getString("username"));
                highscores.add(highscore);
            }

        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
        }
        return highscores;
    }
    public void saveHighscore(String username, int score) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword)) {
            // Hole userid
            int userid = -1;
            try (PreparedStatement userStmt = conn.prepareStatement("SELECT userid FROM users WHERE username = ?")) {
                userStmt.setString(1, username);
                ResultSet rs = userStmt.executeQuery();
                if (rs.next()) {
                    userid = rs.getInt("userid");
                }
            }
            if (userid == -1) {
                System.err.println("Kein User gefunden für Highscore: " + username);
                return;
            }

            // Prüfen, ob Highscore-Eintrag existiert
            String selectSql = "SELECT score FROM highscores WHERE userid = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, userid);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    int oldScore = rs.getInt("score");
                    if (score > oldScore) {
                        // Highscore aktualisieren
                        String updateSql = "UPDATE highscores SET score = ? WHERE userid = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, score);
                            updateStmt.setInt(2, userid);
                            updateStmt.executeUpdate();
                        }
                    }
                } else {
                    // Neuer Highscore-Eintrag
                    String insertSql = "INSERT INTO highscores (userid, username, score) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, userid);
                        insertStmt.setString(2, username);
                        insertStmt.setInt(3, score);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Fehler beim Speichern des Highscores: " + ex.getMessage());
        }
    }
    public List<Map<String, Object>> getAllHighscores() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";
        List<Map<String, Object>> highscores = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT score, userid, username FROM highscores ORDER BY score DESC, username ASC");

            while (rs.next()) {
                Map<String, Object> highscore = new HashMap<>();
                highscore.put("score", rs.getInt("score"));
                highscore.put("userid", rs.getInt("userid"));
                highscore.put("username", rs.getString("username"));
                highscores.add(highscore);
            }

        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
        }
        return highscores;
    }
}
