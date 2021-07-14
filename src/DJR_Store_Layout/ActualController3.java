/**
 * ActualController3 class for project DJR_Store_Layout
 * Controls store layout and all accompanying functionality
 * @author David Roberts
 */

package DJR_Store_Layout;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class ActualController3 {
    /**
     * Launching variables
     */
    private final Parent parent;
    private Stage stage;
    private final Scene scene;

    /**
     * FXML variables
     */
    public HBox hboxWithThePluses, hboxWithTheCells;
    public MenuItem resize, back, saveLayout, testLocation, testPath, testPickingPath, setupInfoMenu, setupOPUstartEnd, setupRegularPickStartEnd;
    public Menu file, m1, m2, m3, m4, m5, m6;
    public MenuBar menuBar;
    public Pane topPthatHelpsPluses, botPthatHelpsPluses, leftPthatHelpsPluses, rightPthatHelpsPluses, topPthatHelpsCells, botPthatHelpsCells, leftPthatHelpsCells, rightPthatHelpsCells;
    public VBox theV;
    public StackPane sP;
    public ContextMenu rightClick, rightClick2, rightClick3;

    /**
     * Variables involved with backing data structures and control
     */
    private GridData3 g;
    private int floors, length, width;
    private final int cols, rows;
    private float cellSizeInFeet;
    private double sX, sY, xRemainderSize1, yRemainderSize1, mouseX, mouseY;
    private final double finalSizeOfCells;
    private boolean highlighting, moving, showingSetupInfoMenu, showingSetOPUstartEnd, showingSetRegStartEnd;
    private GridData3.RNode editGroupNode;
    private Isle isleToMove;
    private SetupIsleInfoController setupInfoStuff;
    private SetupPickingPath setupPath;
    private GraphOfTheGrid graph;

    /**
     * Basic Constructor
     * Calculates dimensions for displaying of grid when creating new layout
     *
     * @param f floors
     * @param l length of floor
     * @param w width of floor
     * @param x screen x dimension
     * @param y scrren y dimension
     */
    public ActualController3(int f, int l, int w, double x, double y)
    {
        floors = f;
        length = l;
        width = w;
        float ratio = (float) l/w;
        sX = x;
        sY = y;

        rows = width/2;
        cols = length/2;
        cellSizeInFeet = 2;

        int sizeOfCells;
        if (ratio < 1.9393)
        {
            sizeOfCells = (int) (sY-25)/rows;
        }
        else
        {
            sizeOfCells = (int) sX/cols;
        }

        System.out.println("Floors: "+floors);
        System.out.println("Ratio: "+ratio);
        System.out.println("Cols: "+cols);
        System.out.println("Rows: "+rows);
        System.out.println("Sx: "+sX);
        System.out.println("Sy: "+sY);

        cellSizeInFeet = 2;

        finalSizeOfCells = sizeOfCells;
        System.out.println("Final Cell Size in Feet: "+finalSizeOfCells);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("actual.fxml"));
        loader.setController(this);
        try
        {
            parent = loader.load();
            scene = new Scene(parent, x, y);
        }
        catch (IOException ex)
        {
            System.out.println("Error displaying login window");
            throw new RuntimeException(ex);
        }

        actualInitialize();
    }

    /**
     * Constructor used when loading layout from file
     * Reads file and stores info in data structures then passed onto initializer
     *
     * @param file saved layout
     * @param x screen horizontal dimension
     * @param y screen vertical dimension
     */
    public ActualController3(File file, double x, double y)
    {
        int cols1 = 0;
        int rows1 = 0;
        float feet = 0;
        ArrayList<String> isleGroupNames = new ArrayList<>();
        ArrayList<Color> isleGroupColors = new ArrayList<>();
        ArrayList<String> backOrFloorArr = new ArrayList<>();
        ArrayList<ArrayList<String>> listOfIsleIDs = new ArrayList<>();
        ArrayList<ArrayList<Hashtable<String, String>>> listOfIslesWithCells = new ArrayList<>();
        ArrayList<ArrayList<Hashtable<String, InfoToMakeIsleFromFile>>> listOfIslesWithSetupInfo = new ArrayList<>();
        String cellsToNull = null;
        String[] startAndEndPickPoints = new String[2];
        try
        {
            Scanner scanner = new Scanner(file);

            String c = scanner.nextLine();
            try
            {
                cols1 = Integer.parseInt(c);
            }
            catch (Exception e)
            {
                System.out.println("Fuck");
            }
            c = scanner.nextLine();
            try
            {
                rows1 = Integer.parseInt(c);
            }
            catch (Exception e)
            {
                System.out.println("Fuck");
            }
            c = scanner.nextLine();
            try
            {
                feet = Float.parseFloat(c);
            }
            catch (Exception e)
            {
                System.out.println("Fuck");
            }

            while(scanner.hasNext())
            {
                String line = scanner.nextLine();
                if (line.compareTo("Nulls") != 0)
                {
                    isleGroupNames.add(line);
                    //System.out.println("Added "+line+" to isleGroupNames");
                    String c1 = scanner.nextLine();
                    String[] c2 = c1.split(" ");
                    isleGroupColors.add(new Color(Double.parseDouble(c2[0]), Double.parseDouble(c2[1]), Double.parseDouble(c2[2]), 1.0));
                    String backOrFloor = scanner.nextLine();
                    backOrFloorArr.add(backOrFloor);
                    //System.out.println("Added color to isleGroupColors");

                    ArrayList<Hashtable<String, String>> islesWithCells = new ArrayList<>();
                    ArrayList<String> isleIDs = new ArrayList<>();
                    ArrayList<Hashtable<String, InfoToMakeIsleFromFile>> islesWithSetupInfo = new ArrayList<>();

                    String nextLine = scanner.nextLine();
                    int numberOfIsles = Integer.parseInt(nextLine);
                    //System.out.println("numberOfIsles: "+numberOfIsles);
                    for (int i=0; i<numberOfIsles; i++)
                    {
                        Hashtable<String, String> isle = new Hashtable<>();
                        Hashtable<String, InfoToMakeIsleFromFile> isleInfoHash = new Hashtable<>();
                        InfoToMakeIsleFromFile newIsle = null;

                        String isleID = scanner.nextLine();
                        //System.out.println("isleID: "+isleID);

                        String isleInfo = scanner.nextLine();
                        if (isleInfo.compareTo("Has Setup Info") == 0)
                        {
                            String s = scanner.nextLine();
                            int numberOfIsleSections = Integer.parseInt(s);
                            //System.out.println(numberOfIsleSections);
                            String subsectionsPerSection = scanner.nextLine();
                            //System.out.println(subsectionsPerSection);
                            String endCap = scanner.nextLine();
                            //System.out.println(endCap);
                            String direction = scanner.nextLine();
                            //System.out.println(direction);
                            newIsle = new InfoToMakeIsleFromFile(numberOfIsleSections, subsectionsPerSection, endCap, direction);

                            isleInfoHash.put(isleID, newIsle);
                        }

                        String cells = scanner.nextLine();
                        //System.out.println("cells: "+cells);

                        isleIDs.add(isleID);

                        isle.put(isleID, cells);

                        islesWithCells.add(isle);
                        islesWithSetupInfo.add(isleInfoHash);
                    }
                    listOfIsleIDs.add(isleIDs);
                    listOfIslesWithCells.add(islesWithCells);
                    listOfIslesWithSetupInfo.add(islesWithSetupInfo);
                }
                else
                {
                    String string = scanner.nextLine();
                    try
                    {
                        int hmm = Integer.parseInt(string.charAt(0)+"");
                        cellsToNull = string;
                    }
                    catch (NumberFormatException ignored) {}

                    String string1 = scanner.nextLine();
                    String[] opuStartEnd = string1.split(":");
                    try
                    {
                        startAndEndPickPoints[0] = opuStartEnd[1];
                    }
                    catch (ArrayIndexOutOfBoundsException e)
                    {
                        e.printStackTrace();
                    }
                    String next = scanner.nextLine();
                    String[] regStartEnd = next.split(":");
                    try
                    {
                        startAndEndPickPoints[1] = regStartEnd[1];
                    }
                    catch (ArrayIndexOutOfBoundsException e)
                    {
                        e.printStackTrace();
                    }

                    break;
                }
            }
            scanner.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found");
        }

        float ratio = (float) cols1/rows1;
        System.out.println("Ratio: "+ratio);
        int sizeOfCells;
        if (ratio < 1.9393)
        {
            sizeOfCells = (int) (y-25)/rows1;
        }
        else
        {
            sizeOfCells = (int) x/cols1;
        }
        System.out.println("Cell Size: "+sizeOfCells);

        cols = cols1;
        rows = rows1;
        finalSizeOfCells = sizeOfCells;
        cellSizeInFeet = feet;
        sX = x;
        sY = y;
        System.out.println("Columns: "+cols);
        System.out.println("Rows: "+rows);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("actual.fxml"));
        loader.setController(this);
        try
        {
            parent = loader.load();
            scene = new Scene(parent, sX, sY);
        }
        catch (IOException ex)
        {
            System.out.println("Error displaying Actual Controller");
            throw new RuntimeException(ex);
        }

        initializeFromFile(isleGroupNames, isleGroupColors, backOrFloorArr, listOfIslesWithCells, listOfIsleIDs, listOfIslesWithSetupInfo, cellsToNull, startAndEndPickPoints);
    }

    /**
     * Launches scene
     * Also sets mouse/screen dimensions for debugging
     */
    public void launchScene(Stage stage)
    {
        this.stage = stage;
        stage.setScene(scene);
        stage.setTitle("Setup Isle Layout");

        stage.addEventFilter(MouseEvent.MOUSE_MOVED, e -> this.sendMouse(e.getSceneX(), e.getSceneY()));

        stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, e ->
                this.sendMouse(e.getSceneX(), e.getSceneY()));

        stage.addEventFilter(MouseEvent.MOUSE_RELEASED, e ->
                this.sendMouse(e.getSceneX(), e.getSceneY()));

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            this.sendScreenX((stage.getWidth()-16));
            this.adjust((stage.getWidth()-16), (stage.getHeight()-39));
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            this.sendScreenY((stage.getHeight()-39));
            this.adjust((stage.getWidth()-16), (stage.getHeight()-39));
        });

        stage.hide();
        stage.show();

        m3.setText("ScreenX: " + (stage.getWidth()-16));
        m4.setText("ScreenY: " + (stage.getHeight()-39));

        stage.setMaximized(true);

        Stage cellSize = new Stage();
        cellSize.initModality(Modality.APPLICATION_MODAL);
        cellSize.initOwner(stage);
        VBox cellSizeVbox = new VBox();
        cellSizeVbox.setSpacing(5);
        cellSizeVbox.setAlignment(Pos.CENTER);
        Label ratioOfCellsToFeet = new Label("Each cell represents "+cellSizeInFeet+" feet.");
        Button ok = new Button("Ok");
        ok.setOnAction(actionEvent -> cellSize.hide());
        cellSizeVbox.getChildren().addAll(ratioOfCellsToFeet, ok);
        Scene cellSizeScene = new Scene(cellSizeVbox);
        cellSize.setScene(cellSizeScene);
        cellSize.show();
    }

    /**
     * Initializer
     * Also setups functionality of some menu buttons
     */
    private void actualInitialize()
    {
        theV.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        sP.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        back.setOnAction(actionEvent -> new LoadOrCreateController().launchScene(stage));

        saveLayout.setOnAction(actionEvent ->
        {
            final JFileChooser fc = new JFileChooser();

            File f = new File("C:\\Users\\rober\\IdeaProjects\\Target2\\src\\Saves");

            fc.setCurrentDirectory(f);

            int returnVal = fc.showSaveDialog(fc.getParent());

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = fc.getSelectedFile();
                saveFile(file);
            }
        });

        initRightClickMenus();

        initOutsidePanes(sX, sY);
        initInsidePanes(finalSizeOfCells);

        drawPluses(finalSizeOfCells, hboxWithThePluses);

        drawCells(finalSizeOfCells, hboxWithTheCells);

        //System.out.println("sp Dimension: "+sP.getWidth()+", "+sP.getHeight());
    }

    /**
     * Initializer for loading layout from file
     *
     * @param groupNames names of isle groups
     * @param groupColors colors of isle groups
     * @param backOrFloorArr list dictating whether the isle group is on the back or on the floor
     * @param listOfIslesWithCells cells making up each isle for each isle group
     * @param listOfIsleIDs isleIDs corresponding to cells
     * @param listOfIslesWithSetupInfo if an isle is setup with location info, stored here
     * @param startAndEndPickPoints index 0 is coords of OPU start/end, index 1 is coords of Regular start/end
     */
    private void initializeFromFile(ArrayList<String> groupNames, ArrayList<Color> groupColors, ArrayList<String> backOrFloorArr, ArrayList<ArrayList<Hashtable<String,
                                    String>>> listOfIslesWithCells, ArrayList<ArrayList<String>> listOfIsleIDs, ArrayList<ArrayList<Hashtable<String, InfoToMakeIsleFromFile>>> listOfIslesWithSetupInfo,
                                    String cellsToNull, String[] startAndEndPickPoints)
    {
        theV.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        sP.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        back.setOnAction(actionEvent -> new LoadOrCreateController().launchScene(stage));

        saveLayout.setOnAction(actionEvent ->
        {
            final JFileChooser fc = new JFileChooser();

            File f = new File("C:\\Users\\rober\\IdeaProjects\\Target2\\src\\Saves");

            fc.setCurrentDirectory(f);

            int returnVal = fc.showSaveDialog(fc.getParent());

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = fc.getSelectedFile();
                saveFile(file);
            }
        });

        testLocation.setOnAction(actionEvent ->
        {
            Stage testStage = new Stage();
            testStage.setTitle("Test Location");
            testStage.initModality(Modality.APPLICATION_MODAL);
            testStage.initOwner(stage);
            VBox testVBox = testLocationSetup(testStage, g);
            Scene testScene = new Scene(testVBox);
            testStage.setScene(testScene);
            testStage.show();
        });

        testPath.setOnAction(actionEvent ->
        {
            Stage testStage = new Stage();
            testStage.setTitle("Test Path");
            testStage.initModality(Modality.APPLICATION_MODAL);
            testStage.initOwner(stage);
            VBox testVBox = testPathSetup(testStage, g, graph);
            Scene testScene = new Scene(testVBox);
            testStage.setScene(testScene);
            testStage.show();
        });

        testPickingPath.setOnAction(actionEvent ->
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("setupPickingPath.fxml"));
            Scene newScene;
            try
            {
                newScene = new Scene(loader.load());
            }
            catch (IOException ex)
            {
                System.out.println("Error displaying login window");
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
            Stage inputStage = new Stage();
            inputStage.initOwner(stage);
            inputStage.setScene(newScene);
            inputStage.show();

            setupPath = loader.getController();
            setupPath.setInfo(g, graph, inputStage);
        });

        initRightClickMenus();

        initOutsidePanes(sX, sY);
        initInsidePanes(finalSizeOfCells);

        drawPluses(finalSizeOfCells, hboxWithThePluses);

        loadCellsFromFile(finalSizeOfCells, hboxWithTheCells, groupNames, groupColors, backOrFloorArr, listOfIslesWithCells, listOfIsleIDs, listOfIslesWithSetupInfo, cellsToNull, startAndEndPickPoints);
    }

    /**
     * Initializes some right click menus for grouping/ungrouping and other
     * related functions
     */
    private void initRightClickMenus()
    {
        rightClick = new ContextMenu();
        MenuItem makeIsle = new MenuItem("Make Isle");
        makeIsle.setOnAction(actionEvent ->
        {
            Stage makeIsleStage = new Stage();
            makeIsleStage.setTitle("New Isle Menu");
            makeIsleStage.initModality(Modality.APPLICATION_MODAL);
            makeIsleStage.initOwner(stage);
            VBox makeIsleVBox = makeIsleMenu(makeIsleStage, g);
            Scene groupSelectScene = new Scene(makeIsleVBox);
            makeIsleStage.setScene(groupSelectScene);
            makeIsleStage.show();
        });
        rightClick.getItems().add(makeIsle);
        MenuItem fillIn = new MenuItem("Fill In");
        fillIn.setOnAction(actionEvent ->
        {
            GridData3.HighlightedList.HighlightedNode curr = g.highlightedList.first;
            while (curr != null)
            {
                curr.rNode.setHighlighted(false);
                g.setCelltoNull(curr.rNode.getX(), curr.rNode.getY());
                curr = curr.next;
            }
            g.highlightedList.clear();
        });
        rightClick.getItems().add(fillIn);

        rightClick2 = new ContextMenu();
        MenuItem ungroup = new MenuItem("Delete Isle");
        ungroup.setOnAction(actionEvent -> g.removeIsle(editGroupNode.getIsle()));
        rightClick2.getItems().add(ungroup);
        MenuItem moveIsle = new MenuItem("Move Isle");
        moveIsle.setOnAction(actionEvent ->
        {
            rightClick2.hide();
            g.resetHighlighted();
            isleToMove = editGroupNode.getIsle();
            GridData3.IsleBeingMovedList toMoveList = g.getToMoveList();
            if (!moving)
            {
                System.out.println("Ungrouping cells from isle");
                Isle.IsleCellList.IsleCellNode curr = editGroupNode.getIsle().getIsleCellList().getFirst();
                while (curr != null)
                {
                    g.getRNode(curr.getrNode().getX(), curr.getrNode().getY()).setIsled(false, null, null, null);
                    toMoveList.add(g.getRNode(curr.getrNode().getX(), curr.getrNode().getY()));
                    g.getRNode(curr.getrNode().getX(), curr.getrNode().getY()).setIsleIsBeingMoved(true, editGroupNode.getColor());
                    curr = curr.getNext();
                }
            }
            moving = true;
        });
        rightClick2.getItems().add(moveIsle);
        setupInfoMenu = new MenuItem("Setup Isle Info");
        setupInfoMenu.setOnAction(actionEvent ->
        {
            Isle.IsleCellList.IsleCellNode curr = editGroupNode.getIsle().getIsleCellList().getFirst();
            while (curr != null)
            {
                curr.getrNode().getR().setOpacity(0.5);
                curr = curr.getNext();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("setupIsleInfo.fxml"));
            Scene newScene;
            try
            {
                newScene = new Scene(loader.load());
            }
            catch (IOException ex)
            {
                System.out.println("Error displaying login window");
                throw new RuntimeException(ex);
            }
            Stage inputStage = new Stage();
            inputStage.setTitle("Setup Isle Info: Isle "+editGroupNode.getIsle().getIsleID());
            inputStage.initOwner(stage);
            inputStage.setScene(newScene);
            inputStage.show();

            setupInfoStuff = loader.getController();
            setupInfoStuff.setImportantInfo(g, editGroupNode.getIsle(), inputStage);
        });
        showingSetupInfoMenu = false;
        setupOPUstartEnd = new MenuItem("Set OPU Start/End Point");
        setupOPUstartEnd.setOnAction(actionEvent -> g.setOPUstartEndNode(g.highlightedList.first.rNode, true));
        setupRegularPickStartEnd = new MenuItem("Set Regular Pick Start/End");
        setupRegularPickStartEnd.setOnAction(actionEvent -> g.setRegStartEndNode(g.highlightedList.first.rNode, true));
        showingSetOPUstartEnd = false;
        showingSetRegStartEnd = false;

        rightClick3 = new ContextMenu();
        MenuItem unnull = new MenuItem("Remove null");
        unnull.setOnAction(actionEvent -> g.removeNull());
        rightClick3.getItems().add(unnull);
    }

    /**
     * Initializes padding panes on outside of grid based on screen coords
     *
     * @param x screen x dimension
     * @param y screen y dimension
     */
    private void initOutsidePanes(double x, double y)
    {
        System.out.println("Initializing Outside Panes w/ x: "+x+" and y: "+y);
        double xRemainderSize = (int) x-(finalSizeOfCells * cols);
        double yRemainderSize = (int) y-(finalSizeOfCells * rows)-25;

        if (xRemainderSize % 2 == 1)
        {
            xRemainderSize1 = (int) (xRemainderSize/2+1);
        }
        else
        {
            xRemainderSize1 = (int) (xRemainderSize/2);
        }
        double xRemainderSize2 = (int) (xRemainderSize/2);

        if (yRemainderSize % 2 == 1)
        {
            yRemainderSize1 = (int) (yRemainderSize/2+1);
        }
        else
        {
            yRemainderSize1 = (int) (yRemainderSize/2);
        }
        double yRemainderSize2 = (int) (yRemainderSize/2);

        if (yRemainderSize1 == 18)
        {
            yRemainderSize1+=1;
            yRemainderSize2+=1;
        }

        leftPthatHelpsCells.setPrefWidth(xRemainderSize1);
        rightPthatHelpsCells.setPrefWidth(xRemainderSize2);
        topPthatHelpsCells.setPrefHeight(yRemainderSize1);
        botPthatHelpsCells.setPrefHeight(yRemainderSize2);

        System.out.println("x rem 1: "+xRemainderSize1);
        System.out.println("x rem 2: "+xRemainderSize2);
        System.out.println("y rem 1: "+yRemainderSize1);
        System.out.println("y rem 2: "+yRemainderSize2);
    }

    /**
     * Creates and draws pluses that separate cells
     *
     * @param z size of pluses
     * @param hbox1 hbox that pluses are added to
     */
    private void drawPluses(double z, HBox hbox1)
    {
        g = new GridData3(cols, rows, finalSizeOfCells, sX, sY, (int) cellSizeInFeet);

        for (int i=0; i<cols-1; i++)
        {
            VBox vbox = new VBox();

            for (int j=0; j<rows-1; j++)
            {
                StackPane sP1 = new StackPane();
                Line lx = new Line((int) -(z/3), 0, (int) (z/3), 0);
                lx.setStrokeWidth(1);
                Line ly = new Line(0, (int) -(z/3), 0, (int) (z/3));
                ly.setStrokeWidth(1);
                sP1.getChildren().addAll(lx, ly);

                g.addPlus(lx, ly, i, j);

                vbox.getChildren().add(sP1);
                vbox.setVgrow(sP1, Priority.ALWAYS);
            }
            hbox1.getChildren().add(vbox);
            hbox1.setHgrow(vbox, Priority.ALWAYS);
        }
    }

    /**
     * Initializes padding panes on inside of outside panes based on screen coords
     *
     * @param x screen x dimension
     */
    private void initInsidePanes(double x)
    {
        int x1 = (int) x;
        int z = x1/2;
        if (x1 % 2 == 1)
        {
            //rightPthatHelpsPluses.setPrefWidth(z+1);
            botPthatHelpsPluses.setPrefHeight(z+1);
        }
        else
        {
            //rightPthatHelpsPluses.setPrefWidth(z);
            botPthatHelpsPluses.setPrefHeight(z);
        }
        rightPthatHelpsPluses.setPrefWidth(z);
        leftPthatHelpsPluses.setPrefWidth(z);
        topPthatHelpsPluses.setPrefHeight(z);
    }

    /**
     * Creates and draws cells
     *
     * @param z size of cells
     * @param hbox2 hbox cells are added to
     */
    private void drawCells(double z, HBox hbox2)
    {
        double startX = xRemainderSize1;
        double startY = yRemainderSize1+25;

        for (int i=0; i<cols; i++)
        {
            VBox vbox = new VBox();
            if (i>0)
            {
                startX = startX + finalSizeOfCells;
                startY = yRemainderSize1+25;
            }

            for (int j=0; j<rows; j++)
            {
                Rectangle r = new Rectangle();
                GridData3.RNode node = g.addRect(r, i, j, startX, startY);

                setupCellsAndFunctions(r, node, z);

                startY = startY + finalSizeOfCells;

                vbox.getChildren().add(r);
                vbox.setVgrow(r, Priority.ALWAYS);
            }
            hbox2.getChildren().add(vbox);
            hbox2.setHgrow(vbox, Priority.ALWAYS);
        }
    }

    /**
     * Draws cells, inputting all info from file
     *
     * @param z pixel size of each cell
     * @param hbox2 container to put cells into
     * @param groupNames names of isle groups
     * @param groupColors colors of isle groups
     * @param backOrFloorArr list dictating whether the isle group is on the back or on the floor
     * @param listOfIslesWithCells cells making up each isle for each isle group
     * @param listOfIsleIDs isleIDs corresponding to cells
     * @param listOfIslesWithSetupInfo if an isle is setup with location info, stored here
     * @param cellsToNull cells to fill in or null
     * @param startAndEndPickPoints index 0 is coords of OPU start/end, index 1 is coords of Regular start/end
     */
    private void loadCellsFromFile(double z, HBox hbox2, ArrayList<String> groupNames, ArrayList<Color> groupColors, ArrayList<String> backOrFloorArr,
                                   ArrayList<ArrayList<Hashtable<String, String>>> listOfIslesWithCells, ArrayList<ArrayList<String>> listOfIsleIDs,
                                   ArrayList<ArrayList<Hashtable<String, InfoToMakeIsleFromFile>>> listOfIslesWithSetupInfo, String cellsToNull,
                                   String[] startAndEndPickPoints)
    {
        double startX = xRemainderSize1;
        double startY = yRemainderSize1+25;

        for (int i=0; i<cols; i++)
        {
            VBox vbox = new VBox();
            if (i>0)
            {
                startX = startX + finalSizeOfCells;
                startY = yRemainderSize1+25;
            }

            for (int j=0; j<rows; j++)
            {
                Rectangle r = new Rectangle();
                GridData3.RNode node = g.addRect(r, i, j, startX, startY);

                setupCellsAndFunctions(r, node, z);

                startY = startY + finalSizeOfCells;

                vbox.getChildren().add(r);
                vbox.setVgrow(r, Priority.ALWAYS);
            }
            hbox2.getChildren().add(vbox);
            hbox2.setHgrow(vbox, Priority.ALWAYS);
        }

        System.out.println("Loading Cells From File");
        System.out.println();
        for (int i=0; i<groupNames.size(); i++)
        {
            for (int j=0; j<listOfIslesWithCells.get(i).size(); j++)
            {
                //System.out.println("Grouping: "+listOfIsleIDs.get(i).get(j));
                String[] groupCoords = listOfIslesWithCells.get(i).get(j).get(listOfIsleIDs.get(i).get(j)).split(",");
                for (int k=0; k<groupCoords.length; k=k+2)
                {
                    g.getRNode(Integer.parseInt(groupCoords[k]), Integer.parseInt(groupCoords[k+1])).setHighlighted(true);
                    //System.out.println("Highlighting: "+Integer.parseInt(groupCoords[k])+","+Integer.parseInt(groupCoords[k+1]));
                }
                if (g.isleGroupList.containsKey(groupNames.get(i)))
                {
                    //System.out.println("Adding "+listOfIsleIDs.get(i).get(j)+" to existing isle group");
                    g.makeIsle(listOfIsleIDs.get(i).get(j), groupNames.get(i), groupColors.get(i), g.isleGroupList.get(groupNames.get(i)), true, null);

                    InfoToMakeIsleFromFile isleInfo = listOfIslesWithSetupInfo.get(i).get(j).get(listOfIsleIDs.get(i).get(j));
                    if (isleInfo != null)
                    {
                        System.out.println("Setting Up Isle "+listOfIsleIDs.get(i).get(j)+" Info From File");
                        ArrayList<Integer> arr = new ArrayList<>();
                        String s = isleInfo.getNumberOfSubsectionsForEachSection();
                        String[] sArr = s.split("-");
                        for (String value : sArr)
                            arr.add(Integer.parseInt(value));

                        g.isleGroupList.get(groupNames.get(i)).getIsleIDList().get(listOfIsleIDs.get(i).get(j)).setupIsleInfo(isleInfo.getNumberOfIsleSections(), arr,
                                isleInfo.getEndCapLocation(), isleInfo.getDirectionOfIncreasingIsleSections());
                    }
                }
                else
                {
                    //System.out.println("New isle group for "+listOfIsleIDs.get(i).get(j));
                    g.makeIsle(listOfIsleIDs.get(i).get(j), groupNames.get(i), groupColors.get(i), null, false, backOrFloorArr.get(i));
                }
            }
        }

        if (cellsToNull != null)
        {
            String[] nullCoords = cellsToNull.split(",");
            for (int j=0; j<nullCoords.length; j=j+2)
            {
                g.setCelltoNull(Integer.parseInt(nullCoords[j]), Integer.parseInt(nullCoords[j+1]));
            }
        }

        try
        {
            System.out.println("OPU Start/End: "+startAndEndPickPoints[0]);
            String[] opuCoords = startAndEndPickPoints[0].split(",");
            int opuX = Integer.parseInt(opuCoords[0]);
            int opuY = Integer.parseInt(opuCoords[1]);
            g.setOPUstartEndNode(g.getRNode(opuX, opuY), true);
        }
        catch (Exception e)
        {
            System.out.println("No start/end location for OPU");
        }
        try
        {
            String[] regCoords = startAndEndPickPoints[1].split(",");
            System.out.println("Reg Start/End: "+startAndEndPickPoints[1]);
            int regX = Integer.parseInt(regCoords[0]);
            int regY = Integer.parseInt(regCoords[1]);
            g.setRegStartEndNode(g.getRNode(regX, regY), true);
        }
        catch (Exception e)
        {
            System.out.println("No start/end location for Regular Pick");
        }

        graph = new GraphOfTheGrid(g);
    }

    /**
     * Setups cell and functions assciated with creating groups
     *
     * @param r rectangle that is the cell on display
     * @param node node of drawn cell in data
     * @param z size of cells
     */
    private void setupCellsAndFunctions(Rectangle r, GridData3.RNode node, double z)
    {
        r.setFill(Color.TRANSPARENT);
        r.setStroke(Color.TRANSPARENT);
        r.setOpacity(0.5);
        r.setOnMouseEntered(mouseEvent ->
        {
            g.setMouseCoordsOnGrid(node.getX(), node.getY());
            setMouseCoordsOnScreen(node.getX(), node.getY());
            if (moving)
            {
                //System.out.println("Moving Isle");

                g.moveIsle(editGroupNode, isleToMove);
                //System.out.println("Done method call");
            }
            if (node.isIsle())
            {
                r.setOpacity(0.5);
            }
            else if (!node.isNulled() && !node.isBeingMoved())
            {
                if (!g.nodeIsPickPoint(node.getX(), node.getY()))
                    r.setFill(Color.GRAY);
            }
        });
        r.setOnMouseExited(mouseEvent ->
        {
            if (node.isHighlighted())
            {
                r.setFill(Color.GRAY);
            }
            else if (node.isIsle())
            {
                r.setFill(node.getColor());
                r.setOpacity(1.0);
            }
            else if (!node.isNulled())
            {
                if (!g.nodeIsPickPoint(node.getX(), node.getY()))
                    r.setFill(Color.TRANSPARENT);
            }
        });
        r.setOnMouseClicked(mouseEvent ->
        {
            if (mouseEvent.getButton() == MouseButton.SECONDARY)
            {
                if (node.isHighlighted())
                {
                    if (g.highlightedList.size() == 1)
                    {
                        if (g.opuStartEndNode != null)
                            rightClick.getItems().remove(setupOPUstartEnd);
                        if (g.regStartEndNode != null)
                            rightClick.getItems().remove(setupRegularPickStartEnd);

                        if (!showingSetOPUstartEnd && g.opuStartEndNode == null)
                            rightClick.getItems().add(setupOPUstartEnd);
                        showingSetOPUstartEnd = true;
                        if (!showingSetRegStartEnd && g.regStartEndNode == null)
                            rightClick.getItems().add(setupRegularPickStartEnd);
                        showingSetRegStartEnd = true;
                    }
                    else
                    {
                        if (showingSetOPUstartEnd)
                            rightClick.getItems().remove(setupOPUstartEnd);
                        showingSetOPUstartEnd = false;
                        if (showingSetRegStartEnd)
                            rightClick.getItems().remove(setupRegularPickStartEnd);
                        showingSetRegStartEnd = false;
                    }
                    rightClick.show(r, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
                else if (node.isIsle())
                {
                    g.resetHighlighted();
                    if (!node.getIsle().hasSetupInfo())
                    {
                        if (!showingSetupInfoMenu)
                            rightClick2.getItems().add(setupInfoMenu);
                        showingSetupInfoMenu = true;
                    }
                    else
                    {
                        if (showingSetupInfoMenu)
                            rightClick2.getItems().remove(setupInfoMenu);
                        showingSetupInfoMenu = false;
                    }

                    rightClick2.show(r, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    editGroupNode = node;
                }
                else if (node.isNulled() && node.isHighlightedNull())
                {
                    rightClick3.show(r, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    editGroupNode = node;
                }
            }
            else if (mouseEvent.getButton() == MouseButton.PRIMARY)
            {
                if (!moving && node.isNulled())
                {
                    g.resetHighlightedNulls();
                    node.setHighlightedNull(true);
                    g.resetHighlighted();
                }
                if (!moving && !node.isNulled())
                {
                    System.out.println();
                    System.out.println("Coords: "+node.getX()+","+node.getY());
                    g.resetHighlighted();
                    g.resetHighlightedNulls();
                    if (node.isIsle())
                    {
                        node.getIsle().printInfo();
                    }
                    else
                    {
                        System.out.println("No Isle/IsleGroup");
                        node.setHighlighted(true);
                    }
                    if (g.nodeIsPickPoint(node.getX(), node.getY()))
                    {
                        System.out.println("Cell is Pick Point");
                    }
                }
                if (!moving && !node.isNulled() && !node.isIsle())
                {
                    System.out.println("Connects to: ");

                    GraphOfTheGrid.Edge curr = graph.graph.get(node.getX()+","+node.getY());
                    while (curr != null)
                    {
                        System.out.print(curr.w+" ");
                        if (curr.next == null)
                            System.out.print("\n");
                        curr = curr.next;
                    }
                }
                if (moving && !node.isNulled())
                {
                    System.out.println("Remade Isle After Move");
                    node.setHighlighted(false);
                    g.makeIsleFromToMoveList(isleToMove.getIsleID(), isleToMove.getIsleGroup().getName(), isleToMove.getIsleGroup().getColor(), isleToMove.getIsleGroup(), isleToMove);
                    moving = false;
                }
            }
        });
        r.setOnMouseDragged(mouseEvent ->
        {
            if (mouseEvent.getButton() == MouseButton.PRIMARY && !node.isNulled() && !g.nodeIsPickPoint(node.getX(), node.getY()))
            {
                g.resetHighlightedNulls();
                if (!highlighting)
                {
                    g.resetHighlighted();
                    highlighting = true;
                    //highlightingIterations = 0;
                    if (!node.isIsle())
                        node.setHighlighted(true);
                }

                double x = mouseEvent.getSceneX();
                double y = mouseEvent.getSceneY();
                double a = (node.getsXMinCoord()-x)/finalSizeOfCells+1;
                double b = (node.getsYMinCoord()-y)/finalSizeOfCells+1;
                double c = (x-node.getsXMaxCoord())/finalSizeOfCells+1;
                double d = (y-node.getsYMaxCoord())/finalSizeOfCells+1;

                if (Math.ceil(a) != g.getHighlightingXLength() || Math.ceil(b) != g.getHighlightingYLength() || Math.ceil(c) != g.getHighlightingXLength() || Math.ceil(d) != g.getHighlightingYLength())
                {
                    //System.out.println("Highlighting");
                    g.resetHighlighted();
                    g.highlight(node.getX(), node.getY(), a, b, c, d);
                    //highlightingIterations++;
                }
            }
            if (mouseEvent.getButton() == MouseButton.PRIMARY && node.isNulled())
            {
                if (!highlighting)
                {
                    g.resetHighlightedNulls();
                    highlighting = true;
                    //highlightingIterations = 0;
                    if (node.isNulled())
                        node.setHighlightedNull(true);
                }

                double x = mouseEvent.getSceneX();
                double y = mouseEvent.getSceneY();
                double a = (node.getsXMinCoord()-x)/finalSizeOfCells+1;
                double b = (node.getsYMinCoord()-y)/finalSizeOfCells+1;
                double c = (x-node.getsXMaxCoord())/finalSizeOfCells+1;
                double d = (y-node.getsYMaxCoord())/finalSizeOfCells+1;

                if (Math.ceil(a) != g.getHighlightingXLength() || Math.ceil(b) != g.getHighlightingYLength() || Math.ceil(c) != g.getHighlightingXLength() || Math.ceil(d) != g.getHighlightingYLength())
                {
                    //System.out.println("Highlighting");
                    g.resetHighlightedNulls();
                    g.highlightNulls(node.getX(), node.getY(), a, b, c, d);
                    //highlightingIterations++;
                }
            }
        });
        r.setOnMouseReleased(mouseEvent ->
        {
            g.resetHighlighted2();
            highlighting = false;
            //System.out.println("highlightingIterations: "+highlightingIterations);
        });

        r.setLayoutX(0);
        r.setLayoutY(0);
        r.setWidth(z-1);
        r.setHeight(z-1);
    }

    /**
     * Sets up popup window for creating an isle for a highlighted are
     *
     * @param s stage
     * @param g data used to create and implement new group
     * @return vbox with all necessary elements
     */
    private VBox makeIsleMenu(Stage s, GridData3 g)
    {
        VBox v = new VBox();

        v.setAlignment(Pos.CENTER);
        v.setSpacing(5);
        HBox hboxA = new HBox();
        hboxA.setAlignment(Pos.CENTER);
        Label isle = new Label("Input Isle Letter & Number: ");
        isle.setStyle("-fx-font-size: 16;");
        TextField idT = new TextField();
        hboxA.getChildren().addAll(isle, idT);
        v.getChildren().add(hboxA);
        Button addToExisting = new Button("Add Isle to Existing Group");
        addToExisting.setOnAction(actionEvent ->
        {
            if (idT.getText().trim().isEmpty())
            {
                Stage warning = new Stage();
                warning.initModality(Modality.APPLICATION_MODAL);
                warning.initOwner(stage);
                VBox warningVbox = new VBox();
                warningVbox.setSpacing(5);
                warningVbox.setAlignment(Pos.CENTER);
                Label warningText = new Label("Please Input an Isle Letter & Number");
                Button ok = new Button("Ok");
                ok.setOnAction(actionEvent1 -> warning.hide());
                warningVbox.getChildren().addAll(warningText, ok);
                Scene cellSizeScene = new Scene(warningVbox);
                warning.setScene(cellSizeScene);
                warning.show();
            }
            else
            {
                String isleID = idT.getText();

                s.hide();

                Stage addToExistingStage = new Stage();
                addToExistingStage.initModality(Modality.APPLICATION_MODAL);
                addToExistingStage.initOwner(stage);
                VBox addToExistingVBox = addToExistingGroupMenu(addToExistingStage, g, isleID);
                Scene groupSelectScene = new Scene(addToExistingVBox);
                addToExistingStage.setScene(groupSelectScene);
                addToExistingStage.show();
            }
        });
        Button makeNew = new Button("Create New Isle Group");
        makeNew.setOnAction(actionEvent ->
        {
            if (idT.getText().trim().isEmpty())
            {
                Stage warning = new Stage();
                warning.initModality(Modality.APPLICATION_MODAL);
                warning.initOwner(stage);
                VBox warningVbox = new VBox();
                warningVbox.setSpacing(5);
                warningVbox.setAlignment(Pos.CENTER);
                Label warningText = new Label("Please Input an Isle Letter & Number");
                Button ok = new Button("Ok");
                ok.setOnAction(actionEvent1 -> warning.hide());
                warningVbox.getChildren().addAll(warningText, ok);
                Scene cellSizeScene = new Scene(warningVbox);
                warning.setScene(cellSizeScene);
                warning.show();
            }
            else
            {
                String isleID = idT.getText();

                s.hide();

                Stage groupSelect = new Stage();
                groupSelect.setTitle("Select Isle Group Name and Color");
                groupSelect.initModality(Modality.APPLICATION_MODAL);
                groupSelect.initOwner(stage);
                VBox groupSelectVbox = setupGroupCreation(groupSelect, g, isleID);
                Scene groupSelectScene = new Scene(groupSelectVbox);
                groupSelect.setScene(groupSelectScene);
                groupSelect.show();
            }
        });
        v.getChildren().addAll(addToExisting, makeNew);

        return v;
    }

    /**
     * Sets up popup window for isle group creation
     *
     * @param s stage
     * @param g data used to create and implement new group
     * @return vbox with all necessary elements
     */
    private VBox setupGroupCreation(Stage s, GridData3 g, String isleID)
    {
        VBox v = new VBox();

        v.setAlignment(Pos.CENTER);
        v.setSpacing(5);
        HBox hboxA = new HBox();
        hboxA.setAlignment(Pos.CENTER);
        Label name = new Label("Input Name: ");
        name.setStyle("-fx-font-size: 16;");
        TextField nameT = new TextField();
        hboxA.getChildren().addAll(name, nameT);
        HBox hboxC = new HBox();
        hboxC.setAlignment(Pos.CENTER);
        Label bOrF = new Label("Isle Group in Back or on Floor: ");
        bOrF.setStyle("-fx-font-size: 16;");
        TextField bOrFtext = new TextField();
        hboxC.getChildren().addAll(bOrF, bOrFtext);
        HBox hboxD = new HBox();
        hboxD.setAlignment(Pos.CENTER);
        Label color = new Label("Select Color: ");
        color.setStyle("-fx-font-size: 16;");
        ColorPicker colorP = new ColorPicker();
        hboxD.getChildren().addAll(color, colorP);
        Button submit = new Button("Submit");
        submit.setOnAction(actionEvent ->
        {
            if (nameT.getText().trim().isEmpty())
            {
                Stage warning = new Stage();
                warning.initModality(Modality.APPLICATION_MODAL);
                warning.initOwner(stage);
                VBox warningVbox = new VBox();
                warningVbox.setSpacing(5);
                warningVbox.setAlignment(Pos.CENTER);
                Label warningText = new Label("Please Input a Isle Group Name");
                Button ok = new Button("Ok");
                ok.setOnAction(actionEvent1 -> warning.hide());
                warningVbox.getChildren().addAll(warningText, ok);
                Scene cellSizeScene = new Scene(warningVbox);
                warning.setScene(cellSizeScene);
                warning.show();
            }
            else
            {
                s.hide();
                g.makeIsle(isleID, nameT.getText(), colorP.getValue(), null, false, bOrFtext.getText());
            }
        });
        v.getChildren().addAll(hboxA, hboxC, hboxD, submit);

        return v;
    }

    /**
     * Sets up popup window for adding new isle to existing isle group
     *
     * @param s stage
     * @param g data used to create and implement new group
     * @param isleID of new isle
     * @return vbox with all necessary elements
     */
    private VBox addToExistingGroupMenu(Stage s, GridData3 g, String isleID)
    {
        VBox v = new VBox();
        v.setPrefWidth(120);

        v.setAlignment(Pos.CENTER);

        Set<String> groups = g.isleGroupList.keySet();
        for (String key : groups)
        {
            Button group = new Button("Group: "+g.isleGroupList.get(key).getName());
            group.setTextFill(Color.WHITE);
            String color = g.isleGroupList.get(key).getColor().toString();
            String[] strings = color.split("x");
            String string = "-fx-background-color: #"+strings[1]+";";
            group.setStyle(string);
            group.setOnAction(actionEvent ->
            {
                if (g.isleGroupList.get(key).containsIsle(isleID))
                {
                    Stage warningStage = new Stage();
                    warningStage.initModality(Modality.APPLICATION_MODAL);
                    warningStage.initOwner(stage);
                    VBox warningVbox = new VBox();
                    warningVbox.setSpacing(5);
                    warningVbox.setAlignment(Pos.CENTER);
                    Label warningLabel1 = new Label("This isleID already exists.");
                    Label warningLabel2 = new Label("Do you want to add this new isle to the already existing one?");
                    Label warningLabel3 = new Label("If not, please rename isle.");
                    Button yes = new Button("Yes");
                    yes.setOnAction(actionEvent1 ->
                    {
                        warningStage.hide();
                        s.hide();
                        g.addNewToExistingIsle(isleID, g.isleGroupList.get(key).getColor(), g.isleGroupList.get(key));
                    });
                    warningVbox.getChildren().addAll(warningLabel1, warningLabel2, warningLabel3, yes);
                    Scene cellSizeScene = new Scene(warningVbox);
                    warningStage.setScene(cellSizeScene);
                    warningStage.show();
                }
                else
                {
                    g.makeIsle(isleID, g.isleGroupList.get(key).getName(), g.isleGroupList.get(key).getColor(), g.isleGroupList.get(key), true, null);
                    s.hide();
                }
            });
            v.getChildren().add(group);
        }

        return v;
    }

    /**
     * Writes all necessary info to file for saving/loading
     *
     * @param file file to write to
     */
    private void saveFile(File file)
    {
        try
        {
            PrintStream fileStream = new PrintStream(file);

            fileStream.println(cols);
            fileStream.println(rows);
            fileStream.println(cellSizeInFeet);

            Set<String> groups = g.isleGroupList.keySet();
            for (String key : groups)
            {
                GridData3.IsleGroup isleGroup = g.isleGroupList.get(key);
                fileStream.println(isleGroup.getName());
                Color color = isleGroup.getColor();
                fileStream.println(color.getRed()+" "+color.getGreen()+" "+color.getBlue());
                fileStream.println(isleGroup.getBackOrFloor());
                fileStream.println(isleGroup.getIsleIDList().size());

                Set<String> isleIDS = isleGroup.getIsleIDList().keySet();
                for (String idKey : isleIDS)
                {
                    fileStream.println(isleGroup.getIsleIDList().get(idKey).getIsleID());
                    if (isleGroup.getIsleIDList().get(idKey).hasSetupInfo())
                    {
                        fileStream.println("Has Setup Info");
                        fileStream.println(isleGroup.getIsleIDList().get(idKey).getNumberOfIsleSections());
                        ArrayList<Integer> arr = isleGroup.getIsleIDList().get(idKey).getNumberOfSubsectionsForEachSection();
                        for (int i : arr)
                        {
                            fileStream.print(i+"-");
                        }
                        fileStream.print("\n");
                        fileStream.println(isleGroup.getIsleIDList().get(idKey).getEndCapLocation());
                        fileStream.println(isleGroup.getIsleIDList().get(idKey).getDirectionOfIncreasingIsleSections());
                    }
                    else
                        fileStream.println("No Setup Info");
                    Isle.IsleCellList isleCellList = isleGroup.getIsleIDList().get(idKey).getIsleCellList();
                    Isle.IsleCellList.IsleCellNode curr = isleCellList.getFirst();
                    while (curr != null)
                    {
                        fileStream.print(curr.getrNode().getX()+","+curr.getrNode().getY());

                        if (curr.getNext() != null)
                            fileStream.print(",");
                        else
                            fileStream.print("\n");

                        curr = curr.getNext();
                    }
                }
            }

            fileStream.println("Nulls");
            Set<String> nulls = g.nullList.keySet();
            if (nulls.size() > 0)
            {
                for (String key : nulls)
                    fileStream.print(key+",");
                fileStream.print("\n");
            }
            else
                fileStream.println("No nulls to null");

            if (g.opuStartEndNode == null)
                fileStream.println("No OPU start/end");
            else
                fileStream.println("OPU start/end:"+g.opuStartEndNode.getX()+","+g.opuStartEndNode.getY());

            if (g.regStartEndNode == null)
                fileStream.println("No Regular start/end");
            else
                fileStream.println("Regular start/end:"+g.regStartEndNode.getX()+","+g.regStartEndNode.getY());

            fileStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Saved new layout to "+file);
    }

    /**
     * Used for testing cell finding given location
     */
    private VBox testLocationSetup(Stage s, GridData3 g)
    {
        VBox v = new VBox();

        v.setAlignment(Pos.CENTER);

        Label title = new Label("Test Location!!");
        title.setStyle("-fx-font-size: 16;");

        TextField tester = new TextField();
        tester.setOnAction(actionEvent ->
        {
            s.hide();

            String loc = tester.getText();

            Isle isleOfTest = null;
            System.out.println();
            try
            {
                int hmm = Integer.parseInt(loc.charAt(0)+"");
                System.out.println("Isle in the back");
                String[] sArr = loc.split(" ");
                if (g.isleGroupExists(sArr[0]))
                {
                    isleOfTest = g.getIsle(sArr[0]+sArr[1]+"", sArr[0]);
                    System.out.println("IsleID: "+isleOfTest.getIsleID());
                    if (isleOfTest.hasSetupInfo())
                    {
                        String subsection = sArr[2].charAt(sArr[2].length()-1)+"";
                        System.out.println("isleSubsection: "+subsection);

                        if (isleOfTest.inputingValidIsleLocationInBack(subsection))
                            System.out.println("Coords Found: "+isleOfTest.getCoordsGivenLocationInBack(subsection));
                        else
                        {
                            Stage warningStage = new Stage();
                            warningStage.initModality(Modality.APPLICATION_MODAL);
                            warningStage.initOwner(stage);
                            VBox warningVbox = new VBox();
                            warningVbox.setSpacing(5);
                            warningVbox.setAlignment(Pos.CENTER);
                            Label warningLabel = new Label("Isle Section/Isle Subsection not valid within possible isle locations");
                            Button ok = new Button("Ok");
                            ok.setOnAction(actionEvent1 -> warningStage.hide());
                            warningVbox.getChildren().addAll(warningLabel, ok);
                            Scene cellSizeScene = new Scene(warningVbox);
                            warningStage.setScene(cellSizeScene);
                            warningStage.show();
                        }
                    }
                    else
                    {
                        Stage warningStage = new Stage();
                        warningStage.initModality(Modality.APPLICATION_MODAL);
                        warningStage.initOwner(stage);
                        VBox warningVbox = new VBox();
                        warningVbox.setSpacing(5);
                        warningVbox.setAlignment(Pos.CENTER);
                        Label warningLabel = new Label("Isle: "+sArr[0]+sArr[1]+" does not exist/has not been setup");
                        Button ok = new Button("Ok");
                        ok.setOnAction(actionEvent1 -> warningStage.hide());
                        warningVbox.getChildren().addAll(warningLabel, ok);
                        Scene cellSizeScene = new Scene(warningVbox);
                        warningStage.setScene(cellSizeScene);
                        warningStage.show();
                    }
                }
                else
                {
                    Stage warningStage = new Stage();
                    warningStage.initModality(Modality.APPLICATION_MODAL);
                    warningStage.initOwner(stage);
                    VBox warningVbox = new VBox();
                    warningVbox.setSpacing(5);
                    warningVbox.setAlignment(Pos.CENTER);
                    Label warningLabel = new Label("Isle Group does not exist");
                    Button ok = new Button("Ok");
                    ok.setOnAction(actionEvent1 -> warningStage.hide());
                    warningVbox.getChildren().addAll(warningLabel, ok);
                    Scene cellSizeScene = new Scene(warningVbox);
                    warningStage.setScene(cellSizeScene);
                    warningStage.show();
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println("Isle on the floor");
                if (g.isleGroupExists(loc.charAt(0)+""))
                {
                    String[] loc1 = loc.split("\\(");
                    System.out.println("IsleID: "+loc1[0]);
                    isleOfTest = g.getIsle(loc1[0], loc.charAt(0)+"");
                    if (isleOfTest != null && isleOfTest.hasSetupInfo())
                    {
                        String[] loc2 = loc1[1].split("\\)");
                        int isleSection = Integer.parseInt(loc2[0]);
                        System.out.println("isleSection: "+isleSection);

                        String[] loc3 = loc2[1].split("-");
                        System.out.println("isleSubsection: "+loc3[0]);

                        if (isleOfTest.inputingValidIsleLocationOnFloor(isleSection, loc3[0]))
                            System.out.println("Coords Found: "+isleOfTest.getCoordsGivenLocationOnFloor(isleSection, loc3[0]));
                        else
                        {
                            Stage warningStage = new Stage();
                            warningStage.initModality(Modality.APPLICATION_MODAL);
                            warningStage.initOwner(stage);
                            VBox warningVbox = new VBox();
                            warningVbox.setSpacing(5);
                            warningVbox.setAlignment(Pos.CENTER);
                            Label warningLabel = new Label("Isle Section/Isle Subsection not valid within possible isle locations");
                            Button ok = new Button("Ok");
                            ok.setOnAction(actionEvent1 -> warningStage.hide());
                            warningVbox.getChildren().addAll(warningLabel, ok);
                            Scene cellSizeScene = new Scene(warningVbox);
                            warningStage.setScene(cellSizeScene);
                            warningStage.show();
                        }
                    }
                    else
                    {
                        Stage warningStage = new Stage();
                        warningStage.initModality(Modality.APPLICATION_MODAL);
                        warningStage.initOwner(stage);
                        VBox warningVbox = new VBox();
                        warningVbox.setSpacing(5);
                        warningVbox.setAlignment(Pos.CENTER);
                        Label warningLabel = new Label("Isle: "+loc1[0]+" does not exist/has not been setup");
                        Button ok = new Button("Ok");
                        ok.setOnAction(actionEvent1 -> warningStage.hide());
                        warningVbox.getChildren().addAll(warningLabel, ok);
                        Scene cellSizeScene = new Scene(warningVbox);
                        warningStage.setScene(cellSizeScene);
                        warningStage.show();
                    }
                }
                else
                {
                    Stage warningStage = new Stage();
                    warningStage.initModality(Modality.APPLICATION_MODAL);
                    warningStage.initOwner(stage);
                    VBox warningVbox = new VBox();
                    warningVbox.setSpacing(5);
                    warningVbox.setAlignment(Pos.CENTER);
                    Label warningLabel = new Label("Isle Group does not exist");
                    Button ok = new Button("Ok");
                    ok.setOnAction(actionEvent1 -> warningStage.hide());
                    warningVbox.getChildren().addAll(warningLabel, ok);
                    Scene cellSizeScene = new Scene(warningVbox);
                    warningStage.setScene(cellSizeScene);
                    warningStage.show();
                }
            }
        });

        v.getChildren().addAll(title, tester);

        return v;
    }

    private VBox testPathSetup(Stage s, GridData3 g, GraphOfTheGrid graph)
    {
        VBox v = new VBox();

        v.setAlignment(Pos.CENTER);

        Label title = new Label("Test Path!!");
        title.setStyle("-fx-font-size: 16;");

        HBox h = new HBox();
        TextField v1 = new TextField();
        Label to = new Label("->");
        TextField v2 = new TextField();

        h.getChildren().addAll(v1, to, v2);

        Button done = new Button("Test!");
        done.setOnAction(actionEvent ->
        {
            GraphOfTheGrid.DistanceReturn dr = graph.findDistanceBetween(v1.getText(), v2.getText());
            System.out.println("Distance: "+dr.getDistance());
            String path = dr.getPath();
            System.out.println("Path: "+path);
            String[] pathArr = path.split(" ");
            for (int i=1; i< pathArr.length; i++)
            {
                try
                {
                    String[] coords = pathArr[i].split(",");
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    Rectangle r = g.getRNode(x, y).getR();
                    r.setFill(Color.RED);
                    r.setOpacity(0.5);
                }
                catch (NumberFormatException ignored) {}
            }
        });

        v.getChildren().addAll(title, h, done);
        return v;
    }

    /**
     * Displays cell coordinates corresponding to mouse on screen
     *
     * @param x mouse x coord
     * @param y mouse y coord
     */
    private void setMouseCoordsOnScreen(int x, int y)
    {
        m5.setText("X Coord: "+x);
        m6.setText("Y Coord: "+y);
    }

    /**
     * Displays mouse coordinates for debugging
     *
     * @param x mouse x coord
     * @param y mouse y coord
     */
    public void sendMouse(double x, double y)
    {
        mouseX = x;
        mouseY = y;
        m1.setText("MouseX: " + x);
        m2.setText("MouseY: " + y);
    }

    /**
     * Adjusts size of elements on display for a change in window size
     *
     * @param x screen x dimension
     * @param y screen y dimension
     */
    public void adjust(double x, double y)
    {
        initOutsidePanes(x, y);

        initInsidePanes(finalSizeOfCells);

        g.adjust(finalSizeOfCells, xRemainderSize1, yRemainderSize1);
    }

    /**
     * Displays screen x dimension for debugging
     *
     * @param x screen x dimension
     */
    public void sendScreenX(double x)
    {
        sX = x;
        m3.setText("ScreenX: " + x);
    }

    /**
     * Displays screen y dimension for debugging
     *
     * @param y screen y dimension
     */
    public void sendScreenY(double y)
    {
        sY = y;
        m4.setText("ScreenY: " + y);
    }
}