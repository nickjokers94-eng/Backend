package bws.hofheim.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WordsService {

    @Autowired
    private DataSource dataSource;

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public List<Map<String, Object>> getWords() {
        List<Map<String, Object>> words = new ArrayList<>();
        String sql = "SELECT wordid, word FROM words ORDER BY wordid";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> wordEntry = new HashMap<>();
                wordEntry.put("wordid", rs.getInt("wordid"));
                wordEntry.put("word", rs.getString("word"));
                words.add(wordEntry);
            }

        } catch (SQLException ex) {
            System.err.println("Fehler beim Abrufen der Wörter: " + ex.getMessage());
            throw new RuntimeException("Datenbankfehler beim Abrufen der Wörter", ex);
        }

        return words;
    }


    public boolean addWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }

        String sql = "INSERT INTO words (word) VALUES (?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, word.trim());
            int changedRows = stmt.executeUpdate();
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler beim Hinzufügen des Wortes: " + ex.getMessage());
            return false;
        }
    }

    public boolean deleteWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }

        String sql = "DELETE FROM words WHERE word = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, word);
            int changedRows = stmt.executeUpdate();
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler beim Löschen des Wortes: " + ex.getMessage());
            return false;
        }
    }

    public boolean changeWord(String word, String wordUpdate) {
        if (word == null || word.trim().isEmpty() || 
            wordUpdate == null || wordUpdate.trim().isEmpty()) {
            return false;
        }

        String sql = "UPDATE words SET word = ? WHERE word = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, wordUpdate.trim());
            stmt.setString(2, word);
            int changedRows = stmt.executeUpdate();
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler beim Ändern des Wortes: " + ex.getMessage());
            return false;
        }
    }

    public String randomWord() {
        String sql = "SELECT word FROM words ORDER BY RANDOM() LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getString("word");
            }

        } catch (SQLException ex) {
            System.err.println("Fehler beim Abrufen eines zufälligen Wortes: " + ex.getMessage());
        }
        
        return "fallback"; // Fallback-Wert
    }
}

