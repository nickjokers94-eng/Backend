package bws.hofheim.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service-Klasse für die Verwaltung von Wörtern.
 * Diese Klasse enthält Methoden zum Abrufen, Hinzufügen, Löschen, Aktualisieren und Abrufen eines zufälligen Wortes.
 */
@Service
public class WordsService {

    // Datenbankverbindungsdetails
    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    private final String user = "postgres";
    private final String dbPassword = "worti";

    /**
     * [getWords] - Ruft alle Wörter aus der Datenbank ab.
     * Erstellt von: [Paul Troschke]
     *
     * @return Eine Liste von Maps, die die Wörter enthalten. Jede Map enthält:
     * - "wordid" (int): Die ID des Wortes.
     * - "word" (String): Das Wort.
     */
    public List<Map<String, Object>> getWords() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";
        List<Map<String, Object>> words = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT wordid, word FROM words");

            while (rs.next()) {
                Map<String, Object> wordEntry = new HashMap<>();
                wordEntry.put("wordid", rs.getInt("wordid"));
                wordEntry.put("word", rs.getString("word"));
                words.add(wordEntry);
            }

        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
        }

        return words;
    }

    /**
     * [addWord] - Fügt ein neues Wort zur Datenbank hinzu.
     * Erstellt von: [Paul Troschke, Nick Jokers überarbeitet]
     *
     * @param word Das hinzuzufügende Wort.
     * @return True, wenn das Wort erfolgreich hinzugefügt wurde, andernfalls false.
     */
    public boolean addWord(String word) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";
        String upperWord = word.toUpperCase();

        // Prüfen, ob das Wort schon existiert
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM words WHERE word = ?")) {
            checkStmt.setString(1, upperWord);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false;
            }
        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }

        // Einfügen, wenn Wort noch nicht existiert
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO words (word) VALUES (?)")) {
            stmt.setString(1, upperWord);
            int changedRows = stmt.executeUpdate();
            return changedRows > 0;
        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
    }

    /**
     * [deleteWord] - Löscht ein Wort aus der Datenbank.
     * Erstellt von: [Paul Troschke, Nick Jokers überarbeitet]
     *
     * @param word Das zu löschende Wort.
     * @return True, wenn das Wort erfolgreich gelöscht wurde, andernfalls false.
     */
    public boolean deleteWord(String word) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";
        String upperWord = word.toUpperCase();

        // Prüfen, ob das Wort existiert
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM words WHERE word = ?")) {
            checkStmt.setString(1, upperWord);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // Wort existiert nicht
                return false;
            }
        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }

        // Löschen, wenn Wort existiert
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM words WHERE word = ?")) {
            stmt.setString(1, upperWord);
            int changedRows = stmt.executeUpdate();
            return changedRows > 0;
        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
    }

    /**
     * [changeWord] - Aktualisiert ein bestehendes Wort in der Datenbank.
     * Erstellt von: [Paul Troschke]
     *
     * @param word Das zu aktualisierende Wort.
     * @param wordUpdate Der neue Wert für das Wort.
     * @return True, wenn das Wort erfolgreich aktualisiert wurde, andernfalls false.
     */
    public boolean changeWord(String word, String wordUpdate) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {

            int changedRows = stmt.executeUpdate("UPDATE words SET word = '" + wordUpdate + "' WHERE word = '" + word + "'");
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
    }

    /**
     * [getRandomWord] - Ruft ein zufälliges Wort aus der Datenbank ab.
     * Erstellt von: [Nick Jokers]
     *
     * @return Ein zufälliges Wort als String oder "ERROR", wenn ein Fehler auftritt.
     */
    public String getRandomWord() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";
        String randomWord = null;

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT word FROM words ORDER BY RANDOM() LIMIT 1");

            if (rs.next()) {
                randomWord = rs.getString("word");
            }

        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
        }

        return randomWord != null ? randomWord : "ERROR";
    }
}