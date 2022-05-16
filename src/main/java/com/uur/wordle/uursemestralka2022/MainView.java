package com.uur.wordle.uursemestralka2022;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
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

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MainView extends Application {
    public static BooleanProperty darkMode;
    public static Color bgColor = Color.BLACK;
    public static Color opColor = Color.WHITE;
    private Color notInWord = Color.GRAY;
    private Color wrongPos = Color.rgb(252, 186, 3);
    private Color correctPos = Color.GREEN;

    private int wordLength = 5;
    private int numberOfTries = 6;
    private StringProperty allowedLetters;
    public static String wordFileLoc;

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
        allowedLetters = new SimpleStringProperty("abcdefghijklmnopqrstuvxyz");
        darkMode = new SimpleBooleanProperty(true);
        wordFileLoc = "defaultDictionary.txt";
        loadGameSettings();
        File file = new File(wordFileLoc);
        if(!file.isFile()){
            Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
            alert2.setTitle("Confirmation");
            alert2.setHeaderText(null);
            alert2.setContentText("Could not find the dictionary\n"+file.getAbsolutePath()+"\nDo you want to load your own dictionary?");

            Optional<ButtonType> result = alert2.showAndWait();
            if (result.get() == ButtonType.OK){
            } else{
                Platform.exit();
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Dictionary");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selected = fileChooser.showOpenDialog(stage);
            if(selected != null){
                wordFileLoc = selected.getAbsolutePath();
            } else{
                createErrorMessage("You did not select any file. Ending program.");
                Platform.exit();
                return;
            }
        }

        try{
            gameController = new GameController(this, wordLength, numberOfTries, allowedLetters.get(), wordFileLoc);
        } catch (Exception E){
            createErrorMessage("Something wrong happened: \n"  + E.getMessage());
            Platform.exit();
            return;
        }


        darkMode.addListener(observable -> {
            String css = this.getClass().getResource("darkMode.css").toExternalForm();
            changeGameColor();
            if(darkMode.get()){
                mainScene.getStylesheets().add(css);
            } else{
                mainScene.getStylesheets().remove("darkMode.css");
                mainScene.getStylesheets().clear();
                //lSystem.out.println("removed");
            }
        });


        mainAppView = new BorderPane();
        leaderboardsView = new LeaderboardsView();
        settingsView = new SettingsView();
        settingsView.getDarkModeCheckBox().selectedProperty().bindBidirectional(darkMode);
        settingsView.getLoadWords().setOnAction(event -> {
            Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
            alert2.setTitle("Confirmation");
            alert2.setHeaderText(null);
            alert2.setContentText("This will reset your game. Are you OK with this?\n" +
                    "Currently loaded dictionary: \n\n" + wordFileLoc);

            Optional<ButtonType> result = alert2.showAndWait();
            if (result.get() == ButtonType.OK){
            } else{
                return;
            }


            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selected = fileChooser.showOpenDialog(stage);
            if(selected != null){
                wordFileLoc = selected.getAbsolutePath();
                fullGameReset();
                List<String> unaddedWords = gameController.getProcessor().getNotAddedWords();
                if(!unaddedWords.isEmpty()){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    //alert.setResizable(true);
                    alert.setTitle("Unadded words");
                    alert.setHeaderText(null);
                    alert.setContentText("These words were not added because they did not match the given criteria. \n\n" +
                            "Word length must be: " + wordLength + "\nAllowed letters in a word: " + allowedLetters.get().toUpperCase());
                    ListView<String> stringList = new ListView<String>(FXCollections.observableArrayList(unaddedWords));
                    alert.setGraphic(stringList);
                    alert.showAndWait();
                }
                fullGameReset();
            }
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



        try{
            leaderboardsView.loadLb();
        } catch (FileNotFoundException e){
            System.out.println("Could not load leaderboards.");
        }
        //leaderboardsView.saveLb();

        stage.setOnCloseRequest(event -> {
            try {
                leaderboardsView.saveLb();
                saveGameSettings();
            } catch (FileNotFoundException e) {
                System.out.println("Could not save leaderboards to a file");
            }
        });

        settingsView.getAllowedLetters().setText(allowedLetters.get());

        settingsView.getAllowedLetters().setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("This will reset your game. Are you OK with this?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                allowedLetters.set(settingsView.getAllowedLetters().getText());
                fullGameReset();
            } else{
                settingsView.getAllowedLetters().setText(allowedLetters.get());
            }
        });
        if(!darkMode.get()){
            changeGameColor();
        } else{
            String css = this.getClass().getResource("darkMode.css").toExternalForm();
            mainScene.getStylesheets().add(css);
        }

        //mainScene.setFill(bgColor);
        stage.setMinHeight(windowMinSizeH + windowMinSizeH/10);
        stage.setMinWidth(windowMinSizeW + windowMinSizeW/10);
        stage.setTitle("Semestralni prace Wordle");
        stage.setScene(mainScene);
        stage.show();
    }


    private void changeGameColor(){
        Color copyOpColor = Color.valueOf(toHexString(opColor));
        Color copyBgColor = Color.valueOf(toHexString(bgColor));
        opColor = copyBgColor;
        bgColor = copyOpColor;
        gameView.getChildren().get(0).setStyle("-fx-background-color: " + toHexString(bgColor));
        mainAppView.setLeft(getLeft());
        mainAppView.setRight(getRight());
        updateGameComponent();
        changeRowText(getCurrentIndex(),currentlyWrittenWord);
    }

    public void saveGameSettings(){
        try {
            PrintWriter printWriter = new PrintWriter("settings.txt");
            printWriter.println("allowedLetters=" + allowedLetters.get());
            //System.out.println(allowedLetters);
            printWriter.println("wordFileLoc=" + wordFileLoc);
            printWriter.println("darkMode=" + darkMode.get());
            printWriter.close();
        } catch (Exception E){
            createErrorMessage("Error, could not save settings.\n" + E.getMessage());
        }
    }

    public void loadGameSettings(){
        try {
            Scanner sc = new Scanner(new File("settings.txt"));
            String[] set = new String[3];
            int i = 0;
            while (sc.hasNextLine()) {
                set[i] = sc.nextLine().split("=")[1];
                i++;
            }
            //System.out.println(set.length);
            String al = set[0];
            String wordFile = set[1];
            boolean dm = Boolean.parseBoolean(set[2]);

            allowedLetters.set(al);
            wordFileLoc = wordFile;
            darkMode.setValue(dm);
        }
        catch (Exception E){
            createErrorMessage("Could not load game settings file. Using default settings.\n" + E.getMessage());

        }
    }

    public static void createErrorMessage(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private int getCurrentIndex(){
        return this.gameController.getGame().getCurrentTry();
    }
    private void writeInComponent(KeyEvent event) {
        //System.out.println(gameController.getGame().getGameState().toString());
        if(gameController.getGame().getGameState() != GameWordle.GAME_STATE.PLAYING){
            //System.out.println(gameController.getGame().getGameState().toString());
            return;
        }

        char keyChar = event.getCode().getChar().toUpperCase().charAt(0);
        String allowedLetters = this.allowedLetters.get().toUpperCase();
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

        //System.out.println(currentIndex);
        changeRowText(currentIndex,currentlyWrittenWord);

        if(event.getCode() == KeyCode.ENTER){
            if(currentlyWrittenWord.length() == wordLength) {
                boolean tf = gameController.getGame().guessWord(currentlyWrittenWord);
                //System.out.println(tf);
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
        BorderPane lbPane = new BorderPane();
        lbPane.setCenter(leaderboardsView.getView());
        lbPane.setTop(getTop());
        mainScene.setRoot(lbPane);
    }

    private void switchToSettingsView(){
        BorderPane settingsPane = new BorderPane();
        settingsPane.setCenter(settingsView.getStrom());
        settingsPane.setTop(getTop());
        mainScene.setRoot(settingsPane);
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
        //ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.setContentText("Choose your next action:");

        if(gameState == GameWordle.GAME_STATE.WIN){
            alert.setTitle("Congratulations");
            alert.setHeaderText("You guessed the word correctly!");
            alert.getButtonTypes().setAll(btnContinueGame, btnNewGame, btnSaveScore);
        }
        else if(gameState == GameWordle.GAME_STATE.LOSE){
            alert.setTitle("Unlucky");
            alert.setHeaderText("You have no more guess attempts left!");
            alert.getButtonTypes().setAll(btnNewGame, btnSaveScore);
        } else{
            return;
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == btnContinueGame){
            continueGame();
        } else if (result.get() == btnNewGame) {
            setNewGame();
        } else if (result.get() == btnSaveScore) {
            saveScore();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    private void saveScore(){
        int score = gameController.getGame().scoreProperty().get();
        TextInputDialog dialog = new TextInputDialog("Player");
        dialog.setTitle("Please input your name");
        dialog.setContentText("Please enter your name:");

        try{
            Optional<String> nameRes = dialog.showAndWait();
            while (!nameRes.isPresent() || nameRes.get().isBlank()){
                nameRes = dialog.showAndWait();
            }

            leaderboardsView.addScore(new ScoreData(nameRes.get(),score, LocalDate.now()));
            setNewGame();
        } catch (Exception E){
            createErrorMessage("Something wrong happened: " + E.getMessage());
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
        try{
            gameController = new GameController(this,wordLength,numberOfTries,allowedLetters.get(),wordFileLoc);
        } catch (Exception E){
            createErrorMessage("Something wrong happened: " + E.getMessage());
            Platform.exit();
            return;
        }
        Text text = (Text) scoreContainer.getChildren().get(0);
        text.textProperty().bind(Bindings.concat("Score: ").concat(gameController.getGame().scoreProperty().asString()));
        updateGameComponent();
        currentlyWrittenWord = "";
    }

    private Node getTop() {
        MenuBar menu = new MenuBar();
        Menu item1 = new Menu("Game");

        MenuItem newGame = new MenuItem("New Game");
        MenuItem saveScore = new MenuItem("Save Score");
        MenuItem lb = new MenuItem("Leaderboards");
        MenuItem settings = new MenuItem("Settings");
        MenuItem mm = new MenuItem("Main Screen");
        MenuItem exit = new MenuItem("Exit");

        saveScore.setOnAction(event -> {
            Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
            alert2.setTitle("Confirmation");
            alert2.setHeaderText(null);
            alert2.setContentText("This will reset your game. Are you OK with this?");
            Optional<ButtonType> result = alert2.showAndWait();
            if (result.get() == ButtonType.OK){
                saveScore();
            } else{
                return;
            }
        });

        newGame.setOnAction(event -> {
            Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
            alert2.setTitle("Confirmation");
            alert2.setHeaderText(null);
            alert2.setContentText("Are you sure you want to start a new game? This will reset your progess.");
            Optional<ButtonType> result = alert2.showAndWait();
            if (result.get() == ButtonType.OK){
                setNewGame();
            } else{
                return;
            }
        });

        mm.setOnAction(event -> {
            switchToDefaultGameView();
        });

        lb.setOnAction(event -> {
            switchToLeaderboardView();
        });

        settings.setOnAction(event -> {
            switchToSettingsView();
        });
        exit.setOnAction(event -> {
            try {
                leaderboardsView.saveLb();
                saveGameSettings();
            } catch (FileNotFoundException e) {
                System.out.println("Could not save leaderboards to a file");
            }
                Platform.exit();
        });

        item1.getItems().addAll(newGame,saveScore,new SeparatorMenuItem(),mm,lb,settings,new SeparatorMenuItem(),exit);
        Menu item2 = new Menu("Help");
        MenuItem help = new MenuItem("How To Play");
        MenuItem about = new MenuItem("About");
        item2.getItems().addAll(help, new SeparatorMenuItem(), about);
        about.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About application");
            alert.setHeaderText(null);
            alert.setContentText("Wordle Game\nUUR Semestral Work 2022 by Vaclav Tran");

            alert.showAndWait();
        });

        help.setOnAction(event -> {
            createInformationAlert("Guess the WORDLE in six tries.\n" +
                    "\n" +
                    "Each guess must be a five-letter word. Hit the enter button to submit.\n" +
                    "\n" +
                    "After each guess, the color of the tiles will change to show how close your guess was to the word." +
                    "\n\nGREEN if the letter is in the correct spot\nYELLOW if it is in the wrong spot\nGRAY if it is not in any spot.");
        });

        menu.getMenus().addAll(item1, item2);
        return menu;
    }

    public static void createInformationAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

        //vb.setStyle("-fx-background-color: " + toHexString(bgColor)+ "; -fx-border-color: blue");
        vb.setStyle("-fx-background-color: " + toHexString(bgColor));

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

        //vb.setStyle("-fx-background-color: " + toHexString(bgColor)+ "; -fx-border-color: blue");
        vb.setStyle("-fx-background-color: " + toHexString(bgColor));

        vb.setPadding(new Insets(10));
        vb.setSpacing(10);

        vb.getChildren().forEach(node -> {
            node.setVisible(false);
        });

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
                //System.out.println(i + " " + j);
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