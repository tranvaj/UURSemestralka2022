package com.uur.wordle.uursemestralka2022;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class SettingsView {
    private TreeView<SettingsData> strom;
    private CheckBox darkModeCheckBox;
    private Button loadWords;


    public SettingsView() {
        //this.darkMode = darkMode;
        init();
    }


    private void init(){
        darkModeCheckBox = new CheckBox();
        darkModeCheckBox.setSelected(true);

        loadWords = new Button("Load dictionary");


        TreeItem<SettingsData> root = new TreeItem<>(
                new SettingsData("Settings", SettingsData.SettingsType.LABEL, null));

        TreeItem<SettingsData> a1 = new TreeItem<>(
                new SettingsData("Graphics",  SettingsData.SettingsType.LABEL,null));
        TreeItem<SettingsData> b1 = new TreeItem<>(
                new SettingsData("Dark Mode", SettingsData.SettingsType.CHECKBOX, darkModeCheckBox));

        TreeItem<SettingsData> a2 = new TreeItem<>(
                new SettingsData("Gameplay",  SettingsData.SettingsType.LABEL, null));
        TreeItem<SettingsData> c1 = new TreeItem<>(
                new SettingsData("Dictionary",  SettingsData.SettingsType.LABEL, loadWords));


        root.getChildren().addAll(a1,a2);
        a1.getChildren().addAll(b1);

        a2.getChildren().addAll(c1);

        strom = new TreeView<>();
        strom.setCellFactory(param -> new SettingsCellFactory());
        strom.setEditable(false);
        strom.setRoot(root);
    }

    public TreeView<SettingsData> getStrom() {
        return strom;
    }

    public Button getLoadWords() {
        return loadWords;
    }

    public CheckBox getDarkModeCheckBox() {
        return darkModeCheckBox;
    }
}
