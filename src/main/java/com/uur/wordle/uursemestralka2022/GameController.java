package com.uur.wordle.uursemestralka2022;

import java.util.List;

public class GameController {
    private MainView view;
    private WordleGame game;
    private int wordLength;
    private int numberOfTries;
    private String allowedLetters;

    public GameController(MainView view, int wordLength, int numberOfTries, String allowedLetters, String fileLoc){
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


    public int getCurrentTry(){
        return game.getCurrentTry();
    }

    public int[] guessWord(String word){
        return game.guessAWord(word);
    }

    public MainView getView() {
        return view;
    }

    public WordleGame getGame() {
        return game;
    }

    public boolean isWonGame(){
        return game.isWonGame();
    }

    public boolean isLostGame(){
        return game.isLostGame();
    }

    public String getCurrentWord(){
       return game.getCurrentWord();
    }
}
