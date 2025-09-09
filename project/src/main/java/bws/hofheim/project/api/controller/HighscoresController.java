package bws.hofheim.project.api.controller;

import bws.hofheim.project.service.HighscoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HighscoresController {

    private HighscoresService highscoresService;

    @Autowired
    public HighscoresController(HighscoresService highscoresService) {
        this.highscoresService = highscoresService;
    }

    @GetMapping("/highscores")
    public ResponseEntity<?> getHighscores() {
        try {
            List<Map<String, Object>> highscores = highscoresService.getHighscores();
            return ResponseEntity.ok(highscores);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Fehler beim Abrufen der Highscores: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/highscores/save")
    public ResponseEntity<?> saveScore(@RequestParam String username, @RequestParam int score) {
        try {
            boolean success = highscoresService.saveScore(username, score);

            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Score erfolgreich gespeichert");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Score konnte nicht gespeichert werden");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Serverfehler beim Speichern: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/highscores/user/{username}")
    public ResponseEntity<?> getUserHighscores(@PathVariable String username) {
        try {
            List<Map<String, Object>> userHighscores = highscoresService.getUserHighscores(username);
            return ResponseEntity.ok(userHighscores);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Fehler beim Abrufen der User-Highscores: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @DeleteMapping("/highscores/clear")
    public ResponseEntity<?> clearHighscores() {
        try {
            boolean success = highscoresService.clearAllHighscores();

            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Alle Highscores gelöscht");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Highscores konnten nicht gelöscht werden");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Serverfehler beim Löschen: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}