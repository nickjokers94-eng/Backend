package bws.hofheim.project.api.model;

/**
 * Modellklasse für einen Highscore-Eintrag.
 * Entspricht der Tabelle 'highscores' in der Datenbank.
 * Erstellt von: [Nick Jokers]
 */
public class Highscore {

    private int userid;        // Die ID des Benutzers
    private String username;   // Der Benutzername
    private int score;         // Der Punktestand

    /**
     * Konstruktor mit allen Attributen.
     * @param userid Die Benutzer-ID.
     * @param username Der Benutzername.
     * @param score Der Punktestand.
     */
    public Highscore(int userid, String username, int score) {
        this.userid = userid;
        this.username = username;
        this.score = score;
    }

    /**
     * Leerer Konstruktor.
     */
    public Highscore() {}

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}