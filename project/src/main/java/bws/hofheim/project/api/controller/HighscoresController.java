package bws.hofheim.project.api.controller;

import bws.hofheim.project.service.HighscoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class HighscoresController {

    private HighscoresService highscoresService;

    @Autowired
    public HighscoresController(HighscoresService highscoresService) {
        this.highscoresService = highscoresService;
    }

    @GetMapping("/highscores")
    public List<Map<String, Object>> getHighscores() {
        return highscoresService.getHighscores();
    }

    // NEU: Punkte speichern
    @PostMapping("/highscores/save")
    public void saveHighscore(@RequestParam String username, @RequestParam int score) {
        highscoresService.saveHighscore(username, score);
    }
}