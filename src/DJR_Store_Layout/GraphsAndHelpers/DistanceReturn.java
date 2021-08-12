package DJR_Store_Layout.GraphsAndHelpers;

import java.util.Comparator;

public class DistanceReturn
{
    private final int distance;
    private final String path;
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

    public static class DistanceComparator implements Comparator<DistanceReturn>
    {
        public int compare(DistanceReturn dr1, DistanceReturn dr2)
        {
            return Integer.compare(dr1.distance, dr2.distance);
        }
    }
}