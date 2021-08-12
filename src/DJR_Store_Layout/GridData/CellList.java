package DJR_Store_Layout.GridData;

public class CellList
{
    private CellNode first;
    private CellNode last;
    private int size;

    public CellList()
    {
        first = null;
        last = null;
        size = 0;
    }

    public CellNode getFirst() {return first;}

    /**
     * Adds new rectangle to LinkedList
     *
     * @param node new rectangle to add
     */
    public void add(RNode node)
    {
        if (size == 0)
        {
            first = new CellNode(node);
            last = first;
            size = 1;
        }
        else if (size > 0)
        {
            last.next = new CellNode(node);
            last = last.next;
            size++;
        }
    }

    /**
     * Resets LinkedList
     */
    public void clear()
    {
        first = null;
        last = null;
        size = 0;
    }

    public int size() {return size;}

    public class CellNode
    {
        private final RNode rNode;
        private CellNode next;

        private CellNode(RNode node)
        {
            rNode = node;
            next = null;
        }

        public RNode getrNode() {return rNode;}

        public CellNode getNext() {return next;}
    }
}