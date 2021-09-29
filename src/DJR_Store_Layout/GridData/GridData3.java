/**
 * GridData3 class for project DJR_Store_Layout
 * Where all the data is stored and processed and manipulated
 * BACKEND
 * @author David Roberts
 */

package DJR_Store_Layout.GridData;

import DJR_Store_Layout.HelperClasses.Coords;
import DJR_Store_Layout.HelperClasses.InfoToMakeAisleFromFile;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GridData3 {

    /** number of cells */
    private int size;
    /** grid dimensions */
    private int colSize, rowSize;
    /** limit values used for adding correct amount of cells in rows/cols */
    private int limit;
    private double limitOfPluses;
    /** cell dimensions */
    private double boxSize;
    private int cellSize;
    /** screen dimensions */
    private double screenX, screenY;
    /** grids of cells and of pluses */
    private RNode[][] grid;
    private final PNode[][] plusGrid;
    /** plus dimensions */
    private int sizeOfPluses;
    /** list of Aisle Groups */
    private Hashtable<String, AisleGroup> aisleGroupList;
    /** LinkedLists of highlighted cells in grid */
    private CellList highlightedList, highlightedNullList;
    /** Hashtable of null cells */
    private Hashtable<String, RNode> nullList;
    /** Length of highlighted area on x and y axis */
    private int highlightingXLength, highlightingYLength;
    /** List of cells for moving an Aisle */
    private CellList toMoveList;
    /** Cell coordinate corresponding to mouse on screen */
    private int xCoordOfMouseOnGrid, yCoordOfMouseOnGrid;
    /** Nodes of start/end points of picking: useful for determining quickest path*/
    private RNode regOpuStartEndNode, groOpuStartEndNode, standardStartEndNode;

    /**
     * Basic constructor
     * @param cols columns in grid
     * @param rows rows in grid
     * @param bS boxSize
     * @param x screen x
     * @param y screen y
     * @param cS cellSize
     */
    public GridData3(int cols, int rows, double bS, double x, double y, int cS)
    {
        grid = new RNode[cols][rows];
        size = 0;
        colSize = cols;
        rowSize = rows;
        limit = 0;
        boxSize = bS;
        plusGrid = new PNode[cols-1][rows-1];
        sizeOfPluses = 0;
        limitOfPluses = 0;
        aisleGroupList = new Hashtable<>();
        highlightedList = new CellList();
        screenX = x;
        screenY = y;
        cellSize = cS;
        nullList = new Hashtable<>();
        toMoveList = new CellList();
        highlightingXLength = 1;
        highlightingYLength = 1;
        highlightedNullList = new CellList();
    }

    /**
     * Constructor to load data from cell, no UI involved
     * @param fileName name of file
     */
    public GridData3(String fileName)
    {
        File file = new File(fileName);

        int cols, rows;
        plusGrid = null;
        try
        {
            Scanner scanner = new Scanner(file);

            cols = scanner.nextInt();
            rows = scanner.nextInt();
            float dontNeedWasCellSize = (int) scanner.nextFloat();
            String idk = scanner.nextLine();

            grid = new RNode[cols][rows];
            size = 0;
            colSize = cols;
            rowSize = rows;
            aisleGroupList = new Hashtable<>();
            nullList = new Hashtable<>();
            highlightedList = new CellList();

            for (int i=0; i<cols; i++)
            {
                for (int j=0; j<rows; j++)
                {
                    grid[i][j] = new RNode(new Rectangle(), i, j, -1, -1, this);
                }
            }

            while(scanner.hasNext())
            {
                String groupName = scanner.nextLine();
                if (groupName.compareTo("Nulls") != 0)
                {
                    String dontNeedWasColor = scanner.nextLine();
                    String backOrFloor = scanner.nextLine();

                    String nextLine = scanner.nextLine();
                    int numberOfAisles = Integer.parseInt(nextLine);
                    //System.out.println("numberOfAisles: "+numberOfAisles);
                    for (int i=0; i<numberOfAisles; i++)
                    {
                        String AisleID = scanner.nextLine();
                        //System.out.println("AisleID: "+AisleID);
                        String AisleInfo = scanner.nextLine();
                        InfoToMakeAisleFromFile aisleToMake = null;
                        if (AisleInfo.compareTo("Has Setup Info") == 0)
                        {
                            String s = scanner.nextLine();
                            int numberOfAisleSections = Integer.parseInt(s);
                            //System.out.println(numberOfAisleSections);
                            String subsectionsPerSection = scanner.nextLine();
                            //System.out.println(subsectionsPerSection);
                            String endCap = scanner.nextLine();
                            //System.out.println(endCap);
                            String direction = scanner.nextLine();
                            //System.out.println(direction);
                            aisleToMake = new InfoToMakeAisleFromFile(numberOfAisleSections, subsectionsPerSection, endCap, direction);
                        }

                        String cells = scanner.nextLine();
                        //System.out.println("cells: "+cells);
                        String[] cellCoords = cells.split(",");
                        for (int j=0; j<cellCoords.length; j=j+2)
                        {
                            Coords coords = new Coords(cellCoords[j]+","+cellCoords[j+1]);
                            grid[coords.getX()][coords.getY()].setHighlighted(true);
                        }

                        if (aisleGroupExists(groupName))
                        {
                            makeAisle(AisleID, groupName, null, aisleGroupList.get(groupName), true, backOrFloor);
                            //System.out.println("Made Aisle: "+AisleID+" w/ addingToExisting: true");
                        }
                        else
                        {
                            makeAisle(AisleID, groupName, null, null, false, backOrFloor);
                            //System.out.println("Made Aisle: "+AisleID+" w/ addingToExisting: false");
                        }

                        if (aisleToMake != null)
                        {
                            String[] sections = aisleToMake.getNumberOfSubsectionsForEachSection().split(",");
                            Hashtable<Integer, Integer> table = new Hashtable<>();
                            Arrays.stream(sections).forEach(e -> table.put(Integer.parseInt(e.split("-")[0]), Integer.parseInt(e.split("-")[1])));

                            getAisle(AisleID, groupName).setupAisleInfo(aisleToMake.getNumberOfAisleSections(), table, aisleToMake.getEndCapLocation(),
                                    aisleToMake.getDirectionOfIncreasingAisleSections());
                        }
                    }
                }
                else
                {
                    String cellsToNull = scanner.nextLine();
                    try
                    {
                        int hmm = Integer.parseInt(cellsToNull.charAt(0)+"");
                        String[] cellCoords = cellsToNull.split(",");
                        for (int j=0; j<cellCoords.length; j=j+2)
                        {
                            Coords coords = new Coords(cellCoords[j]+","+cellCoords[j+1]);
                            setCelltoNull(coords.getX(), coords.getY(), false);
                        }
                    }
                    catch (NumberFormatException ignored) {}

                    String string1 = scanner.nextLine();
                    String[] regOpuStartEnd = string1.split(":");
                    try
                    {
                        Coords coords = new Coords(regOpuStartEnd[1]);
                        setRegOPUstartEndNode(grid[coords.getX()][coords.getY()], true);
                    }
                    catch (ArrayIndexOutOfBoundsException ignored) {}

                    String string2 = scanner.nextLine();
                    String[] groOpuStartEnd = string2.split(":");
                    try
                    {
                        Coords coords = new Coords(groOpuStartEnd[1]);
                        setGroOpuStartEndNode(grid[coords.getX()][coords.getY()], true);
                    }
                    catch (ArrayIndexOutOfBoundsException ignored) {}
                    String string3 = scanner.nextLine();
                    String[] regStartEnd = string3.split(":");
                    try
                    {
                        Coords coords = new Coords(regStartEnd[1]);
                        setStandardStartEndNode(grid[coords.getX()][coords.getY()], true);
                    }
                    catch (ArrayIndexOutOfBoundsException ignored) {}

                    break;
                }
            }
            scanner.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found");
        }
    }

    /** Print method */
    public void print()
    {
        for (int i=0; i<rowSize; i++)
        {
            StringBuilder sb = new StringBuilder();

            for (int j=0; j<colSize; j++)
            {
                sb.append(grid[j][i].getCoords());
                sb.append(" ");
            }
            System.out.println(sb);
        }
    }

    /**
     * Adds pluses to plusGrid
     * @param hLine horizontal line of new plus
     * @param vLine vertical line of new plus
     * @param x coord of new plus
     * @param y coord of new plus
     */
    public void addPlus(Line hLine, Line vLine, int x, int y)
    {
        //System.out.println("Adding new plus to: "+x1+","+y1);
        PNode node;

        if (sizeOfPluses == 0)
        {
            //System.out.println("Making a new grid boss.");
            node = new PNode(vLine, hLine);
            plusGrid[0][0] = node;
            sizeOfPluses = 1;
            limitOfPluses = 1;
        }
        else if (limitOfPluses == rowSize-1)
        {
            //System.out.println("Making a new col boss.");
            node = new PNode(vLine, hLine);
            plusGrid[x][y] = node;
            sizeOfPluses++;
            limitOfPluses = 1;
        }
        else
        {
            //System.out.println("Making a new row boss.");
            node = new PNode(vLine, hLine);
            plusGrid[x][y] = node;
            sizeOfPluses++;
            limitOfPluses++;
        }
    }

    /**
     * Adds new rectangle for grid
     * @param r rectangle to add
     * @param x coord of new rectangle
     * @param y coord of new rectangle
     * @param startX display x coord
     * @param startY display y coord
     * @return RNode in of created rectangle in grid
     */
    public RNode addRect(Rectangle r, int x, int y, double startX, double startY)
    {
        //System.out.println("Adding new r to: "+x+","+y);
        //System.out.println("New r at "+startX+","+startY);
        RNode node;

        if (size == 0)
        {
            //System.out.println("Making a new grid boss.");
            node = new RNode(r, 0, 0, startX, startY, this);
            grid[0][0] = node;
            size = 1;
            limit = 1;
        }
        else if (limit == rowSize)
        {
            //System.out.println("Making a new col boss.");
            node = new RNode(r, x, y, startX, startY, this);
            grid[x][y] = node;
            size++;
            limit = 1;
        }
        else
        {
            //System.out.println("Making a new row boss.");
            node = new RNode(r, x, y, startX, startY, this);
            grid[x][y] = node;
            size++;
            limit++;
        }
        return node;
    }

    /** Clears highlighted nodes in grid */
    public void resetHighlighted()
    {
        CellList.CellNode curr = highlightedList.getFirst();

        while(curr != null)
        {
            grid[curr.getrNode().getX()][curr.getrNode().getY()].setHighlighted(false);
            curr = curr.getNext();
        }

        highlightedList.clear();
    }

    /** Clears all highlighted and grouped nodes in grid, is factory reset */
    public void resetGrid()
    {
        CellList.CellNode curr = highlightedList.getFirst();

        for (int i=0; i<highlightedList.size(); i++)
        {
            grid[curr.getrNode().getX()][curr.getrNode().getY()].setHighlighted(false);
            curr = curr.getNext();
        }
        highlightedList.clear();

        for (int i=0; i<rowSize; i++)
        {
            for (int j=0; j<colSize; j++)
            {
                grid[j][i].setAisled(false, null, null, null);
            }
        }
        aisleGroupList.clear();
    }

    /**
     * Highlights a square are on the grid
     * @param xCoord of cell where highlighting started
     * @param yCoord of cell where highlighting started
     * @param a length in number of cells to highlight in the West direction
     * @param b length in number of cells to highlight in the North direction
     * @param c length in number of cells to highlight in the East directions
     * @param d length in number of cells to highlight in the South direction
     */
    public void highlight(int xCoord, int yCoord, double a, double b, double c, double d)
    {
        if (a>0 && b>0)
        {
            //System.out.println("Northwest");
            for (int i=0; i<a; i++)
            {
                for (int j=0; j<b; j++)
                {
                    try
                    {
                        if (!grid[xCoord-i][yCoord-j].isAisle() && !grid[xCoord-i][yCoord-j].isNulled() && !nodeIsPickPoint(xCoord-i, yCoord-j))
                            grid[xCoord-i][yCoord-j].setHighlighted(true);
                    }
                    catch (IndexOutOfBoundsException ignored) {}
                }
            }
            highlightingXLength = (int) Math.ceil(a);
            highlightingYLength = (int) Math.ceil(b);
        }
        else if (a>0 && d>0)
        {
            //System.out.println("Southwest");
            for (int i=0; i<a; i++)
            {
                for (int j=0; j<d; j++)
                {
                    try
                    {
                        if (!grid[xCoord-i][yCoord+j].isAisle() && !grid[xCoord-i][yCoord+j].isNulled() && !nodeIsPickPoint(xCoord-i, yCoord+j))
                            grid[xCoord-i][yCoord+j].setHighlighted(true);
                    }
                    catch (IndexOutOfBoundsException ignored) {}
                }
            }
            highlightingXLength = (int) Math.ceil(a);
            highlightingYLength = (int) Math.ceil(d);
        }
        else if (c>0 && b>0)
        {
            //System.out.println("Northeast");
            for (int i=0; i<c; i++)
            {
                for (int j=0; j<b; j++)
                {
                    try
                    {
                        if (!grid[xCoord+i][yCoord-j].isAisle() && !grid[xCoord+i][yCoord-j].isNulled() && !nodeIsPickPoint(xCoord+i, yCoord-j))
                            grid[xCoord+i][yCoord-j].setHighlighted(true);
                    }
                    catch (IndexOutOfBoundsException ignored) {}
                }
            }
            highlightingXLength = (int) Math.ceil(c);
            highlightingYLength = (int) Math.ceil(b);
        }
        else if (c>0 && d>0)
        {
            //System.out.println("Southeast");
            for (int i=0; i<c; i++)
            {
                for (int j=0; j<d; j++)
                {
                    try
                    {
                        if (!grid[xCoord+i][yCoord+j].isAisle() && !grid[xCoord+i][yCoord+j].isNulled() && !nodeIsPickPoint(xCoord+i, yCoord+j))
                            grid[xCoord+i][yCoord+j].setHighlighted(true);
                    }
                    catch (IndexOutOfBoundsException ignored) {}
                }
            }
            highlightingXLength = (int) Math.ceil(c);
            highlightingYLength = (int) Math.ceil(d);
        }
    }

    /** Resets highlighting length distances to 1 */
    public void resetHighlighted2()
    {
        //System.out.println("Reset Highlight 2");
        highlightingXLength = 1;
        highlightingYLength = 1;
    }

    /**
     * @param which highlighted list or highlighted null list
     * @return southeastmost RNode
     */
    public RNode getSoutheastMostHighlightedCell(String which)
    {
        CellList.CellNode curr;
        if (which.equals("Normal"))
            curr = highlightedList.getFirst();
        else
            curr = highlightedNullList.getFirst();

        int biggestX = -1;
        int biggestY = -1;

        try
        {
            biggestX = curr.getrNode().getX();
            biggestY = curr.getrNode().getY();
        }
        catch (NullPointerException e)
        {
            return null;
        }

        while (curr != null)
        {
            if (curr.getrNode().getX() > biggestX || curr.getrNode().getY() > biggestY)
            {
                biggestX = curr.getrNode().getX();
                biggestY = curr.getrNode().getY();
            }
            curr = curr.getNext();
        }

        return grid[biggestX][biggestY];
    }

    /**
     * @param Aisle of interest
     * @return southeastmost RNode
     */
    public RNode getSoutheastMostAisleCell(Aisle Aisle)
    {
        CellList.CellNode curr = Aisle.getAisleCellList().getFirst();

        int biggestX = curr.getrNode().getX();
        int biggestY = curr.getrNode().getY();

        while (curr != null)
        {
            if (curr.getrNode().getX() > biggestX || curr.getrNode().getY() > biggestY)
            {
                biggestX = curr.getrNode().getX();
                biggestY = curr.getrNode().getY();
            }
            curr = curr.getNext();
        }

        return grid[biggestX][biggestY];
    }

    /**
     * Adjusts size of cells and pluses for a window resize
     * @param z new size of cells/pluses
     * @param xR1 remaining horizontal distance of panes
     * @param yR1 remianing vertical distance of panes
     */
    public void adjust(double z, double xR1, double yR1)
    {
        //System.out.println("z: "+z);
        boxSize = z;

        for (int i=0; i<rowSize-1; i++)
        {
            for (int j=0; j<colSize-1; j++)
            {
                plusGrid[j][i].vLine.setStartX((int) -(z/3));
                plusGrid[j][i].vLine.setStartY(0);
                plusGrid[j][i].vLine.setEndX((int) (z/3));
                plusGrid[j][i].vLine.setEndY(0);
                plusGrid[j][i].hLine.setStartY((int) -(z/3));
                plusGrid[j][i].hLine.setStartX(0);
                plusGrid[j][i].hLine.setEndY((int) (z/3));
                plusGrid[j][i].hLine.setEndX(0);
            }
        }

        double startX = xR1;
        double startY = yR1+25;

        for (int i=0; i<rowSize; i++)
        {
            if (i>0)
            {
                startY = startY + z;
                startX = xR1;
            }

            //System.out.println("StartY: " + startY);

            for (int j=0; j<colSize; j++)
            {
               // System.out.println("StartX: " + startX);

                grid[j][i].getR().setWidth(z-1);
                grid[j][i].getR().setHeight(z-1);
                grid[j][i].setsXMinCoord(startX);
                grid[j][i].setsXMaxCoord(startX + z - 1);
                grid[j][i].setsYMinCoord(startY);
                grid[j][i].setsYMaxCoord(startY + z - 1);

                startX = startX + z;
            }
        }
    }

    /**
     * Creates new Aisle given highlighted area
     * @param AisleID id of Aisle
     * @param igName Aisle group name
     * @param c color
     * @param aisleGroup Aisle group
     * @param addToAisleGroup true: adding to existing Aisle group, false: creating new Aisle group
     * @param backOrFloor if Aisle is in back or on the floor
     */
    public void makeAisle(String AisleID, String igName, Color c, AisleGroup aisleGroup, boolean addToAisleGroup, String backOrFloor)
    {
        if (!addToAisleGroup)
        {
            CellList AisleGroupCellList = new CellList();
            AisleGroup igNew = new AisleGroup(igName, c);

            CellList AisleCellList = new CellList();
            Aisle newAisle = new Aisle(AisleID, igNew, this);

            CellList.CellNode curr = highlightedList.getFirst();
            while (curr != null)
            {
                grid[curr.getrNode().getX()][curr.getrNode().getY()].setAisled(true, newAisle, c, igNew);
                AisleGroupCellList.add(grid[curr.getrNode().getX()][curr.getrNode().getY()]);
                AisleCellList.add(grid[curr.getrNode().getX()][curr.getrNode().getY()]);

                curr = curr.getNext();
            }

            igNew.setAisleGroupCellList(AisleGroupCellList);
            newAisle.setAisleCellList(AisleCellList);

            igNew.addNewID(AisleID, newAisle);
            igNew.setBackOrFloor(backOrFloor);

            aisleGroupList.put(igName, igNew);
            highlightedList.clear();
        }
        else
        {
            CellList AisleGroupCellList = aisleGroup.getAisleGroupCellList();

            CellList AisleCellList = new CellList();
            Aisle newAisle = new Aisle(AisleID, aisleGroup, this);

            CellList.CellNode curr = highlightedList.getFirst();
            while (curr != null)
            {
                grid[curr.getrNode().getX()][curr.getrNode().getY()].setAisled(true, newAisle, c, aisleGroup);
                AisleGroupCellList.add(grid[curr.getrNode().getX()][curr.getrNode().getY()]);
                AisleCellList.add(grid[curr.getrNode().getX()][curr.getrNode().getY()]);

                curr = curr.getNext();
            }

            newAisle.setAisleCellList(AisleCellList);

            aisleGroup.addNewID(AisleID, newAisle);

            highlightedList.clear();
        }
    }

    /**
     * Adds new highlighted area to existing Aisle
     * @param AisleID id of Aisle
     * @param c color
     * @param aisleGroup Aisle group
     */
    public void addNewToExistingAisle(String AisleID, Color c, AisleGroup aisleGroup)
    {
        CellList AisleGroupCellList = aisleGroup.getAisleGroupCellList();
        CellList AisleCellList = aisleGroup.getAisleIDList().get(AisleID).getAisleCellList();

        CellList.CellNode curr = highlightedList.getFirst();
        while (curr != null)
        {
            grid[curr.getrNode().getX()][curr.getrNode().getY()].setAisled(true, aisleGroup.getAisleIDList().get(AisleID), c, aisleGroup);
            AisleGroupCellList.add(grid[curr.getrNode().getX()][curr.getrNode().getY()]);
            AisleCellList.add(grid[curr.getrNode().getX()][curr.getrNode().getY()]);

            curr = curr.getNext();
        }

        highlightedList.clear();
    }

    /**
     * Deletes Aisle from gird
     * @param i Aisle
     */
    public void removeAisle(Aisle i)
    {
        CellList.CellNode curr = i.getAisleCellList().getFirst();

        while (curr != null)
        {
            grid[curr.getrNode().getX()][curr.getrNode().getY()].setAisled(false, null, null, null);
            curr = curr.getNext();
        }

        i.getAisleGroup().getAisleIDList().remove(i.getAisleID());
        if (i.getAisleGroup().getAisleIDList().size() == 0)
        {
            aisleGroupList.remove(i.getAisleGroup().getName());
        }
    }

    /**
     * "Fill In" Option
     * Sets cell to background color and makes it unselectable
     * @param x coord of cell
     * @param y coord of cell
     */
    public void setCelltoNull(int x, int y, boolean careAboutPluses)
    {
        grid[x][y].setNulled(true);
        nullList.put(x+","+y, grid[x][y]);
        if (careAboutPluses)
        {
            for (int i=1; i>-1; i--)
            {
                for (int j=1; j>-1; j--)
                {
                    try
                    {
                        plusGrid[x-i][y-j].setNulled(true);
                    }
                    catch (ArrayIndexOutOfBoundsException ignored) {}
                    catch (NullPointerException e)
                    {
                        System.out.println("Error at plus coords: "+(x-i)+","+(y-j));
                    }
                }
            }
        }
    }

    /**
     * Moves Aisle keeping size and all info
     * @param node selected node
     * @param cellAisle Aisle of interest
     */
    public void moveAisle(RNode node, Aisle cellAisle)
    {
        int xDif = xCoordOfMouseOnGrid - node.getX();
        int yDif = yCoordOfMouseOnGrid - node.getY();

        //System.out.println("xDif: "+xDif);
        //System.out.println("yDif: "+yDif);

        CellList.CellNode curr2 = toMoveList.getFirst();
        while (curr2 != null)
        {
            grid[curr2.getrNode().getX()][curr2.getrNode().getY()].setAisleIsBeingMoved(false, null);
            curr2 = curr2.getNext();
        }
        toMoveList.clear();

        CellList.CellNode curr = cellAisle.getAisleCellList().getFirst();
        while (curr != null)
        {
            try
            {
                if (!grid[curr.getrNode().getX()+xDif][curr.getrNode().getY()+yDif].isAisle()
                        && !grid[curr.getrNode().getX()+xDif][curr.getrNode().getY()+yDif].isNulled())
                {
                    toMoveList.add(grid[curr.getrNode().getX()+xDif][curr.getrNode().getY()+yDif]);
                    grid[curr.getrNode().getX()+xDif][curr.getrNode().getY()+yDif].setAisleIsBeingMoved(true, cellAisle.getAisleGroup().getColor());
                }
                curr = curr.getNext();
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                curr = curr.getNext();
            }
        }
    }

    /**
     * Remakes Aisle after it was moved
     * @param AisleID id of Aisle
     * @param igName name of Aisle group
     * @param c color
     * @param aisleGroup Aisle group
     */
    public void makeAisleFromToMoveList(String AisleID, String igName, Color c, AisleGroup aisleGroup, Aisle Aisle)
    {
        CellList.CellNode curr = toMoveList.getFirst();

        while (curr != null)
        {
            grid[curr.getrNode().getX()][curr.getrNode().getY()].setHighlighted(true);
            curr = curr.getNext();
        }

        makeAisle(AisleID, igName, c, aisleGroup, true, null);
        aisleGroupList.get(igName).getAisleIDList().get(AisleID).setupAisleInfo(Aisle.getNumberOfAisleSections(), Aisle.getNumberOfSubsectionsForEachSection(), Aisle.getEndCapLocation(), Aisle.getDirectionOfIncreasingAisleSections());
        toMoveList.clear();
    }

    /**
     * Sets display of cell coordinates on screen
     * @param x coord
     * @param y coord
     */
    public void setMouseCoordsOnGrid(int x, int y)
    {
        //System.out.println("Set Mouse Coords on Grid: "+x+","+y);
        xCoordOfMouseOnGrid = x;
        yCoordOfMouseOnGrid = y;
    }

    /**
     * Determines if Aisle group exists in grid
     * @param s name of Aisle group
     * @return boolean true if found false if not
     */
    public boolean aisleGroupExists(String s) {return aisleGroupList.get(s) != null;}

    /**
     * Highlights area of cells that are null
     * Used for removing cells from null
     * @param xCoord of cell where highlighting started
     * @param yCoord of cell where highlighting started
     * @param a length in number of cells to highlight in the West direction
     * @param b length in number of cells to highlight in the North direction
     * @param c length in number of cells to highlight in the East directions
     * @param d length in number of cells to highlight in the South direction
     */
    public void highlightNulls(int xCoord, int yCoord, double a, double b, double c, double d)
    {
        if (a>0 && b>0)
        {
            //System.out.println("Northwest");
            for (int i=0; i<a; i++)
            {
                for (int j=0; j<b; j++)
                {
                    try
                    {
                        if (grid[xCoord-i][yCoord-j].isNulled())
                            grid[xCoord-i][yCoord-j].setHighlightedNull(true);
                    }
                    catch (IndexOutOfBoundsException ignored) {}
                }
            }
            highlightingXLength = (int) Math.ceil(a);
            highlightingYLength = (int) Math.ceil(b);
        }
        else if (a>0 && d>0)
        {
            //System.out.println("Southwest");
            for (int i=0; i<a; i++)
            {
                for (int j=0; j<d; j++)
                {
                    try
                    {
                        if (grid[xCoord-i][yCoord+j].isNulled())
                            grid[xCoord-i][yCoord+j].setHighlightedNull(true);
                    }
                    catch (IndexOutOfBoundsException ignored) {}
                }
            }
            highlightingXLength = (int) Math.ceil(a);
            highlightingYLength = (int) Math.ceil(d);
        }
        else if (c>0 && b>0)
        {
            //System.out.println("Northeast");
            for (int i=0; i<c; i++)
            {
                for (int j=0; j<b; j++)
                {
                    try
                    {
                        if (grid[xCoord+i][yCoord-j].isNulled())
                            grid[xCoord+i][yCoord-j].setHighlightedNull(true);
                    }
                    catch (IndexOutOfBoundsException ignored) {}
                }
            }
            highlightingXLength = (int) Math.ceil(c);
            highlightingYLength = (int) Math.ceil(b);
        }
        else if (c>0 && d>0)
        {
            //System.out.println("Southeast");
            for (int i=0; i<c; i++)
            {
                for (int j=0; j<d; j++)
                {
                    try
                    {
                        if (grid[xCoord+i][yCoord+j].isNulled())
                            grid[xCoord+i][yCoord+j].setHighlightedNull(true);
                    }
                    catch (IndexOutOfBoundsException ignored) {}
                }
            }
            highlightingXLength = (int) Math.ceil(c);
            highlightingYLength = (int) Math.ceil(d);
        }
    }

    /** Resets currently highlighted nulls */
    public void resetHighlightedNulls()
    {
        CellList.CellNode curr = highlightedNullList.getFirst();

        while(curr != null)
        {
            grid[curr.getrNode().getX()][curr.getrNode().getY()].setHighlightedNull(false);
            curr = curr.getNext();
        }

        highlightedNullList.clear();
    }

    /** Given highlighted area of nulls, removes them from null */
    public void removeNull()
    {
        CellList.CellNode curr = highlightedNullList.getFirst();

        while(curr != null)
        {
            removeCellfromNull(curr.getrNode().getX(), curr.getrNode().getY());
            curr = curr.getNext();
        }

        highlightedNullList.clear();
    }

    /**
     * Removes cell form null, returning it to interactable status
     * @param x cell x coord
     * @param y cell y coord
     */
    public void removeCellfromNull(int x, int y)
    {
        grid[x][y].setNulled(false);

        nullList.remove(x+","+y);

        try
        {
            if (!grid[x-1][y-1].isNulled() && !grid[x-1][y].isNulled() && !grid[x][y-1].isNulled())
                plusGrid[x-1][y-1].setNulled(false);
        }
        catch (ArrayIndexOutOfBoundsException ignored) {}
        try
        {
            if (!grid[x-1][y+1].isNulled() && !grid[x-1][y].isNulled() && !grid[x][y+1].isNulled())
                plusGrid[x-1][y].setNulled(false);
        }
        catch (ArrayIndexOutOfBoundsException ignored) {}
        try
        {
            if (!grid[x+1][y+1].isNulled() && !grid[x][y+1].isNulled() && !grid[x+1][y].isNulled())
                plusGrid[x][y].setNulled(false);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            try
            {
                plusGrid[x][y].setNulled(true);
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
        }
        try
        {
            if (!grid[x+1][y-1].isNulled() && !grid[x][y-1].isNulled() && !grid[x+1][y].isNulled())
                plusGrid[x][y-1].setNulled(false);
        }
        catch (ArrayIndexOutOfBoundsException ignored) {}
    }

    /**
     * @param rNode of interest
     * @param hmm true or false
     */
    public void setRegOPUstartEndNode(RNode rNode, boolean hmm)
    {
        resetHighlighted();
        if (hmm)
        {
            regOpuStartEndNode = rNode;
            rNode.getR().setFill(Color.RED);
            rNode.getR().setStroke(Color.RED);
            rNode.getR().setOpacity(1.0);
        }
        else
        {
            regOpuStartEndNode = null;
            rNode.getR().setFill(Color.TRANSPARENT);
            rNode.getR().setStroke(Color.TRANSPARENT);
            rNode.getR().setOpacity(0.5);
        }
    }

    /**
     * @param rNode of interest
     * @param hmm true or false
     */
    public void setGroOpuStartEndNode(RNode rNode, boolean hmm)
    {
        resetHighlighted();
        if (hmm)
        {
            groOpuStartEndNode = rNode;
            rNode.getR().setFill(Color.RED);
            rNode.getR().setStroke(Color.RED);
            rNode.getR().setOpacity(1.0);
        }
        else
        {
            groOpuStartEndNode = null;
            rNode.getR().setFill(Color.TRANSPARENT);
            rNode.getR().setStroke(Color.TRANSPARENT);
            rNode.getR().setOpacity(0.5);
        }
    }

    /**
     * @param rNode of interest
     * @param hmm true or false
     */
    public void setStandardStartEndNode(RNode rNode, boolean hmm)
    {
        resetHighlighted();
        if (hmm)
        {
            standardStartEndNode = rNode;
            rNode.getR().setFill(Color.RED);
            rNode.getR().setStroke(Color.RED);
            rNode.getR().setOpacity(1.0);
        }
        else
        {
            standardStartEndNode = null;
            rNode.getR().setFill(Color.TRANSPARENT);
            rNode.getR().setStroke(Color.TRANSPARENT);
            rNode.getR().setOpacity(0.5);
        }
    }

    /**
     * Checks to see if coordinates in pick point
     * @param x coordinate
     * @param y coordinate
     * @return true or false
     */
    public boolean nodeIsPickPoint(int x, int y)
    {
        try
        {
            int opuX = regOpuStartEndNode.getX();
            int opuY = regOpuStartEndNode.getY();

            if (x == opuX && y == opuY)
                return true;
        }
        catch (NullPointerException e)
        {
            return false;
        }

        try
        {
            int opuX = groOpuStartEndNode.getX();
            int opuY = groOpuStartEndNode.getY();

            if (x == opuX && y == opuY)
                return true;
        }
        catch (NullPointerException e)
        {
            return false;
        }

        try
        {
            int regX = standardStartEndNode.getX();
            int regY = standardStartEndNode.getY();

            if (x == regX && y == regY)
                return true;
        }
        catch (NullPointerException e)
        {
            return false;
        }

        return false;
    }

    /**
     * @param x coordinate
     * @param y coordinate
     * @return cell given coordinates
     */
    public RNode getRNode(int x, int y) {return grid[x][y];}

    public Aisle getAisle(String id, String ig) {return aisleGroupList.get(ig).getAisleIDList().get(id);}

    /**
     * @param id AisleID of Aisle in question
     * @return Aisle that matches, null if no Aisle
     */
    public Aisle getAisleWithUnknownIG(String id)
    {
        for (String ig : aisleGroupList.keySet())
        {
            for (String i : aisleGroupList.get(ig).getAisleIDList().keySet())
            {
                if (i.compareTo(id) == 0)
                    return aisleGroupList.get(ig).getAisleIDList().get(i);
            }
        }
        return null;
    }

    /**
     * Find coordinates on grid that correlate to given location (ex: D23(1) 1-1-1)
     * @param location inputted
     * @return correlating coordinates
     */
    public String getCoordsGivenLocation(String location)
    {
        Aisle Aisle;
        try
        {
            int hmm = Integer.parseInt(location.charAt(0)+"");
            //System.out.println("Aisle in the back");
            String[] sArr = location.split(" ");
            if (aisleGroupExists(sArr[0]))
            {
                Aisle = getAisle(sArr[0]+sArr[1]+"", sArr[0]);
                //System.out.println("AisleID: "+Aisle.getAisleID());
                if (Aisle.hasSetupInfo())
                {
                    String subsection = sArr[2].charAt(sArr[2].length()-1)+"";
                    //System.out.println("AisleSubsection: "+subsection);

                    if (Aisle.inputingValidAisleLocationInBack(subsection))
                        return Aisle.getCoordsGivenLocationInBack(subsection);
                }
            }
        }
        catch (NumberFormatException e)
        {
            //System.out.println("Aisle on the floor");
            String[] loc1 = location.split("\\(");
            String AisleGroup = location.charAt(0)+"";
            //System.out.println("AisleID: "+loc1[0]);
            if (loc1.length > 1)
            {
                Aisle = getAisle(loc1[0], AisleGroup);

                if (Aisle.hasSetupInfo())
                {
                    String[] loc2 = loc1[1].split("\\) ");
                    int AisleSection = Integer.parseInt(loc2[0]);
                    //System.out.println("AisleSection: "+AisleSection);

                    String[] loc3 = loc2[1].split("-");
                    //System.out.println("AisleSubsection: "+loc3[0]);

                    if (Aisle.inputingValidAisleLocationOnFloor(AisleSection, loc3[0]))
                        return Aisle.getCoordsGivenLocationOnFloor(AisleSection, loc3[0]);
                }
                else
                    return Aisle.getAisleID();
            }
            else
                return location;
        }
        return "Getting location didn't work.";
    }

    public int getSize() {return size;}

    public int getColSize() {return colSize;}

    public int getRowSize() {return rowSize;}

    public double getBoxSize() {return boxSize;}

    public CellList getHighlightedList() {return highlightedList;}

    public CellList getHighlightedNullList() {return highlightedNullList;}

    public Hashtable<String, AisleGroup> getAisleGroupList() {return aisleGroupList;}

    public Hashtable<String, RNode> getNullList() {return nullList;}

    public int getHighlightingXLength() {return highlightingXLength;}

    public int getHighlightingYLength() {return highlightingYLength;}

    public CellList getToMoveList() {return toMoveList;}

    public RNode getRegOpuStartEndNode() {return regOpuStartEndNode;}

    public RNode getGroOpuStartEndNode() {return groOpuStartEndNode;}

    public RNode getStandardStartEndNode() {return standardStartEndNode;}

    public RNode[][] getGrid() {return grid;}

    public PNode[][] getPlusGrid() {return plusGrid;}
}