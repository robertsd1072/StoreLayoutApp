/**
 * Isle class for retaining all information for an isle
 * This includes isle info pertaining to: isle sections and isle subsections
 * Also retrieves cell within isle corresponding to location string of an item
 *
 * @author David Roberts
 */

package DJR_Store_Layout;

import java.util.ArrayList;

public class Isle
{
    private final GridData3 g;
    private final String isleID;
    private final GridData3.IsleGroup isleGroup;
    private IsleCellList isleCellList;
    private int numberOfIsleSections;
    private ArrayList<Integer> numberOfSubsectionsForEachSection;
    private String endCapLocation;
    /**
     * Given by up/down/left/right
     */
    private String directionOfIncreasingIsleSections;

    /**
     * Basic Constructor
     *
     * @param id id of isle
     * @param ig isle group
     * @param grid grid data class
     */
    public Isle(String id, GridData3.IsleGroup ig, GridData3 grid)
    {
        isleID = id;
        isleGroup = ig;
        g = grid;
    }

    public void setIsleCellList(IsleCellList icl)
    {
        isleCellList = icl;
    }

    public String getIsleID()
    {
        return isleID;
    }

    public GridData3.IsleGroup getIsleGroup()
    {
        return isleGroup;
    }

    public IsleCellList getIsleCellList()
    {
        return isleCellList;
    }

    /**
     * Setups isle info for sections and subsections
     *
     * @param n number of isle sections
     * @param arr number of subsections for each section
     * @param endCap location
     * @param direction direction of increasing sections
     */
    public void setupIsleInfo(int n, ArrayList<Integer> arr, String endCap, String direction)
    {
        numberOfIsleSections = n;
        numberOfSubsectionsForEachSection = arr;
        endCapLocation = endCap;
        directionOfIncreasingIsleSections = direction;
    }

    /**
     * Prints to debug isle info
     */
    public void printInfo()
    {
        System.out.println("IsleID: "+isleID);
        System.out.println("IsleGroup: "+isleGroup.getName());
        System.out.println("Back or FLoor: "+isleGroup.getBackOrFloor());
        if (hasSetupInfo())
        {
            System.out.println("EndCap Location: "+endCapLocation);
            System.out.println("Direction: "+directionOfIncreasingIsleSections);
            System.out.println("Number Of Isle Sections: "+numberOfIsleSections);
            for (int i=0; i<numberOfSubsectionsForEachSection.size(); i++)
            {
                System.out.println("Isle Section "+i+": "+numberOfSubsectionsForEachSection.get(i)+" subsection(s)");
            }
        }
        else
            System.out.println("Isle Info has not been setup");
    }

    public int getNumberOfIsleSections()
    {
        return numberOfIsleSections;
    }

    /**
     * Finds cell within isle that corresponds to given item location in back
     *
     * @param isleSection isle sections
     * @param isleSubsection isle subsection
     * @return String of coords for cell
     */
    public String getCoordsGivenLocationInBack(int isleSection, String isleSubsection)
    {
        //"A" numeric value is 10 so if subsection is A then whatSubection = 1.
        //If subsection is B then whatSubsection = 2, and so on.
        int whatSubsection = Character.getNumericValue(isleSubsection.charAt(0))-9;

        System.out.println("Returning from Section and Subsection");
        return getIsleCoordsGivenSectionAndSubsection(1, whatSubsection);
    }

    /**
     * Finds cell within isle that corresponds to given item location on floor
     *
     * @param isleSection isle sections
     * @param isleSubsection isle subsection
     * @return String of coords for cell
     */
    public String getCoordsGivenLocationOnFloor(int isleSection, String isleSubsection)
    {
        try
        {
            int hmm = Integer.parseInt(isleID.charAt(1)+"");
            System.out.println("Isle not clothes");

            if (isleSection == 0)
            {
                System.out.println("Returning cuz at endcap");
                if (endCapLocation.compareTo("west") == 0)
                {
                    return getIsleCoordsGivenEndcap("west");
                }
                if (endCapLocation.compareTo("south") == 0)
                {
                    return getIsleCoordsGivenEndcap("south");
                }
                if (endCapLocation.compareTo("east") == 0)
                {
                    return getIsleCoordsGivenEndcap("east");
                }
                if (endCapLocation.compareTo("north") == 0)
                {
                    return getIsleCoordsGivenEndcap("north");
                }
            }
            else
            {
                System.out.println("Returning using Section and Subsection");
                return getIsleCoordsGivenSectionAndSubsection(isleSection, Integer.parseInt(isleSubsection));
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Isle is clothes");
            return "closest";
        }

        return "getCoordsGivenLocationOnFloor didn't work";
    }

    /**
     * Finds cell corresponding to isle end cap
     *
     * @param where location of endcap: north/east/south/west
     * @return String of coords for cell
     */
    private String getIsleCoordsGivenEndcap(String where)
    {
        //System.out.println("Endcap where: "+where);
        IsleCellList.IsleCellNode nodeToReturnCoords = null;

        if (where.compareTo("south") == 0)
        {
            int furthestYCoord = 0;

            IsleCellList.IsleCellNode curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getY() > furthestYCoord)
                {
                    furthestYCoord = curr.getrNode().getY();
                    nodeToReturnCoords = curr;
                }
                curr = curr.next;
            }
            return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
        }
        if (where.compareTo("north") == 0)
        {
            int nearestYCoord = 10000000;

            IsleCellList.IsleCellNode curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getY() < nearestYCoord)
                {
                    nearestYCoord = curr.getrNode().getY();
                    nodeToReturnCoords = curr;
                }
                curr = curr.next;
            }
            return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
        }
        if (where.compareTo("west") == 0)
        {
            int nearestXCoord = 10000000;

            IsleCellList.IsleCellNode curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getX() < nearestXCoord)
                {
                    nearestXCoord = curr.getrNode().getX();
                    nodeToReturnCoords = curr;
                }
                curr = curr.next;
            }
            return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
        }
        if (where.compareTo("east") == 0)
        {
            int furthestXCoord = 0;

            IsleCellList.IsleCellNode curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getX() > furthestXCoord)
                {
                    furthestXCoord = curr.getrNode().getX();
                    nodeToReturnCoords = curr;
                }
                curr = curr.next;
            }
            return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
        }
        return "Get Endcap Coord didnt work";
    }

    /**
     * @return total cell count in isle
     */
    private int getNumberOfCellsInIsle()
    {
        IsleCellList.IsleCellNode curr = isleCellList.first;
        int numberOfCells = 0;
        while (curr != null)
        {
            numberOfCells++;
            curr = curr.next;
        }
        return numberOfCells;
    }

    /**
     * Finds cell within isle that corresponds to given item location
     * This takes into account total subsections of isle and scales accordingly
     *
     * @param whatSection section
     * @param whatSubsection subsection
     * @return String of coords for cell
     */
    private String getIsleCoordsGivenSectionAndSubsection(int whatSection, int whatSubsection)
    {
        //If Isle is in the Back or with Only One Section
        if (whatSection == 1 && numberOfIsleSections == 2)
        {
            System.out.println("Just 2 Sections: Simple Version");
            System.out.println("Direction: "+directionOfIncreasingIsleSections);

            if (directionOfIncreasingIsleSections.compareTo("right") == 0)
            {
                String s = getIsleCoordsGivenEndcap("west");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int xToReturn = (int) (x+(getCellsToSubsection()*whatSubsection));
                if (endCapLocation.compareTo("west") != 0)
                    xToReturn--;

                return xToReturn+","+y;
            }
            if (directionOfIncreasingIsleSections.compareTo("up") == 0)
            {
                String s = getIsleCoordsGivenEndcap("south");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int yToReturn = (int) (y-(getCellsToSubsection()*whatSubsection));
                if (endCapLocation.compareTo("south") != 0)
                    yToReturn++;

                return x+","+yToReturn;
            }
            if (directionOfIncreasingIsleSections.compareTo("left") == 0)
            {
                String s = getIsleCoordsGivenEndcap("east");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int xToReturn = (int) (x-(getCellsToSubsection()*whatSubsection));
                if (endCapLocation.compareTo("east") != 0)
                    xToReturn++;

                return xToReturn+","+y;
            }
            if (directionOfIncreasingIsleSections.compareTo("down") == 0)
            {
                String s = getIsleCoordsGivenEndcap("north");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int yToReturn = (int) (y+(getCellsToSubsection()*whatSubsection));
                if (endCapLocation.compareTo("north") != 0)
                    yToReturn--;

                return x+","+yToReturn;
            }
        }
        //If Isle is on the Floor w/ multiple Sections
        else
        {
            System.out.println("Multiple Sections: Complex Version");
            float cellsToSubsection = getCellsToSubsection();
            System.out.println("Cells to Subsection: "+cellsToSubsection);

            if (directionOfIncreasingIsleSections.compareTo("right") == 0)
            {
                String s = getIsleCoordsGivenEndcap("west");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int xToReturn = x;
                for (int i=1; i<whatSection+1; i++)
                {
                    if (i==whatSection)
                    {
                        xToReturn+=whatSubsection*cellsToSubsection;
                    }
                    else
                    {
                        xToReturn+=(numberOfSubsectionsForEachSection.get(i)*cellsToSubsection);
                    }
                }

                if (endCapLocation.compareTo("west") != 0)
                    xToReturn--;

                return xToReturn+","+y;
            }
            if (directionOfIncreasingIsleSections.compareTo("up") == 0)
            {
                String s = getIsleCoordsGivenEndcap("south");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int yToReturn = y;
                for (int i=1; i<whatSection+1; i++)
                {
                    if (i==whatSection)
                        yToReturn-=whatSubsection*cellsToSubsection;
                    else
                        yToReturn-=(numberOfSubsectionsForEachSection.get(i)*cellsToSubsection);
                }

                if (endCapLocation.compareTo("south") != 0)
                    yToReturn++;

                return x+","+yToReturn;
            }
            if (directionOfIncreasingIsleSections.compareTo("left") == 0)
            {
                String s = getIsleCoordsGivenEndcap("east");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int xToReturn = x;
                for (int i=1; i<whatSection+1; i++)
                {
                    if (i==whatSection)
                        xToReturn-=whatSubsection*cellsToSubsection;
                    else
                        xToReturn-=(numberOfSubsectionsForEachSection.get(i)*cellsToSubsection);
                }

                if (endCapLocation.compareTo("east") != 0)
                    xToReturn++;

                return xToReturn+","+y;
            }
            if (directionOfIncreasingIsleSections.compareTo("down") == 0)
            {
                String s = getIsleCoordsGivenEndcap("north");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int yToReturn = y;
                for (int i=1; i<whatSection+1; i++)
                {
                    if (i==whatSection)
                        yToReturn+=whatSubsection*cellsToSubsection;
                    else
                        yToReturn+=(numberOfSubsectionsForEachSection.get(i)*cellsToSubsection);
                }

                if (endCapLocation.compareTo("north") != 0)
                    yToReturn--;

                return x+","+yToReturn;
            }
        }
        return "Get Subsection Coord didnt work";
    }

    /**
     * @return ratio of cells coorsponding to one isle subsection
     */
    public float getCellsToSubsection()
    {
        int totalNumberOfSubsections = 0;
        //Skip isle section 0 cuz endcap.
        for (int i=1; i<numberOfSubsectionsForEachSection.size(); i++)
            totalNumberOfSubsections+=numberOfSubsectionsForEachSection.get(i);

        return (float) getNumberOfCellsInIsle()/totalNumberOfSubsections;
    }

    public String getEndCapLocation()
    {
        return endCapLocation;
    }

    public String getDirectionOfIncreasingIsleSections()
    {
        return directionOfIncreasingIsleSections;
    }

    public boolean hasSetupInfo()
    {
        return numberOfIsleSections > 0;
    }

    public ArrayList<Integer> getNumberOfSubsectionsForEachSection()
    {
        return numberOfSubsectionsForEachSection;
    }

    /**
     * For determining if given item location is valid for isle
     *
     * @param isleSection section
     * @param isleSubsection subsection
     * @return true if valid, false if invald
     */
    public boolean inputingValidIsleLocation(int isleSection, String isleSubsection)
    {
        int isleSubsec = Integer.parseInt(isleSubsection);
        try
        {
            return isleSection <= numberOfIsleSections && isleSubsec <= numberOfSubsectionsForEachSection.get(isleSection);
        }
        catch (IndexOutOfBoundsException e)
        {
            return false;
        }
    }

    /**
     * List of cells within isle
     */
    public static class IsleCellList
    {
        private IsleCellNode first;
        private IsleCellNode last;
        private int size;

        public IsleCellList()
        {
            first = null;
            last = null;
            size = 0;
        }

        public void add(GridData3.RNode node)
        {
            if (size == 0)
            {
                first = new IsleCellNode(node);
                last = first;
                size = 1;
            }
            else if (size > 0)
            {
                last.next = new IsleCellNode(node);
                last = last.next;
                size++;
            }
        }

        private void clear()
        {
            first = null;
            last = null;
            size = 0;
        }

        public IsleCellNode getFirst()
        {
            return first;
        }

        public class IsleCellNode
        {
            private final GridData3.RNode rNode;
            private IsleCellNode next;

            public IsleCellNode(GridData3.RNode node)
            {
                rNode = node;
                next = null;
            }

            public GridData3.RNode getrNode()
            {
                return rNode;
            }

            public IsleCellNode getNext()
            {
                return next;
            }
        }
    }
}
