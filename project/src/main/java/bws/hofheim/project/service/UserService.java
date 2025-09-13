package bws.hofheim.project.service;

import bws.hofheim.project.api.model.User;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service-Klasse für die Verwaltung von Benutzern.
 * Diese Klasse enthält Methoden zum Abrufen, Registrieren, Anmelden, Löschen, Aktualisieren und Verwalten von Benutzern.
 * Erstellt von: [name]
 */
@Service
public class UserService {

    // Datenbankverbindungsdetails
    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    private final String user = "postgres";
    private final String dbPassword = "worti";

    /**
     * [getUser] - Ruft einen Benutzer anhand des Benutzernamens aus der Datenbank ab.
     * Erstellt von: [name]
     *
     * @param username Der Benutzername des gesuchten Benutzers.
     * @return Ein User-Objekt, wenn der Benutzer gefunden wurde, sonst null.
     */
    public User getUser(String username) {
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

    /**
     * [registerUser] - Registriert einen neuen Benutzer, sofern der Benutzername noch nicht existiert.
     * Erstellt von: [name]
     *
     * @param username Der gewünschte Benutzername.
     * @param password Das gewünschte Passwort.
     * @return True, wenn die Registrierung erfolgreich war, andernfalls false.
     */
    public boolean registerUser(String username, String password) {
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

    /**
     * [passwordChange] - Ändert das Passwort eines Benutzers.
     * Erstellt von: [name]
     *
     * @param username Der Benutzername, dessen Passwort geändert werden soll.
     * @param password Das neue Passwort.
     * @return True, wenn das Passwort erfolgreich geändert wurde, andernfalls false.
     */
    public boolean passwordChange(String username, String password) {
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {
            int changedRows = stmt.executeUpdate("UPDATE users SET password = '" + password + "' WHERE username = '" + username + "'");
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
    }

    /**
     * [loginUser] - Meldet einen Benutzer mit Benutzername und Passwort an.
     * Erstellt von: [name]
     *
     * @param username Der Benutzername.
     * @param password Das Passwort.
     * @return Ein User-Objekt, wenn die Anmeldung erfolgreich war, andernfalls null.
     */
    public User loginUser(String username, String password) {
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

    // ADMIN-Funktionen

    /**
     * [unlockUser] - Entsperrt einen Benutzer (setzt Status auf 'active').
     * Erstellt von: [name]
     *
     * @param username Der Benutzername des zu entsperrenden Benutzers.
     * @return True, wenn der Benutzer erfolgreich entsperrt wurde, andernfalls false.
     */
    public boolean unlockUser(String username) {
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {

            int changedRows = stmt.executeUpdate("UPDATE users SET status = 'active' WHERE username = '" + username + "'");
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
    }

    /**
     * [deleteUser] - Löscht einen Benutzer aus der Datenbank.
     * Erstellt von: [name]
     *
     * @param username Der Benutzername des zu löschenden Benutzers.
     * @return True, wenn der Benutzer erfolgreich gelöscht wurde, andernfalls false.
     */
    public boolean deleteUser(String username) {
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {

            int changedRows = stmt.executeUpdate("DELETE FROM users WHERE username = '" + username + "'");
            return changedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;
        }
    }

    /**
     * [createUser] - Erstellt einen neuen Benutzer mit einer bestimmten Rolle.
     * Erstellt von: [name]
     *
     * @param username Der Benutzername.
     * @param password Das Passwort.
     * @param role Die Rolle des Benutzers (z.B. 'user', 'admin').
     * @return True, wenn der Benutzer erfolgreich erstellt wurde, andernfalls false.
     */
    public boolean createUser(String username, String password, String role) {
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             Statement stmt = conn.createStatement()) {
            int changedRows = stmt.executeUpdate("INSERT INTO users (username, password, role, status) VALUES ('" + username + "', '" + password + "', '" + role + "', 'active')");
            return changedRows > 0;
        } catch (SQLException ex) {
            System.err.println("Fehler: " + ex.getMessage());
            return false;

        }
    }

    /**
     * [getLockedUsers] - Ruft alle gesperrten Benutzer (Status 'locked') aus der Datenbank ab.
     * Erstellt von: [name]
     *
     * @return Eine Liste von User-Objekten, die gesperrt sind.
     */
    public List<User> getLockedUsers() {
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

    /**
     * [updateUserRole] - Aktualisiert die Rolle eines Benutzers.
     * Erstellt von: [name]
     *
     * @param username Der Benutzername.
     * @param newRole Die neue Rolle, die zugewiesen werden soll.
     * @return True, wenn die Rolle erfolgreich aktualisiert wurde, andernfalls false.
     */
    public boolean updateUserRole(String username, String newRole) {
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

    /**
     * [createHighscoreEntryForUser] - Legt einen Highscore-Eintrag für einen Benutzer an, falls noch nicht vorhanden.
     * Erstellt von: [name]
     *
     * @param userid Die ID des Benutzers.
     * @param username Der Benutzername.
     */
    public void createHighscoreEntryForUser(int userid, String username) {
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