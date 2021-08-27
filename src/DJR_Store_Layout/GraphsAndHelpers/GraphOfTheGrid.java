package DJR_Store_Layout.GraphsAndHelpers;

import DJR_Store_Layout.GridData.CellList;
import DJR_Store_Layout.GridData.GridData3;
import DJR_Store_Layout.GridData.RNode;
import DJR_Store_Layout.HelperClasses.Coords;
import DJR_Store_Layout.GridData.Isle;

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

        int cols = g.getColSize();
        int rows = g.getRowSize();

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
                                while (curr != null && curr.getNext() != null)
                                    curr = curr.getNext();

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
                                while (curr != null && curr.getNext() != null)
                                    curr = curr.getNext();

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
                                while (curr != null && curr.getNext() != null)
                                    curr = curr.getNext();

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
                                while (curr != null && curr.getNext() != null)
                                    curr = curr.getNext();

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

        graph.keySet().forEach(s ->
        {
            distance.put(s, Integer.MAX_VALUE);
            previous.put(s, new StringBuilder(""));
            visited.put(s, false);
        });

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
                if (!visited.get(currEdge.getW()))
                {
                    if (!q.contains(currEdge.getW()))
                        q.add(currEdge.getW());

                    int dist = distance.get(currEdge.getU()) + currEdge.getLength();
                    if (dist < distance.get(currEdge.getW()))
                    {
                        distance.put(currEdge.getW(), dist);

                        previous.get(currEdge.getW()).append(previous.get(currEdge.getU())).append(" ").append(currEdge.getU());
                    }
                }
                currEdge = currEdge.getNext();
            }
        }

        StringBuilder path = previous.get(end).append(" ").append(end);
        String[] pathArr = path.toString().split(" ");
        return new DistanceReturn(pathArr.length-2, path.toString(), end1);
    }

    public FindingPathReturn findPickingPath(Hashtable<String, String> list, String which)
    {
        int x;
        int y;
        if (which.compareTo("OPU Regular") == 0)
        {
            x = grid.getRegOpuStartEndNode().getX();
            y = grid.getRegOpuStartEndNode().getY();
        }
        else
        {
            x = grid.getRegStartEndNode().getX();
            y = grid.getRegStartEndNode().getY();
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
            EdgePQ pq = new EdgePQ(numberOfEdges);
            for (String vertex : set)
            {
                if (!visited.get(vertex))
                {
                    System.out.println("Adding edge to "+vertex+" to pq");
                    unvisitedVertexList.add(vertex);
                }
            }

            Edge root = getClosestNeighborFromNode(curr, previousCell, unvisitedVertexList);
            //System.out.println("Root: "+root.u+"->"+root.w);
            //System.out.println("Comparing "+ids.get(root.u)+" and "+ids.get(root.w));
            if (!ids.get(root.getU()).equals(ids.get(root.getW())))
            {
                edgePath.add(root);
                vertexPath.add(root.getDistanceReturn().getEnd());
                locationPath.add(list.get(root.getW()));
                //System.out.println("Adding "+root.dr.end+" to coordinate path");
                //System.out.println("Adding "+list.get(root.w)+" to location path");

                int id = ids.get(root.getU());
                for (String vertex : set)
                {
                    if (ids.get(vertex) == id)
                        ids.put(vertex, ids.get(root.getW()));
                }
                ids.put(root.getU(), ids.get(root.getW()));

                curr = root.getW();
                previousCell = root.getDistanceReturn().getEnd();
                visited.put(root.getW(), true);
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
            cellPath.append(e.getCellPath());
        }

        return new FindingPathReturn(locationPath, vertexPath, cellPath.toString());
    }

    public FindingPathReturn findPickingPath2(Hashtable<String, String> list, String which)
    {
        int x;
        int y;
        if (which.compareTo("OPU Regular") == 0)
        {
            x = grid.getRegOpuStartEndNode().getX();
            y = grid.getRegOpuStartEndNode().getY();
        }
        else
        {
            x = grid.getRegStartEndNode().getX();
            y = grid.getRegStartEndNode().getY();
        }
        startAndEnd = x+","+y;

        Hashtable<String, Boolean> visited = new Hashtable<>();
        Hashtable<String, Integer> vertexSectorIds = new Hashtable<>();
        list.keySet().forEach(vertex ->
        {
            visited.put(vertex, false);
            vertexSectorIds.put(vertex, getSectorOfCoordinate(vertex));
        });

        System.out.println("Sector List:");
        list.keySet().forEach(vertex -> System.out.println(vertex+" in sector: "+vertexSectorIds.get(vertex)));

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
            while (sectorMap[sectorOrder[i]].getVerticesInSectorList().size() > 0)
            {
                System.out.println("Curr: "+curr);
                System.out.println("Previous Cell: "+previousCell);

                ArrayList<String> unvisitedVertexList = new ArrayList<>();
                for (String vertex : sectorMap[sectorOrder[i]].getVerticesInSectorList())
                {
                    if (!visited.get(vertex))
                    {
                        System.out.println("Adding edge to "+vertex+" to pq");
                        unvisitedVertexList.add(vertex);
                    }
                }

                Edge closestNeighbor = getClosestNeighborFromNode(curr, previousCell, unvisitedVertexList);
                System.out.println("Closest: "+closestNeighbor.getU()+"->"+closestNeighbor.getW());

                edgePath.add(closestNeighbor);
                vertexPath.add(closestNeighbor.getDistanceReturn().getEnd());

                String loc = list.get(closestNeighbor.getW());
                if (loc == null)
                {
                    Coords coords = new Coords(closestNeighbor.getW());
                    loc = grid.getRNode(coords.getX(), coords.getY()).getIsle().getIsleID();
                    locationPath.add(list.get(loc));
                }
                else
                    locationPath.add(loc);

                System.out.println("Locatns: "+list.get(closestNeighbor.getU())+"->"+loc);

                curr = loc;
                previousCell = closestNeighbor.getDistanceReturn().getEnd();
                visited.put(curr, true);
                try
                {
                    sectorMap[vertexSectorIds.get(curr)].getVerticesInSectorList().remove(curr);
                }
                catch (NullPointerException e)
                {
                    sectorMap[vertexSectorIds.get(closestNeighbor.getW())].getVerticesInSectorList().remove(closestNeighbor.getW());
                }
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
        edgePath.forEach(e -> cellPath.append(e.getCellPath()));

        return new FindingPathReturn(locationPath, vertexPath, cellPath.toString());
    }

    public FindingPathReturn findPickingPath3(Hashtable<String, String> list, String which, boolean testing)
    {
        int x;
        int y;
        if (which.compareTo("OPU Regular") == 0)
        {
            x = grid.getRegOpuStartEndNode().getX();
            y = grid.getRegOpuStartEndNode().getY();
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

            if (sectorMap[sectorOrder[i]].getVerticesInSectorList().size() > 0)
            {
                String nearestVertexToNextSector = getNearestVertexToNextSector(i, sectorOrder,
                        sectorMap[sectorOrder[i]].getVerticesInSectorList());
                //System.out.println("Closest vertex to next sector: "+nearestVertexToNextSector);

                ArrayList<Edge> backwardsVertexPathInSector = new ArrayList<>();
                String lastSectorEnd = curr;
                String lastSectorEndEndCoords = previousCell;

                curr = nearestVertexToNextSector;
                previousCell = nearestVertexToNextSector;
                visited.put(curr, true);
                sectorMap[sectorOrder[i]].getVerticesInSectorList().remove(curr);

                while (sectorMap[sectorOrder[i]].getVerticesInSectorList().size() > 0)
                {
                    ArrayList<String> unvisitedVertexList = new ArrayList<>();
                    for (String vertex : sectorMap[sectorOrder[i]].getVerticesInSectorList())
                    {
                        if (!visited.get(vertex))
                        {
                            //System.out.println("Adding edge to "+vertex+" to pq");
                            unvisitedVertexList.add(vertex);
                        }
                    }

                    Edge closestNeighbor = getClosestNeighborFromNode(curr, previousCell, unvisitedVertexList);
                    //System.out.println("Root: "+root.u+"->"+root.w);
                    //System.out.println("Comparing "+ids.get(root.u)+" and "+ids.get(root.w));

                    backwardsVertexPathInSector.add(closestNeighbor);
                    //System.out.println("Adding "+root.u+" to backwards path in sector: "+sectorOrder[i]);

                    curr = closestNeighbor.getW();
                    previousCell = closestNeighbor.getDistanceReturn().getEnd();
                    visited.put(curr, true);
                    sectorMap[sectorOrder[i]].getVerticesInSectorList().remove(curr);
                }
                //System.out.println("Visited all vertices in sector: "+sectorOrder[i]);

                DistanceReturn dr = findDistanceBetween(lastSectorEndEndCoords, previousCell);
                Edge fromPreviousSector = new Edge(lastSectorEnd, curr, dr);
                edgePath.add(fromPreviousSector);
                vertexPath.add(fromPreviousSector.getDistanceReturn().getEnd());
                locationPath.add(fromPreviousSector.getW());
                //System.out.println("Adding "+fromPreviousSector.dr.end+" to connect previous sector");

                for (int j=backwardsVertexPathInSector.size()-1; j>-1; j--)
                {
                    edgePath.add(backwardsVertexPathInSector.get(j));
                    vertexPath.add(backwardsVertexPathInSector.get(j).getU());
                    locationPath.add(list.get(backwardsVertexPathInSector.get(j).getU()));
                    //System.out.println("Adding "+backwardsVertexPathInSector.get(j).u+" to path in sector: "+i);
                }

                curr = nearestVertexToNextSector;
                previousCell = nearestVertexToNextSector;
            }
        }
        //System.out.println("Path Complete, returning to start/end");

        DistanceReturn dr = findDistanceBetween(previousCell, startAndEnd);
        edgePath.add(new Edge(curr, startAndEnd, dr.getDistance(), dr.getPath()));

        if (testing)
        {
            StringBuilder cellPath = new StringBuilder();
            for (int i=0; i<edgePath.size(); i++)
            {
                try
                {
                    if (edgePath.get(i).getW().equals(edgePath.get(i-1).getW()))
                        cellPath.append(reversePath(edgePath.get(i).getCellPath()));
                    else
                        cellPath.append(edgePath.get(i).getCellPath());
                }
                catch (IndexOutOfBoundsException e)
                {
                    cellPath.append(edgePath.get(i).getCellPath());
                }
            }

            return new FindingPathReturn(locationPath, vertexPath, cellPath.toString());
        }
        else
            return new FindingPathReturn(locationPath, vertexPath, null);
    }

    private Edge getClosestNeighborFromNode(String startingLocation, String start1, ArrayList<String> endList)
    {
        //Making sure start is on a valid vertex
        Coords startCoords = new Coords(start1);

        String start = start1;
        if (grid.getRNode(startCoords.getX(), startCoords.getY()).isIsle())
            start = findNearestNonIsleCell(start1, startCoords.getX() ,startCoords.getY());

        Hashtable<String, Integer> distance = new Hashtable<>();
        Hashtable<String, StringBuilder> previous = new Hashtable<>();
        Hashtable<String, Boolean> visited = new Hashtable<>();

        graph.keySet().forEach(s ->
        {
            distance.put(s, Integer.MAX_VALUE);
            previous.put(s, new StringBuilder(""));
            visited.put(s, false);
        });

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
                if (!visited.get(currEdge.getW()))
                {
                    if (!q.contains(currEdge.getW()))
                        q.add(currEdge.getW());

                    int dist = distance.get(currEdge.getU()) + currEdge.getLength();
                    if (dist < distance.get(currEdge.getW()))
                    {
                        distance.put(currEdge.getW(), dist);

                        previous.get(currEdge.getW()).append(previous.get(currEdge.getU())).append(" ").append(currEdge.getU());
                    }
                }
                currEdge = currEdge.getNext();
            }
        }

        EdgePQ pq = new EdgePQ(numberOfEdges);
        endList.forEach(end ->
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
                    DistanceReturn dr = new DistanceReturn(pathArr.length-2, path.toString(), end);
                    pq.add(new Edge(startingLocation, dr.getEnd(), dr));
                }
                catch (NullPointerException ignored) {}
            }
            else
            {
                //Irregular coords: Mens' Jeans
                Isle isle = grid.getIsleWithUnknownIG(end);
                System.out.println("Irregular ending for isle: "+end);
                if (isle != null)
                {
                    ArrayList<String> listOfEnds = new ArrayList<>();
                    CellList.CellNode curr = isle.getIsleCellList().getFirst();
                    while (curr != null)
                    {
                        //System.out.println("Finding path for: "+curr.getrNode().getX()+","+curr.getrNode().getY());
                        if (!findNearestNonIsleCell(curr.getrNode().getX()+","+curr.getrNode().getY(), curr.getrNode().getX(), curr.getrNode().getY())
                                .equals(curr.getrNode().getX()+","+curr.getrNode().getY()))
                        {
                            System.out.println("Adding cell "+curr.getrNode().getX()+","+curr.getrNode().getY()+" on edge of isle");
                            listOfEnds.add(curr.getrNode().getX()+","+curr.getrNode().getY());
                        }
                        //System.out.println("Distance: "+listOfPaths.get(listOfPaths.size()-1).getDistance());
                        curr = curr.getNext();
                    }

                    EdgePQ possiblePathsPQ = new EdgePQ(numberOfEdges);
                    listOfEnds.forEach(irregularEnd ->
                    {
                        Coords endCoords = new Coords(irregularEnd);

                        String newEnd = null;
                        if (grid.getRNode(endCoords.getX(), endCoords.getY()).isIsle())
                            newEnd = findNearestNonIsleCell(start1, endCoords.getX() ,endCoords.getY());
                        try
                        {
                            StringBuilder path = previous.get(newEnd).append(" ").append(newEnd);
                            String[] pathArr = path.toString().split(" ");
                            DistanceReturn dr = new DistanceReturn(pathArr.length-2, path.toString(), irregularEnd);
                            possiblePathsPQ.add(new Edge(startingLocation, dr.getEnd(), dr));
                        }
                        catch (NullPointerException ignored) {}
                    });

                    pq.add(possiblePathsPQ.getRoot());
                }
                else
                    System.out.println("Isle not found");
            }
        });

        return pq.getRoot();
    }

    private String findNearestNonIsleCell(String old, int x, int y)
    {
        String newCoords = old;

        while (newCoords.compareTo(old) == 0)
        {
            try
            {
                RNode rnode = grid.getRNode(x-1, y);
                if (!rnode.isNulled() && !rnode.isIsle())
                {
                    newCoords = (x-1)+","+y;
                    break;
                }
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
            try
            {
                RNode rnode = grid.getRNode(x, y+1);
                if (!rnode.isNulled() && !rnode.isIsle())
                {
                    newCoords = x+","+(y+1);
                    break;
                }
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
            try
            {
                RNode rnode = grid.getRNode(x+1, y);
                if (!rnode.isNulled() && !rnode.isIsle())
                {
                    newCoords = (x+1)+","+y;
                    break;
                }
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
            try
            {
                RNode rnode = grid.getRNode(x, y-1);
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

        CellList.CellNode curr = isle.getIsleCellList().getFirst();
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

        graph.keySet().forEach(s ->
        {
            distance.put(s, Integer.MAX_VALUE);
            previous.put(s, new StringBuilder(""));
            visited.put(s, false);
        });

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
                if (!visited.get(currEdge.getW()))
                {
                    if (!q.contains(currEdge.getW()))
                        q.add(currEdge.getW());

                    int dist = distance.get(currEdge.getU()) + currEdge.getLength();
                    if (dist < distance.get(currEdge.getW()))
                    {
                        distance.put(currEdge.getW(), dist);

                        previous.get(currEdge.getW()).append(previous.get(currEdge.getU())).append(" ").append(currEdge.getU());
                    }
                }
                currEdge = currEdge.getNext();
            }
        }

        ArrayList<DistanceReturn> listOfPaths = new ArrayList<>();
        listOfEnds.forEach(end ->
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
        });

        listOfPaths.sort(new DistanceReturn.DistanceComparator());

        return listOfPaths;
    }

    private Sector[] mapVerticesToSectors(Set<String> set, Hashtable<String, Integer> vertexSectorIds)
    {
        Sector[] sectorMap = new Sector[4];
        for (int i=0; i<4; i++)
            sectorMap[i] = new Sector();

        set.forEach(coords -> sectorMap[vertexSectorIds.get(coords)].addVertex(coords));

        return sectorMap;
    }

    private Integer getSectorOfCoordinate(String coords1)
    {
        Coords coords;
        try
        {
            coords = new Coords(coords1);
        }
        catch (NumberFormatException e)
        {
            coords = grid.getIsleWithUnknownIG(coords1).getIsleCellList().getFirst().getrNode().getCoords();
            System.out.println("Irregular coords for getting sector ("+coords1+"). Coords to use: "+coords.toString());
        }

        int x = coords.getX();
        int y = coords.getY();

        if (x < grid.getColSize()/2 && y < grid.getRowSize()/2)
            return 0;
        else if (x < grid.getColSize() && y < grid.getRowSize()/2)
            return 1;
        else if (x < grid.getColSize()/2 && y < grid.getRowSize())
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
        else if (startSector == 0)
        {
            order[1] = 2;
            order[2] = 3;
            order[3] = 1;
        }
        else if (startSector == 1)
        {
            order[1] = 0;
            order[2] = 2;
            order[3] = 3;
        }
        else //startSector == 2
        {
            order[1] = 3;
            order[2] = 1;
            order[3] = 0;
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
        //System.out.println("Current Sector: "+currentSector);
        //System.out.println("Next Sector: "+nextSector);

        String closestVertex = listOfVerticesInCurrSector.get(0);
        if (currentSector == 0 && nextSector == 2)
        {
            int y = grid.getRowSize()/2;
            //System.out.println("Target Coord: y = "+y);
            for (String vertex : listOfVerticesInCurrSector)
            {
                int currY = new Coords(vertex).getY();

                if (y - currY < (y - new Coords(closestVertex).getY()))
                    closestVertex = vertex;
            }
        }
        else if (currentSector == 2 && nextSector == 3)
        {
            int x = grid.getColSize()/2;
            //System.out.println("Target Coord: x = "+x);
            for (String vertex : listOfVerticesInCurrSector)
            {
                int currX = new Coords(vertex).getX();

                if (x - currX < (x - new Coords(closestVertex).getX()))
                    closestVertex = vertex;
            }
        }
        else if (currentSector == 3 && nextSector == 1)
        {
            int y = grid.getRowSize()/2;
            //System.out.println("Target Coord: y = "+y);
            for (String vertex : listOfVerticesInCurrSector)
            {
                int currY = new Coords(vertex).getY();

                if (currY - y < (new Coords(closestVertex).getY() - y))
                    closestVertex = vertex;
            }
        }
        else if (currentSector == 1 && nextSector == 0)
        {
            int x = grid.getColSize()/2;
            //System.out.println("Target Coord: x = "+x);
            for (String vertex : listOfVerticesInCurrSector)
            {
                int currX = new Coords(vertex).getX();

                if (currX - x < (new Coords(closestVertex).getX() - x))
                    closestVertex = vertex;
            }
        }

        return closestVertex;
    }

    private String reversePath(String originalPath)
    {
        StringBuilder newPath = new StringBuilder();

        String[] originalPathArr = originalPath.split(" ");
        for (int i=originalPathArr.length-1; i>-1; i--)
            newPath.append(" ").append(originalPathArr[i]);

        return newPath.toString();
    }
}