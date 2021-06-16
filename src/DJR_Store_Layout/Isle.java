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

    public void setupIsleInfo(int n, ArrayList<Integer> arr)
    {
        numberOfIsleSections = n;
        numberOfSubsectionsForEachSection = arr;
    }

    public void printInfo()
    {
        System.out.println("IsleID: "+isleID);
        System.out.println("IsleGroup: "+isleGroup.getName());
        System.out.println("EndCap Location For Even Isles: "+isleGroup.getEndCapLocationForEvenIsleIDs());
        System.out.println("Back or FLoor: "+isleGroup.getBackOrFloor());
        System.out.println("Direction: "+isleGroup.getDirectionOfIncreasingIsleSections());
        if (numberOfIsleSections > 0)
        {
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

    public String getCoordsGivenLocation(int isleSection, String isleSubsection, String backOrFloor)
    {
        if (backOrFloor.compareTo("back") == 0)
        {
            System.out.println("Isle in the Back");
            if (isleSection == 1)
            {
                //"A" numeric value is 10 so if subsection is A then whatSubection = 1.
                //If subsection is B then whatSubsection = 2, and so on.
                int whatSubsection = Character.getNumericValue(isleSubsection.charAt(0))-9;

                System.out.println("Returning from Section and Subsection");
                return getIsleCoordsGivenSectionAndSubsection(1, whatSubsection, isleGroup.getDirectionOfIncreasingIsleSections());
            }
            else
            {
                throw new RuntimeException("Isle Section for Location Find must be 1 for Isles in the Back: There are no Endcaps for Isles in the Back.");
            }
        }
        else
        {
            System.out.println("Isle on the Floor");
            try
            {
                int hmm = Integer.parseInt(isleID.charAt(1)+"");
                System.out.println("Isle not clothes");

                if (isleSection == 0)
                {
                    System.out.println("Returning from endcap");
                    String endcap = isleGroup.getEndCapLocationForEvenIsleIDs();
                    int lastDigit = Integer.parseInt(isleID.charAt(isleID.length()-1)+"");
                    if (lastDigit % 2 == 0)
                    {
                        if (endcap.compareTo("left") == 0)
                        {
                            return getIsleCoordsGivenEndcap("leftest");
                        }
                        if (endcap.compareTo("bottom") == 0)
                        {
                            return getIsleCoordsGivenEndcap("bottom most");
                        }
                        if (endcap.compareTo("right") == 0)
                        {
                            return getIsleCoordsGivenEndcap("rightest");
                        }
                        if (endcap.compareTo("top") == 0)
                        {
                            return getIsleCoordsGivenEndcap("top most");
                        }
                    }
                    else if (lastDigit % 2 == 1)
                    {
                        if (endcap.compareTo("left") == 0)
                        {
                            return getIsleCoordsGivenEndcap("rightest");
                        }
                        if (endcap.compareTo("bottom") == 0)
                        {
                            return getIsleCoordsGivenEndcap("top most");
                        }
                        if (endcap.compareTo("right") == 0)
                        {
                            return getIsleCoordsGivenEndcap("leftest");
                        }
                        if (endcap.compareTo("top") == 0)
                        {
                            return getIsleCoordsGivenEndcap("bottom most");
                        }
                    }
                }
                else
                {
                    System.out.println("Returning from Section and Subsection");
                    return getIsleCoordsGivenSectionAndSubsection(isleSection, Integer.parseInt(isleSubsection), isleGroup.getDirectionOfIncreasingIsleSections());
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println("Isle is clothes");
                return "closest";
            }
        }

        System.out.println("getCoordsGivenLocation not setup yet");
        return "getCoordsGivenLocation not setup yet";
    }

    private String getIsleCoordsGivenEndcap(String where)
    {
        //System.out.println("Endcap where: "+where);
        IsleCellList.IsleCellNode nodeToReturnCoords = null;

        if (where.compareTo("bottom most") == 0)
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
        if (where.compareTo("top most") == 0)
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
        if (where.compareTo("leftest") == 0)
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
        if (where.compareTo("rightest") == 0)
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

    private String getIsleCoordsGivenSectionAndSubsection(int whatSection, int whatSubsection, String direction)
    {
        //If Isle is in the Back or with Only One Section
        if (whatSection == 1 && numberOfIsleSections == 2)
        {
            System.out.println("Just 2 Sections: Simple Version");
            if (direction.compareTo("left to right") == 0)
            {
                String s = getIsleCoordsGivenEndcap("leftest");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int xToReturn = x+(getCellsToSubsection()*whatSubsection);

                return xToReturn+","+y;
            }
            if (direction.compareTo("bottom to top") == 0)
            {
                String s = getIsleCoordsGivenEndcap("bottom most");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int yToReturn = y-(getCellsToSubsection()*whatSubsection);

                return x+","+yToReturn;
            }
            if (direction.compareTo("right to left") == 0)
            {
                String s = getIsleCoordsGivenEndcap("rightest");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int xToReturn = x-(getCellsToSubsection()*whatSubsection);

                return xToReturn+","+y;
            }
            if (direction.compareTo("top to bottom") == 0)
            {
                String s = getIsleCoordsGivenEndcap("top most");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int yToReturn = y+(getCellsToSubsection()*whatSubsection);

                return x+","+yToReturn;
            }
        }
        //If Isle is on the Floor w/ multiple Sections
        else
        {
            System.out.println("Multiple Sections: Complex Version");
            int cellsToSubsection = getCellsToSubsection();

            if (direction.compareTo("left to right") == 0)
            {
                String s = getIsleCoordsGivenEndcap("leftest");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int xToReturn = x;
                for (int i=1; i<whatSection+1; i++)
                {
                    if (i==whatSection)
                        xToReturn+=whatSubsection*cellsToSubsection;
                    else
                        xToReturn+=(numberOfSubsectionsForEachSection.get(i)*cellsToSubsection);
                }
                return xToReturn+","+y;
            }
            if (direction.compareTo("bottom to top") == 0)
            {
                String s = getIsleCoordsGivenEndcap("bottom most");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int yToReturn = y-(getCellsToSubsection()*whatSubsection);

                return x+","+yToReturn;
            }
            if (direction.compareTo("right to left") == 0)
            {
                String s = getIsleCoordsGivenEndcap("rightest");
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
                return xToReturn+","+y;
            }
            if (direction.compareTo("top to bottom") == 0)
            {
                String s = getIsleCoordsGivenEndcap("top most");
                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                int yToReturn = y+(getCellsToSubsection()*whatSubsection);

                return x+","+yToReturn;
            }
        }
        return "Get Subsection Coord didnt work";
    }

    public int getCellsToSubsection()
    {
        int totalNumberOfSubsections = 0;
        //Skip isle section 0 cuz endcap.
        for (int i=1; i<numberOfSubsectionsForEachSection.size(); i++)
            totalNumberOfSubsections+=numberOfSubsectionsForEachSection.get(i);

        return getNumberOfCellsInIsle()/totalNumberOfSubsections;
    }

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
