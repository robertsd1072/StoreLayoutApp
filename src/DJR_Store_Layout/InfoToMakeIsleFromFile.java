package DJR_Store_Layout;

public class InfoToMakeIsleFromFile
{
    private final int numberOfIsleSections;
    private final String numberOfSubsectionsForEachSection;
    private final String endCapLocation;
    private final String directionOfIncreasingIsleSections;

    public InfoToMakeIsleFromFile(int n, String s1, String s2, String s3)
    {
        numberOfIsleSections = n;
        numberOfSubsectionsForEachSection = s1;
        endCapLocation = s2;
        directionOfIncreasingIsleSections = s3;
    }

    public int getNumberOfIsleSections() {
        return numberOfIsleSections;
    }

    public String getNumberOfSubsectionsForEachSection() {
        return numberOfSubsectionsForEachSection;
    }

    public String getEndCapLocation() {
        return endCapLocation;
    }

    public String getDirectionOfIncreasingIsleSections() {
        return directionOfIncreasingIsleSections;
    }
}
