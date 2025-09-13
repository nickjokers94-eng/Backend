package bws.hofheim.project.api.model;

/**
 * Modellklasse für ein Wort.
 * Entspricht der Tabelle 'words' in der Datenbank.
 * Erstellt von: [Nick Jokers]
 */
public class Word {

    private int wordid;    // Die ID des Wortes
    private String word;   // Das Wort selbst

    /**
     * Konstruktor mit allen Attributen.
     * @param wordid Die Wort-ID.
     * @param word Das Wort.
     */
    public Word(int wordid, String word) {
        this.wordid = wordid;
        this.word = word;
    }

    /**
     * Leerer Konstruktor.
     */
    public Word() {}

    public int getWordid() {
        return wordid;
    }

    public void setWordid(int wordid) {
        this.wordid = wordid;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}