package sample;

public class Player extends Actor
{
    int life = 3;
    public Player()
    {

        super(30, 20);
        setStyle("-fx-border-color:green; -fx-background-color: green;");
    }

        public int getLife()
        {
            return life;
        }

        public void setLife(int Life)
        {
            this.life = life;
        }
}
