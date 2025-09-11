package bws.hofheim.project.service;

import bws.hofheim.project.api.model.User;
import org.springframework.stereotype.Service;

import java.sql.*;
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
             ResultSet rs = stmt.executeQuery("SELECT userid, username, role, status FROM users WHERE username =" + username)) {

            while (rs.next()) {
                System.out.println("ppppppppppppp");
                User userInDB = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                System.out.println(user);
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

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {
            int changedRows = stmt.executeUpdate("INSERT INTO users (username, password, role, status) VALUES ('" + username + "', '" + password + "', 'user', 'locked')");
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
                if (storedPassword.equals(password)) {
                    // Return user only if credentials are correct
                    return new User(
                            rs.getInt("userid"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.getString("status")
                    );
                } else {
                    System.err.println("Incorrect password.");
                }
            } else {
                System.err.println("User not found.");
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }
        return null; // Return null if login fails
    }

    //ADMIN:

    public boolean unlockUser(String username) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String dbPassword = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {

            int changedRows = stmt.executeUpdate("UPDATE users SET status = 'unlocked' WHERE username = '" + username + "'");
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
            int changedRows = stmt.executeUpdate("INSERT INTO users (username, password, role, status) VALUES ('" + username + "', '" + password + "', '" + role + "', 'unlocked')");
            return changedRows > 0;
        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;

        }
    }

}

