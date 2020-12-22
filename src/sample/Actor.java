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
        setMinWidth(width);
        setMinHeight(height);
    }

    public boolean isOverlap(Actor actor)
    {
        // проверка на пересечение
        double leftA = this.getTranslateX();
        double leftB = actor.getTranslateX();
        double rightA = this.getTranslateX() + this.getPrefWidth();
        double rightB = actor.getTranslateX() + actor.getPrefWidth();

        double topA = this.getTranslateY();
        double topB = actor.getTranslateY();
        double bottomA = this.getTranslateY() + this.getPrefHeight();
        double bottomB = actor.getTranslateY() + actor.getPrefHeight();

        // если какой-то край одного прямоугольника оказался внутри другого прямоугольника
        // значит было пересечение
        return leftB > leftA && leftB < rightA && topB > topA && topB < bottomA
                || leftB > leftA && leftB < rightA && bottomB > topA && bottomB < bottomA
                || rightB > leftA && rightB < rightA && bottomB > topA && bottomB < bottomA
                || rightB > leftA && rightB < rightA && topB > topA && topB < bottomA;
    }
}
