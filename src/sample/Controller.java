package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    // поле для контроля состояния игры
    String gameState = "menu";

    // множество под нажатые клавиши
    // если клавиша нажата то в множестве будет присутствовать ее иднетификатор
    HashSet<KeyCode> activeKeys = new HashSet<>();

    @FXML
    Pane mainPane;

    @FXML
    Label lblLife;

    @FXML
    Label lblScore;

    @FXML
    Pane menuPane;

    @FXML
    Label lblMenuTitle;

    ArrayList<Bullet> bullets = new ArrayList<>();

    ArrayList<Invader> invaders = new ArrayList<>();

    Random random = new Random();

    // поле для направления пришельцев
    int invadersDirection = 1;

    // поле для вывода очков
    int score = 0;

    // функция для движения пришельцев
    void moveInvaders()
    {
        // проверяем количество invaders и если их ноль просто выходим из функции
        if (invaders.size() == 0)
        {
            return;
        }

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

    // функция для игрока

    void playerControl()
    {
        //обработка нажатых клавиш
        if (activeKeys.contains(KeyCode.RIGHT))
        {
            player.setTranslateX(player.getTranslateX() + 10);
        }
        if (activeKeys.contains(KeyCode.LEFT))
        {
            player.setTranslateX(player.getTranslateX() - 10);
        }
        if (player.getTranslateX() < 20)
        {
            player.setTranslateX(20);
        }
        if (player.getTranslateX() > (mainPane.getWidth() - player.getWidth()))
        {
            player.setTranslateX(mainPane.getWidth() - player.getWidth());
        }

        if (activeKeys.contains(KeyCode.SPACE))
        {
            // в игре может существовать максимум одна пуля,
            // поэтому будем проверять что есть ровно одна пуля игрока,
            // и если она уже есть, то не будем генерить новые
            if (bullets.stream().filter(bullet -> bullet instanceof PlayerBullet).count() == 0)
            {
                // создаем пулю
                PlayerBullet playerBullet = new PlayerBullet();
                // над игроком
                playerBullet.setTranslateX(player.getTranslateX() + playerBullet.getPrefWidth() / 2);
                playerBullet.setTranslateY(player.getTranslateY() - 30);
                // добавляем в список пуль
                bullets.add(playerBullet);
                // добавили на форму
                mainPane.getChildren().add(playerBullet);
            }
        }
        lblLife.setText("Запас жизней : " + String.valueOf(player.life));
    }

    // метод для пуль
    void bulletsControl()
    {
        for (Bullet bullet : bullets)
        {
            bullet.setTranslateY(bullet.getTranslateY() + bullet.direction * 15);
        }

        ArrayList<Node> nodesToRemove = new ArrayList<>();
        for (Bullet bullet : bullets)
        {
            if (bullet.getTranslateY() < 0 || bullet.getTranslateY() > mainPane.getHeight())
            {
                nodesToRemove.add(bullet);
                continue;
            }

            for (Node node : mainPane.getChildren())
            {
                if (node instanceof Invader && bullet instanceof PlayerBullet)
                {
                    Invader invader = (Invader) node;
                    if (invader.isOverlap(bullet))
                    {
                        nodesToRemove.add(invader);
                        nodesToRemove.add(bullet);
                        score ++;
                        lblScore.setText("Очки : " + String.valueOf(this.score));
                        break;
                    }

                }

                // при попадании пули в игрока уменьшаем жизни, если кол-во жизне = 0, перезапускаем
                if (node instanceof Player && bullet instanceof InvaderBullet)
                {
                    Player invader = (Player) node;
                    if (player.isOverlap(bullet))
                    {
                        player.setLife(player.getLife() -1);
                        if (player.life == 0)
                        {
                            onLose();
                            return;
                        }
                        nodesToRemove.add(bullet);
                        break;
                    }

                }
            }
        }

        // если все пришельцы убиты то перезапуск
        if (invaders.size() == 0)
            {
                onWin();
                return;
            }
        bullets.removeAll(nodesToRemove);
        invaders.removeAll(nodesToRemove);
        mainPane.getChildren().removeAll(nodesToRemove);
    }

    // пули инопришеленцев
    void invadersControl ()
    {
        if (currentTick % invaderMoveTick !=0)
        {
            return;
        }

        for (Invader z : invaders)
        {
            if (random.nextDouble() < 0.01)
            {
                InvaderBullet bullet = new InvaderBullet();
                bullet.setTranslateX(z.getTranslateX() + bullet.getPrefWidth() / 2);
                bullet.setTranslateY(z.getTranslateY() + 30);
                bullets.add(bullet);
                mainPane.getChildren().add(bullet);
            }
        }
    }

    int currentTick = 0; // текущий тик
    int ticketSpeed = 40; // скорость счетчика
    int invaderMoveTick = 500 / ticketSpeed; // скорость движения пришельцев, 500 / 40 = 12

    Player player; // поле игрока

    @Override
    public void initialize(URL location, ResourceBundle resource)
    {

        // слушаем когда к нашей панели привяжется объекты сцены
        mainPane.sceneProperty().addListener((observable, oldValue, newValue) ->
        {
            // к панели уже привязан объект активного окна, находится в переменной newValue

            // подключаем реакцию на нажатие клавиши
            newValue.setOnKeyPressed(event -> activeKeys.add(event.getCode()));
            // и подключаем реакцию на отпускание клавиши
            newValue.setOnKeyReleased(event -> activeKeys.remove(event.getCode()));
        });

        // таймер
        Timeline timeline = new Timeline(new KeyFrame(

              // как часто вызывать при скорости 25 кадров в сек
              Duration.millis(ticketSpeed),
                new EventHandler<ActionEvent>()
               {
                   @Override
                   public void handle(ActionEvent event)
                   {
                       if (gameState == "game")
                       {
                           // фиксируем каждый тик
                           currentTick += 1;
                           moveInvaders();
                           // вызываем playerControl
                           playerControl();
                           // вызываем bulletsControl
                           bulletsControl();
                           // вызываем invadersControl
                           invadersControl();
                       }
                   }
               }
        ));

        // бесконечные клики
        timeline.setCycleCount(Timeline.INDEFINITE);

        // запуск
        timeline.play();

    }

    // функция для отчистки поля
    void killAll()
    {
        ArrayList<Node> nodesToRemove = new ArrayList<>();
        for (Node c : mainPane.getChildren())
        {
            if (c instanceof Actor)
            {
                nodesToRemove.add(c);
            }
        }
        mainPane.getChildren().removeAll(nodesToRemove);
        bullets.removeAll(nodesToRemove);
        invaders.removeAll(nodesToRemove);
    }

    // реакция на выигрыш
    void onWin()
    {
        killAll(); // чистим поле

        this.lblMenuTitle.setText("ВЫ ВЫИГРАЛИ!!!");
        this.menuPane.setVisible(true);
        this.gameState = "menu";

        saveRecords("Победа");
    }

    // реакция на поражение
    void onLose ()
    {
        killAll(); // чистим поле

        this.lblMenuTitle.setText("ВЫ ПРОИГРАЛИ =(");
        this.menuPane.setVisible(true);
        this.gameState = "menu";

        saveRecords("Поражение");
    }

    void saveRecords (String status)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream("records.txt", true);
            fos.write(String.format(
                    "%s | %s | результат: %s\n",
                    java.time.LocalDateTime.now(),
                    status,
                    score
            ).getBytes());
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // метод для перезапуска игры при потере всех жизней гг
    public void ResetGame()
    {
        ArrayList<Node> nodesToRemove = new ArrayList<>();
        for(Node c : mainPane.getChildren())
        {
            if (c instanceof Actor)
            {
                nodesToRemove.add(c);
            }
        }
        mainPane.getChildren().removeAll(nodesToRemove);
        bullets.removeAll(nodesToRemove);
        invaders.removeAll(nodesToRemove);
        this.score = 0;
        lblScore.setText("Очки : " + String.valueOf(this.score));

        // всякое для пришельцев

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
                invader.setTranslateY(i * (invader.getPrefHeight() + padding));
            }
        }

        // всякое для игрока

        player = new Player();

        // добавляем на форму
        mainPane.getChildren().add(player);
        //ставим вниз экрана
        player.setTranslateX(10);
        player.setTranslateY(mainPane.getPrefHeight() - 50 - player.getPrefHeight());
    }


    public void onPlay(ActionEvent actionEvent)
    {
        ResetGame(); // сбрасываем игру
        this.menuPane.setVisible(false); // прячем меню
        this.gameState = "game"; // меняем состояние чтобы таймер перестал работать в холостую
    }

    public void onRecord(ActionEvent actionEvent)
    {
        try {
            // читаем содержимое файла
            String content = new String(Files.readAllBytes(Paths.get("records.txt")));
            // создаем всплывающее окно с текстом
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            // устанавливаем заголовок окна
            alert.setTitle("РЕКОРДЫ");
            // убираем текст заголовка
            alert.setHeaderText(null);
            // загоняем содержимое файла с рекордами
            alert.setContentText(content);
            // показываем окно
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onExit(ActionEvent actionEvent)
    {
        Platform.exit();
    }
}
