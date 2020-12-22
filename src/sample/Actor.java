package sample;

import javafx.scene.control.Label;

public class Actor extends Label
{
    public Actor(int width, int height)
    {
        super();
        // вызов родительского конструктора
        setPrefWidth(width);
        setPrefHeight(height);
        setStyle("-fx-border-color: black;");

    }
}
