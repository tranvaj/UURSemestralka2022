package com.uur.wordle.uursemestralka2022;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameWordle {
    enum GAME_STATE{
        PLAYING,STOPPED
    }

    private List<String> wordsList;
    private GAME_STATE gameState;
    private final int WORD_LENGTH;
    private final int NUMBER_OF_TRIES;

    private String[][] gameArray;
    private int[][] positions;
    private int indexAttempt;
    private String currentWord;

    public GameWordle(List<String> wordsList, int WORD_LENGTH, int NUMBER_OF_TRIES) {
        this.wordsList = wordsList;
        this.WORD_LENGTH = WORD_LENGTH;
        this.NUMBER_OF_TRIES = NUMBER_OF_TRIES;
        this.gameArray = new String[NUMBER_OF_TRIES][1];
        this.positions = new int[NUMBER_OF_TRIES][WORD_LENGTH];
    }

    private void addWord(String guessWord){
        this.gameArray[indexAttempt][0] = guessWord;
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
                    pos[i] = 0;
                    continue;
                }
            }
            //correct guessed pos of char in the word
            pos[i] = 1;
        }
        positions[indexAttempt] = pos;
        indexAttempt++;
    }

    public int[][] getPositions() {
        return Arrays.copyOf(positions, positions.length);
    }

    public String[][] getGameArray() {
        return Arrays.copyOf(gameArray, gameArray.length);
    }

    public boolean guessWord(String guessWord){
        if(this.gameState == GAME_STATE.PLAYING) return false;
        if(guessWord.length() != WORD_LENGTH) return false;
        if(indexAttempt > NUMBER_OF_TRIES) return false;
        addWord(guessWord.toUpperCase());
        return true;
    }

    public void startGame(){
        this.gameState = GAME_STATE.PLAYING;
        indexAttempt = 0;
        chooseRandomWord();
    }

    private void chooseRandomWord(){
        Random rnd = new Random();
        int index = rnd.nextInt(wordsList.size());
        currentWord = wordsList.get(index).toUpperCase();
    }




}
