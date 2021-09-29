/**
 * Aisle class for retaining all information for an Aisle
 * This includes Aisle info pertaining to: Aisle sections and Aisle subsections
 * Also retrieves cell within Aisle corresponding to location string of an item
 * @author David Roberts
 */

package DJR_Store_Layout.GridData;

import DJR_Store_Layout.HelperClasses.Coords;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

public class Aisle
{
    private final GridData3 g;
    private final String AisleID;
    private final AisleGroup AisleGroup;
    private CellList AisleCellList;
    private int numberOfAisleSections;
    /** Input AAisle section, get number of subsections */
    private Hashtable<Integer, Integer> numberOfSubsectionsForEachSection;
    /** Given by north/south/east/west */
    private String endCapLocation;
    /** Given by up/down/left/right */
    private String directionOfIncreasingAisleSections;
    private String shape;

    /**
     * Basic Constructor
     * @param id id of Aisle
     * @param ig Aisle group
     * @param grid grid data class
     */
    public Aisle(String id, AisleGroup ig, GridData3 grid)
    {
        AisleID = id;
        AisleGroup = ig;
        g = grid;
    }

    public void setAisleCellList(CellList cl)
    {
        AisleCellList = cl;
        shape = findShape();
    }

    public String getAisleID() {return AisleID;}

    public AisleGroup getAisleGroup() {return AisleGroup;}

    public CellList getAisleCellList() {return AisleCellList;}

    /**
     * Setups Aisle info for sections and subsections
     * @param n number of Aisle sections
     * @param hashtable number of subsections for each section
     * @param endCap location
     * @param direction direction of increasing sections
     */
    public void setupAisleInfo(int n, Hashtable<Integer, Integer> hashtable, String endCap, String direction)
    {
        numberOfAisleSections = n;
        numberOfSubsectionsForEachSection = hashtable;
        endCapLocation = endCap;
        directionOfIncreasingAisleSections = direction;
    }

    /** Prints to debug Aisle info */
    public void printInfo()
    {
        System.out.println("AisleID: "+AisleID);
        System.out.println("AisleGroup: "+AisleGroup.getName());
        System.out.println("Back or FLoor: "+AisleGroup.getBackOrFloor());
        if (hasSetupInfo())
        {
            System.out.println("EndCap Location: "+endCapLocation);
            System.out.println("Direction: "+directionOfIncreasingAisleSections);
            System.out.println("Number Of Aisle Sections: "+numberOfAisleSections);
            numberOfSubsectionsForEachSection.keySet().forEach(i -> System.out.println("Aisle Section "+i+" has "+numberOfSubsectionsForEachSection.get(i)+" subsections"));
        }
        else
            System.out.println("Aisle Info has not been setup");
    }

    public int getNumberOfAisleSections() {return numberOfAisleSections;}

    /**
     * Finds cell within Aisle that corresponds to given item location in back
     * @param AisleSubsection Aisle subsection
     * @return String of coords for cell
     */
    public String getCoordsGivenLocationInBack(String AisleSubsection)
    {
        if (getNumberOfCellsInAisle() == 1)
        {
            System.out.println("Returning only cell in Aisle");
            return AisleCellList.getFirst().getrNode().getX()+","+AisleCellList.getFirst().getrNode().getY();
        }

        //"A" numeric value is 10 so if subsection is A then whatSubection = 1.
        //If subsection is B then whatSubsection = 2, and so on.
        int whatSubsection = Character.getNumericValue(AisleSubsection.charAt(0))-9;

        //Testing if back Aisle is split
        boolean split = false;
        String which = null;
        if (directionOfIncreasingAisleSections.compareTo("up") == 0 || directionOfIncreasingAisleSections.compareTo("down") == 0)
        {
            CellList.CellNode curr = AisleCellList.getFirst();
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
                curr = curr.getNext();
            }
        }
        else if (directionOfIncreasingAisleSections.compareTo("right") == 0 || directionOfIncreasingAisleSections.compareTo("left") == 0)
        {
            CellList.CellNode curr = AisleCellList.getFirst();
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
                curr = curr.getNext();
            }
        }

        if (split)
        {
            System.out.println("Returning from Section and Subsection Split");
            return getAisleCoordsGivenSectionAndSubsectionSplit(whatSubsection, which);
        }
        else
        {
            System.out.println("Returning from Section and Subsection");
            return getAisleCoordsGivenSectionAndSubsection(1, whatSubsection);
        }
    }

    /**
     * Finds cell within Aisle that corresponds to given item location on floor
     * @param AisleSection Aisle sections
     * @param AisleSubsection Aisle subsection
     * @return String of coords for cell
     */
    public String getCoordsGivenLocationOnFloor(int AisleSection, String AisleSubsection)
    {
        if (getNumberOfCellsInAisle() == 1)
        {
            System.out.println("Returning only cell in Aisle");
            return AisleCellList.getFirst().getrNode().getX()+","+AisleCellList.getFirst().getrNode().getY();
        }
        else if (AisleSection == 0)
        {
            System.out.println("Returning cuz at endcap");
            return getAisleCoordsGivenEndcap(endCapLocation);
        }
        else if (AisleSection > 50)
        {
            System.out.println("Returning cuz at opposite endcap");
            if (endCapLocation.compareTo("west") == 0)
            {
                return getAisleCoordsGivenEndcap("east");
            }
            if (endCapLocation.compareTo("south") == 0)
            {
                return getAisleCoordsGivenEndcap("north");
            }
            if (endCapLocation.compareTo("east") == 0)
            {
                return getAisleCoordsGivenEndcap("west");
            }
            if (endCapLocation.compareTo("north") == 0)
            {
                return getAisleCoordsGivenEndcap("south");
            }
        }
        else
        {
            System.out.println("Returning using Section and Subsection");
            return getAisleCoordsGivenSectionAndSubsection(AisleSection, Integer.parseInt(AisleSubsection));
        }

        return "getCoordsGivenLocationOnFloor didn't work";
    }

    /**
     * Finds cell corresponding to Aisle end cap
     * @param where location of endcap: north/east/south/west
     * @return String of coords for cell
     */
    private String getAisleCoordsGivenEndcap(String where)
    {
        //System.out.println("Endcap where: "+where);
        CellList.CellNode nodeToReturnCoords = null;

        if (where.compareTo("south") == 0)
        {
            int furthestYCoord = 0;

            CellList.CellNode curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getY() > furthestYCoord)
                {
                    furthestYCoord = curr.getrNode().getY();
                    nodeToReturnCoords = curr;
                }
                curr = curr.getNext();
            }
            if (cellIsPartOfAisle(nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY()))
                return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
            else
                throw new RuntimeException("getAisleCoordsGivenEndcap returned a cell not belonging to the Aisle using south");
        }
        if (where.compareTo("north") == 0)
        {
            int nearestYCoord = 10000000;

            CellList.CellNode curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getY() < nearestYCoord)
                {
                    nearestYCoord = curr.getrNode().getY();
                    nodeToReturnCoords = curr;
                }
                curr = curr.getNext();
            }
            if (cellIsPartOfAisle(nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY()))
                return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
            else
                throw new RuntimeException("getAisleCoordsGivenEndcap returned a cell not belonging to the Aisle using north");
        }
        if (where.compareTo("west") == 0)
        {
            int nearestXCoord = 10000000;

            CellList.CellNode curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getX() < nearestXCoord)
                {
                    nearestXCoord = curr.getrNode().getX();
                    nodeToReturnCoords = curr;
                }
                curr = curr.getNext();
            }
            if (cellIsPartOfAisle(nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY()))
                return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
            else
                throw new RuntimeException("getAisleCoordsGivenEndcap returned a cell not belonging to the Aisle using west");
        }
        if (where.compareTo("east") == 0)
        {
            int furthestXCoord = 0;

            CellList.CellNode curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getX() > furthestXCoord)
                {
                    furthestXCoord = curr.getrNode().getX();
                    nodeToReturnCoords = curr;
                }
                curr = curr.getNext();
            }
            if (cellIsPartOfAisle(nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY()))
                return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
            else
                throw new RuntimeException("getAisleCoordsGivenEndcap returned a cell not belonging to the Aisle using east");
        }
        return "Get Endcap Coord didnt work";
    }

    /** @return total cell count in Aisle */
    public int getNumberOfCellsInAisle()
    {
        CellList.CellNode curr = AisleCellList.getFirst();
        int numberOfCells = 0;
        while (curr != null)
        {
            numberOfCells++;
            curr = curr.getNext();
        }
        return numberOfCells;
    }

    /**
     * Finds cell within Aisle that corresponds to given item location
     * This takes into account total subsections of Aisle and scales accordingly
     * @param whatSection section
     * @param whatSubsection subsection
     * @return String of coordinates for cell
     */
    private String getAisleCoordsGivenSectionAndSubsection(int whatSection, int whatSubsection)
    {
        System.out.println("Direction: "+directionOfIncreasingAisleSections);

        if (directionOfIncreasingAisleSections.compareTo("right") == 0)
        {
            Coords coords = new Coords(getAisleCoordsGivenEndcap("west"));
            int x = coords.getX();
            int y = coords.getY();

            coords = new Coords(getAisleCoordsGivenEndcap("east"));
            int lastX = coords.getX();

            int cellsInLineButNotInAisle = 0;
            int tempX = x;
            while (tempX < lastX)
            {
                if (!cellIsPartOfAisle(tempX+","+y))
                    cellsInLineButNotInAisle++;
                tempX++;
            }
            System.out.println("Cells missing from line: "+cellsInLineButNotInAisle);

            int whichSubsection = getWhichSubsectionInAisle(whatSection, whatSubsection);
            System.out.println("whichSubsection: "+whichSubsection);

            int xToReturn = (int) (x+(getCellsToSubsection()*whichSubsection));
            if (endCapLocation.compareTo("west") != 0  && xToReturn != x)
                xToReturn--;

            if (cellsInLineButNotInAisle > 0)
                xToReturn+=cellsInLineButNotInAisle;

            if (cellIsPartOfAisle(xToReturn+","+y))
                return xToReturn+","+y;
            else
                System.out.println("Returned cell "+xToReturn+","+y+" is not in Aisle "+AisleID);
        }
        if (directionOfIncreasingAisleSections.compareTo("up") == 0)
        {
            Coords coords = new Coords(getAisleCoordsGivenEndcap("south"));
            int x = coords.getX();
            int y = coords.getY();

            coords = new Coords(getAisleCoordsGivenEndcap("north"));
            int lastY = coords.getY();

            int cellsInLineButNotInAisle = 0;
            int tempY = y;
            while (tempY > lastY)
            {
                if (!cellIsPartOfAisle(x+","+tempY))
                    cellsInLineButNotInAisle++;
                tempY--;
            }
            System.out.println("Cells missing from line: "+cellsInLineButNotInAisle);

            int whichSubsection = getWhichSubsectionInAisle(whatSection, whatSubsection);
            System.out.println("whichSubsection: "+whichSubsection);

            int yToReturn = (int) (y-(getCellsToSubsection()*whichSubsection));
            if (endCapLocation.compareTo("south") != 0 && yToReturn != y)
                yToReturn++;

            if (cellsInLineButNotInAisle > 0)
                yToReturn-=cellsInLineButNotInAisle;

            if (cellIsPartOfAisle(x+","+yToReturn))
                return x+","+yToReturn;
            else
                System.out.println("Returned cell "+x+","+yToReturn+" is not in Aisle "+AisleID);
        }
        if (directionOfIncreasingAisleSections.compareTo("left") == 0)
        {
            Coords coords = new Coords(getAisleCoordsGivenEndcap("east"));
            int x = coords.getX();
            int y = coords.getY();

            coords = new Coords(getAisleCoordsGivenEndcap("west"));
            int lastX = coords.getX();

            int cellsInLineButNotInAisle = 0;
            int tempX = x;
            while (tempX > lastX)
            {
                if (!cellIsPartOfAisle(tempX+","+y))
                    cellsInLineButNotInAisle++;
                tempX--;
            }
            System.out.println("Cells missing from line: "+cellsInLineButNotInAisle);

            int whichSubsection = getWhichSubsectionInAisle(whatSection, whatSubsection);
            System.out.println("whichSubsection: "+whichSubsection);

            int xToReturn = (int) (x-(getCellsToSubsection()*whichSubsection));
            if (endCapLocation.compareTo("east") != 0 && xToReturn != x)
                xToReturn++;

            if (cellsInLineButNotInAisle > 0)
                xToReturn-=cellsInLineButNotInAisle;

            if (cellIsPartOfAisle(xToReturn+","+y))
                return xToReturn+","+y;
            else
                System.out.println("Returned cell "+xToReturn+","+y+" is not in Aisle "+AisleID);
        }
        if (directionOfIncreasingAisleSections.compareTo("down") == 0)
        {
            Coords coords = new Coords(getAisleCoordsGivenEndcap("north"));
            int x = coords.getX();
            int y = coords.getY();
            
            coords = new Coords(getAisleCoordsGivenEndcap("south"));
            int lastY = coords.getY();

            int cellsInLineButNotInAisle = 0;
            int tempY = y;
            while (tempY < lastY)
            {
                if (!cellIsPartOfAisle(x+","+tempY))
                    cellsInLineButNotInAisle++;
                tempY++;
            }
            System.out.println("Cells missing from line: "+cellsInLineButNotInAisle);

            int whichSubsection = getWhichSubsectionInAisle(whatSection, whatSubsection);
            System.out.println("whichSubsection: "+whichSubsection);

            int yToReturn = (int) (y+(getCellsToSubsection()*whichSubsection));
            if (endCapLocation.compareTo("north") != 0 && yToReturn != y)
                yToReturn--;

            if (cellsInLineButNotInAisle > 0)
                yToReturn+=cellsInLineButNotInAisle;

            if (cellIsPartOfAisle(x+","+yToReturn))
                return x+","+yToReturn;
            else
                System.out.println("Returned cell "+x+","+yToReturn+" is not in Aisle "+AisleID);
        }

        return "Get Subsection Coord didnt work";
    }

    /**
     * For Aisles in back (01A) that are split and have two sides
     * @param whatSubsection section
     * @param which top or bottom or right or left side of Aisle
     * @return String of coordinates for cell
     */
    private String getAisleCoordsGivenSectionAndSubsectionSplit(int whatSubsection, String which)
    {
        System.out.println("Which Side of Split: "+which);

        if (directionOfIncreasingAisleSections.compareTo("right") == 0)
        {
            Coords coords = new Coords(getAisleCoordsGivenEndcapSplit("west", which));
            System.out.println("Starting: "+coords);
            int x = coords.getX();
            int y = coords.getY();

            int xToReturn = (x+((int) (getCellsToSubsection()/2)*whatSubsection));
            if (endCapLocation.compareTo("west") != 0)
                xToReturn--;

            return xToReturn+","+y;
        }
        if (directionOfIncreasingAisleSections.compareTo("up") == 0)
        {
            Coords coords = new Coords(getAisleCoordsGivenEndcapSplit("south", which));
            int x = coords.getX();
            int y = coords.getY();

            int yToReturn = (int) (y-((int) (getCellsToSubsection()/2)*whatSubsection));
            if (endCapLocation.compareTo("south") != 0)
                yToReturn++;

            return x+","+yToReturn;
        }
        if (directionOfIncreasingAisleSections.compareTo("left") == 0)
        {
            Coords coords = new Coords(getAisleCoordsGivenEndcapSplit("east", which));
            int x = coords.getX();
            int y = coords.getY();

            int xToReturn = (int) (x-((int) (getCellsToSubsection()/2)*whatSubsection));
            if (endCapLocation.compareTo("east") != 0)
                xToReturn++;

            return xToReturn+","+y;
        }
        if (directionOfIncreasingAisleSections.compareTo("down") == 0)
        {
            Coords coords = new Coords(getAisleCoordsGivenEndcapSplit("north", which));
            int x = coords.getX();
            int y = coords.getY();

            int yToReturn = (int) (y+((int) (getCellsToSubsection()/2)*whatSubsection));
            if (endCapLocation.compareTo("north") != 0)
                yToReturn--;

            return x+","+yToReturn;
        }

        return "Get Subsection Coord for Splits didnt work";
    }

    /**
     * For Aisles in back (01A) that are split and have two sides
     * @param where north/south/east/west
     * @param which top or bottom or right or left side of Aisle
     * @return String of coordinates for cell
     */
    private String getAisleCoordsGivenEndcapSplit(String where, String which)
    {
        CellList.CellNode nodeToReturnCoords = null;

        if (where.compareTo("south") == 0)
        {
            int xCoord1 = 0;
            int xCoord2 = 0;

            CellList.CellNode curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getX() != xCoord1)
                {
                    xCoord1 = curr.getrNode().getX();
                    break;
                }
                curr = curr.getNext();
            }
            curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getX() != xCoord2 && curr.getrNode().getX() != xCoord1)
                {
                    xCoord2 = curr.getrNode().getX();
                    break;
                }
                curr = curr.getNext();
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

            curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getY() > furthestYCoord)
                {
                    furthestYCoord = curr.getrNode().getY();
                    nodeToReturnCoords = curr;
                }
                curr = curr.getNext();
            }
            return theCorrectX+","+nodeToReturnCoords.getrNode().getY();
        }
        if (where.compareTo("north") == 0)
        {
            int xCoord1 = 0;
            int xCoord2 = 0;

            CellList.CellNode curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getX() != xCoord1)
                {
                    xCoord1 = curr.getrNode().getX();
                    break;
                }
                curr = curr.getNext();
            }
            curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getX() != xCoord2 && curr.getrNode().getX() != xCoord1)
                {
                    xCoord2 = curr.getrNode().getX();
                    break;
                }
                curr = curr.getNext();
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

            curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getY() < nearestYCoord)
                {
                    nearestYCoord = curr.getrNode().getY();
                    nodeToReturnCoords = curr;
                }
                curr = curr.getNext();
            }
            return theCorrectX+","+nodeToReturnCoords.getrNode().getY();
        }
        if (where.compareTo("west") == 0)
        {
            int yCoord1 = 0;
            int yCoord2 = 0;

            CellList.CellNode curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getY() != yCoord1)
                {
                    yCoord1 = curr.getrNode().getY();
                    break;
                }
                curr = curr.getNext();
            }
            curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getY() != yCoord2 && curr.getrNode().getY() != yCoord1)
                {
                    yCoord2 = curr.getrNode().getY();
                    break;
                }
                curr = curr.getNext();
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

            curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getX() < nearestXCoord)
                {
                    nearestXCoord = curr.getrNode().getX();
                    nodeToReturnCoords = curr;
                }
                curr = curr.getNext();
            }
            return nodeToReturnCoords.getrNode().getX()+","+theCorrectY;
        }
        if (where.compareTo("east") == 0)
        {
            int yCoord1 = 0;
            int yCoord2 = 0;

            CellList.CellNode curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getY() != yCoord1)
                {
                    yCoord1 = curr.getrNode().getY();
                    break;
                }
                curr = curr.getNext();
            }
            curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getY() != yCoord2 && curr.getrNode().getY() != yCoord1)
                {
                    yCoord2 = curr.getrNode().getY();
                    break;
                }
                curr = curr.getNext();
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

            curr = AisleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getX() > furthestXCoord)
                {
                    furthestXCoord = curr.getrNode().getX();
                    nodeToReturnCoords = curr;
                }
                curr = curr.getNext();
            }
            return nodeToReturnCoords.getrNode().getX()+","+theCorrectY;
        }
        return "Get Endcap Coord Split didnt work";
    }

    /** @return ratio of cells coorsponding to one Aisle subsection */
    public float getCellsToSubsection()
    {
        int totalSubsections = 0;
        for (int i : numberOfSubsectionsForEachSection.keySet())
        {
            if (i != 0)
            {
                totalSubsections+=numberOfSubsectionsForEachSection.get(i);
            }
        }

        return (float) (getNumberOfCellsInAisle()-1)/totalSubsections;
    }

    /**
     * Determines which subsection in sequential order of all subsections that the inputting section and subsection correspond to
     * @param whatSection section
     * @param whatSubsection subsection
     * @return which subsection in sequential order
     */
    public int getWhichSubsectionInAisle(int whatSection, int whatSubsection)
    {
        List<Integer> list = numberOfSubsectionsForEachSection.keySet().stream().sorted().collect(Collectors.toList());
        int whichSubsection = 0;
        for (int i=1; i<list.size(); i++)
        {
            if (whatSection != list.get(i))
                whichSubsection+=numberOfSubsectionsForEachSection.get(list.get(i));
            else
            {
                whichSubsection+=whatSubsection;
                break;
            }
        }
        return whichSubsection;
    }

    public String getEndCapLocation() {return endCapLocation;}

    public String getDirectionOfIncreasingAisleSections() {return directionOfIncreasingAisleSections;}

    public boolean hasSetupInfo() {return numberOfAisleSections > 0;}

    public Hashtable<Integer, Integer> getNumberOfSubsectionsForEachSection() {return numberOfSubsectionsForEachSection;}

    public String getShape() {return shape;}

    /**
     * For determining if given item location on floor is valid for Aisle
     * @param AisleSection section
     * @param AisleSubsection subsection
     * @return true if valid, false if invalid
     */
    public boolean inputingValidAisleLocationOnFloor(int AisleSection, String AisleSubsection)
    {
        try
        {
            int subsectionsInSection = numberOfSubsectionsForEachSection.get(AisleSection);

            return 0 <= Integer.parseInt(AisleSubsection) && Integer.parseInt(AisleSubsection) <= subsectionsInSection;
        }
        catch (NullPointerException e)
        {
            return false;
        }
    }

    /**
     * For determining if given item location in back is valid for Aisle
     * @param AisleSubsection letter of subsection
     * @return true if valid, false if invalid
     */
    public boolean inputingValidAisleLocationInBack(String AisleSubsection)
    {
        int whatSubsection = Character.getNumericValue(AisleSubsection.charAt(0))-9;

        return (whatSubsection <= numberOfSubsectionsForEachSection.get(1));
    }

    /**
     * To be honest, idk why this is here. The shape quality of an Aisle isn't used.
     * Finds shape of Aisle given cellList
     * @return straight/diagonal/area
     */
    private String findShape()
    {
        /*
        To return:
        "straight"
        "diagonal"
        "area" - Aisles that have little to no location differentiation and are not straight or diagonal.
        Ex: clothes sections where locations change often, island Aisles that float around the store and are small, produce (bananas) Aisles where there is no locations
         */
        ArrayList<Integer> xCoords = new ArrayList<>();
        ArrayList<Integer> yCoords = new ArrayList<>();

        CellList.CellNode curr = AisleCellList.getFirst();
        while (curr != null)
        {
            xCoords.add(curr.getrNode().getX());
            yCoords.add(curr.getrNode().getY());

            curr = curr.getNext();
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

        if (xChange == 1 && AisleGroup.getBackOrFloor().compareTo("back") == 0)
            return "straight";

        if (yChange == 1 && AisleGroup.getBackOrFloor().compareTo("back") == 0)
            return "straight";

        if (xChange == yChange && yChange == getNumberOfCellsInAisle())
            return "diagonal";

        return "area";
    }

    /** @return true if coordinate are part of Aisle, false if not */
    public boolean cellIsPartOfAisle(String s)
    {
        Coords coords = new Coords(s);

        try
        {
            return g.getRNode(coords.getX(), coords.getY()).getAisle().getAisleID().compareTo(AisleID) == 0;
        }
        catch (NullPointerException e)
        {
            return false;
        }
    }
}