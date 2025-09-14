package bws.hofheim.project.api.controller;

import bws.hofheim.project.service.WordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller-Klasse für die Verwaltung von Wörtern.
 * Diese Klasse stellt Endpunkte für Operationen wie Abrufen, Hinzufügen, Löschen, Aktualisieren und Abrufen eines zufälligen Wortes bereit.
 */
@RestController
public class WordsController {

    // Service für die Wörterverwaltung
    private WordsService wordsService;

    /**
     * Konstruktor für die Initialisierung des WordsService.
     * Erstellt von: [Nick Jokers]
     *
     * @param wordsService Der Service, der für die Wörterverwaltung verwendet wird.
     */
    @Autowired
    public WordsController(WordsService wordsService) {
        this.wordsService = wordsService;
    }

    /**
     * [getWords] - Ruft alle Wörter aus der Datenbank ab.
     * Erstellt von: [Paul Troschke]
     *
     * @return Eine Liste von Maps, die die Wörter enthalten.
     */
    @GetMapping("/words")
    public List<Map<String, Object>> getWords() {
        return wordsService.getWords();
    }

    /**
     * [addWord] - Fügt ein neues Wort zur Datenbank hinzu.
     * Erstellt von: [Paul Troschke]
     *
     * @param word Das hinzuzufügende Wort.
     * @return True, wenn das Wort erfolgreich hinzugefügt wurde, andernfalls false.
     */
    @PostMapping("/words/addWord")
    public boolean addWord(@RequestParam String word) {
        return wordsService.addWord(word);
    }

    /**
     * [deleteWord] - Löscht ein Wort aus der Datenbank.
     * Erstellt von: [Nick Jokers]
     *
     * @param word Das zu löschende Wort.
     * @return True, wenn das Wort erfolgreich gelöscht wurde, andernfalls false.
     */
    @DeleteMapping("/words/deleteWord")
    public boolean deleteWord(@RequestParam String word) {
        return wordsService.deleteWord(word);
    }

    /**
     * [changeWord] - Aktualisiert ein bestehendes Wort in der Datenbank.
     * Erstellt von: [Nick Jokers]
     *
     * @param word Das zu aktualisierende Wort.
     * @param wordUpdate Der neue Wert für das Wort.
     * @return True, wenn das Wort erfolgreich aktualisiert wurde, andernfalls false.
     */
    @PutMapping("/words/changeWord")
    public boolean changeWord(@RequestParam String word, String wordUpdate) {
        return wordsService.changeWord(word, wordUpdate);
    }

    /**
     * [getRandomWord] - Ruft ein zufälliges Wort aus der Datenbank ab.
     * Erstellt von: [Nick Jokers]
     *
     * @return Ein zufälliges Wort als String oder "ERROR", wenn ein Fehler auftritt.
     */
    @GetMapping("/words/randomWord")
    public String getRandomWord() {
        return wordsService.getRandomWord();
    }
}