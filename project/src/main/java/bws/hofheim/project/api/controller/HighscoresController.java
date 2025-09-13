package bws.hofheim.project.api.controller;

import bws.hofheim.project.service.HighscoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller-Klasse für die Verwaltung der Highscores.
 * Diese Klasse stellt Endpunkte für das Abrufen und Speichern von Highscores bereit.
 * Erstellt von: [name]
 */
@RestController
public class HighscoresController {

    // Service für die Highscore-Verwaltung
    private HighscoresService highscoresService;

    /**
     * Konstruktor für die Initialisierung des HighscoresService.
     * Erstellt von: [name]
     *
     * @param highscoresService Der Service, der für die Highscore-Verwaltung verwendet wird.
     */
    @Autowired
    public HighscoresController(HighscoresService highscoresService) {
        this.highscoresService = highscoresService;
    }

    /**
     * [getHighscores] - Ruft die Top 10 Highscores ab.
     * Erstellt von: [name]
     *
     * @return Eine Liste von Maps, die die Highscores enthalten.
     */
    @GetMapping("/highscores")
    public List<Map<String, Object>> getHighscores() {
        return highscoresService.getHighscores();
    }

    /**
     * [saveHighscore] - Speichert einen neuen Highscore oder aktualisiert einen bestehenden.
     * Erstellt von: [name]
     *
     * @param payload Ein JSON-Objekt, das den Benutzernamen und die Punktzahl enthält.
     */
    @PostMapping("/highscores/save")
    public void saveHighscore(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        int score = (int) payload.get("score");
        highscoresService.saveHighscore(username, score);
    }

    /**
     * [getAllHighscores] - Ruft alle Highscores ab, sortiert nach Punktzahl und Benutzernamen.
     * Erstellt von: [name]
     *
     * @return Eine Liste von Maps, die alle Highscores enthalten.
     */
    @GetMapping("/highscores/all")
    public List<Map<String, Object>> getAllHighscores() {
        return highscoresService.getAllHighscores();
    }
}