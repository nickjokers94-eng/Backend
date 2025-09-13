package bws.hofheim.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WordsService {


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


    public boolean addWord(String word) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {

            int changedRows = stmt.executeUpdate("INSERT INTO words (word) VALUES ('" + word.toUpperCase() + "')");
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
    }

    public boolean deleteWord(String word) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {

            int changedRows = stmt.executeUpdate("DELETE FROM words WHERE word = '" + word + "'");
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
    }

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
