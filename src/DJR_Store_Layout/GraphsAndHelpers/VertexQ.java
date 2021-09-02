/**
 * Vertex Queue (non-priority) for GraphOfTheGrid.findDistanceBetween()
 * @author David Roberts
 */

package DJR_Store_Layout.GraphsAndHelpers;

public class VertexQ
{
    private final String[] array;
    private int size;

    public VertexQ(int size)
    {
        array = new String[size];
        size = 0;
    }

    public void add(String vertex)
    {
        array[size] = vertex;
        size++;
    }

    public String getRoot()
    {
        String temp = array[0];

        for (int i=0; i<size-1; i++)
        {
            array[i] = array[i+1];
        }
        size--;

        return temp;
    }

    public boolean contains(String v)
    {
        for (int i=0; i<size-1; i++)
        {
            if (array[i].compareTo(v) == 0)
                return true;
        }
        return false;
    }

    public boolean hasStuff() {return size > 0;}
}