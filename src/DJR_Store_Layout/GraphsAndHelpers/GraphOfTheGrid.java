/**
 * Graph class for DJR_Store_Layout
 * Creates a graph of all non-isle, non-null cells on the grid
 * Here is where the quickest path between points on the grid and for a list of locations is found
 * @author David Roberts
 */

package DJR_Store_Layout.GraphsAndHelpers;

import DJR_Store_Layout.GridData.Aisle;
import DJR_Store_Layout.GridData.CellList;
import DJR_Store_Layout.GridData.GridData3;
import DJR_Store_Layout.GridData.RNode;
import DJR_Store_Layout.HelperClasses.Coords;

import java.util.*;

public class GraphOfTheGrid
{
    /**
     * Hashtable of edges
     * Put in string representing the coordinates of a cell,
     * getting a linked list of all cells that connect to the inputted cell.
     */
    public Hashtable<String, Edge> graph;

    private int numberOfVertices;
    private int numberOfEdges;

    private final GridData3 grid;
    /**
     * The start and endpoint for finding picking path
     * Chances based on what kind of pick (Standard, OPU, ...)
     */
    private String startAndEnd;

    /**
     * Constructor builds a graph based on
     * @param g GridData3
     */
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
                if (!g.getRNode(i, j).isNulled() && !g.getRNode(i, j).isAisle())
                {
                    numberOfVertices++;
                    try
                    {
                        //Add edge to west
                        if (!g.getRNode(i-1, j).isNulled() && !g.getRNode(i-1, j).isAisle())
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
                        if (!g.getRNode(i, j-1).isNulled() && !g.getRNode(i, j-1).isAisle())
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
                        if (!g.getRNode(i+1, j).isNulled() && !g.getRNode(i+1, j).isAisle())
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
                        if (!g.getRNode(i, j+1).isNulled() && !g.getRNode(i, j+1).isAisle())
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

    /**
     * Dijkstra's algorithm for finding shortest path between two points
     * @param start1 starting point
     * @param end1 ending point
     * @return DistanceReturn with length of path, coordinate path in string form, and ending point
     */
    public DistanceReturn findDistanceBetween(String start1, String end1)
    {
        //Making sure start is on a valid vertex
        Coords startCoords = new Coords(start1);

        String start = start1;
        if (grid.getRNode(startCoords.getX(), startCoords.getY()).isAisle())
            start = findNearestNonIsleCell(start1, startCoords.getX() ,startCoords.getY());

        //Making sure end is on a valid vertex
        Coords endCoords = new Coords(end1);

        String end = end1;
        if (grid.getRNode(endCoords.getX(), endCoords.getY()).isAisle())
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

    /**
     * NOT BEING USED
     * Standard nearest neighbor algorithm of finding quickest route for a list of locations
     * @param list of locations
     * @param which type of pick (Standard, OPU, ...)
     * @return FindingPathReturn with list of locations in correct order, list of coordinate points in order, and cell path in string form

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
     */

    /**
     * Primary method
     * This algorithm is also nearest neighbor, however it splits the store into 4 sectors in a specific order based on the starting location and
     * every location in the current sector (starting in the sector with the start/end point) has to be visited before moving to the next sector
     * Thus, the picker starts with locations close to them, and ends on the same side of the store where they started
     * @param list of locations
     * @param which type of pick (Standard, OPU, ...)
     * @return FindingPathReturn with list of locations in correct order, list of coordinate points in order, and cell path in string form
     */
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
            x = grid.getStandardStartEndNode().getX();
            y = grid.getStandardStartEndNode().getY();
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
                    loc = grid.getRNode(coords.getX(), coords.getY()).getAisle().getAisleID();
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

    /**
     * Gets nearest neighboring location from current node, used in findPickingPath2
     * Sets up Dijkstra's and inputs list of unvisited neighbors at the end of Dijkstra's
     * @param startingLocation string: D32(1) 1-1-1
     * @param start1 coordinates
     * @param endList list of unvisited neighbors
     * @return nearest edge of unvisited neighbors in sector
     */
    private Edge getClosestNeighborFromNode(String startingLocation, String start1, ArrayList<String> endList)
    {
        //Making sure start is on a valid vertex
        Coords startCoords = new Coords(start1);

        String start = start1;
        if (grid.getRNode(startCoords.getX(), startCoords.getY()).isAisle())
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
                if (grid.getRNode(endCoords.getX(), endCoords.getY()).isAisle())
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
                Aisle isle = grid.getAisleWithUnknownIG(end);
                System.out.println("Irregular ending for isle: "+end);
                if (isle != null)
                {
                    ArrayList<String> listOfEnds = new ArrayList<>();
                    CellList.CellNode curr = isle.getAisleCellList().getFirst();
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
                        if (grid.getRNode(endCoords.getX(), endCoords.getY()).isAisle())
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

    /**
     * If given coordinates are an isle, they are not part of the graph
     * This method finds the nearest cell in the graph to use so no exceptions are found.
     * @param old coordinates
     * @param x coordinate
     * @param y coordinate
     * @return nearest non-isle, non-null cell, only 1 cell away from old
     */
    private String findNearestNonIsleCell(String old, int x, int y)
    {
        String newCoords = old;

        while (newCoords.compareTo(old) == 0)
        {
            try
            {
                RNode rnode = grid.getRNode(x-1, y);
                if (!rnode.isNulled() && !rnode.isAisle())
                {
                    newCoords = (x-1)+","+y;
                    break;
                }
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
            try
            {
                RNode rnode = grid.getRNode(x, y+1);
                if (!rnode.isNulled() && !rnode.isAisle())
                {
                    newCoords = x+","+(y+1);
                    break;
                }
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
            try
            {
                RNode rnode = grid.getRNode(x+1, y);
                if (!rnode.isNulled() && !rnode.isAisle())
                {
                    newCoords = (x+1)+","+y;
                    break;
                }
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
            try
            {
                RNode rnode = grid.getRNode(x, y-1);
                if (!rnode.isNulled() && !rnode.isAisle())
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

    /**
     * FOR TESTING PURPOSES ONLY
     * Finds nearest cell of area isle (Ex: Clothes), used in IsleLayoutController
     * Uses Dijkstra's and inputs a list of possible ending cells at the end
     * @param start1 coordinates
     * @param isleID isle of interest
     * @return sorted arrayList of all possible connections to that isle, gets first element in list
     */
    public ArrayList<DistanceReturn> findClosestCellAndComputeDistanceIfIsleShapeIsArea(String start1, String isleID)
    {
        Aisle isle = grid.getAisleWithUnknownIG(isleID);
        if (isle == null)
            System.out.println("Isle not found");

        Coords startCoords = new Coords(start1);

        String start = start1;
        if (grid.getRNode(startCoords.getX(), startCoords.getY()).isAisle())
            start = findNearestNonIsleCell(start1, startCoords.getX() ,startCoords.getY());

        ArrayList<String> listOfEnds = new ArrayList<>();

        CellList.CellNode curr = isle.getAisleCellList().getFirst();
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
            if (grid.getRNode(endCoords.getX(), endCoords.getY()).isAisle())
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

    /**
     * Sets up array of sectors that contain lists of each location at that sector
     * @param set of all locations
     * @param vertexSectorIds table that maps location to its proper sector
     * @return array of sectors in correct order with list of each location in each sector
     */
    private Sector[] mapVerticesToSectors(Set<String> set, Hashtable<String, Integer> vertexSectorIds)
    {
        Sector[] sectorMap = new Sector[4];
        for (int i=0; i<4; i++)
            sectorMap[i] = new Sector();

        set.forEach(coords -> sectorMap[vertexSectorIds.get(coords)].addVertex(coords));

        return sectorMap;
    }

    /**
     * @param coords1 coordinates of interest
     * @return which sector it belongs to
     */
    private Integer getSectorOfCoordinate(String coords1)
    {
        Coords coords;
        try
        {
            coords = new Coords(coords1);
        }
        catch (NumberFormatException e)
        {
            System.out.println("Getting sector for irregular coords: "+coords1);
            coords = grid.getAisleWithUnknownIG(coords1).getAisleCellList().getFirst().getrNode().getCoords();
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

    /**
     * @param startSector starting sector
     * @return array of ints that represent the sector order (0, 2, 3, 1) ...
     */
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

    /**
     * FOR TESTING PURPOSES ONLY
     * There was a case where the cell path would be reversed so this was used to reverse it
     * Cell path is only used for testing to show path of picking on UI
     * @param originalPath path to be reversed
     * @return path in correct order
     */
    private String reversePath(String originalPath)
    {
        StringBuilder newPath = new StringBuilder();

        String[] originalPathArr = originalPath.split(" ");
        for (int i=originalPathArr.length-1; i>-1; i--)
            newPath.append(" ").append(originalPathArr[i]);

        return newPath.toString();
    }
}