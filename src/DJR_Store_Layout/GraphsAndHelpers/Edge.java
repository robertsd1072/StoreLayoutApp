package DJR_Store_Layout.GraphsAndHelpers;

public class Edge
{
    private final String u;
    private final String w;
    private final int length;
    private String cellPath;
    private DistanceReturn dr;
    private Edge next;

    public Edge(String v1, String v2)
    {
        u = v1;
        w = v2;
        length = 1;
    }

    public Edge(String v1, String v2, int l, String cp)
    {
        u = v1;
        w = v2;
        length = l;
        cellPath = cp;
    }

    public Edge(String v1, String v2, DistanceReturn distanceReturn)
    {
        u = v1;
        w = v2;
        length = distanceReturn.getDistance();
        cellPath = distanceReturn.getPath();
        dr = distanceReturn;
    }

    public String getU() {return u;}

    public String getW() {return w;}

    public int getLength() {return length;}

    public String getCellPath() {return cellPath;}

    public DistanceReturn getDistanceReturn() {return dr;}

    public void setNext(Edge e) {next = e;}

    public Edge getNext() {return next;}
}