/**
 * GridData3 class for project DJR_Store_Layout
 * Is where all the data is stored and processed and manipulated
 * BACKEND
 *
 * @author David Roberts
 */

package DJR_Store_Layout;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import javax.swing.*;
import java.util.Hashtable;
import java.util.Set;

public class GridData3 {

    /**
     * number of cells
     */
    protected int size;
    /**
     * grid dimensions
     */
    protected final int colSize, rowSize;
    /**
     * limit values used for adding correct amount of cells in rows/cols
     */
    private int limit;
    private double limitOfPluses;
    /**
     * cell dimensions
     */
    private double boxSize;
    private final int cellSize;
    /**
     * screen dimensions
     */
    private final double screenX, screenY;
    /**
     * grids of cells and of pluses
     */
    private final RNode[][] grid;
    private final PNode[][] plusGrid;
    /**
     * plus dimensions
     */
    private int sizeOfPluses;
    /**
     * list of Isle Groups
     */
    public Hashtable<String, IsleGroup> isleGroupList;
    /**
     * LinkedList of highlighted cells in grid
     */
    protected HighlightedList highlightedList, highlightedNullList;
    /**
     * Hastable of null cells
     */
    protected Hashtable<String, RNode> nullList;
    /**
     * Length of highlighted area on x and y axis
     */
    protected int highlightingXLength, highlightingYLength;
    /**
     * List of cells for moving an isle
     */
    protected IsleBeingMovedList toMoveList;
    protected boolean moving;
    /**
     * Cell coordinate corresponding to mouse on screen
     */
    protected int xCoordOfMouseOnGrid, yCoordOfMouseOnGrid;
    /**
     * Nodes of start/end points of picking: useful for determining quickest path
     */
    protected RNode regOpuStartEndNode, groOpuStartEndNode, regStartEndNode;

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
        highlightedList = new HighlightedList();
        screenX = x;
        screenY = y;
        cellSize = cS;
        nullList = new Hashtable<>();
        toMoveList = new IsleBeingMovedList();
        highlightingXLength = 1;
        highlightingYLength = 1;
        highlightedNullList = new HighlightedList();
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
                sb.append(grid[j][i].xCoord);
                sb.append(",");
                sb.append(grid[j][i].yCoord);
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
            node = new RNode(r, 0, 0, startX, startY);
            grid[0][0] = node;
            size = 1;
            limit = 1;
        }
        else if (limit == rowSize)
        {
            //System.out.println("Making a new col boss.");
            node = new RNode(r, x, y, startX, startY);
            grid[x][y] = node;
            size++;
            limit = 1;
        }
        else
        {
            //System.out.println("Making a new row boss.");
            node = new RNode(r, x, y, startX, startY);
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
        HighlightedList.HighlightedNode curr = highlightedList.first;

        while(curr != null)
        {
            grid[curr.rNode.xCoord][curr.rNode.yCoord].setHighlighted(false);
            curr = curr.next;
        }

        highlightedList.clear();
    }

    /**
     * Clears all highlighted and grouped nodes in grid
     * Is factory reset
     */
    public void resetGrid()
    {
        HighlightedList.HighlightedNode curr = highlightedList.first;

        for (int i=0; i<highlightedList.size; i++)
        {
            grid[curr.rNode.xCoord][curr.rNode.yCoord].setHighlighted(false);
            curr = curr.next;
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
        HighlightedList.HighlightedNode curr;
        if (which.equals("Normal"))
            curr = highlightedList.first;
        else
            curr = highlightedNullList.first;

        int biggestX = curr.rNode.xCoord;
        int biggestY = curr.rNode.yCoord;

        while (curr != null)
        {
            if (curr.rNode.xCoord > biggestX || curr.rNode.yCoord > biggestY)
            {
                biggestX = curr.rNode.xCoord;
                biggestY = curr.rNode.yCoord;
            }
            curr = curr.next;
        }

        return grid[biggestX][biggestY];
    }

    public RNode getSoutheastMostIsleCell(Isle isle)
    {
        Isle.IsleCellList.IsleCellNode curr = isle.getIsleCellList().getFirst();

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

                grid[j][i].r.setWidth(z-1);
                grid[j][i].r.setHeight(z-1);
                grid[j][i].sXMinCoord = startX;
                grid[j][i].sXMaxCoord = startX + z - 1;
                grid[j][i].sYMinCoord = startY;
                grid[j][i].sYMaxCoord = startY + z - 1;

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
            IsleGroupCellList isleGroupCellList = new IsleGroupCellList();
            IsleGroup igNew = new IsleGroup(igName, c);

            Isle.IsleCellList isleCellList = new Isle.IsleCellList();
            Isle newIsle = new Isle(isleID, igNew, this);

            HighlightedList.HighlightedNode curr = highlightedList.first;
            for (int i=0; i<highlightedList.size; i++)
            {
                grid[curr.rNode.xCoord][curr.rNode.yCoord].setIsled(true, newIsle, c, igNew);
                isleGroupCellList.add(grid[curr.rNode.xCoord][curr.rNode.yCoord]);
                isleCellList.add(grid[curr.rNode.xCoord][curr.rNode.yCoord]);

                curr = curr.next;
            }

            igNew.isleGroupCellList = isleGroupCellList;
            newIsle.setIsleCellList(isleCellList);

            igNew.addNewID(isleID, newIsle);
            igNew.setBackOrFloor(backOrFloor);

            isleGroupList.put(igName, igNew);
            highlightedList.clear();
        }
        else
        {
            IsleGroupCellList isleGroupCellList = isleGroup.getIsleGroupCellList();

            Isle.IsleCellList isleCellList = new Isle.IsleCellList();
            Isle newIsle = new Isle(isleID, isleGroup, this);

            HighlightedList.HighlightedNode curr = highlightedList.first;
            for (int i=0; i<highlightedList.size; i++)
            {
                grid[curr.rNode.xCoord][curr.rNode.yCoord].setIsled(true, newIsle, c, isleGroup);
                isleGroupCellList.add(grid[curr.rNode.xCoord][curr.rNode.yCoord]);
                isleCellList.add(grid[curr.rNode.xCoord][curr.rNode.yCoord]);

                curr = curr.next;
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
        IsleGroupCellList isleGroupCellList = isleGroup.getIsleGroupCellList();
        Isle.IsleCellList isleCellList = isleGroup.getIsleIDList().get(isleID).getIsleCellList();

        HighlightedList.HighlightedNode curr = highlightedList.first;
        for (int i=0; i<highlightedList.size; i++)
        {
            grid[curr.rNode.xCoord][curr.rNode.yCoord].setIsled(true, isleGroup.getIsleIDList().get(isleID), c, isleGroup);
            isleGroupCellList.add(grid[curr.rNode.xCoord][curr.rNode.yCoord]);
            isleCellList.add(grid[curr.rNode.xCoord][curr.rNode.yCoord]);

            curr = curr.next;
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
        Isle.IsleCellList.IsleCellNode curr = i.getIsleCellList().getFirst();

        while (curr != null)
        {
            grid[curr.getrNode().xCoord][curr.getrNode().yCoord].setIsled(false, null, null, null);
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
    public void setCelltoNull(int x, int y)
    {
        grid[x][y].setNulled(true);
        nullList.put(x+","+y, grid[x][y]);
        for (int i=1; i>-1; i--)
        {
            for (int j=1; j>-1; j--)
            {
                try
                {
                    plusGrid[x-i][y-j].setNulled(true);
                }
                catch (ArrayIndexOutOfBoundsException ignored) {}
            }
        }
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

        IsleBeingMovedList.IsleBeingMovedNode curr2 = toMoveList.first;
        while (curr2 != null)
        {
            grid[curr2.rNode.xCoord][curr2.rNode.yCoord].setIsleIsBeingMoved(false, null);
            curr2 = curr2.next;
        }
        toMoveList.clear();

        Isle.IsleCellList.IsleCellNode curr = cellIsle.getIsleCellList().getFirst();
        while (curr != null)
        {
            try
            {
                if (!grid[curr.getrNode().xCoord+xDif][curr.getrNode().yCoord+yDif].isIsle() && !grid[curr.getrNode().xCoord+xDif][curr.getrNode().yCoord+yDif].isNulled())
                {
                    toMoveList.add(grid[curr.getrNode().xCoord+xDif][curr.getrNode().yCoord+yDif]);
                    grid[curr.getrNode().xCoord+xDif][curr.getrNode().yCoord+yDif].setIsleIsBeingMoved(true, cellIsle.getIsleGroup().getColor());
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
        IsleBeingMovedList.IsleBeingMovedNode curr = toMoveList.first;

        while (curr != null)
        {
            grid[curr.rNode.xCoord][curr.rNode.yCoord].setHighlighted(true);
            curr = curr.next;
        }

        makeIsle(isleID, igName, c, isleGroup, true, null);
        isleGroupList.get(igName).getIsleIDList().get(isleID).setupIsleInfo(isle.getNumberOfIsleSections(), isle.getNumberOfSubsectionsForEachSection(), isle.getEndCapLocation(), isle.getDirectionOfIncreasingIsleSections());
        toMoveList.clear();

        moving = false;
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
            System.out.println("Group Name: "+isleGroupList.get(key).name+" Color: "+isleGroupList.get(key).color.toString());
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
        if (highlightedList.size == 0)
        {
            System.out.println("Nothing is highlighted");
        }
        else
        {
            HighlightedList.HighlightedNode curr = highlightedList.first;

            for (int i=0; i<highlightedList.size; i++)
            {
                System.out.println(curr.rNode.xCoord+","+curr.rNode.yCoord);
                curr = curr.next;
            }
        }
    }

    /**
     * Determines if isle group exists in grid
     *
     * @param s name of isle group
     * @return boolean true if found false if not
     */
    public boolean isleGroupExists(String s)
    {
        Set<String> groups = isleGroupList.keySet();

        for (String key : groups)
        {
            if (isleGroupList.get(key).getName().compareTo(s) == 0)
                return true;
        }
        return false;
    }

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
        HighlightedList.HighlightedNode curr = highlightedNullList.first;

        while(curr != null)
        {
            grid[curr.rNode.xCoord][curr.rNode.yCoord].setHighlightedNull(false);
            curr = curr.next;
        }

        highlightedNullList.clear();
    }

    /**
     * Given highlighted area of nulls, removes them from null
     */
    public void removeNull()
    {
        HighlightedList.HighlightedNode curr = highlightedNullList.first;

        while(curr != null)
        {
            removeCellfromNull(curr.rNode.xCoord, curr.rNode.yCoord);
            curr = curr.next;
        }

        highlightedNullList.clear();
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
    public RNode getRNode(int x, int y)
    {
        return grid[x][y];
    }

    public Isle getIsle(String id, String ig)
    {
        return isleGroupList.get(ig).getIsleIDList().get(id);
    }

    public Isle getIsleWithUnknownIG(String id)
    {
        Set<String> groups = isleGroupList.keySet();
        for (String ig : groups)
        {
            Set<String> isles = isleGroupList.get(ig).isleIDList.keySet();
            for (String i : isles)
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
            //System.out.println("IsleID: "+loc1[0]);
            if (loc1.length > 1)
            {
                isle = getIsle(loc1[0], location.charAt(0)+"");
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

    public int getHighlightingXLength()
    {
        return highlightingXLength;
    }

    public int getHighlightingYLength()
    {
        return highlightingYLength;
    }

    public IsleBeingMovedList getToMoveList()
    {
        return toMoveList;
    }

    public RNode getRegOpuStartEndNode()
    {
        return regOpuStartEndNode;
    }

    public RNode getGroOpuStartEndNode()
    {
        return groOpuStartEndNode;
    }

    public RNode getRegStartEndNode()
    {
        return regStartEndNode;
    }

    /**
     * Node class with rectangle and all necessary info
     */
    public class RNode
    {
        private final Rectangle r;
        private final int xCoord, yCoord;
        private final Coords coords;
        private double sXMinCoord, sYMinCoord, sXMaxCoord, sYMaxCoord;
        private boolean highlighted, isIsle, nulled, beingMoved, highlightedNull;
        private Isle isle;
        private IsleGroup isleGroup;
        private Color color;

        public RNode(Rectangle rect, int x, int y, double x1, double y1)
        {
            r = rect;
            xCoord = x;
            yCoord = y;
            sXMinCoord = x1;
            sXMaxCoord = x1+boxSize-1;
            sYMinCoord = y1;
            sYMaxCoord = y1+boxSize-1;
            coords = new Coords(x+","+y);
        }

        public boolean isHighlighted()
        {
            return highlighted;
        }

        public void setHighlighted(boolean hmm)
        {
            if (hmm)
            {
                r.setFill(Color.GRAY);
                highlighted = hmm;
                highlightedList.add(this);
            }
            else
            {
                r.setFill(Color.TRANSPARENT);
                highlighted = hmm;
            }
        }

        public boolean isIsle()
        {
            return isIsle;
        }

        public Color getColor()
        {
            return color;
        }

        public void setIsled(boolean hmm, Isle i, Color c, IsleGroup ig)
        {
            if (hmm)
            {
                color = c;
                isIsle = hmm;
                isle = i;
                isleGroup = ig;
                setHighlighted(false);
                r.setFill(c);
                r.setStroke(c);
                r.setOpacity(1.0);
            }
            else
            {
                color = null;
                isIsle = hmm;
                isle = null;
                isleGroup = null;
                setHighlighted(false);
                r.setFill(Color.TRANSPARENT);
                r.setStroke(Color.TRANSPARENT);
                r.setOpacity(0.5);
            }
        }

        public Rectangle getR() {
            return r;
        }

        public IsleGroup getIsleGroup()
        {
            return isleGroup;
        }

        public Isle getIsle()
        {
            return isle;
        }

        public int getX()
        {
            return xCoord;
        }

        public int getY()
        {
            return yCoord;
        }

        public Coords getCoords()
        {
            return coords;
        }

        private void setNulled(boolean hmm)
        {
            nulled = hmm;
            if (hmm)
            {
                r.setFill(Color.BLACK);
                r.setStroke(Color.BLACK);
                r.setOpacity(1.0);
            }
            else
            {
                r.setFill(Color.TRANSPARENT);
                r.setStroke(Color.TRANSPARENT);
                r.setOpacity(0.5);
            }
        }

        public boolean isNulled()
        {
            return nulled;
        }

        public void setIsleIsBeingMoved(boolean hmm, Color c)
        {
            if (hmm)
            {
                r.setFill(c);
                r.setStroke(Color.TRANSPARENT);
                r.setOpacity(0.5);
                beingMoved = hmm;
            }
            else
            {
                r.setFill(Color.TRANSPARENT);
                r.setStroke(Color.TRANSPARENT);
                r.setOpacity(0.5);
                beingMoved = hmm;
            }
        }

        public boolean isBeingMoved()
        {
            return beingMoved;
        }

        public double getsXMinCoord()
        {
            return sXMinCoord;
        }

        public double getsYMinCoord()
        {
            return sYMinCoord;
        }

        public double getsXMaxCoord()
        {
            return sXMaxCoord;
        }

        public double getsYMaxCoord()
        {
            return sYMaxCoord;
        }

        public void setHighlightedNull(boolean hmm)
        {
            highlightedNull = hmm;
            if (hmm)
            {
                r.setOpacity(0.5);
                highlightedNullList.add(this);
            }
            else
            {
                r.setOpacity(1.0);
            }
        }

        public boolean isHighlightedNull()
        {
            return highlightedNull;
        }
    }

    /**
     * Plus node class
     */
    private static class PNode
    {
        public Line hLine;
        public Line vLine;

        public PNode(Line x, Line y)
        {
            hLine = x;
            vLine = y;
        }

        public void setNulled(boolean hmm)
        {
            hLine.setVisible(!hmm);
            vLine.setVisible(!hmm);
        }
    }

    /**
     * Highlighted Node LinkedList class
     */
    protected class HighlightedList
    {
        protected HighlightedNode first;
        private HighlightedNode last;
        private int size;

        private HighlightedList()
        {
            first = null;
            last = null;
            size = 0;
        }

        /**
         * Adds new rectangle to LinkedList
         *
         * @param node new rectangle to add
         */
        private void add(RNode node)
        {
            if (size == 0)
            {
                first = new HighlightedNode(node);
                last = first;
                size = 1;
            }
            else if (size > 0)
            {
                last.next = new HighlightedNode(node);
                last = last.next;
                size++;
            }
        }

        /**
         * Resets LinkedList
         */
        protected void clear()
        {
            first = null;
            last = null;
            size = 0;
        }

        public int size()
        {
            return size;
        }

        /**
         * Highlighted Node class for each node in LinkedList
         */
        protected class HighlightedNode
        {
            protected final RNode rNode;
            protected HighlightedNode next;

            private HighlightedNode(RNode node)
            {
                rNode = node;
                next = null;
            }
        }
    }

    /**
     * Isle Group class with all necessary info
     */
    public static class IsleGroup
    {
        private final String name;
        private final Color color;
        private IsleGroupCellList isleGroupCellList;
        private final Hashtable<String, Isle> isleIDList;
        private String backOrFloor;

        public IsleGroup(String n, Color c)
        {
            name = n;
            color = c;
            isleIDList = new Hashtable<>();
        }

        public String getName()
        {
            return name;
        }

        public Color getColor()
        {
            return color;
        }

        public IsleGroupCellList getIsleGroupCellList()
        {
            return isleGroupCellList;
        }

        public void addNewID(String newID, Isle i)
        {
            isleIDList.put(newID, i);
        }

        public Hashtable<String, Isle> getIsleIDList()
        {
            return isleIDList;
        }

        public void setBackOrFloor(String s)
        {
            backOrFloor = s;
        }

        public String getBackOrFloor()
        {
            return backOrFloor;
        }

        public boolean containsIsle(String id)
        {
            Set<String> isleIDs = isleIDList.keySet();
            for (String s : isleIDs)
            {
                if (s.compareTo(id) == 0)
                    return true;
            }
            return false;
        }
    }

    /**
     * List of cells in Isle Group class
     */
    protected static class IsleGroupCellList
    {
        protected IsleGroupCellNode first;
        private IsleGroupCellNode last;
        private int size;

        protected IsleGroupCellList()
        {
            first = null;
            last = null;
            size = 0;
        }

        /**
         * Adds new rectangle to GroupedList
         *
         * @param node new rectangle to add
         */
        protected void add(RNode node)
        {
            if (size == 0)
            {
                first = new IsleGroupCellNode(node);
                last = first;
                size = 1;
            }
            else if (size > 0)
            {
                last.next = new IsleGroupCellNode(node);
                last = last.next;
                size++;
            }
        }

        /**
         * Resets LinkedList
         */
        private void clear()
        {
            first = null;
            last = null;
            size = 0;
        }

        /**
         * Grouped Node class for each node in LinkedList
         */
        protected static class IsleGroupCellNode
        {
            protected final RNode rNode;
            protected IsleGroupCellNode next;

            private IsleGroupCellNode(RNode node)
            {
                rNode = node;
                next = null;
            }
        }
    }

    /**
     * List of cells when moving an isle
     */
    protected static class IsleBeingMovedList
    {
        protected IsleBeingMovedNode first;
        protected IsleBeingMovedNode last;
        private int size;

        public IsleBeingMovedList()
        {
            first = null;
            last = null;
            size = 0;
        }

        public void add(RNode node)
        {
            if (size == 0)
            {
                first = new IsleBeingMovedNode(node);
                last = first;
                size = 1;
            }
            else if (size > 0)
            {
                last.next = new IsleBeingMovedNode(node);
                last = last.next;
                size++;
            }
        }

        public void clear()
        {
            first = null;
            last = null;
            size = 0;
        }

        protected class IsleBeingMovedNode
        {
            protected final RNode rNode;
            protected IsleBeingMovedNode next;

            private IsleBeingMovedNode(RNode node)
            {
                rNode = node;
                next = null;
            }
        }
    }
}