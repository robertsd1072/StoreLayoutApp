/**
 * Rectangle Node class for GridData3
 * @author David Roberts
 */

package DJR_Store_Layout.GridData;

import DJR_Store_Layout.HelperClasses.Coords;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RNode
{
    private final Rectangle r;
    private final int xCoord, yCoord;
    private final Coords coords;
    /** Screen bounds of rectangle */
    private double sXMinCoord, sYMinCoord, sXMaxCoord, sYMaxCoord;
    private boolean highlighted, isIsle, nulled, beingMoved;
    private Aisle aisle;
    private AisleGroup aisleGroup;
    private Color color;
    private final GridData3 grid;

    public RNode(Rectangle rect, int x, int y, double x1, double y1, GridData3 g)
    {
        grid = g;
        r = rect;
        xCoord = x;
        yCoord = y;
        sXMinCoord = x1;
        sXMaxCoord = x1+grid.getBoxSize()-1;
        sYMinCoord = y1;
        sYMaxCoord = y1+grid.getBoxSize()-1;
        coords = new Coords(x+","+y);
    }

    public boolean isHighlighted() {return highlighted;}

    public void setHighlighted(boolean hmm)
    {
        if (hmm)
        {
            r.setFill(Color.GRAY);
            highlighted = hmm;
            grid.getHighlightedList().add(this);
        }
        else
        {
            r.setFill(Color.TRANSPARENT);
            highlighted = hmm;
        }
    }

    public boolean isAisle() {return isIsle;}

    public Color getColor() {return color;}

    public void setAisled(boolean hmm, Aisle a, Color c, AisleGroup ig)
    {
        if (hmm)
        {
            color = c;
            isIsle = hmm;
            aisle = a;
            aisleGroup = ig;
            setHighlighted(false);
            r.setFill(c);
            r.setStroke(c);
            r.setOpacity(1.0);
        }
        else
        {
            color = null;
            isIsle = hmm;
            aisle = null;
            aisleGroup = null;
            setHighlighted(false);
            r.setFill(Color.TRANSPARENT);
            r.setStroke(Color.TRANSPARENT);
            r.setOpacity(0.5);
        }
    }

    public Rectangle getR() {return r;}

    public Aisle getAisle() {return aisle;}

    public int getX() {return xCoord;}

    public int getY() {return yCoord;}

    public Coords getCoords() {return coords;}

    public void setNulled(boolean hmm)
    {
        nulled = hmm;
        if (hmm)
        {
            r.setFill(Color.BLACK);
            r.setStroke(Color.BLACK);
            r.setOpacity(1.0);
        }
        else
        {
            r.setFill(Color.TRANSPARENT);
            r.setStroke(Color.TRANSPARENT);
            r.setOpacity(0.5);
        }
    }

    public boolean isNulled() {return nulled;}

    public void setAisleIsBeingMoved(boolean hmm, Color c)
    {
        if (hmm)
        {
            r.setFill(c);
            r.setStroke(Color.TRANSPARENT);
            r.setOpacity(0.5);
            beingMoved = hmm;
        }
        else
        {
            r.setFill(Color.TRANSPARENT);
            r.setStroke(Color.TRANSPARENT);
            r.setOpacity(0.5);
            beingMoved = hmm;
        }
    }

    public void setsXMinCoord(double x) {sXMinCoord = x;}

    public void setsXMaxCoord(double x) {sXMaxCoord = x;}

    public void setsYMinCoord(double y) {sYMinCoord = y;}

    public void setsYMaxCoord(double y) {sYMaxCoord = y;}

    public boolean isBeingMoved() {return beingMoved;}

    public double getsXMinCoord() {return sXMinCoord;}

    public double getsYMinCoord() {return sYMinCoord;}

    public double getsXMaxCoord() {return sXMaxCoord;}

    public double getsYMaxCoord() {return sYMaxCoord;}

    public void setHighlightedNull(boolean hmm)
    {
        if (hmm)
        {
            r.setOpacity(0.5);
            grid.getHighlightedNullList().add(this);
        }
        else
        {
            r.setOpacity(1.0);
        }
    }
}