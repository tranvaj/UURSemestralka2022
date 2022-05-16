package com.uur.wordle.uursemestralka2022;

import javafx.beans.property.*;
import javafx.scene.control.TableView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class ScoreData {
    private StringProperty name;
    private IntegerProperty score;
    private ObjectProperty<LocalDate> localDate;

    public ScoreData(String name, int score, LocalDate localDate) {
        this.name = new SimpleStringProperty(name);
        this.score = new SimpleIntegerProperty(score);
        this.localDate = new SimpleObjectProperty<>(localDate);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public ObjectProperty<LocalDate> localDateProperty() {
        return localDate;
    }

    @Override
    public String toString() {
        return name.get() + ":" + score.get() + ":" + localDate.get().toString();
    }
}
