package DJR_Store_Layout;

public class Coords
{
    private final String coordinates;
    private final int x;
    private final int y;

    public Coords(String s)
    {
        coordinates = s;
        x = Integer.parseInt(s.split(",")[0]);
        y = Integer.parseInt(s.split(",")[1]);
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public String toString()
    {
        return coordinates;
    }
}
