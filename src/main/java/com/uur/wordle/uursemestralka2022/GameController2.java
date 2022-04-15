package com.uur.wordle.uursemestralka2022;

import java.util.List;

public class GameController2 {
    private MainView view;
    private WordleGame game;
    private int wordLength;
    private int numberOfTries;
    private String allowedLetters;

    public GameController2(MainView view, int wordLength, int numberOfTries, String allowedLetters, String fileLoc){
        this.view = view;
        this.wordLength = wordLength;
        this.numberOfTries = numberOfTries;
        this.allowedLetters = allowedLetters;
        //System.out.println(this.getClass().getResource(fileLoc).toString());
        NewGame(fileLoc);
    }


    public void NewGame(String fileLoc){
        WordleStringProcessor processor = new WordleStringProcessor(fileLoc,wordLength,allowedLetters);
        List<String> words = processor.parseData();
        this.game = new WordleGame(words,wordLength);
        this.game.startGame();
    }
}
