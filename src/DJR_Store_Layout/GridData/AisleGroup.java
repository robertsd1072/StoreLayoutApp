/**
 * Isle Group class
 * Contains table of isles, color and name of group
 * @author David Roberts
 */

package DJR_Store_Layout.GridData;

import javafx.scene.paint.Color;
import java.util.Hashtable;

public class AisleGroup
{
    private final String name;
    private final Color color;
    private CellList aisleGroupCellList;
    private final Hashtable<String, Aisle> aisleIDList;
    private String backOrFloor;

    public AisleGroup(String n, Color c)
    {
        name = n;
        color = c;
        aisleIDList = new Hashtable<>();
    }

    public String getName() {return name;}

    public Color getColor() {return color;}

    public CellList getAisleGroupCellList() {return aisleGroupCellList;}

    public void addNewID(String newID, Aisle i) {aisleIDList.put(newID, i);}

    public Hashtable<String, Aisle> getAisleIDList() {return aisleIDList;}

    public void setBackOrFloor(String s) {backOrFloor = s;}

    public String getBackOrFloor() {return backOrFloor;}

    public boolean containsAisle(String id) {return aisleIDList.get(id) != null;}

    public void setAisleGroupCellList(CellList cl) {aisleGroupCellList = cl;}
}