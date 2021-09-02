/**
 * Edge class for GraphOfTheGrid
 * Is also node in a linked list of edges
 * @author David Roberts
 */

package DJR_Store_Layout.GraphsAndHelpers;

public class Edge
{
    /** Starting point */
    private final String u;
    /** Ending point */
    private final String w;
    private final int length;
    private String cellPath;
    /** Includes DistanceReturn for some cases */
    private DistanceReturn dr;
    /** Next node in linked list */
    private Edge next;

    /** Basic Constructor */
    public Edge(String v1, String v2)
    {
        u = v1;
        w = v2;
        length = 1;
    }

    /** Constructor for edge with length greater than 1 (ex: edge between locations rather than between cells) */
    public Edge(String v1, String v2, int l, String cp)
    {
        u = v1;
        w = v2;
        length = l;
        cellPath = cp;
    }

    /** Constructor to include DistanceReturn */
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