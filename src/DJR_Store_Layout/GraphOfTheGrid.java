package DJR_Store_Layout;

import java.util.*;

public class GraphOfTheGrid
{
    public Hashtable<String, EdgeNode> graph;

    private int numberOfVertices;
    private int numberOfEdges;

    private GridData3 grid;
    private String startAndEnd;

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
                                graph.put(i+","+j, new EdgeNode(new Edge(i+","+j, (i-1)+","+j)));
                            else
                            {
                                EdgeNode curr = graph.get(i+","+j);
                                while (curr != null && curr.next != null)
                                    curr = curr.next;

                                curr.setNext(new EdgeNode(new Edge(i+","+j, (i-1)+","+j)));
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
                                graph.put(i+","+j, new EdgeNode(new Edge(i+","+j, i+","+(j-1))));
                            else
                            {
                                EdgeNode curr = graph.get(i+","+j);
                                while (curr != null && curr.next != null)
                                    curr = curr.next;

                                curr.setNext(new EdgeNode(new Edge(i+","+j, i+","+(j-1))));
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
                                graph.put(i+","+j, new EdgeNode(new Edge(i+","+j, (i+1)+","+j)));
                            else
                            {
                                EdgeNode curr = graph.get(i+","+j);
                                while (curr != null && curr.next != null)
                                    curr = curr.next;

                                curr.setNext(new EdgeNode(new Edge(i+","+j, (i+1)+","+j)));
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
                                graph.put(i+","+j, new EdgeNode(new Edge(i+","+j, i+","+(j+1))));
                            else
                            {
                                EdgeNode curr = graph.get(i+","+j);
                                while (curr != null && curr.next != null)
                                    curr = curr.next;

                                curr.setNext(new EdgeNode(new Edge(i+","+j, i+","+(j+1))));
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
        numberOfVertices = 1;

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
        startAndEnd = x+","+y;

        for (String s : list)
        {
            numberOfVertices++;

            EdgeNode newNode;
            String[] hmm = s.split(",");
            if (hmm.length > 1)
            {
                System.out.println(s+" is regular coords");
                DistanceReturn dr = graph1.findDistanceBetween(startAndEnd, s);
                newNode = new EdgeNode(new Edge(startAndEnd, s, dr.getDistance(), dr.getPath()));
            }
            else
            {
                System.out.println(s+" is clothes");
                ArrayList<DistanceReturn> drList = graph1.findClosestCellAndComputeDistanceIfIsleShapeIsArea(s, startAndEnd);
                newNode = new EdgeNode(drList);
            }

            //Add edge from startAndEnd to s
            if (graph.get(startAndEnd) == null)
            {
                graph.put(startAndEnd, newNode);
                numberOfEdges++;
            }
            else
            {
                EdgeNode curr = graph.get(startAndEnd);
                while (curr != null && curr.next != null)
                    curr = curr.next;

                curr.setNext(newNode);
                numberOfEdges++;
            }

            hmm = s.split(",");
            if (hmm.length > 1)
            {
                System.out.println(s+" is regular coords");
                DistanceReturn dr = graph1.findDistanceBetween(startAndEnd, s);
                newNode = new EdgeNode(new Edge(startAndEnd, s, dr.getDistance(), dr.getPath()));
            }
            else
            {
                System.out.println(s+" is clothes");
                ArrayList<DistanceReturn> drList = graph1.findClosestCellAndComputeDistanceIfIsleShapeIsArea(s, startAndEnd);
                newNode = new EdgeNode(drList);
            }

            //Add edge from s to startAndEnd
            graph.put(s, newNode);
            numberOfEdges++;

            //For each other location, add edge from s to that location
            for (String s2 : list)
            {
                if (s.compareTo(s2) != 0)
                {
                    DistanceReturn dr = graph1.findDistanceBetween(startAndEnd, s);

                    EdgeNode curr = graph.get(s);
                    while (curr != null && curr.next != null)
                        curr = curr.next;

                    dr = graph1.findDistanceBetween(s, s2);
                    curr.setNext(new EdgeNode(new Edge(s, s2, dr.getDistance(), dr.getPath())));
                    numberOfEdges++;
                }
            }


        }

        System.out.println("Graph:");
        Set<String> set = graph.keySet();
        for (String s : set)
        {
            EdgeNode curr = graph.get(s);
            while (curr != null)
            {
                System.out.println(curr.getEdge().u+"->"+curr.getEdge().w+" : "+curr.getEdge().length);
                curr = curr.next;
            }
        }
        System.out.println();
        System.out.println("New List:");
        for (String s : list)
            System.out.println(s);
    }

    //Dijkstra's
    public DistanceReturn findDistanceBetween(String start1, String end1)
    {
        //Making sure start is on a valid vertex
        String[] startCoords = start1.split(",");
        int x = Integer.parseInt(startCoords[0]);
        int y = Integer.parseInt(startCoords[1]);

        String start = start1;
        if (grid.getRNode(x, y).isIsle())
            start = findNearestNonIsleCell(start1, x ,y);

        //Making sure end is on a valid vertex
        String[] endCoords = end1.split(",");
        x = Integer.parseInt(endCoords[0]);
        y = Integer.parseInt(endCoords[1]);

        String end = end1;
        if (grid.getRNode(x, y).isIsle())
            end = findNearestNonIsleCell(end1, x, y);

        Hashtable<String, Integer> distance = new Hashtable<>();
        Hashtable<String, StringBuilder> previous = new Hashtable<>();
        Hashtable<String, Boolean> visited = new Hashtable<>();

        Set<String> vertices = graph.keySet();
        for (String s : vertices)
        {
            distance.put(s, Integer.MAX_VALUE);
            previous.put(s, new StringBuilder(""));
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

            EdgeNode currEdge = graph.get(currNode);
            while (currEdge != null)
            {
                if (!visited.get(currEdge.getEdge().w))
                {
                    if (!q.contains(currEdge.getEdge().w))
                        q.add(currEdge.getEdge().w);

                    int dist = distance.get(currEdge.getEdge().u) + currEdge.getEdge().length;
                    if (dist < distance.get(currEdge.getEdge().w))
                    {
                        distance.put(currEdge.getEdge().w, dist);

                        previous.get(currEdge.getEdge().w).append(previous.get(currEdge.getEdge().u)).append(" ").append(currEdge.getEdge().u);
                    }
                }
                currEdge = currEdge.next;
            }
        }

        StringBuilder path = previous.get(end).append(" ").append(end);
        String[] pathArr = path.toString().split(" ");
        return new DistanceReturn(pathArr.length-2, path.toString(), end1);
    }

    public FindingPathReturn findPickingPath(GridData3 g, Hashtable<String, String> list, String which)
    {
        int x;
        int y;
        if (which.compareTo("OPU") == 0)
        {
            x = g.getOpuStartEndNode().getX();
            y = g.getOpuStartEndNode().getY();
        }
        else
        {
            x = g.getRegStartEndNode().getX();
            y = g.getRegStartEndNode().getY();
        }
        startAndEnd = x+","+y;

        Hashtable<String, Boolean> visited = new Hashtable<>();
        Hashtable<String, Integer> ids = new Hashtable<>();
        int i = 0;
        Set<String> set = list.keySet();
        for (String vertex : set)
        {
            ids.put(vertex, i);
            i++;
            visited.put(vertex, false);
        }
        ids.put(startAndEnd, i);

        ArrayList<String> locationPath = new ArrayList<>();
        ArrayList<String> vertexPath = new ArrayList<>();
        ArrayList<Edge> edgePath = new ArrayList<>();

        vertexPath.add(startAndEnd);

        String curr = startAndEnd;
        String previousCell = startAndEnd;
        while (visited.containsValue(false))
        {
            System.out.println("Curr: "+curr);
            visited.put(curr, true);
            System.out.println("Previous Cell: "+previousCell);

            EdgePQ pq = new EdgePQ();
            for (String vertex : set)
            {
                if (!visited.get(vertex))
                {
                    System.out.println("Adding edge to "+vertex+" to pq");
                    String[] hmm = vertex.split(",");
                    if (hmm.length > 1)
                    {
                        System.out.println(vertex+" is regular coords");
                        DistanceReturn dr;
                        if (!curr.equals(previousCell))
                            dr = findDistanceBetween(previousCell, vertex);
                        else
                            dr = findDistanceBetween(curr, vertex);
                        pq.add(new Edge(curr, vertex, dr));
                    }
                    else
                    {
                        System.out.println(vertex + " is clothes");
                        DistanceReturn dr;
                        if (!curr.equals(previousCell))
                            dr = findClosestCellAndComputeDistanceIfIsleShapeIsArea(previousCell, vertex).get(0);
                        else
                            dr = findClosestCellAndComputeDistanceIfIsleShapeIsArea(curr, vertex).get(0);
                        pq.add(new Edge(curr, vertex, dr));
                    }
                }
            }

            System.out.println("PQ:");
            pq.print();
            System.out.println();

            Edge root = pq.getRoot();
            System.out.println("Root: "+root.u+"->"+root.w);
            System.out.println("Comparing "+ids.get(root.u)+" and "+ids.get(root.w));
            if (!ids.get(root.u).equals(ids.get(root.w)))
            {
                edgePath.add(root);
                vertexPath.add(root.dr.end);
                locationPath.add(list.get(root.w));
                System.out.println("Adding "+root.dr.end+" to coordinate path");
                System.out.println("Adding "+list.get(root.w)+" to location path");

                int id = ids.get(root.u);
                for (String vertex : set)
                {
                    if (ids.get(vertex) == id)
                        ids.put(vertex, ids.get(root.w));
                }
                ids.put(root.u, ids.get(root.w));

                curr = root.w;
                previousCell = root.dr.end;
                visited.put(root.w, true);
            }
        }

        DistanceReturn dr;
        if (!curr.equals(previousCell))
            dr = findDistanceBetween(previousCell, startAndEnd);
        else
            dr = findDistanceBetween(curr, startAndEnd);
        edgePath.add(new Edge(curr, startAndEnd, dr.getDistance(), dr.getPath()));

        StringBuilder cellPath = new StringBuilder();
        for (Edge e : edgePath)
        {
            cellPath.append(e.cellPath);
        }

        return new FindingPathReturn(locationPath, vertexPath, cellPath.toString());
    }

    public StringBuilder[] findPickingPath2()
    {
        //Add each edge to pq, add pq to spanning tree if connecting nodes dont already have more than two edge coming out of them

        return null;
    }

    private String findNearestNonIsleCell(String old, int x, int y)
    {
        String newCoords = old;

        while (newCoords.compareTo(old) == 0)
        {
            try
            {
                GridData3.RNode rnode = grid.getRNode(x-1, y);
                if (!rnode.isNulled() && !rnode.isIsle())
                {
                    newCoords = (x-1)+","+y;
                    break;
                }
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
            try
            {
                GridData3.RNode rnode = grid.getRNode(x, y+1);
                if (!rnode.isNulled() && !rnode.isIsle())
                {
                    newCoords = x+","+(y+1);
                    break;
                }
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
            try
            {
                GridData3.RNode rnode = grid.getRNode(x+1, y);
                if (!rnode.isNulled() && !rnode.isIsle())
                {
                    newCoords = (x+1)+","+y;
                    break;
                }
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
            try
            {
                GridData3.RNode rnode = grid.getRNode(x, y-1);
                if (!rnode.isNulled() && !rnode.isIsle())
                {
                    newCoords = x+","+(y-1);
                    break;
                }
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
            if (newCoords.compareTo(old) == 0)
                break;
        }

        return newCoords;
    }

    public ArrayList<DistanceReturn> findClosestCellAndComputeDistanceIfIsleShapeIsArea(String start1, String isleID)
    {
        Isle isle = grid.getIsleWithUnknownIG(isleID);
        if (isle == null)
            System.out.println("Isle not found");

        String[] startCoords = start1.split(",");
        int x = Integer.parseInt(startCoords[0]);
        int y = Integer.parseInt(startCoords[1]);

        String start = start1;
        if (grid.getRNode(x, y).isIsle())
            start = findNearestNonIsleCell(start1, x ,y);

        ArrayList<String> listOfEnds = new ArrayList<>();

        Isle.IsleCellList.IsleCellNode curr = isle.getIsleCellList().getFirst();
        while (curr != null)
        {
            //System.out.println("Finding path for: "+curr.getrNode().getX()+","+curr.getrNode().getY());
            listOfEnds.add(curr.getrNode().getX()+","+curr.getrNode().getY());
            //System.out.println("Distance: "+listOfPaths.get(listOfPaths.size()-1).getDistance());
            curr = curr.getNext();
        }

        Hashtable<String, Integer> distance = new Hashtable<>();
        Hashtable<String, StringBuilder> previous = new Hashtable<>();
        Hashtable<String, Boolean> visited = new Hashtable<>();

        Set<String> vertices = graph.keySet();
        for (String s : vertices)
        {
            distance.put(s, Integer.MAX_VALUE);
            previous.put(s, new StringBuilder(""));
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

            EdgeNode currEdge = graph.get(currNode);
            while (currEdge != null)
            {
                if (!visited.get(currEdge.getEdge().w))
                {
                    if (!q.contains(currEdge.getEdge().w))
                        q.add(currEdge.getEdge().w);

                    int dist = distance.get(currEdge.getEdge().u) + currEdge.getEdge().length;
                    if (dist < distance.get(currEdge.getEdge().w))
                    {
                        distance.put(currEdge.getEdge().w, dist);

                        previous.get(currEdge.getEdge().w).append(previous.get(currEdge.getEdge().u)).append(" ").append(currEdge.getEdge().u);
                    }
                }
                currEdge = currEdge.next;
            }
        }

        ArrayList<DistanceReturn> listOfPaths = new ArrayList<>();
        for (String end : listOfEnds)
        {
            String[] endCoords = end.split(",");
            x = Integer.parseInt(endCoords[0]);
            y = Integer.parseInt(endCoords[1]);

            String newEnd = null;
            if (grid.getRNode(x, y).isIsle())
                newEnd = findNearestNonIsleCell(end, x, y);
            try
            {
                StringBuilder path = previous.get(newEnd).append(" ").append(newEnd);
                String[] pathArr = path.toString().split(" ");
                listOfPaths.add(new DistanceReturn(pathArr.length-2, path.toString(), end));
            }
            catch (NullPointerException ignored) {}
        }

        listOfPaths.sort(new DistanceReturn.DistanceComparator());

        return listOfPaths;
    }

    protected static class EdgeNode
    {
        private final Edge edge;
        private final ArrayList<DistanceReturn> list;
        private EdgeNode next;

        public EdgeNode(Edge e)
        {
            edge = e;
            list = null;
        }

        public EdgeNode(ArrayList<DistanceReturn> l)
        {
            list = l;
            edge = null;
        }

        public void setNext(EdgeNode en)
        {
            next = en;
        }

        public Edge getEdge() {
            return edge;
        }

        public ArrayList<DistanceReturn> getList() {
            return list;
        }

        public EdgeNode getNext()
        {
            return next;
        }
    }

    protected static class Edge
    {
        protected String u;
        protected String w;
        protected int length;
        protected String cellPath;
        protected DistanceReturn dr;

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

        public Edge(String v1, String v2, DistanceReturn distanceReturn)
        {
            u = v1;
            w = v2;
            length = distanceReturn.distance;
            cellPath = distanceReturn.path;
            dr = distanceReturn;
        }

        public String getU()
        {
            return u;
        }

        public String getW()
        {
            return w;
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

    private class EdgePQ
    {
        private final Edge[] edgeArray;
        private int size;

        public EdgePQ()
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
                    if (e.length < parent.length)
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
                    if (e.length < parent.length)
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
                        if (edgeArray[index].length < edgeArray[(2*index)+1].length && edgeArray[index].length < edgeArray[(2*index)+2].length)
                            break;

                        if (edgeArray[(2*index)+1].length < edgeArray[(2*index)+2].length)
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
                        if (edgeArray[index].length > edgeArray[(2*index)+1].length)
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

        public boolean hasStuff()
        {
            return size > 0;
        }

        public void print()
        {
            for (int i=0; i<size; i++)
            {
                System.out.println(edgeArray[i].u+"->"+edgeArray[i].w+" : "+edgeArray[i].length);
            }
        }
    }

    public static class DistanceReturn
    {
        private final int distance;
        private final String path;
        private final String end;

        private DistanceReturn(int d, String p, String e)
        {
            distance = d;
            path = p;
            end = e;
        }

        public int getDistance()
        {
            return distance;
        }

        public String getPath()
        {
            return path;
        }

        public String getEnd()
        {
            return end;
        }

        private static class DistanceComparator implements Comparator<DistanceReturn>
        {
            public int compare(DistanceReturn dr1, DistanceReturn dr2)
            {
                if (dr1.distance == dr2.distance)
                    return 0;
                else if (dr1.distance > dr2.distance)
                    return 1;
                else
                    return -1;
            }
        }
    }

    public class FindingPathReturn
    {
        private ArrayList<String> locationPath;
        private ArrayList<String> vertexPath;
        private String cellPath;

        private FindingPathReturn(ArrayList<String> a1, ArrayList<String> a2, String s)
        {
            locationPath = a1;
            vertexPath = a2;
            cellPath = s;
        }

        public ArrayList<String> getLocationPath()
        {
            return locationPath;
        }

        public ArrayList<String> getVertexPath()
        {
            return vertexPath;
        }

        public String getCellPath()
        {
            return cellPath;
        }
    }
}

