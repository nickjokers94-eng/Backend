package bws.hofheim.project.api.controller;

import bws.hofheim.project.service.HighscoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class HighscoresController {

    private HighscoresService highscoresService;

    @Autowired
    public HighscoresController(HighscoresService highscoresService ){this.highscoresService = highscoresService;}

    @GetMapping("/highscores")
    public List<Map<String, Object>> getHighscores() {
        return highscoresService.getHighscores();
    }

}
