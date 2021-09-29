/**
 * Class where info is stored to make isle from file
 * This could have been done in Aisle.java but to change all of my methods involved
 * in making an isle would be too much work so I just made a new class
 * @author David Roberts
 */

package DJR_Store_Layout.HelperClasses;

public class InfoToMakeAisleFromFile
{
    private final int numberOfIsleSections;
    private final String numberOfSubsectionsForEachSection;
    private final String endCapLocation;
    private final String directionOfIncreasingIsleSections;

    public InfoToMakeAisleFromFile(int n, String s1, String s2, String s3)
    {
        numberOfIsleSections = n;
        numberOfSubsectionsForEachSection = s1;
        endCapLocation = s2;
        directionOfIncreasingIsleSections = s3;
    }

    public int getNumberOfAisleSections() {return numberOfIsleSections;}

    public String getNumberOfSubsectionsForEachSection() {return numberOfSubsectionsForEachSection;}

    public String getEndCapLocation() {return endCapLocation;}

    public String getDirectionOfIncreasingAisleSections() {return directionOfIncreasingIsleSections;}
}