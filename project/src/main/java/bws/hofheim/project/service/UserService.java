package bws.hofheim.project.service;

import bws.hofheim.project.api.model.User;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
@Service
public class UserService {

    public User getUser(String username) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT userid, username, role, status FROM users WHERE username = '" + username + "'")) {

            if (rs.next()) {
                User userInDB = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                return userInDB;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    public boolean registerUser(String username, String password) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";
        // Prüfen, ob Username schon existiert
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?")) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Username existiert bereits!
                return false;
            }
        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
        // Hinzufügen, wenn Username noch nicht existiert
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username, password, role, status) VALUES (?, ?, 'user', 'locked')")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            int changedRows = stmt.executeUpdate();
            return changedRows > 0;
        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
    }


    public boolean passwordChange(String username, String password) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {
            int changedRows = stmt.executeUpdate("UPDATE users SET password = '" + password + "' WHERE username = '" + username + "'");
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
    }

    public User loginUser(String username, String password) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";

        String query = "SELECT userid, username, role, status, password FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String status = rs.getString("status");
                if (!storedPassword.equals(password)) {
                    return null;
                }
                if ("locked".equals(status)) {
                    return new User(
                            rs.getInt("userid"),
                            rs.getString("username"),
                            rs.getString("role"),
                            "locked"
                    );
                }
                if ("active".equals(status)) {
                    return new User(
                            rs.getInt("userid"),
                            rs.getString("username"),
                            rs.getString("role"),
                            "active"
                    );
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }
        return null;
    }

    //ADMIN:

    public boolean unlockUser(String username) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {

            int changedRows = stmt.executeUpdate("UPDATE users SET status = 'active' WHERE username = '" + username + "'");
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
    }

    public boolean deleteUser(String username) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {

            int changedRows = stmt.executeUpdate("DELETE FROM users WHERE username = '" + username + "'");
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
    }

    public boolean createUser(String username, String password, String role) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {
            int changedRows = stmt.executeUpdate("INSERT INTO users (username, password, role, status) VALUES ('" + username + "', '" + password + "', '" + role + "', 'active')");
            return changedRows > 0;
        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;

        }
    }
    public List<User> getLockedUsers() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";
        List<User> lockedUsers = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT userid, username, role, status FROM users WHERE status = 'locked'")) {

            while (rs.next()) {
                lockedUsers.add(new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return lockedUsers;
    }
    public boolean updateUserRole(String username, String newRole) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE users SET role = ? WHERE username = ?")) {
            stmt.setString(1, newRole);
            stmt.setString(2, username);
            int changedRows = stmt.executeUpdate();
            return changedRows > 0;
        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
    }
    public void createHighscoreEntryForUser(int userid, String username) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO highscores (userid, username, score) VALUES (?, ?, 0) ON CONFLICT (userid) DO NOTHING"
             )) {
            stmt.setInt(1, userid);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Fehler beim Anlegen des Highscore-Eintrags: " + ex.getMessage());
        }
    }
}

