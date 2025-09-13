package bws.hofheim.project.api.model;

/**
 * Modellklasse für einen Benutzer.
 * Entspricht der Tabelle 'users' in der Datenbank.
 * Erstellt von: [Paul Troschke]
 */
public class User {

    public int userID;        // Die ID des Benutzers
    public String username;   // Der Benutzername
    public String role;       // Die Rolle des Benutzers (z.B. 'user', 'admin')
    public String status;     // Der Status des Benutzers (z.B. 'active', 'locked')

    /**
     * Konstruktor mit allen Attributen.
     * @param userID Die Benutzer-ID.
     * @param username Der Benutzername.
     * @param role Die Rolle des Benutzers.
     * @param status Der Status des Benutzers.
     */
    public User(int userID, String username, String role, String status){
        this.userID = userID;
        this.username = username;
        this.role = role;
        this.status = status;
    }

    /**
     * Leerer Konstruktor.
     */
    public User(){}

    /**
     * Gibt die Benutzer-ID zurück.
     * @return Die Benutzer-ID.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Setzt die Benutzer-ID.
     * @param userID Die neue Benutzer-ID.
     */
    public void setUserID(int userID){
        this.userID = userID;
    }

    /**
     * Gibt den Benutzernamen zurück.
     * @return Der Benutzername.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setzt den Benutzernamen.
     * @param username Der neue Benutzername.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gibt die Rolle des Benutzers zurück.
     * @return Die Rolle.
     */
    public String getRole() {
        return role;
    }

    /**
     * Setzt die Rolle des Benutzers.
     * @param role Die neue Rolle.
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gibt den Status des Benutzers zurück.
     * @return Der Status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Setzt den Status des Benutzers.
     * @param status Der neue Status.
     */
    public void setStatus(String status) {
        this.status = status;
    }
}