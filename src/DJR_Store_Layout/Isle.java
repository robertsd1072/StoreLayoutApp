package DJR_Store_Layout;

import java.util.ArrayList;

public class Isle
{
    private final String isleID;
    private final GridData3.IsleGroup isleGroup;
    private IsleCellList isleCellList;
    private int numberOfIsleSections;
    private ArrayList<Integer> numberOfSubsectionsForEachSection;

    public Isle(String id, GridData3.IsleGroup ig)
    {
        isleID = id;
        isleGroup = ig;
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

    public String getCoordsGivenLocation(int isleSection, int isleSubsection, int Shelf, double location)
    {
        if (isleSection == 0)
        {

        }

        System.out.println("getCoordsGivenLocation not setup yet");
        return null;
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
