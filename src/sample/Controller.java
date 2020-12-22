package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    @FXML
    Pane mainPane;

    ArrayList<Invader> invaders = new ArrayList<>();

    // поле для направления
    int invadersDirection = 1;

    // функция для движения пришельцев
    void moveInvaders()
    {
        // если текущий тик не делится на invaderMoveTick без остатка то пропускаем
            if (currentTick % invaderMoveTick != 0)
            {
                return;
            }

        double minX = invaders.get(0).getTranslateX();
        double maxX = invaders.get(0).getTranslateX() + invaders.get(0).getWidth();

        // проходимся в цикле по всем объектам
        for (Invader z : invaders)
        {
            // ищем левый край
            if(z.getTranslateX() < minX)
            {
                minX = z.getTranslateX();
            }
         // ищем левый край учитавая длинну пришельца
            if(z.getTranslateX() + z.getWidth() > maxX)
            {
                maxX = z.getTranslateX() + z.getWidth();
            }
        }

        // флаг нужно ли идти вниз
        boolean moveDown = false;
        if (maxX >= mainPane.getWidth())
        {
            // если края достигли то да
            moveDown = true;
            invadersDirection = - 1;
        }
        // меняем направление
        else if (minX <= 0)
        {
            moveDown = true;
            invadersDirection = 1;
        }

        for(Invader z : invaders)
        {
            z.setTranslateX(z.getTranslateX() + 10 * invadersDirection);
            if (moveDown)
            {
                z.setTranslateY(z.getTranslateY() + 10);
            }
        }
    }

    int currentTick = 0; // текущий тик
    int ticketSpeed = 40; // скорость счетчика
    int invaderMoveTick = 500 / ticketSpeed; // скорость движения пришельцев, 500 / 40 = 12

    @Override
    public void initialize(URL location, ResourceBundle resource)
    {
        // отступ
        int padding = 10;

        // 5 рядов
        for (int i = 0; i < 5; ++i)
        {
            // 11 инопланетян
            for (int j = 0; j < 11; j++)
            {
                // создание экземпляра
                Invader invader = new Invader();
                // добавляем на форму
                mainPane.getChildren().add(invader);
                // добавляем в список
                invaders.add(invader);

                // расставляем на форму
                invader.setTranslateX(j * (invader.getPrefWidth() + padding));
                invader.setTranslateY(i * (invader.getPrefHeight() + 2*padding));
            }
        }

        // таймер
        Timeline timeline = new Timeline(new KeyFrame(

              // как часто вызывать при скорости 25 кадров в сек
              Duration.millis(ticketSpeed),
               new EventHandler<ActionEvent>()
               {
                   @Override
                   public void handle(ActionEvent event)
                   {
                       // фиксируем каждый тик
                       currentTick += 1;
                       moveInvaders();
                   }
               }
        ));

        // бесконечные клики
        timeline.setCycleCount(Timeline.INDEFINITE);

        // запуск
        timeline.play();
    }


}
