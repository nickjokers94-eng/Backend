package bws.hofheim.project.api.controller;

import bws.hofheim.project.service.WordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public boolean addWord(@RequestParam String word) {
        return wordsService.addWord(word);
    }

    @DeleteMapping("/words/deleteWord")
    public boolean deleteWord(@RequestParam String word){return wordsService.deleteWord(word);}

    @PutMapping("/words/changeWord")
    public boolean changeWord(@RequestParam String word, String wordUpdate){return wordsService.changeWord(word, wordUpdate);}

    @GetMapping("/words/randomWord")
    public String randomWord() {return wordsService.randomWord();}
    }