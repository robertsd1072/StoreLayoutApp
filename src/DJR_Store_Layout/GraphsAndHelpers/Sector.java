/**
 * Sector class for GraphOfTheGrid.findPickingPath2()
 * @author David
 */

package DJR_Store_Layout.GraphsAndHelpers;

import java.util.ArrayList;

public class Sector
{
    /** list of locations in sector */
    private final ArrayList<String> verticesInSectorList;

    /** Basic Constructor */
    public Sector() {verticesInSectorList = new ArrayList<>();}

    public void addVertex(String vertex) {verticesInSectorList.add(vertex);}

    public ArrayList<String> getVerticesInSectorList() {return verticesInSectorList;}
}