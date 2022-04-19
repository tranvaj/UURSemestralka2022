package com.uur.wordle.uursemestralka2022;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.ArrayList;

public class LeaderboardsView {
    private ObservableList<ScoreData> scoreList;
    private TableView<ScoreData> table;
    private int maxScores;

    public LeaderboardsView(){
        init();
    }

    private void init(){
        scoreList = FXCollections.observableArrayList();
        table = new TableView<>(scoreList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        scoreList.add(new ScoreData("Test",123, LocalDate.now()));
        TableColumn<ScoreData,String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<ScoreData,Integer> score = new TableColumn<>("Score");
        score.setSortType(TableColumn.SortType.DESCENDING);
        score.setCellValueFactory(new PropertyValueFactory<>("score"));
        score.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            int oldValue = 0;
            @Override
            public String toString(Integer object) {
                oldValue = object;
                return object.toString();
            }

            @Override
            public Integer fromString(String string) {
                int x = 0;
                try{
                    x = Integer.parseInt(string);
                } catch (NumberFormatException e){
                    x = oldValue;
                }
                if(x < 0) x = oldValue;
                return x;
            }
        }));

        TableColumn<ScoreData,LocalDate> date = new TableColumn<>("Date");


        table.getSortOrder().add(score);
        table.getColumns().addAll(name,score,date);
        date.setCellValueFactory(new PropertyValueFactory<>("localDate"));
        date.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<LocalDate>() {
            LocalDate oldValue;
            @Override
            public String toString(LocalDate object) {
                oldValue = object;
                return object.toString();
            }

            @Override
            public LocalDate fromString(String string) {
                LocalDate x;
                try{
                    x = LocalDate.parse(string);
                } catch(Exception e){
                    x = oldValue;
                }
                return x;
            }
        }));
    }

    public void addScore(ScoreData e){
        scoreList.add(e);
        table.sort();
    }

    public Node getView(){
        return table;
    }
}
