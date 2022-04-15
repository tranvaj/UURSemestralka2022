package com.uur.wordle.uursemestralka2022;

import java.util.List;
import java.util.Random;

public class WordleGame {
    /**
     * The state of the game
     */
    enum GAME_STATE{
        PLAYING,PAUSED,STOPPED;
    }

    /**
     * Contains list of words that will be used in the game
     */
    private List<String> wordsList;
    /**
     * Contains all attempted words
     */
    private List<String> attemptedWords;
    /**
     * The current word
     */
    private String currentWord;
    /**
     * The length of each word
     */
    private final int WORD_LENGTH;
    /**
     * The current try
     */
    private int currentTry = 1;

    /**
     * has the player won
     */
    private boolean wonGame = false;
    /**
     * lost game
     */
    private boolean lostGame = false;

    /**
     * Win streak count
     */
    private int winStreak = 0;
    /**
     * Score
     */
    private int score = 0;

    private GAME_STATE gameState;
    public WordleGame(List<String> words, int wordLength){
        this.wordsList = words;
        this.gameState = GAME_STATE.STOPPED;
        this.WORD_LENGTH = wordLength;
    }

    public void startGame(){
        this.gameState = GAME_STATE.PLAYING;
        currentTry = 1;
        chooseRandomWord();
    }

    /**
     * This method compares the current word with the given word.
     * Returns integer array with length of 1 if the word doesn't match
     * the specific criteria.
     * Returns integer array that describes the status of each char in the given word.<br>
     * For arrays with length bigger than one, these rules apply:<br>
     * -1 = Character not in the word<br>
     * 0 = Wrong position but the character is in the word<br>
     * 1 = Character correct position<br><br>
     * For arrays with length equal to one, these rules apply:<br>
     * 100 = Won the game<br>
     * -100 = Lost the game<br>
     * @param word The guessed word that is to be compared with the current word
     * @return Returns integer array of length 1 if the word doesn't match the specific criteria or
     * returns integer array that describes the status of each char in given word (if the word matches the required length or if the game status is set to PLAYING).
     */
    public int[] guessAWord(String word){
        word = word.toUpperCase();
        if(this.gameState == GAME_STATE.PLAYING){
            if(currentTry == 6){
                setLostGame(true);
            }
            if(word.length() != WORD_LENGTH){
                return new int[]{
                  -1
                };
            }
            int[] positions = new int[WORD_LENGTH];
            for(int i = 0; i < WORD_LENGTH; i++){
                //if char not at right pos
                if(word.charAt(i) != currentWord.charAt(i)){
                    //if char not at right pos and doesnt contain the char
                    if(currentWord.indexOf(word.charAt(i)) == -1){
                        positions[i] = -1;
                        continue;
                    } else{
                        //if char not at right pos but the word contains the char
                        positions[i] = 0;
                        continue;
                    }
                }
                //correct guessed pos of char in the word
                positions[i] = 1;
            }

            int correctCount = 0;
            for(int x : positions){
                if(x == 1){
                    correctCount++;
                }
            }
            if(correctCount == WORD_LENGTH) {
                setWonGame(true);
            }
            currentTry++;
            return positions;

        } else{
            //not playing
            return new int[]{
                    -2
            };
        }
    }

    private void setLostGame(boolean lostGame){
        this.lostGame = lostGame;
        winStreak = 0;
        score = 0;
        endGame();
    }

    private void setWonGame(boolean wonGame) {
        this.wonGame = wonGame;
        winStreak++;
        score++;
        endGame();
    }

    public boolean isWonGame(){
        return this.wonGame;
    }

    public boolean isLostGame() {
        return lostGame;
    }

    public void endGame(){
        this.gameState = GAME_STATE.STOPPED;
        //wordsList.removeIf(s -> s.equals(currentWord));
    }

    public int getCurrentTry() {
        return currentTry;
    }

    private void chooseRandomWord(){
        Random rnd = new Random();
        int index = rnd.nextInt(wordsList.size());
        currentWord = wordsList.get(index).toUpperCase();
    }

    //testing purposes
    public String getCurrentWord() {
        return currentWord;
    }
}
