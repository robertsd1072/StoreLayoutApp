/**
 * GridData3 class for project DJR_Store_Layout
 * Is where all the data is stored and processed and manipulated
 *
 * @author David Roberts
 */

package DJR_Store_Layout;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Set;

public class GridData3 {

    /**
     * number of cells
     */
    private int size;
    /**
     * grid dimensions
     */
    private final int colSize, rowSize;
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
     * list of groups
     */
    public Hashtable<String, Group> groupList;
    /**
     * LinkedList of highlighted cells in grid
     */
    private HighlightedList highlightedList;

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
        groupList = new Hashtable<>();
        highlightedList = new HighlightedList();
        screenX = x;
        screenY = y;
        cellSize = cS;
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
                grid[j][i].setGrouped(false, null, null, null);
            }
        }
        groupList.clear();
    }

    /**
     * Highlights section of cells in grid based on starting cell and current mouse(x,y)
     *
     * @param node rectangle where highlighting started
     * @param x mouse x coord
     * @param y mouse y coord
     */
    public void highlight(RNode node, double x, double y)
    {
        resetHighlighted();
        if (!node.isGrouped() && !node.isNulled())
        {
            node.setHighlighted(true);
        }

        int xCoord = node.xCoord;
        int yCoord = node.yCoord;
        double xMin = node.sXMinCoord;
        double yMin = node.sYMinCoord;
        double xMax = node.sXMaxCoord;
        double yMax = node.sYMaxCoord;

        double a = (int) (xMin-x)/boxSize+1;
        double b = (int) (yMin-y)/boxSize+1;
        double c = (int) (x-xMax)/boxSize+1;
        double d = (int) (y-yMax)/boxSize+1;

        for (int i=1; i<a; i++)
        {
            //West
            try
            {
                if (!grid[xCoord-i][yCoord].isGrouped() && !grid[xCoord-i][yCoord].isNulled())
                    grid[xCoord-i][yCoord].setHighlighted(true);
            }
            catch (IndexOutOfBoundsException e)
            {
                System.out.println("Tried to locate at invalid index");
            }
        }
        for (int i=1; i<b; i++)
        {
            //North
            try
            {
                if (!grid[xCoord][yCoord-i].isGrouped() && !grid[xCoord][yCoord-i].isNulled())
                    grid[xCoord][yCoord-i].setHighlighted(true);
                for (int j=1; j<a; j++)
                {
                    if (!grid[xCoord-j][yCoord-i].isGrouped() && !grid[xCoord-j][yCoord-i].isNulled())
                        grid[xCoord-j][yCoord-i].setHighlighted(true);
                }
                for (int k=1; k<c; k++)
                {
                    if (!grid[xCoord+k][yCoord-i].isGrouped() && !grid[xCoord+k][yCoord-i].isNulled())
                        grid[xCoord+k][yCoord-i].setHighlighted(true);
                }
            }
            catch (IndexOutOfBoundsException e)
            {
                System.out.println("Tried to locate at invalid index");
            }
        }
        for (int i=1; i<c; i++)
        {
            //East
            try
            {
                if (!grid[xCoord+i][yCoord].isGrouped() && !grid[xCoord+i][yCoord].isNulled())
                    grid[xCoord+i][yCoord].setHighlighted(true);
            }
            catch (IndexOutOfBoundsException e)
            {
                System.out.println("Tried to locate at invalid index");
            }
        }
        for (int i=1; i<d; i++)
        {
            //South
            try
            {
                if (!grid[xCoord][yCoord+i].isGrouped() && !grid[xCoord][yCoord+i].isNulled())
                    grid[xCoord][yCoord+i].setHighlighted(true);
                for (int j=1; j<a; j++)
                {
                    if (!grid[xCoord-j][yCoord+i].isGrouped() && !grid[xCoord-j][yCoord+i].isNulled())
                        grid[xCoord-j][yCoord+i].setHighlighted(true);
                }
                for (int k=1; k<c; k++)
                {
                    if (!grid[xCoord+k][yCoord+i].isGrouped() && !grid[xCoord+k][yCoord+i].isNulled())
                        grid[xCoord+k][yCoord+i].setHighlighted(true);
                }
            }
            catch (IndexOutOfBoundsException e)
            {
                System.out.println("Tried to locate at invalid index");
            }
        }
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
        System.out.println("z: "+z);
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
     * Groups highlighted nodes into a group
     * Doubles as changing group name/color
     *
     * @param name group name
     * @param c group color
     * @param editGroup boolean for whether or not to edit group
     * @param editGroupNode editing group node for getting relevant data
     */
    public void group(String name, Color c, boolean editGroup, RNode editGroupNode)
    {
        if (!editGroup)
        {
            GroupedList list = new GroupedList();
            Group g = new Group(name, c, cellSize);

            SortHighlightedReturn shr = sortHighlightedList(highlightedList);
            highlightedList = shr.hList;

            HighlightedList.HighlightedNode curr = highlightedList.first;
            for (int i=0; i<highlightedList.size; i++)
            {
                grid[curr.rNode.xCoord][curr.rNode.yCoord].setGrouped(true, name, c, g);
                list.add(grid[curr.rNode.xCoord][curr.rNode.yCoord]);

                curr = curr.next;
            }

            g.groupedList = list;

            g.maxWidth = shr.xRange+1;
            g.maxHeight = shr.yRange+1;
            groupList.put(name, g);
            highlightedList.clear();

            g.irregular = g.isIrregular();
        }
        else
        {
            Group g = editGroupNode.getGroup();

            Group gNew = new Group(name, c, cellSize);

            GroupedList.GroupedNode curr = g.groupedList.first;

            for (int i=0; i<g.groupedList.size; i++)
            {
                grid[curr.rNode.xCoord][curr.rNode.yCoord].setGrouped(true, name, c, gNew);
                curr = curr.next;
            }

            gNew.groupedList = g.groupedList;
            gNew.maxWidth = g.maxWidth;
            gNew.maxHeight = g.maxHeight;
            gNew.setIsleLayout(g.grid);
            gNew.setIsleLayoutBool(g.hasIsleLayout());
            groupList.put(gNew.name, gNew);
            groupList.remove(g.name, g);

            gNew.irregular = gNew.isIrregular();
        }
    }

    /**
     * Adds highlighted cells to already existing group
     *
     * @param name group name
     * @param c group color
     */
    public void addToGroup(String name, Color c)
    {
        Group g = groupList.get(name);

        HighlightedList.HighlightedNode curr = highlightedList.first;

        for (int i=0; i<highlightedList.size; i++)
        {
            grid[curr.rNode.xCoord][curr.rNode.yCoord].setGrouped(true, name, c, g);
            g.groupedList.add(grid[curr.rNode.xCoord][curr.rNode.yCoord]);

            curr = curr.next;
        }

        highlightedList.clear();

        if (g.hasIsleLayout())
        {
            System.out.println("Resetting isle layout");
            g.setIsleLayout(null);
            g.setIsleLayoutBool(false);
        }

        SortGroupReturn sgr = sortGroupedList(g.groupedList, g.name);

        g.groupedList = sgr.gList;
        g.maxWidth = sgr.xRange+1;
        g.maxHeight = sgr.yRange+1;

        g.irregular = g.isIrregular();
    }

    /**
     * Deletes group and resets group nodes
     *
     * @param name string of group name to delete
     */
    public void ungroup(String name)
    {
        Group g = groupList.get(name);

        GroupedList.GroupedNode curr = g.groupedList.first;

        for (int i=0; i<g.groupedList.size; i++)
        {
            grid[curr.rNode.xCoord][curr.rNode.yCoord].setGrouped(false, null, null, null);
            curr = curr.next;
        }

        g.groupedList.clear();
        groupList.remove(g.name);
    }

    /**
     * Sorts a HighlightedList into a new list going from left to right, lowest to highest
     * ex: 0,0 -> 0,1 -> 0,2 -> 1,0...
     *
     * @param list HighlightedList of interest
     * @return new SortHighlightedReturn class with necessary values
     */
    private SortHighlightedReturn sortHighlightedList(HighlightedList list)
    {
        ArrayList<Integer> listOfXCoords = new ArrayList<>();
        ArrayList<Integer> listOfYCoords = new ArrayList<>();

        HighlightedList.HighlightedNode curr = list.first;
        for (int i=0; i<list.size; i++)
        {
            if (!listOfXCoords.contains(curr.rNode.xCoord))
                listOfXCoords.add(curr.rNode.xCoord);

            if (!listOfYCoords.contains(curr.rNode.yCoord))
                listOfYCoords.add(curr.rNode.yCoord);

            curr = curr.next;
        }
        Collections.sort(listOfXCoords);
        Collections.sort(listOfYCoords);

        HighlightedList newList = new HighlightedList();
        int startX = listOfXCoords.get(0);
        int startY = listOfYCoords.get(0);
        for (int i=startX; i<startX+listOfXCoords.size(); i++)
        {
            for (int j=startY; j<startY+listOfYCoords.size(); j++)
            {
                if (!grid[i][j].isGrouped())
                    newList.add(grid[i][j]);
            }
        }

        return new SortHighlightedReturn(newList, listOfXCoords.get(listOfXCoords.size()-1)-startX, listOfYCoords.get(listOfYCoords.size()-1)-startY);
    }

    /**
     * Sorts a GroupedList into a new list going from left to right, lowest to highest
     * ex: 0,0 -> 0,1 -> 0,2 -> 1,0...
     *
     * @param list GroupedList of interest
     * @param groupName name of group of GroupedList
     * @return new SortGroupedReturn class with necessary values
     */
    private SortGroupReturn sortGroupedList(GroupedList list, String groupName)
    {
        ArrayList<Integer> listOfXCoords = new ArrayList<>();
        ArrayList<Integer> listOfYCoords = new ArrayList<>();

        GroupedList.GroupedNode curr = list.first;
        for (int i=0; i<list.size; i++)
        {
            if (!listOfXCoords.contains(curr.rNode.xCoord))
                listOfXCoords.add(curr.rNode.xCoord);

            if (!listOfYCoords.contains(curr.rNode.yCoord))
                listOfYCoords.add(curr.rNode.yCoord);

            curr = curr.next;
        }
        Collections.sort(listOfXCoords);
        Collections.sort(listOfYCoords);

        GroupedList newList = new GroupedList();
        int startX = listOfXCoords.get(0);
        int startY = listOfYCoords.get(0);
        for (int i=startX; i<startX+listOfXCoords.size(); i++)
        {
            for (int j=startY; j<startY+listOfYCoords.size(); j++)
            {
                if (grid[i][j].isGrouped() && grid[i][j].groupName.equals(groupName))
                    newList.add(grid[i][j]);
            }
        }

        return new SortGroupReturn(newList, listOfXCoords.get(listOfXCoords.size()-1)-startX, listOfYCoords.get(listOfYCoords.size()-1)-startY);
    }

    /**
     * Sets cell to background color and makes it unselectable
     * Used in irregular group isle layout setup
     *
     * @param x coord of cell
     * @param y coord of cell
     */
    public void setCelltoNull(int x, int y)
    {
        grid[x][y].setNulled();
        for (int i=1; i>-1; i--)
        {
            for (int j=1; j>-1; j--)
            {
                try
                {
                    plusGrid[x-i][y-j].setNulled();
                }
                catch (ArrayIndexOutOfBoundsException ignored) {}
            }
        }
    }

    /**
     * Prints out groups and data for each group
     */
    public void printGroups()
    {
        if (groupList.isEmpty())
        {
            System.out.println("There are no groups");
        }

        Set<String> groups = groupList.keySet();
        for (String key : groups)
        {
            System.out.println("Group Name: "+groupList.get(key).name+" Color: "+groupList.get(key).color.toString());
            System.out.println("Group MaxWidth: "+groupList.get(key).maxWidth+" Group MaxHeight: "+groupList.get(key).maxHeight);
            System.out.println("Group Irregularity: "+groupList.get(key).irregular);
            System.out.println("Group has Layout: "+groupList.get(key).hasIsleLayout());

            System.out.println("Grouped List:");

            GroupedList.GroupedNode curr = groupList.get(key).groupedList.first;

            for (int j=0; j<groupList.get(key).groupedList.size; j++)
            {
                System.out.println(curr.rNode.xCoord+","+curr.rNode.yCoord);
                curr = curr.next;
            }
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

    public double getBoxSize()
    {
        return boxSize;
    }

    public double getScreenX()
    {
        return screenX;
    }

    public double getScreenY()
    {
        return screenY;
    }

    public int getColSize()
    {
        return colSize;
    }

    public int getRowSize()
    {
        return rowSize;
    }

    public RNode getRNode(int x, int y)
    {
        return grid[x][y];
    }

    public int getCellSize()
    {
        return cellSize;
    }

    public int getNumOfGroups()
    {
        return groupList.size();
    }

    /**
     * Node class with rectangle and all necessary info
     */
    public class RNode
    {
        private final Rectangle r;
        private final int xCoord, yCoord;
        private double sXMinCoord, sYMinCoord, sXMaxCoord, sYMaxCoord;
        private boolean highlighted, grouped, nulled;
        private Group group;
        private Color color;
        private String groupName;

        public RNode(Rectangle rect, int x, int y, double x1, double y1)
        {
            r = rect;
            xCoord = x;
            yCoord = y;
            sXMinCoord = x1;
            sXMaxCoord = x1+boxSize-1;
            sYMinCoord = y1;
            sYMaxCoord = y1+boxSize-1;
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
                r.setStroke(Color.GRAY);
                r.setOpacity(1.0);
                highlighted = hmm;
                highlightedList.add(this);
            }
            else
            {
                r.setFill(Color.TRANSPARENT);
                r.setStroke(Color.GRAY);
                r.setOpacity(0.5);
                highlighted = hmm;
            }
        }

        public boolean isGrouped()
        {
            return grouped;
        }

        public Color getColor()
        {
            return color;
        }

        public String getGroupName()
        {
            return groupName;
        }

        public void setGrouped(boolean hmm, String n, Color c, Group g)
        {
            if (hmm)
            {
                color = c;
                groupName = n;
                grouped = hmm;
                group = g;
                setHighlighted(false);
                r.setFill(c);
                r.setStroke(c);
                r.setOpacity(1.0);
            }
            else
            {
                color = null;
                groupName = null;
                grouped = hmm;
                setHighlighted(false);
            }
        }

        public Rectangle getR() {
            return r;
        }

        public Group getGroup()
        {
            return group;
        }

        public int getX()
        {
            return xCoord;
        }

        public int getY()
        {
            return yCoord;
        }

        public void setNulled()
        {
            nulled = true;

            r.setFill(Color.RED);
            r.setStroke(Color.RED);
            r.setOpacity(1.0);
        }

        public boolean isNulled()
        {
            return nulled;
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

        public void setNulled()
        {
            hLine.setVisible(false);
            vLine.setVisible(false);
        }
    }

    /**
     * Highlighted Node LinkedList class
     */
    private class HighlightedList
    {
        private HighlightedNode first;
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
        private void clear()
        {
            first = null;
            last = null;
            size = 0;
        }

        /**
         * Highlighted Node class for each node in LinkedList
         */
        private class HighlightedNode
        {
            private final RNode rNode;
            private HighlightedNode next;

            private HighlightedNode(RNode node)
            {
                rNode = node;
                next = null;
            }
        }
    }

    /**
     * Group class with all necessary info
     */
    public class Group
    {
        private final String name;
        private final Color color;
        private GroupedList groupedList;
        private int maxWidth, maxHeight;
        private boolean irregular, isleLayoutBool;
        private ArrayList<RNode> cellsToRemoveIfIrregular;
        private final int cellSize;
        private GridData3 grid;
        private int lowestX, lowestY;

        public Group(String n, Color c, int cS)
        {
            name = n;
            color = c;
            cellSize = cS;
        }

        public String getName()
        {
            return name;
        }

        public Color getColor()
        {
            return color;
        }

        public int getMaxWidth()
        {
            return maxWidth;
        }

        public int getMaxHeight()
        {
            return maxHeight;
        }

        public void setIsleLayoutBool(boolean hmm)
        {
            isleLayoutBool = hmm;
        }

        public boolean hasIsleLayout()
        {
            return isleLayoutBool;
        }

        public void setIsleLayout(GridData3 g)
        {
            grid = g;
        }

        public GridData3 getGrid()
        {
            return grid;
        }

        public ArrayList<RNode> getCellsToRemoveIfIrregular()
        {
            return cellsToRemoveIfIrregular;
        }

        public int getCellSize()
        {
            return cellSize;
        }

        public GroupedList getGroupedList()
        {
            return groupedList;
        }

        public int getLowestX()
        {
            return lowestX;
        }

        public int getLowestY()
        {
            return lowestY;
        }

        /**
         * @return false if group is perfect square/rectangle
         *         true is group is not perfect square/rectangle
         */
        public boolean isIrregular()
        {
            ArrayList<Integer> listOfXCoords = new ArrayList<>();
            ArrayList<Integer> listOfYCoords = new ArrayList<>();

            GroupedList.GroupedNode curr = this.groupedList.first;
            for (int i=0; i<this.groupedList.size; i++)
            {
                if (!listOfXCoords.contains(curr.rNode.xCoord))
                    listOfXCoords.add(curr.rNode.xCoord);

                if (!listOfYCoords.contains(curr.rNode.yCoord))
                    listOfYCoords.add(curr.rNode.yCoord);

                curr = curr.next;
            }
            Collections.sort(listOfXCoords);
            Collections.sort(listOfYCoords);

            ArrayList<RNode> cellsToNull = new ArrayList<>();

            int startX = listOfXCoords.get(0);
            int startY = listOfYCoords.get(0);
            lowestX = startX;
            lowestY = startY;
            for (int i=startX; i<startX+listOfXCoords.size(); i++)
            {
                for (int j=startY; j<startY+listOfYCoords.size(); j++)
                {
                    if (!getRNode(i, j).isGrouped())
                    {
                        cellsToNull.add(getRNode(i, j));
                        System.out.println("Added to cellsToNull: "+i+","+j);
                    }
                    else if (!getRNode(i, j).groupName.equals(this.name))
                    {
                        cellsToNull.add(getRNode(i, j));
                        System.out.println("Added to cellsToNull: "+i+","+j);
                    }
                }
            }
            if (cellsToNull.size() > 0)
            {
                cellsToRemoveIfIrregular = cellsToNull;
                return true;
            }
            else
                return false;
        }
    }

    /**
     * Grouped Node LinkedList class
     */
    protected class GroupedList
    {
        protected GroupedNode first;
        private GroupedNode last;
        private int size;

        private GroupedList()
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
        private void add(RNode node)
        {
            if (size == 0)
            {
                first = new GroupedNode(node);
                last = first;
                size = 1;
            }
            else if (size > 0)
            {
                last.next = new GroupedNode(node);
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
        protected class GroupedNode
        {
            protected final RNode rNode;
            protected GroupedNode next;

            private GroupedNode(RNode node)
            {
                rNode = node;
                next = null;
            }
        }
    }

    /**
     * Class with different data to return from method sortHighlightedList()
     */
    private class SortHighlightedReturn
    {
        private final HighlightedList hList;
        private final int xRange, yRange;

        public SortHighlightedReturn(HighlightedList list, int x, int y)
        {
            hList = list;
            xRange = x;
            yRange = y;
        }
    }

    /**
     * Class with different data to return from method sortGroupedList()
     */
    private class SortGroupReturn
    {
        private final GroupedList gList;
        private final int xRange, yRange;

        public SortGroupReturn(GroupedList list, int x, int y)
        {
            gList = list;
            xRange = x;
            yRange = y;
        }
    }
}