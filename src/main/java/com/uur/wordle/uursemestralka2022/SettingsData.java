package com.uur.wordle.uursemestralka2022;

import javafx.beans.property.*;
import javafx.scene.Node;

import java.util.Set;

public class SettingsData {
    enum SettingsType{
        LABEL, CHECKBOX, BUTTON
    }

    private StringProperty settingName;
    private ObjectProperty<SettingsType> type;
    private ObjectProperty<Node> represent;

    public SettingsData(String settingName, SettingsType type, Node represent) {
        this.settingName = new SimpleStringProperty(settingName);
        this.type = new SimpleObjectProperty<>(type);
        this.represent = new SimpleObjectProperty<>(represent);
    }


    public ObjectProperty<Node> representProperty() {
        return represent;
    }

    public StringProperty settingNameProperty() {
        return settingName;
    }

    public ObjectProperty<SettingsType> typeProperty() {
        return type;
    }
}
