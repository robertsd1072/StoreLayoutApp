package DJR_Store_Layout.GraphsAndHelpers;

import java.util.ArrayList;

public class FindingPathReturn
{
    private final ArrayList<String> locationPath;
    private final ArrayList<String> vertexPath;
    private final String cellPath;

    public FindingPathReturn(ArrayList<String> a1, ArrayList<String> a2, String s)
    {
        locationPath = a1;
        vertexPath = a2;
        cellPath = s;
    }

    public ArrayList<String> getLocationPath() {return locationPath;}

    public ArrayList<String> getVertexPath() {return vertexPath;}

    public String getCellPath() {return cellPath;}
}