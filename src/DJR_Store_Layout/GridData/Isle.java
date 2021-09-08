/**
 * Isle class for retaining all information for an isle
 * This includes isle info pertaining to: isle sections and isle subsections
 * Also retrieves cell within isle corresponding to location string of an item
 * @author David Roberts
 */

package DJR_Store_Layout.GridData;

import DJR_Store_Layout.HelperClasses.Coords;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

public class Isle
{
    private final GridData3 g;
    private final String isleID;
    private final IsleGroup isleGroup;
    private CellList isleCellList;
    private int numberOfIsleSections;
    /** Input isle section, get number of subsections */
    private Hashtable<Integer, Integer> numberOfSubsectionsForEachSection;
    /** Given by north/south/east/west */
    private String endCapLocation;
    /** Given by up/down/left/right */
    private String directionOfIncreasingIsleSections;
    private String shape;

    /**
     * Basic Constructor
     * @param id id of isle
     * @param ig isle group
     * @param grid grid data class
     */
    public Isle(String id, IsleGroup ig, GridData3 grid)
    {
        isleID = id;
        isleGroup = ig;
        g = grid;
    }

    public void setIsleCellList(CellList cl)
    {
        isleCellList = cl;
        shape = findShape();
    }

    public String getIsleID() {return isleID;}

    public IsleGroup getIsleGroup() {return isleGroup;}

    public CellList getIsleCellList() {return isleCellList;}

    /**
     * Setups isle info for sections and subsections
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

    /** Prints to debug isle info */
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
            numberOfSubsectionsForEachSection.keySet().forEach(i -> System.out.println("Isle Section "+i+" has "+numberOfSubsectionsForEachSection.get(i)+" subsections"));
        }
        else
            System.out.println("Isle Info has not been setup");
    }

    public int getNumberOfIsleSections() {return numberOfIsleSections;}

    /**
     * Finds cell within isle that corresponds to given item location in back
     * @param isleSubsection isle subsection
     * @return String of coords for cell
     */
    public String getCoordsGivenLocationInBack(String isleSubsection)
    {
        if (getNumberOfCellsInIsle() == 1)
        {
            System.out.println("Returning only cell in isle");
            return isleCellList.getFirst().getrNode().getX()+","+isleCellList.getFirst().getrNode().getY();
        }

        //"A" numeric value is 10 so if subsection is A then whatSubection = 1.
        //If subsection is B then whatSubsection = 2, and so on.
        int whatSubsection = Character.getNumericValue(isleSubsection.charAt(0))-9;

        //Testing if back isle is split
        boolean split = false;
        String which = null;
        if (directionOfIncreasingIsleSections.compareTo("up") == 0 || directionOfIncreasingIsleSections.compareTo("down") == 0)
        {
            CellList.CellNode curr = isleCellList.getFirst();
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
        else if (directionOfIncreasingIsleSections.compareTo("right") == 0 || directionOfIncreasingIsleSections.compareTo("left") == 0)
        {
            CellList.CellNode curr = isleCellList.getFirst();
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
     * @param isleSection isle sections
     * @param isleSubsection isle subsection
     * @return String of coords for cell
     */
    public String getCoordsGivenLocationOnFloor(int isleSection, String isleSubsection)
    {
        if (getNumberOfCellsInIsle() == 1)
        {
            System.out.println("Returning only cell in isle");
            return isleCellList.getFirst().getrNode().getX()+","+isleCellList.getFirst().getrNode().getY();
        }
        else if (isleSection == 0)
        {
            System.out.println("Returning cuz at endcap");
            return getIsleCoordsGivenEndcap(endCapLocation);
        }
        else if (isleSection > 50)
        {
            System.out.println("Returning cuz at opposite endcap");
            if (endCapLocation.compareTo("west") == 0)
            {
                return getIsleCoordsGivenEndcap("east");
            }
            if (endCapLocation.compareTo("south") == 0)
            {
                return getIsleCoordsGivenEndcap("north");
            }
            if (endCapLocation.compareTo("east") == 0)
            {
                return getIsleCoordsGivenEndcap("west");
            }
            if (endCapLocation.compareTo("north") == 0)
            {
                return getIsleCoordsGivenEndcap("south");
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
     * @param where location of endcap: north/east/south/west
     * @return String of coords for cell
     */
    private String getIsleCoordsGivenEndcap(String where)
    {
        //System.out.println("Endcap where: "+where);
        CellList.CellNode nodeToReturnCoords = null;

        if (where.compareTo("south") == 0)
        {
            int furthestYCoord = 0;

            CellList.CellNode curr = isleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getY() > furthestYCoord)
                {
                    furthestYCoord = curr.getrNode().getY();
                    nodeToReturnCoords = curr;
                }
                curr = curr.getNext();
            }
            if (cellIsPartOfIsle(nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY()))
                return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
            else
                throw new RuntimeException("getIsleCoordsGivenEndcap returned a cell not belonging to the isle using south");
        }
        if (where.compareTo("north") == 0)
        {
            int nearestYCoord = 10000000;

            CellList.CellNode curr = isleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getY() < nearestYCoord)
                {
                    nearestYCoord = curr.getrNode().getY();
                    nodeToReturnCoords = curr;
                }
                curr = curr.getNext();
            }
            if (cellIsPartOfIsle(nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY()))
                return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
            else
                throw new RuntimeException("getIsleCoordsGivenEndcap returned a cell not belonging to the isle using north");
        }
        if (where.compareTo("west") == 0)
        {
            int nearestXCoord = 10000000;

            CellList.CellNode curr = isleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getX() < nearestXCoord)
                {
                    nearestXCoord = curr.getrNode().getX();
                    nodeToReturnCoords = curr;
                }
                curr = curr.getNext();
            }
            if (cellIsPartOfIsle(nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY()))
                return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
            else
                throw new RuntimeException("getIsleCoordsGivenEndcap returned a cell not belonging to the isle using west");
        }
        if (where.compareTo("east") == 0)
        {
            int furthestXCoord = 0;

            CellList.CellNode curr = isleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getX() > furthestXCoord)
                {
                    furthestXCoord = curr.getrNode().getX();
                    nodeToReturnCoords = curr;
                }
                curr = curr.getNext();
            }
            if (cellIsPartOfIsle(nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY()))
                return nodeToReturnCoords.getrNode().getX()+","+nodeToReturnCoords.getrNode().getY();
            else
                throw new RuntimeException("getIsleCoordsGivenEndcap returned a cell not belonging to the isle using east");
        }
        return "Get Endcap Coord didnt work";
    }

    /** @return total cell count in isle */
    public int getNumberOfCellsInIsle()
    {
        CellList.CellNode curr = isleCellList.getFirst();
        int numberOfCells = 0;
        while (curr != null)
        {
            numberOfCells++;
            curr = curr.getNext();
        }
        return numberOfCells;
    }

    /**
     * Finds cell within isle that corresponds to given item location
     * This takes into account total subsections of isle and scales accordingly
     * @param whatSection section
     * @param whatSubsection subsection
     * @return String of coordinates for cell
     */
    private String getIsleCoordsGivenSectionAndSubsection(int whatSection, int whatSubsection)
    {
        System.out.println("Direction: "+directionOfIncreasingIsleSections);

        if (directionOfIncreasingIsleSections.compareTo("right") == 0)
        {
            Coords coords = new Coords(getIsleCoordsGivenEndcap("west"));
            int x = coords.getX();
            int y = coords.getY();

            coords = new Coords(getIsleCoordsGivenEndcap("east"));
            int lastX = coords.getX();

            int cellsInLineButNotInIsle = 0;
            int tempX = x;
            while (tempX < lastX)
            {
                if (!cellIsPartOfIsle(tempX+","+y))
                    cellsInLineButNotInIsle++;
                tempX++;
            }
            System.out.println("Cells missing from line: "+cellsInLineButNotInIsle);

            int whichSubsection = getWhichSubsectionInIsle(whatSection, whatSubsection);
            System.out.println("whichSubsection: "+whichSubsection);

            int xToReturn = (int) (x+(getCellsToSubsection()*whichSubsection));
            if (endCapLocation.compareTo("west") != 0  && xToReturn != x)
                xToReturn--;

            if (cellsInLineButNotInIsle > 0)
                xToReturn+=cellsInLineButNotInIsle;

            if (cellIsPartOfIsle(xToReturn+","+y))
                return xToReturn+","+y;
            else
                System.out.println("Returned cell "+xToReturn+","+y+" is not in isle "+isleID);
        }
        if (directionOfIncreasingIsleSections.compareTo("up") == 0)
        {
            Coords coords = new Coords(getIsleCoordsGivenEndcap("south"));
            int x = coords.getX();
            int y = coords.getY();

            coords = new Coords(getIsleCoordsGivenEndcap("north"));
            int lastY = coords.getY();

            int cellsInLineButNotInIsle = 0;
            int tempY = y;
            while (tempY > lastY)
            {
                if (!cellIsPartOfIsle(x+","+tempY))
                    cellsInLineButNotInIsle++;
                tempY--;
            }
            System.out.println("Cells missing from line: "+cellsInLineButNotInIsle);

            int whichSubsection = getWhichSubsectionInIsle(whatSection, whatSubsection);
            System.out.println("whichSubsection: "+whichSubsection);

            int yToReturn = (int) (y-(getCellsToSubsection()*whichSubsection));
            if (endCapLocation.compareTo("south") != 0 && yToReturn != y)
                yToReturn++;

            if (cellsInLineButNotInIsle > 0)
                yToReturn-=cellsInLineButNotInIsle;

            if (cellIsPartOfIsle(x+","+yToReturn))
                return x+","+yToReturn;
            else
                System.out.println("Returned cell "+x+","+yToReturn+" is not in isle "+isleID);
        }
        if (directionOfIncreasingIsleSections.compareTo("left") == 0)
        {
            Coords coords = new Coords(getIsleCoordsGivenEndcap("east"));
            int x = coords.getX();
            int y = coords.getY();

            coords = new Coords(getIsleCoordsGivenEndcap("west"));
            int lastX = coords.getX();

            int cellsInLineButNotInIsle = 0;
            int tempX = x;
            while (tempX > lastX)
            {
                if (!cellIsPartOfIsle(tempX+","+y))
                    cellsInLineButNotInIsle++;
                tempX--;
            }
            System.out.println("Cells missing from line: "+cellsInLineButNotInIsle);

            int whichSubsection = getWhichSubsectionInIsle(whatSection, whatSubsection);
            System.out.println("whichSubsection: "+whichSubsection);

            int xToReturn = (int) (x-(getCellsToSubsection()*whichSubsection));
            if (endCapLocation.compareTo("east") != 0 && xToReturn != x)
                xToReturn++;

            if (cellsInLineButNotInIsle > 0)
                xToReturn-=cellsInLineButNotInIsle;

            if (cellIsPartOfIsle(xToReturn+","+y))
                return xToReturn+","+y;
            else
                System.out.println("Returned cell "+xToReturn+","+y+" is not in isle "+isleID);
        }
        if (directionOfIncreasingIsleSections.compareTo("down") == 0)
        {
            Coords coords = new Coords(getIsleCoordsGivenEndcap("north"));
            int x = coords.getX();
            int y = coords.getY();
            
            coords = new Coords(getIsleCoordsGivenEndcap("south"));
            int lastY = coords.getY();

            int cellsInLineButNotInIsle = 0;
            int tempY = y;
            while (tempY < lastY)
            {
                if (!cellIsPartOfIsle(x+","+tempY))
                    cellsInLineButNotInIsle++;
                tempY++;
            }
            System.out.println("Cells missing from line: "+cellsInLineButNotInIsle);

            int whichSubsection = getWhichSubsectionInIsle(whatSection, whatSubsection);
            System.out.println("whichSubsection: "+whichSubsection);

            int yToReturn = (int) (y+(getCellsToSubsection()*whichSubsection));
            if (endCapLocation.compareTo("north") != 0 && yToReturn != y)
                yToReturn--;

            if (cellsInLineButNotInIsle > 0)
                yToReturn+=cellsInLineButNotInIsle;

            if (cellIsPartOfIsle(x+","+yToReturn))
                return x+","+yToReturn;
            else
                System.out.println("Returned cell "+x+","+yToReturn+" is not in isle "+isleID);
        }

        return "Get Subsection Coord didnt work";
    }

    /**
     * For isles in back (01A) that are split and have two sides
     * @param whatSubsection section
     * @param which top or bottom or right or left side of isle
     * @return String of coordinates for cell
     */
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

    /**
     * For isles in back (01A) that are split and have two sides
     * @param where north/south/east/west
     * @param which top or bottom or right or left side of isle
     * @return String of coordinates for cell
     */
    private String getIsleCoordsGivenEndcapSplit(String where, String which)
    {
        CellList.CellNode nodeToReturnCoords = null;

        if (where.compareTo("south") == 0)
        {
            int xCoord1 = 0;
            int xCoord2 = 0;

            CellList.CellNode curr = isleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getX() != xCoord1)
                {
                    xCoord1 = curr.getrNode().getX();
                    break;
                }
                curr = curr.getNext();
            }
            curr = isleCellList.getFirst();
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

            curr = isleCellList.getFirst();
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

            CellList.CellNode curr = isleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getX() != xCoord1)
                {
                    xCoord1 = curr.getrNode().getX();
                    break;
                }
                curr = curr.getNext();
            }
            curr = isleCellList.getFirst();
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

            curr = isleCellList.getFirst();
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

            CellList.CellNode curr = isleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getY() != yCoord1)
                {
                    yCoord1 = curr.getrNode().getY();
                    break;
                }
                curr = curr.getNext();
            }
            curr = isleCellList.getFirst();
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

            curr = isleCellList.getFirst();
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

            CellList.CellNode curr = isleCellList.getFirst();
            while (curr != null)
            {
                if (curr.getrNode().getY() != yCoord1)
                {
                    yCoord1 = curr.getrNode().getY();
                    break;
                }
                curr = curr.getNext();
            }
            curr = isleCellList.getFirst();
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

            curr = isleCellList.getFirst();
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

    /** @return ratio of cells coorsponding to one isle subsection */
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

        return (float) (getNumberOfCellsInIsle()-1)/totalSubsections;
    }

    /**
     * Determines which subsection in sequential order of all subsections that the inputting section and subsection correspond to
     * @param whatSection section
     * @param whatSubsection subsection
     * @return which subsection in sequential order
     */
    public int getWhichSubsectionInIsle(int whatSection, int whatSubsection)
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

    public String getDirectionOfIncreasingIsleSections() {return directionOfIncreasingIsleSections;}

    public boolean hasSetupInfo() {return numberOfIsleSections > 0;}

    public Hashtable<Integer, Integer> getNumberOfSubsectionsForEachSection() {return numberOfSubsectionsForEachSection;}

    public String getShape() {return shape;}

    /**
     * For determining if given item location on floor is valid for isle
     * @param isleSection section
     * @param isleSubsection subsection
     * @return true if valid, false if invalid
     */
    public boolean inputingValidIsleLocationOnFloor(int isleSection, String isleSubsection)
    {
        try
        {
            int subsectionsInSection = numberOfSubsectionsForEachSection.get(isleSection);

            return 0 <= Integer.parseInt(isleSubsection) && Integer.parseInt(isleSubsection) <= subsectionsInSection;
        }
        catch (NullPointerException e)
        {
            return false;
        }
    }

    /**
     * For determining if given item location in back is valid for isle
     * @param isleSubsection letter of subsection
     * @return true if valid, false if invalid
     */
    public boolean inputingValidIsleLocationInBack(String isleSubsection)
    {
        int whatSubsection = Character.getNumericValue(isleSubsection.charAt(0))-9;

        return (whatSubsection <= numberOfSubsectionsForEachSection.get(1));
    }

    /**
     * To be honest, idk why this is here. The shape quality of an isle isn't used.
     * Finds shape of isle given cellList
     * @return straight/diagonal/area
     */
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

        CellList.CellNode curr = isleCellList.getFirst();
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

        if (xChange == 1 && isleGroup.getBackOrFloor().compareTo("back") == 0)
            return "straight";

        if (yChange == 1 && isleGroup.getBackOrFloor().compareTo("back") == 0)
            return "straight";

        if (xChange == yChange && yChange == getNumberOfCellsInIsle())
            return "diagonal";

        return "area";
    }

    /** @return true if coordinate are part of isle, false if not */
    public boolean cellIsPartOfIsle(String s)
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
}