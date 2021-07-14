package DJR_Store_Layout;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class GraphOfTheGrid
{
    public Hashtable<String, Edge> graph;

    private int numberOfVertices;
    private int numberOfEdges;

    private GridData3 grid;
    private String opuOrReg;

    public GraphOfTheGrid(GridData3 g)
    {
        graph = new Hashtable<>();
        numberOfVertices = 0;
        numberOfEdges = 0;

        grid = g;

        int cols = g.colSize;
        int rows = g.rowSize;

        for (int i=0; i<cols; i++)
        {
            for (int j=0; j<rows; j++)
            {
                if (!g.getRNode(i, j).isNulled() && !g.getRNode(i, j).isIsle())
                {
                    numberOfVertices++;
                    try
                    {
                        //Add edge to west
                        if (!g.getRNode(i-1, j).isNulled() && !g.getRNode(i-1, j).isIsle())
                        {
                            //At index (i+","+j).hashCode(), add edge between i,j and i-1,j
                            if (graph.get(i+","+j) == null)
                                graph.put(i+","+j, new Edge(i+","+j, (i-1)+","+j));
                            else
                            {
                                Edge curr = graph.get(i+","+j);
                                while (curr != null && curr.next != null)
                                    curr = curr.next;

                                curr.setNext(new Edge(i+","+j, (i-1)+","+j));
                            }
                            numberOfEdges++;
                        }
                    }
                    catch (IndexOutOfBoundsException ignored) {}
                    try
                    {
                        //Add edge to north
                        if (!g.getRNode(i, j-1).isNulled() && !g.getRNode(i, j-1).isIsle())
                        {
                            //At index (i+","+j).hashCode(), add edge between i,j and i,j-1
                            if (graph.get(i+","+j) == null)
                                graph.put(i+","+j, new Edge(i+","+j, i+","+(j-1)));
                            else
                            {
                                Edge curr = graph.get(i+","+j);
                                while (curr != null && curr.next != null)
                                    curr = curr.next;

                                curr.setNext(new Edge(i+","+j, i+","+(j-1)));
                            }
                            numberOfEdges++;
                        }
                    }
                    catch (IndexOutOfBoundsException ignored) {}
                    try
                    {
                        //Add edge to east
                        if (!g.getRNode(i+1, j).isNulled() && !g.getRNode(i+1, j).isIsle())
                        {
                            //At index (i+","+j).hashCode(), add edge between i,j and i+1,j
                            if (graph.get(i+","+j) == null)
                                graph.put(i+","+j, new Edge(i+","+j, (i+1)+","+j));
                            else
                            {
                                Edge curr = graph.get(i+","+j);
                                while (curr != null && curr.next != null)
                                    curr = curr.next;

                                curr.setNext(new Edge(i+","+j, (i+1)+","+j));
                            }
                            numberOfEdges++;
                        }
                    }
                    catch (IndexOutOfBoundsException ignored) {}
                    try
                    {
                        //Add edge to north
                        if (!g.getRNode(i, j+1).isNulled() && !g.getRNode(i, j+1).isIsle())
                        {
                            //At index (i+","+j).hashCode(), add edge between i,j and i,j+1
                            if (graph.get(i+","+j) == null)
                                graph.put(i+","+j, new Edge(i+","+j, i+","+(j+1)));
                            else
                            {
                                Edge curr = graph.get(i+","+j);
                                while (curr != null && curr.next != null)
                                    curr = curr.next;

                                curr.setNext(new Edge(i+","+j, i+","+(j+1)));
                            }
                            numberOfEdges++;
                        }
                    }
                    catch (IndexOutOfBoundsException ignored) {}
                }
            }
        }
        System.out.println("Number of Vertices: "+numberOfVertices);
        System.out.println("Number of Edges: "+numberOfEdges);
    }

    public GraphOfTheGrid(GridData3 g, GraphOfTheGrid graph1, ArrayList<String> list, String which)
    {
        graph = new Hashtable<>();
        grid = g;
        numberOfVertices = 0;
        opuOrReg = which;

        int x;
        int y;
        if (which.compareTo("OPU") == 0)
        {
            x = grid.getOpuStartEndNode().getX();
            y = grid.getOpuStartEndNode().getY();
        }
        else
        {
            x = grid.getRegStartEndNode().getX();
            y = grid.getRegStartEndNode().getY();
        }
        String startAndEnd = x+","+y;;

        for (String s : list)
        {
            //Add edge from startAndEnd to s
            if (graph.get(startAndEnd) == null)
            {
                DistanceReturn dr = graph1.findDistanceBetween(startAndEnd, s);
                graph.put(startAndEnd, new Edge(startAndEnd, s, dr.getDistance(), dr.getPath()));
                numberOfVertices++;
            }
            else
            {
                Edge curr = graph.get(startAndEnd);
                while (curr != null && curr.next != null)
                    curr = curr.next;

                DistanceReturn dr = graph1.findDistanceBetween(startAndEnd, s);
                curr.setNext(new Edge(startAndEnd, s, dr.getDistance(), dr.getPath()));
                numberOfVertices++;
            }

            //Add edge from s to startAndEnd
            DistanceReturn dr = graph1.findDistanceBetween(s, startAndEnd);
            graph.put(s, new Edge(s, startAndEnd, dr.getDistance(), dr.getPath()));
            numberOfVertices++;

            //For each other location, add edge from s to that location
            for (String s2 : list)
            {
                if (s.compareTo(s2) != 0)
                {
                    Edge curr = graph.get(s);
                    while (curr != null && curr.next != null)
                        curr = curr.next;

                    dr = graph1.findDistanceBetween(s, s2);
                    curr.setNext(new Edge(s, s2, dr.getDistance(), dr.getPath()));
                    numberOfVertices++;
                }
            }
        }
    }

    public DistanceReturn findDistanceBetween(String start1, String end1)
    {
        //Making sure start is on a valid vertex
        String[] startCoords = start1.split(",");
        int x = Integer.parseInt(startCoords[0]);
        int y = Integer.parseInt(startCoords[1]);

        String start = start1;
        if (grid.getRNode(x, y).isIsle())
        {
            if (!grid.getRNode(x, y-1).isNulled() && !grid.getRNode(x, y-1).isIsle())
            {
                //System.out.println("North");
                start = x+","+(y-1);
            }
            else if (!grid.getRNode(x+1, y).isNulled() && !grid.getRNode(x+1, y).isIsle())
            {
                //System.out.println("East");
                start = (x+1)+","+y;
            }
            else if (!grid.getRNode(x, y+1).isNulled() && !grid.getRNode(x, y+1).isIsle())
            {
                //System.out.println("South");
                start = x+","+(y+1);
            }
            else if (!grid.getRNode(x-1, y).isNulled() && !grid.getRNode(x-1, y).isIsle())
            {
                //System.out.println("West");
                start = (x-1)+","+y;
            }
        }

        //Making sure end is on a valid vertex
        String[] endCoords = end1.split(",");
        x = Integer.parseInt(endCoords[0]);
        y = Integer.parseInt(endCoords[1]);

        String end = end1;
        if (grid.getRNode(x, y).isIsle())
        {
            if (!grid.getRNode(x, y-1).isNulled() && !grid.getRNode(x, y-1).isIsle())
            {
                //System.out.println("North");
                end = x+","+(y-1);
            }
            else if (!grid.getRNode(x+1, y).isNulled() && !grid.getRNode(x+1, y).isIsle())
            {
                //System.out.println("East");
                end = (x+1)+","+y;
            }
            else if (!grid.getRNode(x, y+1).isNulled() && !grid.getRNode(x, y+1).isIsle())
            {
                //System.out.println("South");
                end = x+","+(y+1);
            }
            else if (!grid.getRNode(x-1, y).isNulled() && !grid.getRNode(x-1, y).isIsle())
            {
                //System.out.println("West");
                end = (x-1)+","+y;
            }
        }

        //System.out.println("Start: "+start);
        //System.out.println("End: "+end);

        Hashtable<String, Integer> distance = new Hashtable<>();
        Hashtable<String, StringBuilder> previous = new Hashtable<>();
        Hashtable<String, Boolean> visited = new Hashtable<>();

        Set<String> vertices = graph.keySet();
        for (String s : vertices)
        {
            distance.put(s, Integer.MAX_VALUE);
            previous.put(s, new StringBuilder());
            visited.put(s, false);
        }

        distance.put(start, 0);
        visited.put(start, true);

        VertexQ q = new VertexQ(numberOfVertices);
        q.add(start);

        String currNode;
        while (q.hasStuff())
        {
            currNode = q.getRoot();
            visited.put(currNode, true);

            Edge currEdge = graph.get(currNode);
            while (currEdge != null)
            {
                if (!visited.get(currEdge.w))
                {
                    if (!q.contains(currEdge.w))
                        q.add(currEdge.w);

                    int dist = distance.get(currEdge.u) + currEdge.length;
                    if (dist < distance.get(currEdge.w))
                    {
                        distance.put(currEdge.w, dist);
                        if (previous.get(currEdge.w) == null)
                        {
                            previous.get(currEdge.w).append(previous.get(currEdge.u));
                            previous.get(currEdge.w).append(currEdge.u).append(" ");
                        }
                        else
                            previous.put(currEdge.w, new StringBuilder(previous.get(currEdge.u)+" "+currEdge.u+" "));
                    }
                }
                currEdge = currEdge.next;
            }
        }

        String path = previous.get(end).toString();
        String[] pathArr = path.split(" ");
        return new DistanceReturn(pathArr.length, previous.get(end).toString());
    }

    public String findPickingPath()
    {
        int x;
        int y;
        if (opuOrReg.compareTo("OPU") == 0)
        {
            x = grid.getOpuStartEndNode().getX();
            y = grid.getOpuStartEndNode().getY();
        }
        else
        {
            x = grid.getRegStartEndNode().getX();
            y = grid.getRegStartEndNode().getY();
        }
        String startAndEnd = x+","+y;

        return null;
    }

    protected static class Edge
    {
        protected String u;
        protected String w;
        protected int length;
        protected String cellPath;
        protected Edge next;

        public Edge(String v1, String v2)
        {
            u = v1;
            w = v2;
            length = 1;
        }

        public Edge(String v1, String v2, int l, String cp)
        {
            u = v1;
            w = v2;
            length = l;
            cellPath = cp;
        }

        public void setNext(Edge e)
        {
            next = e;
        }
    }

    private static class VertexQ
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

        public boolean hasStuff()
        {
            return size > 0;
        }

        public void print()
        {
            for (int i=0; i<size; i++)
            {
                System.out.println(array[i]);
            }
        }
    }

    public static class DistanceReturn
    {
        private final int distance;
        private final String path;

        private DistanceReturn(int d, String p)
        {
            distance = d;
            path = p;
        }

        public int getDistance()
        {
            return distance;
        }

        public String getPath()
        {
            return path;
        }
    }
}

