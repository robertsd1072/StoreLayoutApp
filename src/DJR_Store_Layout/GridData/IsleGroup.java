package DJR_Store_Layout.GridData;

import javafx.scene.paint.Color;
import java.util.Hashtable;
import java.util.Set;

public class IsleGroup
{
    private final String name;
    private final Color color;
    private CellList isleGroupCellList;
    private final Hashtable<String, Isle> isleIDList;
    private String backOrFloor;

    public IsleGroup(String n, Color c)
    {
        name = n;
        color = c;
        isleIDList = new Hashtable<>();
    }

    public String getName()
    {
        return name;
    }

    public Color getColor()
    {
        return color;
    }

    public CellList getIsleGroupCellList()
    {
        return isleGroupCellList;
    }

    public void addNewID(String newID, Isle i)
    {
        isleIDList.put(newID, i);
    }

    public Hashtable<String, Isle> getIsleIDList()
    {
        return isleIDList;
    }

    public void setBackOrFloor(String s)
    {
        backOrFloor = s;
    }

    public String getBackOrFloor()
    {
        return backOrFloor;
    }

    public boolean containsIsle(String id)
    {
        Set<String> isleIDs = isleIDList.keySet();
        for (String s : isleIDs)
        {
            if (s.compareTo(id) == 0)
                return true;
        }
        return false;
    }

    public void setIsleGroupCellList(CellList cl) {isleGroupCellList = cl;}
}