package com.uur.wordle.uursemestralka2022;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainView extends Application {
    private Color bgColor = Color.BLACK;
    private Color opColor = Color.WHITE;
    private Color notInWord = Color.GRAY;
    private Color wrongPos = Color.rgb(252, 186, 3);
    private Color correctPos = Color.GREEN;

    private int wordLength = 5;
    private int numberOfTries = 6;

    private List<List<StackPane>> wordPaneList;
    private int windowMinSizeW = 500;
    private int windowMinSizeH = 700;
    private String allowedLetters = "abcdefghijklmnopqrstuvxyz";
    private GameController gameController;
    private String currentlyWrittenWord = "";

    @Override
    public void start(Stage stage) throws IOException {
        //send reference of view to gameController
        System.out.println(System.getProperty("user.dir"));
        gameController = new GameController(this, wordLength, numberOfTries, allowedLetters, "1000-most-common-words.txt");


        BorderPane bp = new BorderPane();
        Scene scene = new Scene(bp, windowMinSizeW, windowMinSizeH);

        //will scale the size of our game component
        GridPane gp = (GridPane) getCenter();
        gp.setMinSize(windowMinSizeW, windowMinSizeH);
        gp.setMaxSize(windowMinSizeW, windowMinSizeH);
        StackPane root = new StackPane(gp);
        NumberBinding maxScale = Bindings.min(root.widthProperty().divide(windowMinSizeW-windowMinSizeW/4),
                root.heightProperty().divide(windowMinSizeH-windowMinSizeH/4));
        gp.scaleXProperty().bind(maxScale);
        gp.scaleYProperty().bind(maxScale);


        bp.setCenter(root);
        bp.setRight(getRight());
        //bp.setBottom(getBottom());

        scene.setOnKeyPressed(event -> {
            writeInComponent(event);
        });




        scene.setFill(bgColor);
        stage.setMinHeight(windowMinSizeH + windowMinSizeH/10);
        stage.setMinWidth(windowMinSizeW + windowMinSizeW/10);
        stage.setTitle("Semestralni prace Wordle");
        stage.setScene(scene);
        stage.show();
    }

    private int getCurrentIndex(){
        return this.gameController.getCurrentTry()-1;
    }
    private void writeInComponent(KeyEvent event) {
        if(gameController.isWonGame()) {
            System.out.println("You won the game!");
            //resetGameComponent();
            return;
        } else if(gameController.isLostGame()){
            System.out.println("You lost!");
            return;
        }

        char keyChar = event.getCode().getChar().toUpperCase().charAt(0);
        int[] charColor = new int[]{};
        String allowedLetters = this.allowedLetters.toUpperCase();
        int currentIndex = getCurrentIndex();

        if(allowedLetters.indexOf(keyChar) != -1 && currentlyWrittenWord.length() < wordLength){
            currentlyWrittenWord += keyChar + "";
        }

        if(event.getCode() == KeyCode.BACK_SPACE && currentlyWrittenWord.length() != 0){
            String str = "";
            for(int i = 0; i < currentlyWrittenWord.length()-1; i++){
                str += currentlyWrittenWord.charAt(i);
            }
            currentlyWrittenWord = str;
        }

        if(event.getCode() == KeyCode.ENTER){
            if(currentlyWrittenWord.length() == wordLength) {
                charColor = gameController.guessWord(currentlyWrittenWord);
                System.out.println(gameController.getCurrentWord());
            }
            System.out.println(currentlyWrittenWord + " length: " + currentlyWrittenWord.length());
        }
        changeRowText(currentIndex,currentlyWrittenWord, charColor);
        if(charColor.length == 5) currentlyWrittenWord = "";

    }

    private void updateGameComponent(){

    }

    private void resetGameComponent() {
        for(int i = 0; i < wordLength; i++){
            for(int j = 0; j < numberOfTries; j++){
                List<StackPane> wrd = wordPaneList.get(i);
                StackPane a = wrd.get(j);
                a.setStyle("-fx-background-color: " + toHexString(bgColor) + ";-fx-border-color: " + toHexString(opColor));
                TextFlow b = (TextFlow) a.getChildren().get(0);
                Text c = (Text) b.getChildren().get(0);
                c.setText(" ");
            }
        }
    }

    public Node getBottom(){
        StackPane sp = new StackPane();
        TextField tf = new TextField();
        sp.getChildren().addAll(tf);
        //sp.setPadding(new Insets(0,10,20,10));
        return null;
    }

    public Node getLeft(){
        return null;
    }

    public Node getRight(){
        StackPane sp = new StackPane();
        sp.setStyle("-fx-background-color: " + toHexString(bgColor));
        return sp;
    }

    public Node getCenter(){
        wordPaneList = new ArrayList<>();
        GridPane gp = new GridPane();
        gp.setStyle("-fx-background-color: " + toHexString(bgColor));
        gp.setHgap(5);
        gp.setVgap(5);
        for(int i = 0; i < wordLength; i++){
            List<StackPane> panes = new ArrayList<>();
            wordPaneList.add(panes);
            for(int j = 0; j < numberOfTries; j++){
                StackPane pane = new StackPane();
                panes.add(pane);
                pane.setStyle("-fx-background-color: " + toHexString(bgColor) + ";-fx-border-color: " + toHexString(opColor));

                TextFlow flowContainer = new TextFlow();
                Text text1 = new Text(" ");
                text1.setFill(Color.WHITE);
                text1.setStyle("-fx-font-weight: bold; -fx-text-fill: " + toHexString(opColor) + "; -fx-font-size: 16px;");

                flowContainer.getChildren().addAll(text1);
                flowContainer.setPadding(new Insets(10));
                //flowContainer.setStyle("-fx-border-color: red");
                flowContainer.setTextAlignment(TextAlignment.CENTER);

                pane.getChildren().add(flowContainer);
                pane.setMinSize(40,40);
                pane.setMaxSize(40,40);
                //StackPane.setAlignment(flowContainer,Pos.CENTER_RIGHT);
                gp.add(pane, i,j,1,1);
                System.out.println(i + " " + j);
            }
        }
        gp.setAlignment(Pos.CENTER);
        return gp;
    }

    public boolean changeRowText(int index, String word, int[] charColor){
        if(word.length() > wordLength) return false;
        if(index > numberOfTries) return false;
        for(int i = 0; i < wordLength; i++){
            List<StackPane> wrd = wordPaneList.get(i);
            StackPane a = wrd.get(index);
            TextFlow b = (TextFlow) a.getChildren().get(0);
            Text c = (Text) b.getChildren().get(0);
            if(i == word.length()){
                while(i != wordLength){
                    c.setText(" ");
                    i++;
                }
                break;
            }
            System.out.println("charColor length: " + charColor.length);
            if(charColor.length == wordLength){
                switch (charColor[i]){
                    case -1:
                        a.setStyle("-fx-background-color: " + toHexString(notInWord) + ";-fx-border-color: " + toHexString(opColor));
                        break;
                    case 0:
                        a.setStyle("-fx-background-color: " + toHexString(wrongPos) + ";-fx-border-color: " + toHexString(opColor));
                        break;
                    case 1:
                        a.setStyle("-fx-background-color: " + toHexString(correctPos) + ";-fx-border-color: " + toHexString(opColor));
                        break;
                }
            }
            c.setText(word.charAt(i) + "");
        }
        return true;
    }

    private static String toHexString(Color color) {
        int r = ((int) Math.round(color.getRed()     * 255)) << 24;
        int g = ((int) Math.round(color.getGreen()   * 255)) << 16;
        int b = ((int) Math.round(color.getBlue()    * 255)) << 8;
        int a = ((int) Math.round(color.getOpacity() * 255));
        return String.format("#%08X", (r + g + b + a));
    }

    public static void main(String[] args) {
        launch();
    }
}