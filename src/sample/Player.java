package sample;

public class Player extends Actor
{
    int life = 3;
    public Player()
    {

        super(30, 20);
        setStyle("-fx-border-color:green; -fx-background-color: green;");
    }

    // количество жизней
        public int getLife()
        {
            return life;
        }

        public void setLife(int life)
        {
            this.life = life;
        }
}
