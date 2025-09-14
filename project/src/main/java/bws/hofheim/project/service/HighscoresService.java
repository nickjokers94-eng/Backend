package bws.hofheim.project.service;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service-Klasse für die Verwaltung der Highscores.
 * Diese Klasse enthält Methoden zum Abrufen, Speichern und Verwalten von Highscores.
 */
@Service
public class HighscoresService {

    /**
     * [getHighscores] - Ruft die Top 10 Highscores aus der Datenbank ab.
     * Erstellt von: [Paul Troschke]
     *
     * @return Eine Liste von Maps, die die Highscores enthalten. Jede Map enthält:
     * - "score" (int): Die Punktzahl.
     * - "userid" (int): Die ID des Benutzers.
     * - "username" (String): Den Benutzernamen.
     */
    public List<Map<String, Object>> getHighscores() {
        // Datenbankverbindungsdetails
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

    /**
     * [saveHighscore] - Speichert einen neuen Highscore oder aktualisiert einen bestehenden.
     * Erstellt von: [Nick Jokers]
     *
     * @param username Der Benutzername des Benutzers.
     * @param score Die Punktzahl, die gespeichert oder aktualisiert werden soll.
     */
    public void saveHighscore(String username, int score) {
        // Datenbankverbindungsdetails
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword)) {
            // Hole die Benutzer-ID (userid)
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

            // Prüfen, ob ein Highscore-Eintrag existiert
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

    /**
     * [getAllHighscores] - Ruft alle Highscores aus der Datenbank ab, sortiert nach Punktzahl und Benutzernamen.
     * Erstellt von: [Nick Jokers]
     *
     * @return Eine Liste von Maps, die alle Highscores enthalten. Jede Map enthält:
     * - "score" (int): Die Punktzahl.
     * - "userid" (int): Die ID des Benutzers.
     * - "username" (String): Den Benutzernamen.
     */
    public List<Map<String, Object>> getAllHighscores() {
        // Datenbankverbindungsdetails
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