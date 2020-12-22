package sample;

public class Bullet extends Actor
{
    int direction;
    public Bullet(int direction)
    {
        super(10,10);
        this.direction = direction;
    }
}
