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

    @GetMapping("/words")
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

    @PostMapping("/words/addWord")
    public boolean addWord(String word) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {

            int changedRows = stmt.executeUpdate("INSERT INTO words (word) VALUES ('" + word + "')");
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
    }
    }
