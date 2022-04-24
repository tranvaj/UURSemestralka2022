package com.uur.wordle.uursemestralka2022;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WordleStringProcessor {
    private String fileLoc;
    private int wordLength;
    private String allowedLetters;
    private List<String> allowedWords;
    private List<String> notAddedWords;

    public WordleStringProcessor(String fileLoc, int wordLength, String allowedLetters){
        this.fileLoc = fileLoc;
        this.wordLength = wordLength;
        this.allowedLetters = allowedLetters;
        allowedWords = new ArrayList<>();
        notAddedWords = new ArrayList<>();
    }

    public String getAllowedLetters(){
        return allowedLetters;
    }

    public void setAllowedLetters(String allowedLetters) {
        this.allowedLetters = allowedLetters;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }

    public void setFileLoc(String fileLoc) {
        this.fileLoc = fileLoc;
    }

    public List<String> parseData(){
        try{
            List<String> listOfWords = new ArrayList<>();
            Scanner sc = new Scanner(new File(fileLoc));

            boolean goNext = false;
            while(sc.hasNextLine()){
                String word = sc.nextLine();
                word = word.toUpperCase();
                if(word.length() != this.wordLength) {
                    notAddedWords.add(word);
                    continue;
                }
                for(int i = 0; i < wordLength; i++){
                    String allowedChars = getAllowedLetters().toUpperCase();
                    if(allowedChars.indexOf(word.charAt(i)) == -1){
                        goNext = true;
                        break;
                    }
                }
                if(goNext){
                    goNext = false;
                    notAddedWords.add(word);
                    continue;
                }
                listOfWords.add(word);
            }
            allowedWords = listOfWords;
            return listOfWords;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<String> getNotAddedWords() {
        return notAddedWords;
    }
}
