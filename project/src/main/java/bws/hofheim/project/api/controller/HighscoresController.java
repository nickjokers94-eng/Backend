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

    //Punkte speichern
    @PostMapping("/highscores/save")
    public void saveHighscore(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        int score = (int) payload.get("score");
        highscoresService.saveHighscore(username, score);
    }
}