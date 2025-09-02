package bws.hofheim.project.api.controller;

import bws.hofheim.project.service.UserService;
import bws.hofheim.project.service.WordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class WordsController {

    private WordsService wordsService;

    @Autowired
    public WordsController(WordsService wordsService) { this.wordsService = wordsService; }

    @GetMapping("/words")
    public List<Map<String, Object>> getWords() {
        return wordsService.getWords();
    }

    @PostMapping("/words/addWord")
    public boolean addWord(String word){ //public boolean addWord(@RequestParam String word)  
        return wordsService.addWord(word);
    }


}
