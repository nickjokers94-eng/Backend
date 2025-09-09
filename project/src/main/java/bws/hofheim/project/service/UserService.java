package bws.hofheim.project.service;

import bws.hofheim.project.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public User getUser(String username) {
        String sql = "SELECT userid, username, role, status FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("userid"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("status")
                );
            }
        } catch (SQLException ex) {
            System.err.println("Fehler beim Abrufen des Users: " + ex.getMessage());
        }
        return null;
    }

    public boolean registerUser(String username, String password) {
        // Validierung
        if (username == null || username.trim().length() < 3 ||
                password == null || password.length() < 3) {
            return false;
        }

        // Prüfen ob User bereits existiert
        if (getUser(username) != null) {
            return false;
        }

        String hashedPassword = passwordEncoder.encode(password);
        String sql = "INSERT INTO users (username, password, role, status) VALUES (?, ?, 'user', 'locked')";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            int changedRows = stmt.executeUpdate();
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler bei der Registrierung: " + ex.getMessage());
            return false;
        }
    }

    public User loginUser(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        String sql = "SELECT userid, username, password, role, status FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String status = rs.getString("status");

                // Passwort prüfen
                if (passwordEncoder.matches(password, storedPassword)) {
                    // Status prüfen
                    if ("unlocked".equals(status)) {
                        return new User(
                                rs.getInt("userid"),
                                rs.getString("username"),
                                rs.getString("role"),
                                status
                        );
                    } else {
                        throw new RuntimeException("Account ist noch nicht freigeschaltet");
                    }
                } else {
                    throw new RuntimeException("Ungültige Anmeldedaten");
                }
            } else {
                throw new RuntimeException("Benutzer nicht gefunden");
            }
        } catch (SQLException ex) {
            System.err.println("Fehler beim Login: " + ex.getMessage());
            throw new RuntimeException("Datenbankfehler beim Login");
        }
    }

    public boolean passwordChange(String username, String password) {
        if (password == null || password.length() < 3) {
            return false;
        }

        String hashedPassword = passwordEncoder.encode(password);
        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hashedPassword);
            stmt.setString(2, username);

            int changedRows = stmt.executeUpdate();
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler beim Passwort ändern: " + ex.getMessage());
            return false;
        }
    }

    // ADMIN-Funktionen
    public boolean unlockUser(String username) {
        String sql = "UPDATE users SET status = 'unlocked' WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            int changedRows = stmt.executeUpdate();
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler beim Freischalten: " + ex.getMessage());
            return false;
        }
    }

    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            int changedRows = stmt.executeUpdate();
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler beim Löschen: " + ex.getMessage());
            return false;
        }
    }

    public boolean createUser(String username, String password, String role) {
        if (username == null || password == null || role == null) {
            return false;
        }

        String hashedPassword = passwordEncoder.encode(password);
        String sql = "INSERT INTO users (username, password, role, status) VALUES (?, ?, ?, 'unlocked')";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, role);

            int changedRows = stmt.executeUpdate();
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler beim Erstellen: " + ex.getMessage());
            return false;
        }
    }

    // Neue Methode für Admin-Panel: Alle Users abrufen
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT userid, username, role, status FROM users ORDER BY userid";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("userid"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException ex) {
            System.err.println("Fehler beim Abrufen aller Users: " + ex.getMessage());
        }

        return users;
    }

    // Neue Methode für Punktemanagement
    public boolean updateUserScore(String username, int score) {
        String sql = "INSERT INTO highscores (userid, username, score, timestamp) " +
                "SELECT userid, ?, ?, NOW() FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setInt(2, score);
            stmt.setString(3, username);

            int changedRows = stmt.executeUpdate();
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler beim Speichern der Punkte: " + ex.getMessage());
            return false;
        }
    }
}