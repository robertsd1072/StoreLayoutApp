package DJR_Store_Layout.GraphsAndHelpers;

import java.util.ArrayList;

public class Sector
{
    private final ArrayList<String> verticesInSectorList;

    public Sector() {verticesInSectorList = new ArrayList<>();}

    public void addVertex(String vertex) {verticesInSectorList.add(vertex);}

    public ArrayList<String> getVerticesInSectorList() {return verticesInSectorList;}
}