/**
 * GridData3 class for project DJR_Store_Layout
 * Is where all the data is stored and processed and manipulated
 * BACKEND
 *
 * @author David Roberts
 */

package DJR_Store_Layout.GridData;

import DJR_Store_Layout.HelperClasses.Coords;
import DJR_Store_Layout.HelperClasses.InfoToMakeIsleFromFile;
import com.sun.javafx.geom.Path2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class GridData3 {

    /**
     * number of cells
     */
    private int size;
    /**
     * grid dimensions
     */
    private int colSize, rowSize;
    /**
     * limit values used for adding correct amount of cells in rows/cols
     */
    private int limit;
    private double limitOfPluses;
    /**
     * cell dimensions
     */
    private double boxSize;
    private int cellSize;
    /**
     * screen dimensions
     */
    private double screenX, screenY;
    /**
     * grids of cells and of pluses
     */
    private RNode[][] grid;
    private final PNode[][] plusGrid;
    /**
     * plus dimensions
     */
    private int sizeOfPluses;
    /**
     * list of Isle Groups
     */
    private Hashtable<String, IsleGroup> isleGroupList;
    /**
     * LinkedList of highlighted cells in grid
     */
    private CellList highlightedList, highlightedNullList;
    /**
     * Hastable of null cells
     */
    private Hashtable<String, RNode> nullList;
    /**
     * Length of highlighted area on x and y axis
     */
    private int highlightingXLength, highlightingYLength;
    /**
     * List of cells for moving an isle
     */
    private CellList toMoveList;
    /**
     * Cell coordinate corresponding to mouse on screen
     */
    private int xCoordOfMouseOnGrid, yCoordOfMouseOnGrid;
    /**
     * Nodes of start/end points of picking: useful for determining quickest path
     */
    private RNode regOpuStartEndNode, groOpuStartEndNode, regStartEndNode;

    /**
     * Basic constructor
     *
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
        isleGroupList = new Hashtable<>();
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
            isleGroupList = new Hashtable<>();
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
                    int numberOfIsles = Integer.parseInt(nextLine);
                    //System.out.println("numberOfIsles: "+numberOfIsles);
                    for (int i=0; i<numberOfIsles; i++)
                    {
                        String isleID = scanner.nextLine();
                        //System.out.println("isleID: "+isleID);
                        String isleInfo = scanner.nextLine();
                        InfoToMakeIsleFromFile isleToMake = null;
                        if (isleInfo.compareTo("Has Setup Info") == 0)
                        {
                            String s = scanner.nextLine();
                            int numberOfIsleSections = Integer.parseInt(s);
                            //System.out.println(numberOfIsleSections);
                            String subsectionsPerSection = scanner.nextLine();
                            //System.out.println(subsectionsPerSection);
                            String endCap = scanner.nextLine();
                            //System.out.println(endCap);
                            String direction = scanner.nextLine();
                            //System.out.println(direction);
                            isleToMake = new InfoToMakeIsleFromFile(numberOfIsleSections, subsectionsPerSection, endCap, direction);
                        }

                        String cells = scanner.nextLine();
                        //System.out.println("cells: "+cells);
                        String[] cellCoords = cells.split(",");
                        for (int j=0; j<cellCoords.length; j=j+2)
                        {
                            Coords coords = new Coords(cellCoords[j]+","+cellCoords[j+1]);
                            grid[coords.getX()][coords.getY()].setHighlighted(true);
                        }

                        if (isleGroupExists(groupName))
                        {
                            makeIsle(isleID, groupName, null, isleGroupList.get(groupName), true, backOrFloor);
                            //System.out.println("Made isle: "+isleID+" w/ addingToExisting: true");
                        }
                        else
                        {
                            makeIsle(isleID, groupName, null, null, false, backOrFloor);
                            //System.out.println("Made isle: "+isleID+" w/ addingToExisting: false");
                        }

                        if (isleToMake != null)
                        {
                            String[] sections = isleToMake.getNumberOfSubsectionsForEachSection().split(",");
                            Hashtable<Integer, Integer> table = new Hashtable<>();
                            Arrays.stream(sections).forEach(e -> table.put(Integer.parseInt(e.split("-")[0]), Integer.parseInt(e.split("-")[1])));

                            getIsle(isleID, groupName).setupIsleInfo(isleToMake.getNumberOfIsleSections(), table, isleToMake.getEndCapLocation(),
                                    isleToMake.getDirectionOfIncreasingIsleSections());
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
                        setRegStartEndNode(grid[coords.getX()][coords.getY()], true);
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

    /**
     * Print method
     */
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
     *
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
     *
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

    /**
     * Clears highlighted nodes in grid
     */
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

    /**
     * Clears all highlighted and grouped nodes in grid
     * Is factory reset
     */
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
                grid[j][i].setIsled(false, null, null, null);
            }
        }
        isleGroupList.clear();
    }

    /**
     * Highlights a square are on the grid
     *
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
                        if (!grid[xCoord-i][yCoord-j].isIsle() && !grid[xCoord-i][yCoord-j].isNulled() && !nodeIsPickPoint(xCoord-i, yCoord-j))
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
                        if (!grid[xCoord-i][yCoord+j].isIsle() && !grid[xCoord-i][yCoord+j].isNulled() && !nodeIsPickPoint(xCoord-i, yCoord+j))
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
                        if (!grid[xCoord+i][yCoord-j].isIsle() && !grid[xCoord+i][yCoord-j].isNulled() && !nodeIsPickPoint(xCoord+i, yCoord-j))
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
                        if (!grid[xCoord+i][yCoord+j].isIsle() && !grid[xCoord+i][yCoord+j].isNulled() && !nodeIsPickPoint(xCoord+i, yCoord+j))
                            grid[xCoord+i][yCoord+j].setHighlighted(true);
                    }
                    catch (IndexOutOfBoundsException ignored) {}
                }
            }
            highlightingXLength = (int) Math.ceil(c);
            highlightingYLength = (int) Math.ceil(d);
        }
    }

    /**
     * Resets highlighting length distances to 1
     */
    public void resetHighlighted2()
    {
        //System.out.println("Reset Highlight 2");
        highlightingXLength = 1;
        highlightingYLength = 1;
    }

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

    public RNode getSoutheastMostIsleCell(Isle isle)
    {
        CellList.CellNode curr = isle.getIsleCellList().getFirst();

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
     *
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
     * Creates new isle given highlighted area
     *
     * @param isleID id of isle
     * @param igName isle group name
     * @param c color
     * @param isleGroup isle group
     * @param addToIsleGroup true: adding to existing isle group, false: creating new isle group
     * @param backOrFloor if isle is in back or on the floor
     */
    public void makeIsle(String isleID, String igName, Color c, IsleGroup isleGroup, boolean addToIsleGroup, String backOrFloor)
    {
        if (!addToIsleGroup)
        {
            CellList isleGroupCellList = new CellList();
            IsleGroup igNew = new IsleGroup(igName, c);

            CellList isleCellList = new CellList();
            Isle newIsle = new Isle(isleID, igNew, this);

            CellList.CellNode curr = highlightedList.getFirst();
            while (curr != null)
            {
                grid[curr.getrNode().getX()][curr.getrNode().getY()].setIsled(true, newIsle, c, igNew);
                isleGroupCellList.add(grid[curr.getrNode().getX()][curr.getrNode().getY()]);
                isleCellList.add(grid[curr.getrNode().getX()][curr.getrNode().getY()]);

                curr = curr.getNext();
            }

            igNew.setIsleGroupCellList(isleGroupCellList);
            newIsle.setIsleCellList(isleCellList);

            igNew.addNewID(isleID, newIsle);
            igNew.setBackOrFloor(backOrFloor);

            isleGroupList.put(igName, igNew);
            highlightedList.clear();
        }
        else
        {
            CellList isleGroupCellList = isleGroup.getIsleGroupCellList();

            CellList isleCellList = new CellList();
            Isle newIsle = new Isle(isleID, isleGroup, this);

            CellList.CellNode curr = highlightedList.getFirst();
            while (curr != null)
            {
                grid[curr.getrNode().getX()][curr.getrNode().getY()].setIsled(true, newIsle, c, isleGroup);
                isleGroupCellList.add(grid[curr.getrNode().getX()][curr.getrNode().getY()]);
                isleCellList.add(grid[curr.getrNode().getX()][curr.getrNode().getY()]);

                curr = curr.getNext();
            }

            newIsle.setIsleCellList(isleCellList);

            isleGroup.addNewID(isleID, newIsle);

            highlightedList.clear();
        }
    }

    /**
     * Adds new highlighted area to existing isle
     *
     * @param isleID id of isle
     * @param c color
     * @param isleGroup isle group
     */
    public void addNewToExistingIsle(String isleID, Color c, IsleGroup isleGroup)
    {
        CellList isleGroupCellList = isleGroup.getIsleGroupCellList();
        CellList isleCellList = isleGroup.getIsleIDList().get(isleID).getIsleCellList();

        CellList.CellNode curr = highlightedList.getFirst();
        while (curr != null)
        {
            grid[curr.getrNode().getX()][curr.getrNode().getY()].setIsled(true, isleGroup.getIsleIDList().get(isleID), c, isleGroup);
            isleGroupCellList.add(grid[curr.getrNode().getX()][curr.getrNode().getY()]);
            isleCellList.add(grid[curr.getrNode().getX()][curr.getrNode().getY()]);

            curr = curr.getNext();
        }

        highlightedList.clear();
    }

    /**
     * Deletes isle from gird
     *
     * @param i isle
     */
    public void removeIsle(Isle i)
    {
        CellList.CellNode curr = i.getIsleCellList().getFirst();

        while (curr != null)
        {
            grid[curr.getrNode().getX()][curr.getrNode().getY()].setIsled(false, null, null, null);
            curr = curr.getNext();
        }

        i.getIsleGroup().getIsleIDList().remove(i.getIsleID());
        if (i.getIsleGroup().getIsleIDList().size() == 0)
        {
            isleGroupList.remove(i.getIsleGroup().getName());
        }
    }

    /**
     * Sets cell to background color and makes it unselectable
     *
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
     * Moves isle keeping size and all info
     *
     * @param node selected node
     * @param cellIsle isle of interest
     */
    public void moveIsle(RNode node, Isle cellIsle)
    {
        int xDif = xCoordOfMouseOnGrid - node.getX();
        int yDif = yCoordOfMouseOnGrid - node.getY();

        //System.out.println("xDif: "+xDif);
        //System.out.println("yDif: "+yDif);

        CellList.CellNode curr2 = toMoveList.getFirst();
        while (curr2 != null)
        {
            grid[curr2.getrNode().getX()][curr2.getrNode().getY()].setIsleIsBeingMoved(false, null);
            curr2 = curr2.getNext();
        }
        toMoveList.clear();

        CellList.CellNode curr = cellIsle.getIsleCellList().getFirst();
        while (curr != null)
        {
            try
            {
                if (!grid[curr.getrNode().getX()+xDif][curr.getrNode().getY()+yDif].isIsle()
                        && !grid[curr.getrNode().getX()+xDif][curr.getrNode().getY()+yDif].isNulled())
                {
                    toMoveList.add(grid[curr.getrNode().getX()+xDif][curr.getrNode().getY()+yDif]);
                    grid[curr.getrNode().getX()+xDif][curr.getrNode().getY()+yDif].setIsleIsBeingMoved(true, cellIsle.getIsleGroup().getColor());
                }
                curr = curr.getNext();
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
        }
    }

    /**
     * Remakes isle after it was moved
     *
     * @param isleID id of isle
     * @param igName name of isle group
     * @param c color
     * @param isleGroup isle group
     */
    public void makeIsleFromToMoveList(String isleID, String igName, Color c, IsleGroup isleGroup, Isle isle)
    {
        CellList.CellNode curr = toMoveList.getFirst();

        while (curr != null)
        {
            grid[curr.getrNode().getX()][curr.getrNode().getY()].setHighlighted(true);
            curr = curr.getNext();
        }

        makeIsle(isleID, igName, c, isleGroup, true, null);
        isleGroupList.get(igName).getIsleIDList().get(isleID).setupIsleInfo(isle.getNumberOfIsleSections(), isle.getNumberOfSubsectionsForEachSection(), isle.getEndCapLocation(), isle.getDirectionOfIncreasingIsleSections());
        toMoveList.clear();
    }

    /**
     * Sets display of cell coordinates on screen
     *
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
     * Prints out groups and data for each group
     */
    public void printGroups()
    {
        if (isleGroupList.isEmpty())
        {
            System.out.println("There are no groups");
        }

        Set<String> groups = isleGroupList.keySet();
        for (String key : groups)
        {
            System.out.println("Group Name: "+isleGroupList.get(key).getName()+" Color: "+isleGroupList.get(key).getColor().toString());
            Set<String> isleIDs = isleGroupList.get(key).getIsleIDList().keySet();
            System.out.println("Isle ID's: ");
            for (String id : isleIDs)
            {
                System.out.print(id+", ");
            }
            System.out.print("\n");
        }
    }

    /**
     * Prints LinkedList of highlight nodes
     */
    public void printHighlighted()
    {
        if (highlightedList.size() == 0)
        {
            System.out.println("Nothing is highlighted");
        }
        else
        {
            CellList.CellNode curr = highlightedList.getFirst();

            for (int i=0; i<highlightedList.size(); i++)
            {
                System.out.println(curr.getrNode().getCoords());
                curr = curr.getNext();
            }
        }
    }

    /**
     * Determines if isle group exists in grid
     *
     * @param s name of isle group
     * @return boolean true if found false if not
     */
    public boolean isleGroupExists(String s) {return isleGroupList.get(s) != null;}

    /**
     * Highlights area of cells that are null
     * Used for removing cells from null
     *
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

    /**
     * Resets currently highlighted nulls
     */
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

    /**
     * Given highlighted area of nulls, removes them from null
     */
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
     *
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

    public void setRegStartEndNode(RNode rNode, boolean hmm)
    {
        resetHighlighted();
        if (hmm)
        {
            regStartEndNode = rNode;
            rNode.getR().setFill(Color.RED);
            rNode.getR().setStroke(Color.RED);
            rNode.getR().setOpacity(1.0);
        }
        else
        {
            regStartEndNode = null;
            rNode.getR().setFill(Color.TRANSPARENT);
            rNode.getR().setStroke(Color.TRANSPARENT);
            rNode.getR().setOpacity(0.5);
        }
    }

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
            int regX = regStartEndNode.getX();
            int regY = regStartEndNode.getY();

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
     * @param x coord
     * @param y coord
     * @return cell given coords
     */
    public RNode getRNode(int x, int y) {return grid[x][y];}

    public Isle getIsle(String id, String ig) {return isleGroupList.get(ig).getIsleIDList().get(id);}

    public Isle getIsleWithUnknownIG(String id)
    {
        for (String ig : isleGroupList.keySet())
        {
            for (String i : isleGroupList.get(ig).getIsleIDList().keySet())
            {
                if (i.compareTo(id) == 0)
                    return isleGroupList.get(ig).getIsleIDList().get(i);
            }
        }
        return null;
    }

    public String getCoordsGivenLocation(String location)
    {
        Isle isle;
        try
        {
            int hmm = Integer.parseInt(location.charAt(0)+"");
            //System.out.println("Isle in the back");
            String[] sArr = location.split(" ");
            if (isleGroupExists(sArr[0]))
            {
                isle = getIsle(sArr[0]+sArr[1]+"", sArr[0]);
                //System.out.println("IsleID: "+isle.getIsleID());
                if (isle.hasSetupInfo())
                {
                    String subsection = sArr[2].charAt(sArr[2].length()-1)+"";
                    //System.out.println("isleSubsection: "+subsection);

                    if (isle.inputingValidIsleLocationInBack(subsection))
                        return isle.getCoordsGivenLocationInBack(subsection);
                }
            }
        }
        catch (NumberFormatException e)
        {
            //System.out.println("Isle on the floor");
            String[] loc1 = location.split("\\(");
            String isleGroup = location.charAt(0)+"";
            //System.out.println("IsleID: "+loc1[0]);
            if (loc1.length > 1)
            {
                isle = getIsle(loc1[0], isleGroup);

                if (isle.hasSetupInfo())
                {
                    String[] loc2 = loc1[1].split("\\) ");
                    int isleSection = Integer.parseInt(loc2[0]);
                    //System.out.println("isleSection: "+isleSection);

                    String[] loc3 = loc2[1].split("-");
                    //System.out.println("isleSubsection: "+loc3[0]);

                    if (isle.inputingValidIsleLocationOnFloor(isleSection, loc3[0]))
                        return isle.getCoordsGivenLocationOnFloor(isleSection, loc3[0]);
                }
                else
                    return isle.getIsleID();
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

    public Hashtable<String, IsleGroup> getIsleGroupList() {return isleGroupList;}

    public Hashtable<String, RNode> getNullList() {return nullList;}

    public int getHighlightingXLength() {return highlightingXLength;}

    public int getHighlightingYLength() {return highlightingYLength;}

    public CellList getToMoveList() {return toMoveList;}

    public RNode getRegOpuStartEndNode() {return regOpuStartEndNode;}

    public RNode getGroOpuStartEndNode() {return groOpuStartEndNode;}

    public RNode getRegStartEndNode() {return regStartEndNode;}

    public RNode[][] getGrid() {return grid;}

    public PNode[][] getPlusGrid() {return plusGrid;}
}