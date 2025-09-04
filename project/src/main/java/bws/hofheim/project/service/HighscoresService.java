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
}
