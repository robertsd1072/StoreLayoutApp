package DJR_Store_Layout;

import javafx.scene.paint.Color;

public class IsleInfo
{
    private final String isleID;
    private final String isleGroupName;
    private final Color isleColor;
    private final GridData3.IsleGroup isleGroup;

    public IsleInfo(String i, String g, Color c, GridData3.IsleGroup ig)
    {
        isleID = i;
        isleGroupName = g;
        isleColor = c;
        isleGroup = ig;
    }

    public String getIsleID()
    {
        return isleID;
    }

    public String getIsleGroupName()
    {
        return isleGroupName;
    }

    public Color getIsleColor()
    {
        return isleColor;
    }

    public GridData3.IsleGroup getIsleGroup()
    {
        return isleGroup;
    }
}
