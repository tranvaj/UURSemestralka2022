package com.uur.wordle.uursemestralka2022;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainView extends Application {
    private BooleanProperty darkMode;
    private Color bgColor = Color.BLACK;
    private Color opColor = Color.WHITE;
    private Color notInWord = Color.GRAY;
    private Color wrongPos = Color.rgb(252, 186, 3);
    private Color correctPos = Color.GREEN;

    private int wordLength = 5;
    private int numberOfTries = 6;
    private String allowedLetters = "abcdefghijklmnopqrstuvxyz";
    private String wordFileLoc = "1000-most-common-words.txt";

    private List<List<StackPane>> wordPaneList;
    private int windowMinSizeW = 500;
    private int windowMinSizeH = 700;

    private GameController gameController;
    private TextFlow scoreContainer;
    private String currentlyWrittenWord = "";

    private Scene mainScene;
    private BorderPane mainAppView;
    private StackPane gameView;
    private LeaderboardsView leaderboardsView;
    private SettingsView settingsView;
    @Override
    public void start(Stage stage) throws IOException {
        //send reference of view to gameController
        gameController = new GameController(this, wordLength, numberOfTries, allowedLetters, wordFileLoc);



        darkMode = new SimpleBooleanProperty(true);
        darkMode.addListener(observable -> {
            Color copyOpColor = Color.valueOf(toHexString(opColor));
            Color copyBgColor = Color.valueOf(toHexString(bgColor));
            opColor = copyBgColor;
            bgColor = copyOpColor;
            gameView.getChildren().get(0).setStyle("-fx-background-color: " + toHexString(bgColor));
            mainAppView.setLeft(getLeft());
            mainAppView.setRight(getRight());
            updateGameComponent();
            changeRowText(getCurrentIndex(),currentlyWrittenWord);
        });


        mainAppView = new BorderPane();
        leaderboardsView = new LeaderboardsView();
        settingsView = new SettingsView();
        settingsView.getDarkModeCheckBox().selectedProperty().bindBidirectional(darkMode);
        settingsView.getLoadWords().setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File selected = fileChooser.showOpenDialog(stage);
            wordFileLoc = selected.getAbsolutePath();
            fullGameReset();
        });

        mainScene = new Scene(mainAppView, windowMinSizeW, windowMinSizeH);


        //will scale the size of our game component
        GridPane grid = (GridPane) getCenter();


        gameView = new StackPane(grid);

        //root.setStyle("-fx-border-color: red; -fx-background-color: black");
        NumberBinding maxScale = Bindings.min(gameView.widthProperty().divide(windowMinSizeW-windowMinSizeW/2),
                gameView.heightProperty().divide(windowMinSizeH-windowMinSizeH/2));
        grid.scaleXProperty().bind(maxScale);
        grid.scaleYProperty().bind(maxScale);


        mainAppView.setCenter(gameView);
        mainAppView.setTop(getTop());
        mainAppView.setRight(getRight());
        mainAppView.setLeft(getLeft());
        //bp.setBottom(getBottom());

        mainScene.setOnKeyPressed(event -> {
            grid.requestFocus();
            writeInComponent(event);
        });

        mainScene.setOnMouseClicked(event -> {
            grid.requestFocus();
        });



        //mainScene.setFill(bgColor);
        stage.setMinHeight(windowMinSizeH + windowMinSizeH/10);
        stage.setMinWidth(windowMinSizeW + windowMinSizeW/10);
        stage.setTitle("Semestralni prace Wordle");
        stage.setScene(mainScene);
        stage.show();
    }



    private int getCurrentIndex(){
        return this.gameController.getGame().getCurrentTry();
    }
    private void writeInComponent(KeyEvent event) {
        System.out.println(gameController.getGame().getGameState().toString());
        if(gameController.getGame().getGameState() != GameWordle.GAME_STATE.PLAYING){
            System.out.println(gameController.getGame().getGameState().toString());
            return;
        }

        char keyChar = event.getCode().getChar().toUpperCase().charAt(0);
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

        System.out.println(currentIndex);
        changeRowText(currentIndex,currentlyWrittenWord);

        if(event.getCode() == KeyCode.ENTER){
            if(currentlyWrittenWord.length() == wordLength) {
                boolean tf = gameController.getGame().guessWord(currentlyWrittenWord);
                System.out.println(tf);
                if(tf){
                    updateGameComponent();
                    currentlyWrittenWord = "";
                    System.out.println(gameController.getGame().getCurrentWord());
                    if(gameController.getGame().getGameState() != GameWordle.GAME_STATE.PLAYING){
                        showGameOverAlert(gameController.getGame().getGameState());
                    }
                }
            }
        }
    }


    private void switchToDefaultGameView(){
        mainScene.setRoot(mainAppView);
    }

    private void switchToLeaderboardView(){
        BorderPane bp = new BorderPane();
        bp.setCenter(leaderboardsView.getView());
        bp.setRight(getRight());
        bp.setTop(getTop());
        mainScene.setRoot(bp);
    }

    private void switchToSettingsView(){
        BorderPane bp = new BorderPane();
        bp.setCenter(settingsView.getStrom());
        bp.setRight(getRight());
        bp.setTop(getTop());
        mainScene.setRoot(bp);
    }

    private void continueGame(){
        gameController.getGame().startGame();
        updateGameComponent();
    }

    private void setNewGame(){
        gameController.getGame().resetGame();
        updateGameComponent();
    }

    private void showGameOverAlert(GameWordle.GAME_STATE gameState) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType btnContinueGame = new ButtonType("Continue");
        ButtonType btnNewGame = new ButtonType("New game");
        ButtonType btnSaveScore = new ButtonType("Save score & Start new game");
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.setContentText("Choose your next action:");

        if(gameState == GameWordle.GAME_STATE.WIN){
            alert.setTitle("Congratulations");
            alert.setHeaderText("You guessed the word correctly!");
            alert.getButtonTypes().setAll(btnContinueGame, btnNewGame, btnSaveScore,btnCancel);
        }
        else if(gameState == GameWordle.GAME_STATE.LOSE){
            alert.setTitle("Unlucky");
            alert.setHeaderText("You have no more guess attempts left!");
            alert.getButtonTypes().setAll(btnNewGame, btnSaveScore,btnCancel);
        } else{
            return;
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == btnContinueGame){
            continueGame();
        } else if (result.get() == btnNewGame) {
            setNewGame();
        } else if (result.get() == btnSaveScore) {
            int score = gameController.getGame().scoreProperty().get();
            TextInputDialog dialog = new TextInputDialog("Player");
            dialog.setTitle("Please input your name");
            dialog.setContentText("Please enter your name:");

            Optional<String> nameRes = dialog.showAndWait();
            while (!nameRes.isPresent() || nameRes.get().isBlank()){
                if(nameRes.get().isBlank()){

                }
                nameRes = dialog.showAndWait();
            }
            leaderboardsView.addScore(new ScoreData(nameRes.get(),score, LocalDate.now()));
            setNewGame();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    private void updateGameComponent(){
        String[] gameArr = gameController.getGame().getGameArray();
        int[][] posColor = gameController.getGame().getPositions();
        for(int i = 0; i < gameController.getGame().getWORD_LENGTH(); i++){
            for(int j = 0; j < gameController.getGame().getNUMBER_OF_TRIES(); j++){
                List<StackPane> wrd = wordPaneList.get(i);
                StackPane a = wrd.get(j);
                switch (posColor[j][i]){
                    case -1:
                        a.setStyle("-fx-background-color: " + toHexString(notInWord) + ";-fx-border-color: " + toHexString(opColor));
                        break;
                    case -2:
                        a.setStyle("-fx-background-color: " + toHexString(wrongPos) + ";-fx-border-color: " + toHexString(opColor));
                        break;
                    case 1:
                        a.setStyle("-fx-background-color: " + toHexString(correctPos) + ";-fx-border-color: " + toHexString(opColor));
                        break;
                    default:
                        a.setStyle("-fx-background-color: " + toHexString(bgColor) + ";-fx-border-color: " + toHexString(opColor));
                        break;
                }
                TextFlow b = (TextFlow) a.getChildren().get(0);
                Text c = (Text) b.getChildren().get(0);
                c.setFill(opColor);
                if(gameArr[j] != null){
                    c.setText(gameArr[j].charAt(i)+"");
                } else{
                    c.setText(" ");
                }
            }
        }
        //currentlyWrittenWord = "";
    }

    private void fullGameReset() {
        gameController = new GameController(this,wordLength,numberOfTries,allowedLetters,wordFileLoc);
        updateGameComponent();
        currentlyWrittenWord = "";
    }

    private Node getTop() {
        MenuBar menu = new MenuBar();
        Menu item1 = new Menu("test");
        menu.getMenus().addAll(item1);
        return menu;
    }

    public Node getBottom(){
        StackPane sp = new StackPane();
        TextField tf = new TextField();
        sp.getChildren().addAll(tf);
        //sp.setPadding(new Insets(0,10,20,10));
        return null;
    }

    public Node getLeft(){
        VBox vb = new VBox();

        scoreContainer = new TextFlow();
        Text text1 = new Text();
        text1.textProperty().bind(Bindings.concat("Score: ").concat(gameController.getGame().scoreProperty().asString()));

        text1.setFill(opColor);
        text1.setStyle("-fx-font-weight: bold; -fx-text-fill: " + toHexString(opColor) + "; -fx-font-size: 16px;");
        scoreContainer.getChildren().addAll(text1);

        vb.getChildren().addAll(scoreContainer);

        vb.setStyle("-fx-background-color: " + toHexString(bgColor)+ "; -fx-border-color: blue");
        vb.setPadding(new Insets(10));
        vb.setSpacing(10);
        return vb;
    }

    public Node getRight(){
        VBox vb = new VBox();
        Button newGame = new Button("New game");
        Button leaderboards = new Button("Leaderboards");
        Button home = new Button("Main game");
        Button settings = new Button("Settings");

        vb.getChildren().addAll(home, newGame,leaderboards, settings);
        home.setOnAction(event -> {
            switchToDefaultGameView();
        });
        leaderboards.setOnAction(event -> {
            switchToLeaderboardView();
        });
        newGame.setOnAction(event -> {
            setNewGame();
        });

        settings.setOnAction(event -> {
            switchToSettingsView();
        });

        vb.setStyle("-fx-background-color: " + toHexString(bgColor)+ "; -fx-border-color: blue");
        vb.setPadding(new Insets(10));
        vb.setSpacing(10);
        return vb;
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
                text1.setFill(opColor);
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
                //GridPane.setConstraints(pane,i,j,1,1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS)
                System.out.println(i + " " + j);
            }
        }
        gp.setAlignment(Pos.CENTER);
        return gp;
    }

    public boolean changeRowText(int index, String word){
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
            //System.out.println("charColor length: " + charColor.length);
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