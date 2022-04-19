package com.uur.wordle.uursemestralka2022;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.BorderPane;

public class SettingsCellFactory extends TreeCell<SettingsData> {
    private BorderPane borderPane;
    private Label fSettingsName;

    public SettingsCellFactory(){
        super();
        borderPane = new BorderPane();
        fSettingsName = new Label();

        borderPane.setLeft(fSettingsName);
        //TextField fSettingsName = new TextField();

    }

    @Override
    protected void updateItem(SettingsData item, boolean empty) {
        super.updateItem(item, empty);
        if(empty){
            setText(null);
            setGraphic(null);
        } else{
            if(isEditing()){
                setText(null);
                setGraphic(null);
            } else{
                fSettingsName.setText(item.settingNameProperty().get());
                if(item.representProperty().getValue() != null && !borderPane.getChildren().contains(item.representProperty().get())){
                    borderPane.setRight(item.representProperty().get());
                }
                setText(null);
                setGraphic(borderPane);
            }
        }

    }

    @Override
    public void startEdit() {
        super.startEdit();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
    }

}
