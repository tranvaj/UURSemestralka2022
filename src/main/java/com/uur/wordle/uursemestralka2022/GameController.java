package com.uur.wordle.uursemestralka2022;

import javafx.beans.property.IntegerProperty;

import java.util.List;

public class GameController {
    private MainView view;
    private GameWordle game;
    private int wordLength;
    private int numberOfTries;
    private String allowedLetters;
    private WordleStringProcessor processor;

    public GameController(MainView view, int wordLength, int numberOfTries, String allowedLetters, String fileLoc) throws Exception {
        this.view = view;
        this.wordLength = wordLength;
        this.numberOfTries = numberOfTries;
        this.allowedLetters = allowedLetters;
        //System.out.println(this.getClass().getResource(fileLoc).toString());
        NewGame(fileLoc);
    }


    private void NewGame(String fileLoc) throws Exception {
        processor = new WordleStringProcessor(fileLoc,wordLength,allowedLetters);
        List<String> words = processor.parseData();
        this.game = new GameWordle(words,wordLength, numberOfTries);
        this.game.startGame();
    }

    public GameWordle getGame() {
        return game;
    }

    public WordleStringProcessor getProcessor() {
        return processor;
    }
}
