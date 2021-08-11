/**
 * Isle class for retaining all information for an isle
 * This includes isle info pertaining to: isle sections and isle subsections
 * Also retrieves cell within isle corresponding to location string of an item
 *
 * @author David Roberts
 */

package DJR_Store_Layout;

import java.util.ArrayList;
import java.util.Hashtable;

public class Isle
{
    private final GridData3 g;
    private final String isleID;
    private final GridData3.IsleGroup isleGroup;
    private IsleCellList isleCellList;
    private int numberOfIsleSections;
    private Hashtable<Integer, Integer> numberOfSubsectionsForEachSection;
    /**
     * Given by north/south/east/west
     */
    private String endCapLocation;
    /**
     * Given by up/down/left/right
     */
    private String directionOfIncreasingIsleSections;
    private String shape;

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
        shape = findShape();
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
     * @param hashtable number of subsections for each section
     * @param endCap location
     * @param direction direction of increasing sections
     */
    public void setupIsleInfo(int n, Hashtable<Integer, Integer> hashtable, String endCap, String direction)
    {
        numberOfIsleSections = n;
        numberOfSubsectionsForEachSection = hashtable;
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
            for (int i : numberOfSubsectionsForEachSection.keySet())
                System.out.println("Isle Section "+i+" has "+numberOfSubsectionsForEachSection.get(i)+" subsections");
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
     * @param isleSubsection isle subsection
     * @return String of coords for cell
     */
    public String getCoordsGivenLocationInBack(String isleSubsection)
    {
        //"A" numeric value is 10 so if subsection is A then whatSubection = 1.
        //If subsection is B then whatSubsection = 2, and so on.
        int whatSubsection = Character.getNumericValue(isleSubsection.charAt(0))-9;

        //Testing if back isle is split
        boolean split = false;
        String which = null;
        if (directionOfIncreasingIsleSections.compareTo("up") == 0 || directionOfIncreasingIsleSections.compareTo("down") == 0)
        {
            IsleCellList.IsleCellNode curr = isleCellList.first;
            int x = curr.getrNode().getX();
            while (curr != null)
            {
                if (curr.getrNode().getX() != x)
                {
                    split = true;

                    int hmm = whatSubsection % 2;
                    if (hmm == 1)
                        which = "right";
                    else
                        which = "left";

                    break;
                }
                curr = curr.next;
            }
        }
        else if (directionOfIncreasingIsleSections.compareTo("right") == 0 || directionOfIncreasingIsleSections.compareTo("left") == 0)
        {
            IsleCellList.IsleCellNode curr = isleCellList.first;
            int y = curr.getrNode().getY();
            while (curr != null)
            {
                if (curr.getrNode().getY() != y)
                {
                    split = true;

                    int hmm = whatSubsection % 2;
                    if (hmm == 1)
                        which = "top";
                    else
                        which = "bottom";

                    break;
                }
                curr = curr.next;
            }
        }

        if (split)
        {
            System.out.println("Returning from Section and Subsection Split");
            return getIsleCoordsGivenSectionAndSubsectionSplit(whatSubsection, which);
        }
        else
        {
            System.out.println("Returning from Section and Subsection");
            return getIsleCoordsGivenSectionAndSubsection(1, whatSubsection);
        }
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
            if (verifyCell(nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY()))
                return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
            else
                throw new RuntimeException("getIsleCoordsGivenEndcap returned a cell not belonging to the isle using south");
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
            if (verifyCell(nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY()))
                return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
            else
                throw new RuntimeException("getIsleCoordsGivenEndcap returned a cell not belonging to the isle using north");
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
            if (verifyCell(nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY()))
                return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
            else
                throw new RuntimeException("getIsleCoordsGivenEndcap returned a cell not belonging to the isle using west");
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
            if (verifyCell(nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY()))
                return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
            else
                throw new RuntimeException("getIsleCoordsGivenEndcap returned a cell not belonging to the isle using east");
        }
        return "Get Endcap Coord didnt work";
    }

    /**
     * @return total cell count in isle
     */
    public int getNumberOfCellsInIsle()
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
                Coords coords = new Coords(getIsleCoordsGivenEndcap("west"));
                int x = coords.getX();
                int y = coords.getY();

                int xToReturn = (int) (x+(getCellsToSubsection()*whatSubsection));
                if (endCapLocation.compareTo("west") != 0  && xToReturn != x)
                    xToReturn--;

                if (verifyCell(xToReturn+","+y))
                    return xToReturn+","+y;
                else
                    System.out.println("Cell "+xToReturn+","+y+" is not in isle "+isleID);
            }
            if (directionOfIncreasingIsleSections.compareTo("up") == 0)
            {
                Coords coords = new Coords(getIsleCoordsGivenEndcap("south"));
                int x = coords.getX();
                int y = coords.getY();

                int yToReturn = (int) (y-(getCellsToSubsection()*whatSubsection));
                if (endCapLocation.compareTo("south") != 0 && yToReturn != y)
                    yToReturn++;

                if (verifyCell(x+","+yToReturn))
                    return x+","+yToReturn;
                else
                    System.out.println("Cell "+x+","+yToReturn+" is not in isle "+isleID);
            }
            if (directionOfIncreasingIsleSections.compareTo("left") == 0)
            {
                Coords coords = new Coords(getIsleCoordsGivenEndcap("east"));
                int x = coords.getX();
                int y = coords.getY();

                int xToReturn = (int) (x-(getCellsToSubsection()*whatSubsection));
                if (endCapLocation.compareTo("east") != 0 && xToReturn != x)
                    xToReturn++;

                if (verifyCell(xToReturn+","+y))
                    return xToReturn+","+y;
                else
                    System.out.println("Cell "+xToReturn+","+y+" is not in isle "+isleID);
            }
            if (directionOfIncreasingIsleSections.compareTo("down") == 0)
            {
                Coords coords = new Coords(getIsleCoordsGivenEndcap("north"));
                int x = coords.getX();
                int y = coords.getY();

                int yToReturn = (int) (y+(getCellsToSubsection()*whatSubsection));
                if (endCapLocation.compareTo("north") != 0 && yToReturn != y)
                    yToReturn--;

                if (verifyCell(x+","+yToReturn))
                    return x+","+yToReturn;
                else
                    System.out.println("Cell "+x+","+yToReturn+" is not in isle "+isleID);
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
                Coords coords = new Coords(getIsleCoordsGivenEndcap("west"));
                System.out.println("Starting: "+coords);
                int x = coords.getX();
                int y = coords.getY();

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

                if (endCapLocation.compareTo("west") != 0 && xToReturn != x)
                    xToReturn--;

                if (verifyCell(xToReturn+","+y))
                    return xToReturn+","+y;
                else
                    throw new RuntimeException("getIsleCoordsGivenSectionAndSubsection returned a cell not belonging to the isle using direction right");
            }
            if (directionOfIncreasingIsleSections.compareTo("up") == 0)
            {
                Coords coords = new Coords(getIsleCoordsGivenEndcap("south"));
                int x = coords.getX();
                int y = coords.getY();

                int yToReturn = y;
                for (int i=1; i<whatSection+1; i++)
                {
                    if (i==whatSection)
                        yToReturn-=whatSubsection*cellsToSubsection;
                    else
                        yToReturn-=(numberOfSubsectionsForEachSection.get(i)*cellsToSubsection);
                }

                if (endCapLocation.compareTo("south") != 0 && yToReturn != y)
                    yToReturn++;

                if (verifyCell(x+","+yToReturn))
                    return x+","+yToReturn;
                else
                    throw new RuntimeException("getIsleCoordsGivenSectionAndSubsection returned a cell not belonging to the isle using direction up");
            }
            if (directionOfIncreasingIsleSections.compareTo("left") == 0)
            {
                Coords coords = new Coords(getIsleCoordsGivenEndcap("east"));
                int x = coords.getX();
                int y = coords.getY();

                int xToReturn = x;
                for (int i=1; i<whatSection+1; i++)
                {
                    if (i==whatSection)
                        xToReturn-=whatSubsection*cellsToSubsection;
                    else
                        xToReturn-=(numberOfSubsectionsForEachSection.get(i)*cellsToSubsection);
                }

                if (endCapLocation.compareTo("east") != 0 && xToReturn != x)
                    xToReturn++;

                if (verifyCell(xToReturn+","+y))
                    return xToReturn+","+y;
                else
                    throw new RuntimeException("getIsleCoordsGivenSectionAndSubsection returned a cell not belonging to the isle using direction left");
            }
            if (directionOfIncreasingIsleSections.compareTo("down") == 0)
            {
                Coords coords = new Coords(getIsleCoordsGivenEndcap("north"));
                int x = coords.getX();
                int y = coords.getY();

                int yToReturn = y;
                for (int i=1; i<whatSection+1; i++)
                {
                    if (i==whatSection)
                        yToReturn+=whatSubsection*cellsToSubsection;
                    else
                        yToReturn+=(numberOfSubsectionsForEachSection.get(i)*cellsToSubsection);
                }

                if (endCapLocation.compareTo("north") != 0 && yToReturn != y)
                    yToReturn--;

                if (verifyCell(x+","+yToReturn))
                    return x+","+yToReturn;
                else
                    throw new RuntimeException("getIsleCoordsGivenSectionAndSubsection returned a cell not belonging to the isle using direction down");
            }
        }

        return "Get Subsection Coord didnt work";
    }

    private String getIsleCoordsGivenSectionAndSubsectionSplit(int whatSubsection, String which)
    {
        System.out.println("Which Side of Split: "+which);

        if (directionOfIncreasingIsleSections.compareTo("right") == 0)
        {
            Coords coords = new Coords(getIsleCoordsGivenEndcapSplit("west", which));
            System.out.println("Starting: "+coords);
            int x = coords.getX();
            int y = coords.getY();

            int xToReturn = (x+((int) (getCellsToSubsection()/2)*whatSubsection));
            if (endCapLocation.compareTo("west") != 0)
                xToReturn--;

            return xToReturn+","+y;
        }
        if (directionOfIncreasingIsleSections.compareTo("up") == 0)
        {
            Coords coords = new Coords(getIsleCoordsGivenEndcapSplit("south", which));
            int x = coords.getX();
            int y = coords.getY();

            int yToReturn = (int) (y-((int) (getCellsToSubsection()/2)*whatSubsection));
            if (endCapLocation.compareTo("south") != 0)
                yToReturn++;

            return x+","+yToReturn;
        }
        if (directionOfIncreasingIsleSections.compareTo("left") == 0)
        {
            Coords coords = new Coords(getIsleCoordsGivenEndcapSplit("east", which));
            int x = coords.getX();
            int y = coords.getY();

            int xToReturn = (int) (x-((int) (getCellsToSubsection()/2)*whatSubsection));
            if (endCapLocation.compareTo("east") != 0)
                xToReturn++;

            return xToReturn+","+y;
        }
        if (directionOfIncreasingIsleSections.compareTo("down") == 0)
        {
            Coords coords = new Coords(getIsleCoordsGivenEndcapSplit("north", which));
            int x = coords.getX();
            int y = coords.getY();

            int yToReturn = (int) (y+((int) (getCellsToSubsection()/2)*whatSubsection));
            if (endCapLocation.compareTo("north") != 0)
                yToReturn--;

            return x+","+yToReturn;
        }

        return "Get Subsection Coord for Splits didnt work";
    }

    private String getIsleCoordsGivenEndcapSplit(String where, String which)
    {
        IsleCellList.IsleCellNode nodeToReturnCoords = null;

        if (where.compareTo("south") == 0)
        {
            int xCoord1 = 0;
            int xCoord2 = 0;

            IsleCellList.IsleCellNode curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getX() != xCoord1)
                {
                    xCoord1 = curr.getrNode().getX();
                    break;
                }
                curr = curr.next;
            }
            curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getX() != xCoord2 && curr.getrNode().getX() != xCoord1)
                {
                    xCoord2 = curr.getrNode().getX();
                    break;
                }
                curr = curr.next;
            }

            System.out.println("xCoord1: "+xCoord1);
            System.out.println("xCoord2: "+xCoord1);

            int theCorrectX;
            if (which.compareTo("left") == 0)
            {
                if (xCoord1 < xCoord2)
                    theCorrectX = xCoord1;
                else
                    theCorrectX = xCoord2;
            }
            else
            {
                if (xCoord1 > xCoord2)
                    theCorrectX = xCoord1;
                else
                    theCorrectX = xCoord2;
            }
            System.out.println("X: "+theCorrectX);

            int furthestYCoord = 0;

            curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getY() > furthestYCoord)
                {
                    furthestYCoord = curr.getrNode().getY();
                    nodeToReturnCoords = curr;
                }
                curr = curr.next;
            }
            return theCorrectX+","+nodeToReturnCoords.getrNode().getY();
        }
        if (where.compareTo("north") == 0)
        {
            int xCoord1 = 0;
            int xCoord2 = 0;

            IsleCellList.IsleCellNode curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getX() != xCoord1)
                {
                    xCoord1 = curr.getrNode().getX();
                    break;
                }
                curr = curr.next;
            }
            curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getX() != xCoord2 && curr.getrNode().getX() != xCoord1)
                {
                    xCoord2 = curr.getrNode().getX();
                    break;
                }
                curr = curr.next;
            }

            System.out.println("xCoord1: "+xCoord1);
            System.out.println("xCoord2: "+xCoord1);

            int theCorrectX;
            if (which.compareTo("left") == 0)
            {
                if (xCoord1 < xCoord2)
                    theCorrectX = xCoord1;
                else
                    theCorrectX = xCoord2;
            }
            else
            {
                if (xCoord1 > xCoord2)
                    theCorrectX = xCoord1;
                else
                    theCorrectX = xCoord2;
            }
            System.out.println("X: "+theCorrectX);

            int nearestYCoord = 10000000;

            curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getY() < nearestYCoord)
                {
                    nearestYCoord = curr.getrNode().getY();
                    nodeToReturnCoords = curr;
                }
                curr = curr.next;
            }
            return theCorrectX+","+nodeToReturnCoords.getrNode().getY();
        }
        if (where.compareTo("west") == 0)
        {
            int yCoord1 = 0;
            int yCoord2 = 0;

            IsleCellList.IsleCellNode curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getY() != yCoord1)
                {
                    yCoord1 = curr.getrNode().getY();
                    break;
                }
                curr = curr.next;
            }
            curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getY() != yCoord2 && curr.getrNode().getY() != yCoord1)
                {
                    yCoord2 = curr.getrNode().getY();
                    break;
                }
                curr = curr.next;
            }

            System.out.println("yCoord1: "+yCoord1);
            System.out.println("yCoord2: "+yCoord2);

            int theCorrectY;
            if (which.compareTo("top") == 0)
            {
                if (yCoord1 < yCoord2)
                    theCorrectY = yCoord1;
                else
                    theCorrectY = yCoord2;
            }
            else
            {
                if (yCoord1 > yCoord2)
                    theCorrectY = yCoord1;
                else
                    theCorrectY = yCoord2;
            }
            System.out.println("Y: "+theCorrectY);

            int nearestXCoord = 10000000;

            curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getX() < nearestXCoord)
                {
                    nearestXCoord = curr.getrNode().getX();
                    nodeToReturnCoords = curr;
                }
                curr = curr.next;
            }
            return nodeToReturnCoords.getrNode().getX()+","+theCorrectY;
        }
        if (where.compareTo("east") == 0)
        {
            int yCoord1 = 0;
            int yCoord2 = 0;

            IsleCellList.IsleCellNode curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getY() != yCoord1)
                {
                    yCoord1 = curr.getrNode().getY();
                    break;
                }
                curr = curr.next;
            }
            curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getY() != yCoord2 && curr.getrNode().getY() != yCoord1)
                {
                    yCoord2 = curr.getrNode().getY();
                    break;
                }
                curr = curr.next;
            }

            System.out.println("yCoord1: "+yCoord1);
            System.out.println("yCoord2: "+yCoord2);

            int theCorrectY;
            if (which.compareTo("top") == 0)
            {
                if (yCoord1 < yCoord2)
                    theCorrectY = yCoord1;
                else
                    theCorrectY = yCoord2;
            }
            else
            {
                if (yCoord1 > yCoord2)
                    theCorrectY = yCoord1;
                else
                    theCorrectY = yCoord2;
            }
            System.out.println("Y: "+theCorrectY);

            int furthestXCoord = 0;

            curr = isleCellList.first;
            while (curr != null)
            {
                if (curr.getrNode().getX() > furthestXCoord)
                {
                    furthestXCoord = curr.getrNode().getX();
                    nodeToReturnCoords = curr;
                }
                curr = curr.next;
            }
            return nodeToReturnCoords.getrNode().getX()+","+theCorrectY;
        }
        return "Get Endcap Coord Split didnt work";
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

        return (float) (getNumberOfCellsInIsle()-1)/totalNumberOfSubsections;
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

    public Hashtable<Integer, Integer> getNumberOfSubsectionsForEachSection()
    {
        return numberOfSubsectionsForEachSection;
    }

    public String getShape()
    {
        return shape;
    }

    /**
     * For determining if given item location is valid for isle
     *
     * @param isleSection section
     * @param isleSubsection subsection
     * @return true if valid, false if invald
     */
    public boolean inputingValidIsleLocationOnFloor(int isleSection, String isleSubsection)
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

    public boolean inputingValidIsleLocationInBack(String isleSubsection)
    {
        int whatSubsection = Character.getNumericValue(isleSubsection.charAt(0))-9;

        return (whatSubsection <= numberOfSubsectionsForEachSection.get(1));
    }

    private String findShape()
    {
        /*
        To return:
        "straight"
        "diagonal"
        "area" - isles that have little to no location differentiation and are not straight or diagonal.
        Ex: clothes sections where locations change often, island isles that float around the store and are small, produce (bananas) isles where there is no locations
         */
        ArrayList<Integer> xCoords = new ArrayList<>();
        ArrayList<Integer> yCoords = new ArrayList<>();

        IsleCellList.IsleCellNode curr = isleCellList.first;
        while (curr != null)
        {
            xCoords.add(curr.getrNode().getX());
            yCoords.add(curr.getrNode().getY());

            curr = curr.next;
        }

        int xCoord = xCoords.get(0);
        int yCoord = yCoords.get(0);
        int xChange = 0;
        int yChange = 0;

        for (int i=1; i<xCoords.size(); i++)
        {
            if (xCoords.get(i) != xCoord)
                xChange++;

            if (yCoords.get(i) != yCoord)
                yChange++;
        }

        if (xChange == 0 ^ yChange == 0)
            return "straight";

        if (xChange == 1 && isleGroup.getBackOrFloor().compareTo("back") == 0)
            return "straight";

        if (yChange == 1 && isleGroup.getBackOrFloor().compareTo("back") == 0)
            return "straight";

        if (xChange == yChange && yChange == getNumberOfCellsInIsle())
            return "diagonal";

        return "area";
    }

    public boolean verifyCell(String s)
    {
        Coords coords = new Coords(s);

        try
        {
            return g.getRNode(coords.getX(), coords.getY()).getIsle().getIsleID().compareTo(isleID) == 0;
        }
        catch (NullPointerException e)
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
