/**
 * Custom returning class of various information for GraphOfTheGrid.findDistanceBetween()
 * @author David Roberts
 */

package DJR_Store_Layout.GraphsAndHelpers;

import java.util.Comparator;

public class DistanceReturn
{
    private final int distance;
    /** Cell Path 34,56,76,95... */
    private final String path;
    /** Ending coordinates */
    private final String end;

    public DistanceReturn(int d, String p, String e)
    {
        distance = d;
        path = p;
        end = e;
    }

    public int getDistance() {return distance;}

    public String getPath() {return path;}

    public String getEnd() {return end;}

    /**
     * FOR TESTING PURPOSES ONLY
     * Was used in GraphOfTheGrid.findClosestCellAndComputeDistanceIfIsleShapeIsArea()
     */
    public static class DistanceComparator implements Comparator<DistanceReturn>
    {
        public int compare(DistanceReturn dr1, DistanceReturn dr2)
        {
            return Integer.compare(dr1.distance, dr2.distance);
        }
    }
}