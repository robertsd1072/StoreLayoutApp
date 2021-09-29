/**
 * IsleLayoutController class for project DJR_Store_Layout
 * Controls store layout and all accompanying functionality of UI
 * @author David Roberts
 */

package DJR_Store_Layout.UserInterface;

import DJR_Store_Layout.GraphsAndHelpers.DistanceReturn;
import DJR_Store_Layout.GraphsAndHelpers.Edge;
import DJR_Store_Layout.GraphsAndHelpers.GraphOfTheGrid;
import DJR_Store_Layout.GridData.*;
import DJR_Store_Layout.HelperClasses.Coords;
import DJR_Store_Layout.HelperClasses.InfoToMakeAisleFromFile;
import DJR_Store_Layout.HelperClasses.MyPopup;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class IsleLayoutController {
    /** Launching variables */
    private final Parent parent;
    private Stage stage;
    private final Scene scene;

    /** FXML variables */
    public HBox hboxWithThePluses, hboxWithTheCells;
    public MenuItem back, load, saveLayout, testLocation, testPath, testPickingPath, setupInfoMenu, setupRegOPUstartEnd,
            setupGroOPUstartEnd, setupStandardPickStartEnd, layoutInstructionsMenu, isleInfoInstructionsMenu, displayCoords;
    public Menu fileMenu;
    public Pane topPthatHelpsPluses, botPthatHelpsPluses, leftPthatHelpsPluses, rightPthatHelpsPluses, topPthatHelpsCells,
            botPthatHelpsCells, leftPthatHelpsCells, rightPthatHelpsCells;
    public VBox theV;
    public StackPane sP;
    public ContextMenu rightClick, rightClick2, rightClick3;
    public Label xCoordText, yCoordText, isleText;

    /** Variables involved with backing data structures and control */
    private GridData3 g;
    private int floors, length, width;
    private final int cols, rows;
    private float cellSizeInFeet;
    private double sX, sY, xRemainderSize1, yRemainderSize1;
    private final double finalSizeOfCells;
    private boolean highlighting, moving, settingRegOpuStartEnd, settingGroOpuStartEnd, settingRegStartEnd;
    private RNode editGroupNode;
    private Aisle aisleToMove;
    private SetupIsleInfoController setupInfoStuff;
    private SetupPickingPathController setupPath;
    private GraphOfTheGrid graph;

    /**
     * Basic Constructor
     * Calculates dimensions for displaying of grid when creating new layout
     * @param f floors
     * @param l length of floor
     * @param w width of floor
     * @param x screen x dimension
     * @param y scrren y dimension
     */
    public IsleLayoutController(int f, int l, int w, double x, double y)
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
        System.out.println("Final Cell Size in Pixels: "+finalSizeOfCells);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("isleLayout.fxml"));
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
     * Passes file onward until the data can be read and implemented directly
     * @param file saved layout
     * @param x screen horizontal dimension
     * @param y screen vertical dimension
     */
    public IsleLayoutController(File file, double x, double y)
    {
        Long start = System.nanoTime();
        int cols1 = 0;
        int rows1 = 0;
        float feet = 0;

        try
        {
            Scanner scanner = new Scanner(file);

            cols1 = scanner.nextInt();
            rows1 = scanner.nextInt();
            feet = scanner.nextFloat();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found");
        }

        float ratio = (float) cols1/rows1;
        int sizeOfCells;
        if (ratio < 1.9393)
            sizeOfCells = (int) (y-25)/rows1;
        else
            sizeOfCells = (int) x/cols1;

        cols = cols1;
        rows = rows1;
        finalSizeOfCells = sizeOfCells;
        cellSizeInFeet = feet;
        sX = x;
        sY = y;
        System.out.println("Floors: "+floors);
        System.out.println("Ratio: "+ratio);
        System.out.println("Cols: "+cols);
        System.out.println("Rows: "+rows);
        System.out.println("Sx: "+sX);
        System.out.println("Sy: "+sY);
        System.out.println("Cell Size: "+sizeOfCells);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("isleLayout.fxml"));
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

        Long end = System.nanoTime();
        System.out.println("Constructor From File Time: "+(end-start));

        initializeFromFile(file);
    }

    /** Launches scene, also sets mouse/screen dimensions for debugging */
    public void launchScene(Stage stage, boolean fromFile)
    {
        this.stage = stage;
        stage.setScene(scene);
        stage.setTitle("Setup Isle Layout");

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

        stage.setMaximized(true);

        if (!fromFile)
            displayLayoutInstructions();

        displayCoordsOnScreen();
    }

    /** Initializer, also setups functionality of some menu buttons */
    private void actualInitialize()
    {
        theV.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        sP.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        back.setOnAction(actionEvent -> new CreateNewLayoutController().launchScene(stage));

        initMenus();

        initOutsidePanes(sX, sY);
        initInsidePanes(finalSizeOfCells);

        drawPluses(finalSizeOfCells, hboxWithThePluses);

        drawCells(finalSizeOfCells, hboxWithTheCells);

        fileMenu.getItems().remove(testLocation);
        fileMenu.getItems().remove(testPath);
        fileMenu.getItems().remove(testPickingPath);
    }

    /** Initializer from file */
    private void initializeFromFile(File file)
    {
        Long start = System.nanoTime();
        theV.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        sP.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        back.setOnAction(actionEvent -> new LoadOrCreateController().launchScene(stage));

        new Thread(() -> initOutsidePanes(sX, sY)).start();
        new Thread(() -> initInsidePanes(finalSizeOfCells)).start();

        AtomicReference<AtomicBoolean> initMenusDone = new AtomicReference<>(new AtomicBoolean(false));
        new Thread(() -> initMenusDone.set(initMenus())).start();

        drawPluses(finalSizeOfCells, hboxWithThePluses);

        while (!initMenusDone.get().get()) {System.out.println("Not Done");}

        System.out.println("Drew pluses w/ length: "+g.getPlusGrid().length+" and width: "+g.getPlusGrid()[0].length);

        Long end = System.nanoTime();
        System.out.println("Init From File Time: "+(end-start));

        loadCellsFromFile(finalSizeOfCells, hboxWithTheCells, file);

        fileMenu.getItems().remove(testLocation);
        fileMenu.getItems().remove(testPath);
        fileMenu.getItems().remove(testPickingPath);
    }

    /**
     * Initializes some right click menus for grouping/ungrouping and other related functions
     * Is AtomicBoolean because is called in new thread
     */
    private AtomicBoolean initMenus()
    {
        rightClick = new ContextMenu();
        MenuItem makeIsle = new MenuItem("Make Isle");
        makeIsle.setOnAction(actionEvent ->
        {
            Stage makeIsleStage = new Stage();
            makeIsleStage.setTitle("New Isle Menu");
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
            CellList.CellNode curr = g.getHighlightedList().getFirst();
            while (curr != null)
            {
                curr.getrNode().setHighlighted(false);
                g.setCelltoNull(curr.getrNode().getX(), curr.getrNode().getY(), true);
                curr = curr.getNext();
            }
            g.getHighlightedList().clear();
        });
        rightClick.getItems().add(fillIn);

        rightClick2 = new ContextMenu();
        MenuItem ungroup = new MenuItem("Delete Isle");
        ungroup.setOnAction(actionEvent -> g.removeAisle(editGroupNode.getAisle()));
        rightClick2.getItems().add(ungroup);
        MenuItem moveIsle = new MenuItem("Move Isle");
        moveIsle.setOnAction(actionEvent ->
        {
            rightClick2.hide();
            g.resetHighlighted();
            aisleToMove = editGroupNode.getAisle();
            CellList toMoveList = g.getToMoveList();
            if (!moving)
            {
                System.out.println("Ungrouping cells from isle");
                CellList.CellNode curr = editGroupNode.getAisle().getAisleCellList().getFirst();
                while (curr != null)
                {
                    System.out.println("Ungrouping curr");
                    g.getRNode(curr.getrNode().getX(), curr.getrNode().getY()).setAisled(false, null, null, null);
                    toMoveList.add(g.getRNode(curr.getrNode().getX(), curr.getrNode().getY()));
                    g.getRNode(curr.getrNode().getX(), curr.getrNode().getY()).setAisleIsBeingMoved(true, editGroupNode.getColor());
                    curr = curr.getNext();
                }
                System.out.println("Done ungrouping");
            }
            moving = true;
            new Robot().mouseMove(editGroupNode.getsXMinCoord(), editGroupNode.getsYMinCoord());
        });
        rightClick2.getItems().add(moveIsle);
        setupInfoMenu = new MenuItem("Setup Isle Info");
        setupInfoMenu.setOnAction(actionEvent ->
        {
            CellList.CellNode curr = editGroupNode.getAisle().getAisleCellList().getFirst();
            while (curr != null)
            {
                curr.getrNode().getR().setFill(Color.RED);
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
            inputStage.setTitle("Setup Isle Info: Isle "+editGroupNode.getAisle().getAisleID());
            inputStage.initOwner(stage);
            inputStage.setScene(newScene);
            inputStage.show();

            setupInfoStuff = loader.getController();
            setupInfoStuff.setImportantInfo(g, editGroupNode.getAisle(), inputStage, this);
            if (editGroupNode.getAisle().hasSetupInfo())
                setupInfoStuff.editingInfo();
        });
        rightClick2.getItems().add(setupInfoMenu);

        load.setOnAction(actionEvent ->
        {
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            double x = screenBounds.getWidth();
            double y = screenBounds.getHeight();

            final JFileChooser fc = new JFileChooser();

            File f = new File("src\\Saves");

            fc.setCurrentDirectory(f);

            int returnVal = fc.showOpenDialog(fc.getParent());

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = fc.getSelectedFile();
                new IsleLayoutController(file, x, y-63).launchScene(stage, true);
            }
        });

        if (fileMenu.getItems().contains(testLocation))
        {
            saveLayout.setOnAction(actionEvent ->
            {
                final JFileChooser fc = new JFileChooser();

                File f = new File("src\\Saves");

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
                testStage.initOwner(stage);
                VBox testVBox = testPathSetup(testStage, g, graph);
                Scene testScene = new Scene(testVBox);
                testStage.setScene(testScene);
                testStage.show();
            });
        }

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

        setupRegOPUstartEnd.setOnAction(actionEvent ->
        {
            if (g.getRegOpuStartEndNode() != null)
                g.setRegOPUstartEndNode(g.getRegOpuStartEndNode(), false);

            g.resetHighlighted();

            Label whatToDo = new Label("Click on the cell that best represents the area in the store where Regular OPU pickers start and end.");
            whatToDo.setStyle("-fx-font-size: 16;");
            ArrayList<Node> list = new ArrayList<>();
            list.add(whatToDo);
            new MyPopup(list, stage).getStage(true).show();

            settingRegOpuStartEnd = true;
        });
        setupGroOPUstartEnd.setOnAction(actionEvent ->
        {
            if (g.getGroOpuStartEndNode() != null)
                g.setGroOpuStartEndNode(g.getGroOpuStartEndNode(), false);

            g.resetHighlighted();

            Label whatToDo = new Label("Click on the cell that best represents the area in the store where Grocery OPU pickers start and end.");
            whatToDo.setStyle("-fx-font-size: 16;");
            ArrayList<Node> list = new ArrayList<>();
            list.add(whatToDo);
            new MyPopup(list, stage).getStage(true).show();

            settingGroOpuStartEnd = true;
        });
        setupStandardPickStartEnd.setOnAction(actionEvent ->
        {
            if (g.getStandardStartEndNode() != null)
                g.setStandardStartEndNode(g.getStandardStartEndNode(), false);

            g.resetHighlighted();

            Label whatToDo = new Label("Click on the cell that best represents the area in the store where Regular pickers start and end.");
            whatToDo.setStyle("-fx-font-size: 16;");
            ArrayList<Node> list = new ArrayList<>();
            list.add(whatToDo);
            new MyPopup(list, stage).getStage(true).show();

            settingRegStartEnd = true;
        });
        highlighting = false;

        layoutInstructionsMenu.setOnAction(actionEvent ->
        {
            displayLayoutInstructions();
        });
        isleInfoInstructionsMenu.setOnAction(actionEvent ->
        {
            displayIsleInfoInstructions();
        });
        displayCoords.setOnAction(actionEvent -> displayCoordsOnScreen());

        rightClick3 = new ContextMenu();
        MenuItem unnull = new MenuItem("Remove null");
        unnull.setOnAction(actionEvent -> g.removeNull());
        rightClick3.getItems().add(unnull);

        return new AtomicBoolean(true);
    }

    /**
     * Initializes padding panes on outside of grid based on screen coords
     * @param x screen x dimension
     * @param y screen y dimension
     */
    private void initOutsidePanes(double x, double y)
    {
        System.out.println("Initializing Outside Panes w/ x: "+x+" and y: "+y);
        double xRemainderSize = x-(finalSizeOfCells * cols)+3;
        double yRemainderSize = y-(finalSizeOfCells * rows)-25+2.5;
        System.out.println("x rem: "+xRemainderSize);
        System.out.println("y rem: "+yRemainderSize);

        leftPthatHelpsCells.setPrefWidth(xRemainderSize/2);
        rightPthatHelpsCells.setPrefWidth(xRemainderSize/2);
        topPthatHelpsCells.setPrefHeight(yRemainderSize/2);
        botPthatHelpsCells.setPrefHeight(yRemainderSize/2);

        xRemainderSize1 = xRemainderSize/2;
        yRemainderSize1 = yRemainderSize/2;

        System.out.println("x rem 1: "+xRemainderSize/2);
        System.out.println("x rem 2: "+xRemainderSize/2);
        System.out.println("y rem 1: "+yRemainderSize/2);
        System.out.println("y rem 2: "+yRemainderSize/2);
    }

    /**
     * Initializes padding panes on inside of outside panes based on screen coordinates
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
     * Creates and draws pluses that separate cells
     * @param z size of pluses
     * @param hbox1 hbox that pluses are added to
     */
    private void drawPluses(double z, HBox hbox1)
    {
        Long start = System.nanoTime();
        g = new GridData3(cols, rows, finalSizeOfCells, sX, sY, (int) cellSizeInFeet);

        for (int i=0; i<cols-1; i++)
        {
            VBox vbox = new VBox();

            for (int j=0; j<rows-1; j++)
            {
                StackPane sP1 = new StackPane();
                Line xLine = new Line((int) -(z/3), 0, (int) (z/3), 0);
                xLine.setStrokeWidth(1);
                Line yLine = new Line(0, (int) -(z/3), 0, (int) (z/3));
                yLine.setStrokeWidth(1);
                sP1.getChildren().addAll(xLine, yLine);

                g.addPlus(xLine, yLine, i, j);

                vbox.getChildren().add(sP1);
                vbox.setVgrow(sP1, Priority.ALWAYS);
            }
            hbox1.getChildren().add(vbox);
            hbox1.setHgrow(vbox, Priority.ALWAYS);
        }

        Long end = System.nanoTime();
        System.out.println("Draw Pluses Time: "+(end-start));
    }

    /**
     * Creates and draws cells
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
                RNode node = g.addRect(r, i, j, startX, startY);

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
     * @param z pixel size of each cell
     * @param hbox2 container to put cells into
     * @param file from which the info is loaded
     */
    private void loadCellsFromFile(double z, HBox hbox2, File file)
    {
        Long start = System.nanoTime();
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
                RNode node = g.addRect(r, i, j, startX, startY);

                setupCellsAndFunctions(r, node, z);

                startY = startY + finalSizeOfCells;

                vbox.getChildren().add(r);
                vbox.setVgrow(r, Priority.ALWAYS);
            }
            hbox2.getChildren().add(vbox);
            hbox2.setHgrow(vbox, Priority.ALWAYS);
        }
        System.out.println("Drew cells");

        try
        {
            Scanner scanner = new Scanner(file);

            int cols1 = scanner.nextInt();
            int rows1 = scanner.nextInt();
            float feet = scanner.nextFloat();
            String idk = scanner.nextLine();

            while(scanner.hasNext())
            {
                String groupName = scanner.nextLine();
                if (groupName.compareTo("Nulls") != 0)
                {
                    String c1 = scanner.nextLine();
                    String[] c2 = c1.split(" ");
                    Color groupColor = new Color(Double.parseDouble(c2[0]), Double.parseDouble(c2[1]), Double.parseDouble(c2[2]), 1.0);
                    String backOrFloor = scanner.nextLine();

                    String nextLine = scanner.nextLine();
                    int numberOfIsles = Integer.parseInt(nextLine);
                    for (int i=0; i<numberOfIsles; i++)
                    {
                        String isleID = scanner.nextLine();
                        System.out.print("isleID: "+isleID+" -> ");

                        String isleInfo = scanner.nextLine();
                        InfoToMakeAisleFromFile isleToMake = null;
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
                            isleToMake = new InfoToMakeAisleFromFile(numberOfIsleSections, subsectionsPerSection, endCap, direction);
                        }

                        String cells = scanner.nextLine();
                        //System.out.println("cells: "+cells);
                        String[] cellCoords = cells.split(",");
                        for (int j=0; j<cellCoords.length; j=j+2)
                        {
                            Coords coords = new Coords(cellCoords[j]+","+cellCoords[j+1]);
                            g.getRNode(coords.getX(), coords.getY()).setHighlighted(true);
                        }

                        if (g.aisleGroupExists(groupName))
                            g.makeAisle(isleID, groupName, groupColor, g.getAisleGroupList().get(groupName), true, backOrFloor);
                        else
                            g.makeAisle(isleID, groupName, groupColor, null, false, backOrFloor);

                        if (isleToMake != null)
                        {
                            String[] sections = isleToMake.getNumberOfSubsectionsForEachSection().split(",");
                            Hashtable<Integer, Integer> table = new Hashtable<>();
                            Arrays.stream(sections).forEach(e -> table.put(Integer.parseInt(e.split("-")[0]), Integer.parseInt(e.split("-")[1])));

                            g.getAisle(isleID, groupName).setupAisleInfo(isleToMake.getNumberOfAisleSections(), table, isleToMake.getEndCapLocation(),
                                    isleToMake.getDirectionOfIncreasingAisleSections());
                            System.out.print("setup info\n");
                        }
                        else
                            System.out.print("no info to setup\n");
                    }
                }
                else
                {String cellsToNull = scanner.nextLine();
                    try
                    {
                        int hmm = Integer.parseInt(cellsToNull.charAt(0)+"");
                        String[] cellCoords = cellsToNull.split(",");
                        for (int j=0; j<cellCoords.length; j=j+2)
                        {
                            Coords coords = new Coords(cellCoords[j]+","+cellCoords[j+1]);
                            g.setCelltoNull(coords.getX(), coords.getY(), true);
                        }
                    }
                    catch (NumberFormatException ignored) {}

                    String string1 = scanner.nextLine();
                    String[] regOpuStartEnd = string1.split(":");
                    try
                    {
                        Coords coords = new Coords(regOpuStartEnd[1]);
                        g.setRegOPUstartEndNode(g.getRNode(coords.getX(), coords.getY()), true);
                    }
                    catch (ArrayIndexOutOfBoundsException ignored) {}

                    String string2 = scanner.nextLine();
                    String[] groOpuStartEnd = string2.split(":");
                    try
                    {
                        Coords coords = new Coords(groOpuStartEnd[1]);
                        g.setGroOpuStartEndNode(g.getRNode(coords.getX(), coords.getY()), true);
                    }
                    catch (ArrayIndexOutOfBoundsException ignored) {}
                    String string3 = scanner.nextLine();
                    String[] regStartEnd = string3.split(":");
                    try
                    {
                        Coords coords = new Coords(regStartEnd[1]);
                        g.setStandardStartEndNode(g.getRNode(coords.getX(), coords.getY()), true);
                    }
                    catch (ArrayIndexOutOfBoundsException ignored) {}

                    break;
                }
            }
            scanner.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found");
        }

        graph = new GraphOfTheGrid(g);

        Long end = System.nanoTime();
        System.out.println("Load Cells Time: "+(end-start));
    }

    /**
     * Setups cell and functions associated with creating groups
     * @param r rectangle that is the cell on display
     * @param node node of drawn cell in data
     * @param z size of cells
     */
    private void setupCellsAndFunctions(Rectangle r, RNode node, double z)
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
                System.out.println("Moving Isle");
                g.moveAisle(editGroupNode, aisleToMove);
                System.out.println("Done method call");
            }
            if (node.isAisle())
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
            else if (node.isAisle())
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
            if (mouseEvent.getButton() == MouseButton.PRIMARY)
            {
                g.resetHighlighted();
                rightClick.hide();
                rightClick2.hide();
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
                    if (node.isAisle())
                    {
                        node.getAisle().printInfo();

                        if (!node.getAisle().hasSetupInfo())
                            setupInfoMenu.setText("Setup Isle Info");
                        else
                            setupInfoMenu.setText("Edit Isle Info");

                        RNode rNode = g.getSoutheastMostAisleCell(node.getAisle());
                        rightClick2.show(rNode.getR(), rNode.getsXMaxCoord()+finalSizeOfCells, rNode.getsYMaxCoord()+finalSizeOfCells);
                        editGroupNode = node;
                    }
                    else
                    {
                        System.out.println("No Isle/IsleGroup");
                        node.setHighlighted(true);

                        if (graph != null)
                        {
                            System.out.println("Connects to: ");
                            Edge curr = graph.graph.get(node.getX()+","+node.getY());
                            while (curr != null)
                            {
                                System.out.print(curr.getW()+" ");
                                if (curr.getNext() == null)
                                    System.out.print("\n");
                                curr = curr.getNext();
                            }
                        }

                        if (g.getHighlightedList().size() == 1)
                        {
                            if (settingRegOpuStartEnd)
                            {
                                g.setRegOPUstartEndNode(node, true);

                                settingRegOpuStartEnd = false;
                            }
                            else if (settingGroOpuStartEnd)
                            {
                                g.setGroOpuStartEndNode(node, true);

                                settingGroOpuStartEnd = false;
                            }
                            else if (settingRegStartEnd)
                            {
                                g.setStandardStartEndNode(node, true);

                                settingRegStartEnd = false;
                            }
                            else
                            {
                                RNode rNode = g.getSoutheastMostHighlightedCell("Normal");
                                rightClick.show(rNode.getR(), rNode.getsXMaxCoord()+finalSizeOfCells, rNode.getsYMaxCoord()+finalSizeOfCells);
                            }
                        }
                    }
                    if (g.nodeIsPickPoint(node.getX(), node.getY()))
                    {
                        System.out.println("Cell is Pick Point");
                    }
                }
                if (moving && !node.isNulled() && !node.isAisle())
                {
                    System.out.println("Remade Isle After Move");
                    node.setHighlighted(false);
                    g.makeAisleFromToMoveList(aisleToMove.getAisleID(), aisleToMove.getAisleGroup().getName(), aisleToMove.getAisleGroup().getColor(), aisleToMove.getAisleGroup(), aisleToMove);
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
                    if (!node.isAisle())
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
                g.resetHighlighted();
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
            rightClick2.hide();
            rightClick3.hide();
            if (!moving)
            {
                if (!node.isNulled() && !node.isAisle() && g.getHighlightedList().size() > 1)
                {
                    RNode rNode = g.getSoutheastMostHighlightedCell("Normal");
                    rightClick.show(rNode.getR(), rNode.getsXMaxCoord()+finalSizeOfCells, rNode.getsYMaxCoord()+finalSizeOfCells);
                }
                else if (node.isNulled())
                {
                    RNode rNode = g.getSoutheastMostHighlightedCell("Nulls");
                    if (rNode != null)
                        rightClick3.show(rNode.getR(), rNode.getsXMaxCoord()+finalSizeOfCells, rNode.getsYMaxCoord()+finalSizeOfCells);
                    editGroupNode = node;
                }
            }
            //System.out.println("highlightingIterations: "+highlightingIterations);
        });

        r.setLayoutX(0);
        r.setLayoutY(0);
        r.setWidth(z-1);
        r.setHeight(z-1);
    }

    /**
     * Sets up popup window for creating an isle for a highlighted are
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
        ImageView iv = new ImageView(Objects.requireNonNull(getClass().getResource("Pictures/makeIsleInfo.jpg")).toExternalForm());
        iv.setFitWidth(350);
        iv.setFitHeight(61.5);
        v.getChildren().add(iv);
        Button addToExisting = new Button("Add Isle to Existing Group");
        addToExisting.setOnAction(actionEvent ->
        {
            if (idT.getText().trim().isEmpty())
            {
                ArrayList<Node> list = new ArrayList<>();
                Label label = new Label("Please Input an Isle Letter & Number");
                list.add(label);
                new MyPopup(list, stage).getStage(true).show();
            }
            else if (g.getAisleGroupList().size() > 0)
            {
                String isleID = idT.getText();

                Stage addToExistingStage = new Stage();
                addToExistingStage.initModality(Modality.APPLICATION_MODAL);
                addToExistingStage.initOwner(stage);
                VBox addToExistingVBox = addToExistingGroupMenu(addToExistingStage, g, isleID, s);
                Scene groupSelectScene = new Scene(addToExistingVBox);
                addToExistingStage.setScene(groupSelectScene);
                addToExistingStage.show();
            }
            else
            {
                Label label = new Label("There are no existing isle groups, please create a new one.");
                ArrayList<Node> list = new ArrayList<>();
                list.add(label);
                new MyPopup(list, stage).getStage(true).show();
            }
        });
        Button makeNew = new Button("Create New Isle Group");
        makeNew.setOnAction(actionEvent ->
        {
            if (idT.getText().trim().isEmpty())
            {
                ArrayList<Node> list = new ArrayList<>();
                Label label = new Label("Please Input an Isle Letter & Number");
                list.add(label);
                new MyPopup(list, stage).getStage(true).show();
            }
            else
            {
                String isleID = idT.getText();

                Stage groupSelect = new Stage();
                groupSelect.setTitle("Select Isle Group Name and Color");
                groupSelect.initModality(Modality.APPLICATION_MODAL);
                groupSelect.initOwner(stage);
                VBox groupSelectVbox = setupGroupCreation(groupSelect, g, isleID, s);
                Scene groupSelectScene = new Scene(groupSelectVbox);
                groupSelect.setScene(groupSelectScene);
                groupSelect.show();
            }
        });
        v.getChildren().addAll(makeNew, addToExisting);

        return v;
    }

    /**
     * Sets up popup window for isle group creation
     * @param s stage
     * @param g data used to create and implement new group
     * @return vbox with all necessary elements
     */
    private VBox setupGroupCreation(Stage s, GridData3 g, String isleID, Stage previousWindowStage)
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
        ImageView iv = new ImageView(Objects.requireNonNull(getClass().getResource("Pictures/newIsleGroupInfo.jpg")).toExternalForm());
        iv.setFitWidth(290);
        iv.setFitHeight(48.1);
        HBox hboxC = new HBox();
        hboxC.setAlignment(Pos.CENTER);
        Label bOrF = new Label("Isle Group in Back or on Floor: ");
        bOrF.setStyle("-fx-font-size: 16;");
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setItems(FXCollections.observableArrayList("floor", "back"));
        hboxC.getChildren().addAll(bOrF, choiceBox);
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
                ArrayList<Node> list = new ArrayList<>();
                Label label = new Label("Please Input a Isle Group Name");
                list.add(label);
                new MyPopup(list, stage).getStage(true).show();
            }
            else
            {
                s.hide();
                g.makeAisle(isleID, nameT.getText(), colorP.getValue(), null, false, choiceBox.getValue().toString());
                previousWindowStage.hide();
            }
        });
        v.getChildren().addAll(hboxA, iv, hboxC, hboxD, submit);

        return v;
    }

    /**
     * Sets up popup window for adding new isle to existing isle group
     * @param s stage
     * @param g data used to create and implement new group
     * @param isleID of new isle
     * @return vbox with all necessary elements
     */
    private VBox addToExistingGroupMenu(Stage s, GridData3 g, String isleID, Stage previousWindowStage)
    {
        VBox v = new VBox();
        v.setPrefWidth(120);

        v.setAlignment(Pos.CENTER);

        g.getAisleGroupList().keySet().forEach(e ->
        {
            Button group = new Button("Group: "+g.getAisleGroupList().get(e).getName());
            group.setTextFill(Color.WHITE);
            String color = g.getAisleGroupList().get(e).getColor().toString();
            String[] strings = color.split("x");
            String string = "-fx-background-color: #"+strings[1]+";";
            group.setStyle(string);
            group.setOnAction(actionEvent ->
            {
                if (g.getAisleGroupList().get(e).containsAisle(isleID))
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
                        previousWindowStage.hide();
                        g.addNewToExistingAisle(isleID, g.getAisleGroupList().get(e).getColor(), g.getAisleGroupList().get(e));
                    });
                    warningVbox.getChildren().addAll(warningLabel1, warningLabel2, warningLabel3, yes);
                    Scene cellSizeScene = new Scene(warningVbox);
                    warningStage.setScene(cellSizeScene);
                    warningStage.show();
                }
                else
                {
                    g.makeAisle(isleID, g.getAisleGroupList().get(e).getName(), g.getAisleGroupList().get(e).getColor(),
                            g.getAisleGroupList().get(e), true, null);
                    s.hide();
                    previousWindowStage.hide();
                }
            });
            v.getChildren().add(group);
        });

        return v;
    }

    /**
     * Writes all necessary info to file for saving/loading
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

            g.getAisleGroupList().keySet().forEach(ig ->
            {
                AisleGroup aisleGroup = g.getAisleGroupList().get(ig);
                fileStream.println(aisleGroup.getName());
                Color color = aisleGroup.getColor();
                fileStream.println(color.getRed()+" "+color.getGreen()+" "+color.getBlue());
                fileStream.println(aisleGroup.getBackOrFloor());
                fileStream.println(aisleGroup.getAisleIDList().size());

                for (String isle : aisleGroup.getAisleIDList().keySet())
                {
                    fileStream.println(aisleGroup.getAisleIDList().get(isle).getAisleID());
                    if (aisleGroup.getAisleIDList().get(isle).hasSetupInfo())
                    {
                        fileStream.println("Has Setup Info");
                        fileStream.println(aisleGroup.getAisleIDList().get(isle).getNumberOfAisleSections());
                        Hashtable<Integer, Integer> table = aisleGroup.getAisleIDList().get(isle).getNumberOfSubsectionsForEachSection();
                        for (int i : table.keySet())
                            fileStream.print(i+"-"+table.get(i)+",");
                        fileStream.print("\n");
                        fileStream.println(aisleGroup.getAisleIDList().get(isle).getEndCapLocation());
                        fileStream.println(aisleGroup.getAisleIDList().get(isle).getDirectionOfIncreasingAisleSections());
                    }
                    else
                        fileStream.println("No Setup Info");
                    CellList isleCellList = aisleGroup.getAisleIDList().get(isle).getAisleCellList();
                    CellList.CellNode curr = isleCellList.getFirst();
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
            });

            fileStream.println("Nulls");
            if (g.getNullList().keySet().size() > 0)
            {
                g.getNullList().keySet().forEach(e -> fileStream.print(e+","));
                fileStream.print("\n");
            }
            else
                fileStream.println("No nulls to null");

            if (g.getRegOpuStartEndNode() == null)
                fileStream.println("No Reg OPU start/end");
            else
                fileStream.println("Reg OPU start/end:"+g.getRegOpuStartEndNode().getX()+","+g.getRegOpuStartEndNode().getY());

            if (g.getGroOpuStartEndNode() == null)
                fileStream.println("No Gro OPU start/end");
            else
                fileStream.println("Gro OPU start/end:"+g.getGroOpuStartEndNode().getX()+","+g.getGroOpuStartEndNode().getY());

            if (g.getStandardStartEndNode() == null)
                fileStream.println("No Standard start/end");
            else
                fileStream.println("Standard start/end:"+g.getStandardStartEndNode().getX()+","+g.getStandardStartEndNode().getY());

            fileStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Saved new layout to "+file);
    }

    /** Used for testing cell finding given location */
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

            Aisle aisleOfTest = null;
            System.out.println();
            try
            {
                int hmm = Integer.parseInt(loc.charAt(0)+"");
                System.out.println("Isle in the back");
                String[] sArr = loc.split(" ");
                if (g.aisleGroupExists(sArr[0]))
                {
                    aisleOfTest = g.getAisle(sArr[0]+sArr[1]+"", sArr[0]);
                    System.out.println("IsleID: "+aisleOfTest.getAisleID());
                    if (aisleOfTest.hasSetupInfo())
                    {
                        String subsection = sArr[2].charAt(sArr[2].length()-1)+"";
                        System.out.println("isleSubsection: "+subsection);

                        if (aisleOfTest.inputingValidAisleLocationInBack(subsection))
                            System.out.println("Coords Found: "+aisleOfTest.getCoordsGivenLocationInBack(subsection));
                        else
                        {
                            ArrayList<Node> list = new ArrayList<>();
                            Label label = new Label("Isle Section/Isle Subsection not valid within possible isle locations");
                            list.add(label);
                            new MyPopup(list, stage).getStage(true).show();
                        }
                    }
                    else
                    {
                        ArrayList<Node> list = new ArrayList<>();
                        Label label = new Label("Isle: "+sArr[0]+sArr[1]+" does not exist/has not been setup");
                        list.add(label);
                        new MyPopup(list, stage).getStage(true).show();
                    }
                }
                else
                {
                    ArrayList<Node> list = new ArrayList<>();
                    Label label = new Label("Isle Group does not exist");
                    list.add(label);
                    new MyPopup(list, stage).getStage(true).show();
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println("Isle on the floor");
                if (g.aisleGroupExists(loc.charAt(0)+""))
                {
                    String[] loc1 = loc.split("\\(");
                    System.out.println("IsleID: "+loc1[0]);
                    aisleOfTest = g.getAisle(loc1[0], loc.charAt(0)+"");
                    if (aisleOfTest != null && aisleOfTest.hasSetupInfo())
                    {
                        String[] loc2 = loc1[1].split("\\) ");
                        int isleSection = Integer.parseInt(loc2[0]);
                        System.out.println("isleSection: "+isleSection);

                        String[] loc3 = loc2[1].split("-");
                        System.out.println("isleSubsection: "+loc3[0]);

                        if (aisleOfTest.inputingValidAisleLocationOnFloor(isleSection, loc3[0]))
                            System.out.println("Coords Found: "+aisleOfTest.getCoordsGivenLocationOnFloor(isleSection, loc3[0]));
                        else
                        {
                            ArrayList<Node> list = new ArrayList<>();
                            Label label = new Label("Isle Section/Isle Subsection not valid within possible isle locations");
                            list.add(label);
                            new MyPopup(list, stage).getStage(true).show();
                        }
                    }
                    else
                    {
                        ArrayList<Node> list = new ArrayList<>();
                        Label label = new Label("Isle: "+loc1[0]+" does not exist/has not been setup");
                        list.add(label);
                        new MyPopup(list, stage).getStage(true).show();
                    }
                }
                else
                {
                    ArrayList<Node> list = new ArrayList<>();
                    Label label = new Label("Isle Group does not exist");
                    list.add(label);
                    new MyPopup(list, stage).getStage(true).show();
                }
            }
        });

        v.getChildren().addAll(title, tester);

        return v;
    }

    /** Used for testing path between two cells */
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
            DistanceReturn dr;
            try
            {
                int hmm = Integer.parseInt(v2.getText().charAt(0)+"");
                dr = graph.findDistanceBetween(v1.getText(), v2.getText());
            }
            catch (NumberFormatException e)
            {
                dr = graph.findClosestCellAndComputeDistanceIfIsleShapeIsArea(v2.getText(), v1.getText()).get(0);
            }
            System.out.println("Distance: "+dr.getDistance());
            String path = dr.getPath();
            System.out.println("Path:"+path);
            String[] pathArr = path.split(" ");
            for (int i=1; i< pathArr.length; i++)
            {
                try
                {
                    Coords coords = new Coords(pathArr[i]);
                    Rectangle r = g.getRNode(coords.getX(), coords.getY()).getR();
                    r.setFill(Color.RED);
                    r.setOpacity(0.5);
                }
                catch (NumberFormatException ignored) {}
            }
        });

        v.getChildren().addAll(title, h, done);
        return v;
    }

    /** Displays helpful info about making the layout */
    private void displayLayoutInstructions()
    {
        ArrayList<Node> list = new ArrayList<>();
        ImageView iv = new ImageView(Objects.requireNonNull(getClass().getResource("Pictures/introInfoFirst.jpg")).toExternalForm());
        iv.setFitWidth(600);
        iv.setFitHeight(337.5);
        ImageView iv2 = new ImageView(Objects.requireNonNull(getClass().getResource("Pictures/introInfoSecond.jpg")).toExternalForm());
        iv2.setFitWidth(600);
        iv2.setFitHeight(337.5);
        HBox hbox = new HBox();
        hbox.getChildren().addAll(iv, iv2);
        list.add(hbox);
        ImageView iv3 = new ImageView(Objects.requireNonNull(getClass().getResource("Pictures/introInfoThird.jpg")).toExternalForm());
        iv3.setFitWidth(600);
        iv3.setFitHeight(170.93);
        list.add(iv3);
        Label cellSize = new Label("Each cell represents "+cellSizeInFeet+" feet.");
        cellSize.setStyle("-fx-font-size: 16;");
        list.add(cellSize);
        new MyPopup(list, stage).getStage(true).show();
    }

    /** Displays helpful info about setting isle info */
    public void displayIsleInfoInstructions()
    {
        ArrayList<Node> list = new ArrayList<>();
        ImageView iv = new ImageView(Objects.requireNonNull(getClass().getResource("Pictures/setupIsleInfoPicFirst.jpg")).toExternalForm());
        iv.setFitWidth(600);
        iv.setFitHeight(337.5);
        list.add(iv);
        ImageView iv2 = new ImageView(Objects.requireNonNull(getClass().getResource("Pictures/setupIsleInfoPicSecond.jpg")).toExternalForm());
        iv2.setFitWidth(600);
        iv2.setFitHeight(232.5);
        list.add(iv2);
        new MyPopup(list, stage).getStage(true).show();
    }

    /** Little window that displays grid coordinates of the mouse and isle if so */
    public void displayCoordsOnScreen()
    {
        xCoordText = new Label("X Coordinate: 0");
        yCoordText = new Label("Y Coordinate: 0");
        xCoordText.setStyle("-fx-font-size: 16;");
        yCoordText.setStyle("-fx-font-size: 16;");
        isleText = new Label("Isle: null");
        isleText.setStyle("-fx-font-size: 16;");
        Label move = new Label("Move this window");
        Label move1 = new Label("wherever you need.");
        move.setStyle("-fx-font-size: 10;");
        move1.setStyle("-fx-font-size: 10;");
        ArrayList<Node> list = new ArrayList<>();
        list.add(xCoordText);
        list.add(yCoordText);
        list.add(isleText);
        list.add(move);
        list.add(move1);
        Stage popup = new MyPopup(list, stage).getStage(false);
        popup.setWidth(175);
        popup.show();
    }

    /**
     * Displays cell coordinates corresponding to mouse on screen
     * @param x mouse x coord
     * @param y mouse y coord
     */
    private void setMouseCoordsOnScreen(int x, int y)
    {
        xCoordText.setText("X Coordinate: "+x);
        yCoordText.setText("Y Coordinate: "+y);
        try
        {
            isleText.setText("Isle: "+g.getRNode(x, y).getAisle().getAisleID());
        }
        catch (NullPointerException e)
        {
            isleText.setText("Isle: null");
        }
    }

    /**
     * Adjusts size of elements on display for a change in window size
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
     * @param x screen x dimension
     */
    public void sendScreenX(double x) {sX = x;}

    /**
     * Displays screen y dimension for debugging
     * @param y screen y dimension
     */
    public void sendScreenY(double y) {sY = y;}
}