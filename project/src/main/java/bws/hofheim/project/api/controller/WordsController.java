package bws.hofheim.project.api.controller;

import bws.hofheim.project.service.WordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//a
@RestController
@RequestMapping("/api")
public class WordsController {

    private WordsService wordsService;

    @Autowired
    public WordsController(WordsService wordsService) { 
        this.wordsService = wordsService; 
    }

    @GetMapping("/words")
    public ResponseEntity<?> getWords() {
        try {
            List<Map<String, Object>> words = wordsService.getWords();
            return ResponseEntity.ok(words);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Fehler beim Abrufen der Wörter: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/words/addWord")
    public ResponseEntity<?> addWord(@RequestParam String word) {
        try {
            boolean success = wordsService.addWord(word);
            Map<String, Object> response = new HashMap<>();
            
            if (success) {
                response.put("success", true);
                response.put("message", "Wort erfolgreich hinzugefügt");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Wort konnte nicht hinzugefügt werden");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Serverfehler: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/words/deleteWord")
    public ResponseEntity<?> deleteWord(@RequestParam String word) {
        try {
            boolean success = wordsService.deleteWord(word);
            Map<String, Object> response = new HashMap<>();
            
            if (success) {
                response.put("success", true);
                response.put("message", "Wort erfolgreich gelöscht");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Wort konnte nicht gelöscht werden");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Serverfehler: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/words/changeWord")
    public ResponseEntity<?> changeWord(@RequestParam String word, @RequestParam String wordUpdate) {
        try {
            boolean success = wordsService.changeWord(word, wordUpdate);
            Map<String, Object> response = new HashMap<>();
            
            if (success) {
                response.put("success", true);
                response.put("message", "Wort erfolgreich geändert");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Wort konnte nicht geändert werden");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Serverfehler: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/words/randomWord")
    public ResponseEntity<?> randomWord() {
        try {
            String word = wordsService.randomWord();
            Map<String, String> response = new HashMap<>();
            response.put("word", word);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Fehler beim Abrufen eines zufälligen Wortes: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}