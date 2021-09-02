/**
 * Edge Priority Queue with shortest edge at top
 * @author David Roberts
 */

package DJR_Store_Layout.GraphsAndHelpers;

public class EdgePQ
{
    /** Nodes in array but sorted like a binary tree */
    private final Edge[] edgeArray;
    private int size;

    public EdgePQ(int numberOfEdges)
    {
        edgeArray = new Edge[numberOfEdges];
        size = 0;
    }

    public void add(Edge e)
    {
        edgeArray[size] = e;

        int index = size;
        while (index > 0)
        {
            if (index % 2 == 1)
            {
                index = (index-1)/2;
                Edge parent = edgeArray[index];
                if (e.getLength() < parent.getLength())
                {
                    edgeArray[(2*index)+1] = parent;
                }
                else
                {
                    edgeArray[(2*index)+1] = e;
                    break;
                }
            }
            else
            {
                index = (index-2)/2;
                Edge parent = edgeArray[index];
                if (e.getLength() < parent.getLength())
                {
                    edgeArray[(2*index)+2] = parent;
                }
                else
                {
                    edgeArray[(2*index)+2] = e;
                    break;
                }
            }

            if (index == 0)
            {
                edgeArray[index] = e;
            }
        }
        size++;
    }

    public Edge getRoot()
    {
        Edge root = edgeArray[0];

        Edge last = edgeArray[size-1];
        edgeArray[size-1] = null;

        edgeArray[0] = last;

        Edge edgeToSwap = edgeArray[0];
        int index = 0;
        try
        {
            while (edgeArray[(2*index)+1] != null)
            {
                if (edgeArray[(2*index)+2] != null)
                {
                    if (edgeArray[index].getLength() < edgeArray[(2*index)+1].getLength()
                            && edgeArray[index].getLength() < edgeArray[(2*index)+2].getLength())
                        break;

                    if (edgeArray[(2*index)+1].getLength() < edgeArray[(2*index)+2].getLength())
                    {
                        Edge tempEdge = edgeArray[(2*index)+1];
                        edgeArray[(2*index)+1] = edgeToSwap;
                        edgeArray[index] = tempEdge;

                        index = (2*index)+1;
                        edgeToSwap = edgeArray[index];
                    }
                    else
                    {
                        Edge tempEdge = edgeArray[(2*index)+2];
                        edgeArray[(2*index)+2] = edgeToSwap;
                        edgeArray[index] = tempEdge;

                        index = (2*index)+2;
                        edgeToSwap = edgeArray[index];
                    }
                }
                else
                {
                    if (edgeArray[index].getLength() > edgeArray[(2*index)+1].getLength())
                    {
                        Edge tempEdge = edgeArray[(2*index)+1];
                        edgeArray[(2*index)+1] = edgeToSwap;
                        edgeArray[index] = tempEdge;

                        index = (2*index)+1;
                        edgeToSwap = edgeArray[index];
                    }
                    else
                        break;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException ignored) {}
        size--;

        return root;
    }
}