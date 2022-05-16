package com.uur.wordle.uursemestralka2022;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameWordle {


    enum GAME_STATE{
        PLAYING,STOPPED,WIN,LOSE;
    }

    private List<String> wordsList;
    private GAME_STATE gameState;
    private final int WORD_LENGTH;
    private final int NUMBER_OF_TRIES;

    private String[] gameArray;
    private int[][] positions;
    private int indexAttempt;
    private String currentWord;

    private IntegerProperty score;


    public GameWordle(List<String> wordsList, int WORD_LENGTH, int NUMBER_OF_TRIES) throws Exception {
        if(wordsList.size() < 1){
            throw new Exception("There are 0 words in the list!");
        }
        this.wordsList = wordsList;
        this.WORD_LENGTH = WORD_LENGTH;
        this.NUMBER_OF_TRIES = NUMBER_OF_TRIES;
        this.score = new SimpleIntegerProperty(0);
        startGame();
    }


    private void addWord(String guessWord){
        this.gameArray[indexAttempt] = guessWord;
        int[] pos = new int[WORD_LENGTH];
        for(int i = 0; i < WORD_LENGTH; i++){
            //if char not at right pos
            if(guessWord.charAt(i) != currentWord.charAt(i)){
                //if char not at right pos and doesnt contain the char
                if(currentWord.indexOf(guessWord.charAt(i)) == -1){
                    pos[i] = -1;
                    continue;
                } else{
                    //if char not at right pos but the word contains the char
                    pos[i] = -2;
                    continue;
                }
            }
            //correct guessed pos of char in the word
            pos[i] = 1;
        }
        positions[indexAttempt] = pos;
        if(checkIfWon()) return;
        indexAttempt++;
        checkIfLoseGame();
    }

    private boolean checkIfWon(){
        int correctCount = 0;
        for(int x : positions[indexAttempt]){
            if(x == 1){
                correctCount++;
            }
        }
        if(correctCount == WORD_LENGTH) {
            gameState = GAME_STATE.WIN;
            scoreProperty().set(scoreProperty().getValue()+1);
            return true;
        }
        return false;
    }

    public void resetGame(){
        scoreProperty().set(0);
        startGame();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    private boolean checkIfLoseGame(){
        if(indexAttempt >= NUMBER_OF_TRIES) {
            gameState = GAME_STATE.LOSE;
            return true;
        }
        return false;
    }

    public GAME_STATE getGameState() {
        return gameState;
    }

    public int[][] getPositions() {
        return Arrays.copyOf(positions, positions.length);
    }

    public String[] getGameArray() {
        return Arrays.copyOf(gameArray, gameArray.length);
    }

    public boolean guessWord(String guessWord){
        if(this.gameState != GAME_STATE.PLAYING) return false;
        if(guessWord.length() != WORD_LENGTH) return false;
        addWord(guessWord.toUpperCase());
        return true;
    }

    public void startGame(){
        this.gameState = GAME_STATE.PLAYING;
        indexAttempt = 0;
        this.gameArray = new String[NUMBER_OF_TRIES];
        this.positions = new int[NUMBER_OF_TRIES][WORD_LENGTH];
        chooseRandomWord();
    }

    public int getCurrentTry() {
        return indexAttempt;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public int getWORD_LENGTH() {
        return WORD_LENGTH;
    }

    public int getNUMBER_OF_TRIES() {
        return NUMBER_OF_TRIES;
    }

    private void chooseRandomWord(){
        Random rnd = new Random();
        int index = rnd.nextInt(wordsList.size());

        //System.out.println(wordsList.size());
        currentWord = wordsList.get(index).toUpperCase();
    }




}
