package DJR_Store_Layout;

import java.util.*;

public class GraphOfTheGrid
{
    public Hashtable<String, Edge> graph;

    private int numberOfVertices;
    private int numberOfEdges;

    private final GridData3 grid;
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
                                graph.put(i+","+j, new Edge(i+","+j, (i-1)+","+j));
                            else
                            {
                                Edge curr = graph.get(i+","+j);
                                while (curr != null && curr.next != null)
                                    curr = curr.next;

                                curr.next = new Edge(i+","+j, (i-1)+","+j);
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

                                curr.next = new Edge(i+","+j, i+","+(j-1));
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

                                curr.next = new Edge(i+","+j, (i+1)+","+j);
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

                                curr.next = new Edge(i+","+j, i+","+(j+1));
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

    public DistanceReturn findDistanceBetween(String start1, String end1)
    {
        //Making sure start is on a valid vertex
        Coords startCoords = new Coords(start1);

        String start = start1;
        if (grid.getRNode(startCoords.getX(), startCoords.getY()).isIsle())
            start = findNearestNonIsleCell(start1, startCoords.getX() ,startCoords.getY());

        //Making sure end is on a valid vertex
        Coords endCoords = new Coords(end1);

        String end = end1;
        if (grid.getRNode(endCoords.getX(), endCoords.getY()).isIsle())
            end = findNearestNonIsleCell(end1, endCoords.getX(), endCoords.getY());

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

                        previous.get(currEdge.w).append(previous.get(currEdge.u)).append(" ").append(currEdge.u);
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
            //System.out.println("Curr: "+curr);
            visited.put(curr, true);
            //System.out.println("Previous Cell: "+previousCell);

            ArrayList<String> unvisitedVertexList = new ArrayList<>();
            ArrayList<DistanceReturn> drList = new ArrayList<>();
            EdgePQ pq = new EdgePQ();
            for (String vertex : set)
            {
                if (!visited.get(vertex))
                {
                    System.out.println("Adding edge to "+vertex+" to pq");
                    unvisitedVertexList.add(vertex);
                }
            }

            if (!curr.equals(previousCell))
                drList = getAllEdgesFromNode(previousCell, unvisitedVertexList);
            else
                drList = getAllEdgesFromNode(curr, unvisitedVertexList);

            for (DistanceReturn dr : drList)
            {
                pq.add(new Edge(curr, dr.getEnd(), dr));
            }

            System.out.println("PQ:");
            pq.print();
            System.out.println();

            Edge root = pq.getRoot();
            //System.out.println("Root: "+root.u+"->"+root.w);
            //System.out.println("Comparing "+ids.get(root.u)+" and "+ids.get(root.w));
            if (!ids.get(root.u).equals(ids.get(root.w)))
            {
                edgePath.add(root);
                vertexPath.add(root.dr.end);
                locationPath.add(list.get(root.w));
                //System.out.println("Adding "+root.dr.end+" to coordinate path");
                //System.out.println("Adding "+list.get(root.w)+" to location path");

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

    public FindingPathReturn findPickingPath2(Hashtable<String, String> list, String which)
    {
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

        Hashtable<String, Boolean> visited = new Hashtable<>();
        Hashtable<String, Integer> vertexSectorIds = new Hashtable<>();
        for (String vertex : list.keySet())
        {
            visited.put(vertex, false);
            vertexSectorIds.put(vertex, getSectorOfCoordinate(vertex));
        }

        Sector[] sectorMap = mapVerticesToSectors(list.keySet(), vertexSectorIds);

        ArrayList<String> locationPath = new ArrayList<>();
        ArrayList<String> vertexPath = new ArrayList<>();
        ArrayList<Edge> edgePath = new ArrayList<>();

        vertexPath.add(startAndEnd);

        int sectorOfStartAndEnd = getSectorOfCoordinate(startAndEnd);
        vertexSectorIds.put(startAndEnd, sectorOfStartAndEnd);
        int[] sectorOrder = getSectorOrderGivenStart(sectorOfStartAndEnd);

        String curr = startAndEnd;
        String previousCell = startAndEnd;
        visited.put(curr, true);

        for (int i=0; i<sectorOrder.length; i++)
        {
            System.out.println("Going to all vertices in sector: "+sectorOrder[i]);
            while (sectorMap[sectorOrder[i]].verticesInSectorList.size() > 0)
            {
                //System.out.println("Curr: "+curr);
                //System.out.println("Previous Cell: "+previousCell);

                ArrayList<String> unvisitedVertexList = new ArrayList<>();
                for (String vertex : sectorMap[sectorOrder[i]].verticesInSectorList)
                {
                    if (!visited.get(vertex))
                    {
                        //System.out.println("Adding edge to "+vertex+" to pq");
                        unvisitedVertexList.add(vertex);
                    }
                }

                ArrayList<DistanceReturn> drList = getAllEdgesFromNode(previousCell, unvisitedVertexList);

                EdgePQ pq = new EdgePQ();
                for (DistanceReturn dr : drList)
                    pq.add(new Edge(curr, dr.getEnd(), dr));

                System.out.println("PQ:");
                pq.print();

                Edge root = pq.getRoot();
                //System.out.println("Root: "+root.u+"->"+root.w);
                //System.out.println("Comparing "+ids.get(root.u)+" and "+ids.get(root.w));

                edgePath.add(root);
                vertexPath.add(root.dr.end);
                locationPath.add(list.get(root.w));
                System.out.println("Adding "+root.w+" to path in sector: "+vertexSectorIds.get(root.w));

                curr = root.w;
                previousCell = root.dr.end;
                visited.put(curr, true);
                sectorMap[vertexSectorIds.get(curr)].verticesInSectorList.remove(curr);
            }
            System.out.println("Visited all vertices in sector: "+sectorOrder[i]);
        }
        System.out.println("Path Complete, returning to start/end");

        DistanceReturn dr;
        if (!curr.equals(previousCell))
            dr = findDistanceBetween(previousCell, startAndEnd);
        else
            dr = findDistanceBetween(curr, startAndEnd);
        edgePath.add(new Edge(curr, startAndEnd, dr.getDistance(), dr.getPath()));

        StringBuilder cellPath = new StringBuilder();
        for (Edge e : edgePath)
            cellPath.append(e.cellPath);

        return new FindingPathReturn(locationPath, vertexPath, cellPath.toString());
    }

    public FindingPathReturn findPickingPath3(Hashtable<String, String> list, String which)
    {
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

        Hashtable<String, Boolean> visited = new Hashtable<>();
        Hashtable<String, Integer> vertexSectorIds = new Hashtable<>();
        for (String vertex : list.keySet())
        {
            visited.put(vertex, false);
            vertexSectorIds.put(vertex, getSectorOfCoordinate(vertex));
        }

        Sector[] sectorMap = mapVerticesToSectors(list.keySet(), vertexSectorIds);

        ArrayList<String> locationPath = new ArrayList<>();
        ArrayList<String> vertexPath = new ArrayList<>();
        ArrayList<Edge> edgePath = new ArrayList<>();

        vertexPath.add(startAndEnd);

        int sectorOfStartAndEnd = getSectorOfCoordinate(startAndEnd);
        vertexSectorIds.put(startAndEnd, sectorOfStartAndEnd);
        int[] sectorOrder = getSectorOrderGivenStart(sectorOfStartAndEnd);

        String curr = startAndEnd;
        String previousCell = startAndEnd;
        for (int i=0; i<sectorOrder.length; i++)
        {
            //System.out.println("Going to all vertices in sector: "+sectorOrder[i]);

            if (sectorMap[sectorOrder[i]].verticesInSectorList.size() > 0)
            {
                String nearestVertexToNextSector = getNearestVertexToNextSector(i, sectorOrder, sectorMap[sectorOrder[i]].verticesInSectorList);
                System.out.println("Closest vertex to next sector: "+nearestVertexToNextSector);

                ArrayList<Edge> backwardsVertexPathInSector = new ArrayList<>();
                String lastSectorEnd = curr;
                String lastSectorEndEndCoords = previousCell;

                curr = nearestVertexToNextSector;
                previousCell = nearestVertexToNextSector;
                visited.put(curr, true);
                sectorMap[sectorOrder[i]].verticesInSectorList.remove(curr);

                while (sectorMap[sectorOrder[i]].verticesInSectorList.size() > 0)
                {
                    ArrayList<String> unvisitedVertexList = new ArrayList<>();
                    for (String vertex : sectorMap[sectorOrder[i]].verticesInSectorList)
                    {
                        if (!visited.get(vertex))
                        {
                            //System.out.println("Adding edge to "+vertex+" to pq");
                            unvisitedVertexList.add(vertex);
                        }
                    }

                    ArrayList<DistanceReturn> drList = getAllEdgesFromNode(previousCell, unvisitedVertexList);

                    EdgePQ pq = new EdgePQ();
                    for (DistanceReturn dr : drList)
                        pq.add(new Edge(curr, dr.getEnd(), dr));

                    //System.out.println("PQ:");
                    //pq.print();

                    Edge root = pq.getRoot();
                    //System.out.println("Root: "+root.u+"->"+root.w);
                    //System.out.println("Comparing "+ids.get(root.u)+" and "+ids.get(root.w));

                    backwardsVertexPathInSector.add(root);
                    //System.out.println("Adding "+root.u+" to backwards path in sector: "+sectorOrder[i]);

                    curr = root.w;
                    previousCell = root.dr.end;
                    visited.put(curr, true);
                    sectorMap[sectorOrder[i]].verticesInSectorList.remove(curr);
                }
                //System.out.println("Visited all vertices in sector: "+sectorOrder[i]);

                DistanceReturn dr = findDistanceBetween(lastSectorEndEndCoords, previousCell);
                Edge fromPreviousSector = new Edge(lastSectorEnd, curr, dr);
                edgePath.add(fromPreviousSector);
                vertexPath.add(fromPreviousSector.dr.end);
                locationPath.add(fromPreviousSector.w);
                //System.out.println("Adding "+fromPreviousSector.dr.end+" to connect previous sector");

                for (int j=backwardsVertexPathInSector.size()-1; j>-1; j--)
                {
                    edgePath.add(backwardsVertexPathInSector.get(j));
                    vertexPath.add(backwardsVertexPathInSector.get(j).u);
                    locationPath.add(list.get(backwardsVertexPathInSector.get(j).u));
                    //System.out.println("Adding "+backwardsVertexPathInSector.get(j).u+" to path in sector: "+i);
                }

                curr = nearestVertexToNextSector;
                previousCell = nearestVertexToNextSector;
            }
        }
        //System.out.println("Path Complete, returning to start/end");

        DistanceReturn dr = findDistanceBetween(previousCell, startAndEnd);
        edgePath.add(new Edge(curr, startAndEnd, dr.getDistance(), dr.getPath()));

        StringBuilder cellPath = new StringBuilder();
        for (Edge e : edgePath)
            cellPath.append(e.cellPath);

        return new FindingPathReturn(locationPath, vertexPath, cellPath.toString());
    }

    private ArrayList<DistanceReturn> getAllEdgesFromNode(String start1, ArrayList<String> endList)
    {
        ArrayList<DistanceReturn> drList = new ArrayList<>();

        //Making sure start is on a valid vertex
        Coords startCoords = new Coords(start1);

        String start = start1;
        if (grid.getRNode(startCoords.getX(), startCoords.getY()).isIsle())
            start = findNearestNonIsleCell(start1, startCoords.getX() ,startCoords.getY());

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

                        previous.get(currEdge.w).append(previous.get(currEdge.u)).append(" ").append(currEdge.u);
                    }
                }
                currEdge = currEdge.next;
            }
        }

        for (String end : endList)
        {
            String[] hmm = end.split(",");
            if (hmm.length > 1)
            {
                //Regular coords: 99,99
                Coords endCoords = new Coords(end);

                String newEnd = null;
                if (grid.getRNode(endCoords.getX(), endCoords.getY()).isIsle())
                    newEnd = findNearestNonIsleCell(start1, endCoords.getX() ,endCoords.getY());
                try
                {
                    StringBuilder path = previous.get(newEnd).append(" ").append(newEnd);
                    String[] pathArr = path.toString().split(" ");
                    drList.add(new DistanceReturn(pathArr.length-2, path.toString(), end));
                }
                catch (NullPointerException ignored) {}
            }
            else
            {
                //Irregular coords: Mens' Jeans
                Isle isle = grid.getIsleWithUnknownIG(end);
                if (isle == null)
                    System.out.println("Isle not found");

                ArrayList<String> listOfEnds = new ArrayList<>();
                Isle.IsleCellList.IsleCellNode curr = isle.getIsleCellList().getFirst();
                while (curr != null)
                {
                    //System.out.println("Finding path for: "+curr.getrNode().getX()+","+curr.getrNode().getY());
                    listOfEnds.add(curr.getrNode().getX()+","+curr.getrNode().getY());
                    //System.out.println("Distance: "+listOfPaths.get(listOfPaths.size()-1).getDistance());
                    curr = curr.getNext();
                }

                ArrayList<DistanceReturn> listOfPaths = new ArrayList<>();
                for (String irregularEnd : listOfEnds)
                {
                    Coords endCoords = new Coords(irregularEnd);

                    String newEnd = null;
                    if (grid.getRNode(endCoords.getX(), endCoords.getY()).isIsle())
                        newEnd = findNearestNonIsleCell(start1, endCoords.getX() ,endCoords.getY());
                    try
                    {
                        StringBuilder path = previous.get(newEnd).append(" ").append(newEnd);
                        String[] pathArr = path.toString().split(" ");
                        listOfPaths.add(new DistanceReturn(pathArr.length-2, path.toString(), irregularEnd));
                    }
                    catch (NullPointerException ignored) {}
                }

                listOfPaths.sort(new DistanceReturn.DistanceComparator());

                drList.add(listOfPaths.get(0));
            }
        }

        return drList;
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

        Coords startCoords = new Coords(start1);

        String start = start1;
        if (grid.getRNode(startCoords.getX(), startCoords.getY()).isIsle())
            start = findNearestNonIsleCell(start1, startCoords.getX() ,startCoords.getY());

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

                        previous.get(currEdge.w).append(previous.get(currEdge.u)).append(" ").append(currEdge.u);
                    }
                }
                currEdge = currEdge.next;
            }
        }

        ArrayList<DistanceReturn> listOfPaths = new ArrayList<>();
        for (String end : listOfEnds)
        {
            Coords endCoords = new Coords(end);

            String newEnd = null;
            if (grid.getRNode(endCoords.getX(), endCoords.getY()).isIsle())
                newEnd = findNearestNonIsleCell(start1, endCoords.getX() ,endCoords.getY());
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

    private Sector[] mapVerticesToSectors(Set<String> set, Hashtable<String, Integer> vertexSectorIds)
    {
        Sector[] sectorMap = new Sector[4];
        for (int i=0; i<sectorMap.length; i++)
        {
            sectorMap[i] = new Sector();
        }

        for (String coords : set)
        {
            sectorMap[vertexSectorIds.get(coords)].addVertex(coords);
        }

        return sectorMap;
    }

    private Integer getSectorOfCoordinate(String coords1)
    {
        Coords coords = new Coords(coords1);
        int x = coords.getX();
        int y = coords.getY();

        if (x < grid.colSize/2 && y < grid.rowSize/2)
            return 0;
        else if (x < grid.colSize && y < grid.rowSize/2)
            return 1;
        else if (x < grid.colSize/2 && y < grid.rowSize)
            return 2;
        else
            return 3;
    }

    private int[] getSectorOrderGivenStart(int startSector)
    {
        int[] order = new int[4];

        order[0] = startSector;
        if (startSector == 3)
        {
            order[1] = 1;
            order[2] = 0;
            order[3] = 2;
        }

        return order;
    }

    private String getNearestVertexToNextSector(int sectorOrderCounter, int[] sectorOrder, ArrayList<String> listOfVerticesInCurrSector)
    {
        int currentSector = sectorOrder[sectorOrderCounter];
        int nextSector;
        try
        {
            nextSector = sectorOrder[sectorOrderCounter+1];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            nextSector = sectorOrder[0];
        }
        System.out.println("Current Sector: "+currentSector);
        System.out.println("Next Sector: "+nextSector);

        String closestVertex = listOfVerticesInCurrSector.get(0);
        if (currentSector == 0 && nextSector == 2)
        {
            int y = grid.rowSize/2;
            System.out.println("Target Coord: y = "+y);
            for (String vertex : listOfVerticesInCurrSector)
            {
                int currY = new Coords(vertex).getY();

                if (y - currY < (y - new Coords(closestVertex).getY()))
                    closestVertex = vertex;
            }
        }
        else if (currentSector == 2 && nextSector == 3)
        {
            int x = grid.colSize/2;
            System.out.println("Target Coord: x = "+x);
            for (String vertex : listOfVerticesInCurrSector)
            {
                int currX = new Coords(vertex).getX();

                if (x - currX < (x - new Coords(closestVertex).getX()))
                    closestVertex = vertex;
            }
        }
        else if (currentSector == 3 && nextSector == 1)
        {
            int y = grid.rowSize/2;
            System.out.println("Target Coord: y = "+y);
            for (String vertex : listOfVerticesInCurrSector)
            {
                int currY = new Coords(vertex).getY();

                if (currY - y < (new Coords(closestVertex).getY() - y))
                    closestVertex = vertex;
            }
        }
        else if (currentSector == 1 && nextSector == 0)
        {
            int x = grid.colSize/2;
            System.out.println("Target Coord: x = "+x);
            for (String vertex : listOfVerticesInCurrSector)
            {
                int currX = new Coords(vertex).getX();

                if (currX - x < (new Coords(closestVertex).getX() - x))
                    closestVertex = vertex;
            }
        }

        return closestVertex;
    }

    protected static class Edge
    {
        protected String u;
        protected String w;
        protected int length;
        protected String cellPath;
        protected DistanceReturn dr;
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

        public Edge getNext()
        {
            return next;
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
                return Integer.compare(dr1.distance, dr2.distance);
            }
        }
    }

    public static class FindingPathReturn
    {
        private final ArrayList<String> locationPath;
        private final ArrayList<String> vertexPath;
        private final String cellPath;

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

    private static class Sector
    {
        private final ArrayList<String> verticesInSectorList;

        private Sector()
        {
            verticesInSectorList = new ArrayList<>();
        }

        private void addVertex(String vertex)
        {
            verticesInSectorList.add(vertex);
        }
    }
}

