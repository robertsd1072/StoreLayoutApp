/**
 * ActualController3 class for project DJR_Store_Layout
 * Controls store layout and all accompanying functionality
 * Doubles as controlling class for isle layout setup
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
import javafx.scene.image.Image;
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    public MenuItem resize, reset, printGroups, printNulls, saveLayout, setupIsleLayoutMenuItem, addToGroup;
    public Menu file, m1, m2, m3, m4;
    public MenuBar menuBar;
    public Pane topPthatHelpsPluses, botPthatHelpsPluses, leftPthatHelpsPluses, rightPthatHelpsPluses, topPthatHelpsCells, botPthatHelpsCells, leftPthatHelpsCells, rightPthatHelpsCells;
    public VBox theV;
    public StackPane sP;
    public ContextMenu rightClick, rightClick2, rightClick3, addToGroupList;

    /**
     * Variables involved with backing data structures and control
     */
    private GridData3 g;
    private int floors, length, width;
    private final int cols, rows;
    private float cellSizeInFeet;
    private double sX, sY, xRemainderSize1, yRemainderSize1, addToGroupMouseX, addToGroupMouseY;
    private final double finalSizeOfCells;
    private boolean editGroupBool, contextMenuShowing;
    private boolean setupIsleLayout;
    private GridData3.RNode editGroupNode, addToGroupNode, setupIslesNode;

    /**
     * Basic Constructor
     * Calculates dimensions for displaying of grid
     *
     * @param f floors
     * @param l length of floor
     * @param w width of floor
     * @param x screen x dimension
     * @param y scrren y dimension
     * @param setupIsles boolean to check for isle layout setup
     * @param cellSize cell dimensions based on length, width, screen
     * @param node used for accessing node data in isle layout setup
     * @param cM rightClick context menu that ?????
     */
    public ActualController3(int f, int l, int w, double x, double y, boolean setupIsles, float cellSize, GridData3.RNode node, ContextMenu cM)
    {
        floors = f;
        length = l;
        width = w;
        float ratio = (float) l/w;
        sX = x;
        sY = y;
        setupIsleLayout = setupIsles;
        rightClick3 = cM;
        contextMenuShowing = false;

        if (setupIsles)
            setupIslesNode = node;

        rows = width/2;
        cols = length/2;
        cellSizeInFeet = 2;

        int sizeOfCells = 0;
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

        actualInitialize(false);
    }

    /*
     * NOT CURRENTLY BEING USED
     * Constructor used when viewing previously made isle layout
     *
     * @param grid existing isle layout data
     * @param node used for accessing node data in isle layout view

    public ActualController3(GridData3 grid, GridData3.RNode node)
    {
        cols = grid.getColSize();
        rows = grid.getRowSize();
        sX = grid.getScreenX();
        sY = grid.getScreenY();
        finalSizeOfCells = grid.getBoxSize();
        g = grid;
        setupIsleLayout = true;
        setupIslesNode = node;
        cellSizeInFeet = grid.getCellSize();
        contextMenuShowing = false;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("actual.fxml"));
        loader.setController(this);
        try
        {
            parent = loader.load();
            scene = new Scene(parent, sX, sY);
        }
        catch (IOException ex)
        {
            System.out.println("Error displaying login window");
            throw new RuntimeException(ex);
        }

        actualInitialize(true);
    }
    */

    /**
     * Constructor used when loading layout from file
     *
     * @param file saved layout
     * @param x screen horizontal dimension
     * @param y screen vertical dimension
     */
    public ActualController3(File file, double x, double y)
    {
        int cols1 = 0;
        int rows1 = 0;
        double cellSize = 0;
        float feet = 0;
        ArrayList<String> isleGroupNames = new ArrayList<>();
        ArrayList<Color> isleGroupColors = new ArrayList<>();
        ArrayList<ArrayList<String>> listOfIsleIDs = new ArrayList<>();
        ArrayList<ArrayList<Hashtable<String, String>>> listOfIslesWithCells = new ArrayList<>();
        String cellsToNull = null;
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
                cellSize = Double.parseDouble(c);
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
                    System.out.println("Added "+line+" to isleGroupNames");
                    String c1 = scanner.nextLine();
                    String[] c2 = c1.split(" ");
                    isleGroupColors.add(new Color(Double.parseDouble(c2[0]), Double.parseDouble(c2[1]), Double.parseDouble(c2[2]), 1.0));
                    System.out.println("Added color to isleGroupColors");

                    ArrayList<Hashtable<String, String>> islesWithCells = new ArrayList<>();
                    ArrayList<String> isleIDs = new ArrayList<>();

                    String nextLine = scanner.nextLine();
                    int numberOfIsles = Integer.parseInt(nextLine);
                    System.out.println("numberOfIsles: "+numberOfIsles);
                    for (int i=0; i<numberOfIsles; i++)
                    {
                        Hashtable<String, String> isle = new Hashtable<>();

                        String isleID = scanner.nextLine();
                        System.out.println("isleID: "+isleID);
                        String cells = scanner.nextLine();
                        System.out.println("cells: "+cells);

                        isleIDs.add(isleID);

                        isle.put(isleID, cells);
                        islesWithCells.add(isle);
                    }
                    listOfIsleIDs.add(isleIDs);
                    listOfIslesWithCells.add(islesWithCells);
                }
                else
                {
                    if (scanner.hasNext())
                        cellsToNull = scanner.nextLine();
                }
            }
            scanner.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found");
        }

        cols = cols1;
        rows = rows1;
        finalSizeOfCells = cellSize;
        cellSizeInFeet = feet;
        sX = x;
        sY = y;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("actual.fxml"));
        loader.setController(this);
        try
        {
            parent = loader.load();
            scene = new Scene(parent, sX, sY);
        }
        catch (IOException ex)
        {
            System.out.println("Error displaying login window");
            throw new RuntimeException(ex);
        }

        initializeFromFile(isleGroupNames, isleGroupColors, listOfIslesWithCells, listOfIsleIDs, cellsToNull);
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

        stage.addEventFilter(MouseEvent.MOUSE_MOVED, e ->
                this.sendMouse(e.getSceneX(), e.getSceneY()));

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
     * Setups functionality of some menu buttons
     *
     * @param viewingGrid boolean for determining if viewing an already existing isle layout
     */
    private void actualInitialize(boolean viewingGrid)
    {
        theV.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        sP.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        if (!viewingGrid)
        {
            resize.setOnAction(actionEvent -> {
                try {
                    new SampleController2().launchScene(stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        else
            file.getItems().remove(resize);

        reset.setOnAction(actionEvent -> g.resetGrid());

        printGroups.setOnAction(actionEvent -> g.printGroups());

        printNulls.setOnAction(actionEvent -> g.printNulls());

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

        if (!viewingGrid)
        {
            drawPluses(finalSizeOfCells, hboxWithThePluses);

            drawCells(finalSizeOfCells, hboxWithTheCells);
        }
        else
        {
            loadPluses(finalSizeOfCells, hboxWithThePluses);

            loadCells(hboxWithTheCells);
        }

        //System.out.println("sp Dimension: "+sP.getWidth()+", "+sP.getHeight());
    }

    private void initializeFromFile(ArrayList<String> groupNames, ArrayList<Color> groupColors, ArrayList<ArrayList<Hashtable<String, String>>> listOfIslesWithCells, ArrayList<ArrayList<String>> listOfIsleIDs, String cellsToNull)
    {
        theV.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        sP.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        reset.setOnAction(actionEvent -> g.resetGrid());

        printGroups.setOnAction(actionEvent -> g.printGroups());

        printNulls.setOnAction(actionEvent -> g.printNulls());

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

        loadCellsFromFile(finalSizeOfCells, hboxWithTheCells, groupNames, groupColors, listOfIslesWithCells, listOfIsleIDs, cellsToNull);
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
    }

    /**
     * Initializes padding panes on outside of grid based on screen coords
     *
     * @param x screen x dimension
     * @param y screen y dimension
     */
    private void initOutsidePanes(double x, double y)
    {
        //System.out.println("Initializing Outside Panes w/ x: "+x+" and y: "+y);
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

        leftPthatHelpsCells.setPrefWidth(xRemainderSize1);
        rightPthatHelpsCells.setPrefWidth(xRemainderSize2);
        topPthatHelpsCells.setPrefHeight(yRemainderSize1);
        botPthatHelpsCells.setPrefHeight(yRemainderSize2);

        //System.out.println("x rem 1: "+xRemainderSize1);
        //System.out.println("x rem 2: "+xRemainderSize2);
        //System.out.println("y rem 1: "+yRemainderSize1);
        //System.out.println("y rem 2: "+yRemainderSize2);
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
     * Loads pluses for viewing of isle layout
     *
     * @param z size of pluses
     * @param hbox1 hbox that pluses are added to
     */
    private void loadPluses(Double z, HBox hbox1)
    {
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
        leftPthatHelpsPluses.setPrefWidth((int) x/2);
        rightPthatHelpsPluses.setPrefWidth((int) x/2);
        topPthatHelpsPluses.setPrefHeight((int) x/2);
        botPthatHelpsPluses.setPrefHeight((int) x/2);
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
     * Loads cells for viewing of isle layout
     *
     * @param hbox2 hbox cells are added to
     */
    private void loadCells(HBox hbox2)
    {
        for (int i=0; i<cols; i++)
        {
            VBox vbox = new VBox();

            for (int j=0; j<rows; j++)
            {
                Rectangle r = g.getRNode(i, j).getR();

                vbox.getChildren().add(r);
                vbox.setVgrow(r, Priority.ALWAYS);
            }
            hbox2.getChildren().add(vbox);
            hbox2.setHgrow(vbox, Priority.ALWAYS);
        }
    }

    private void loadCellsFromFile(double z, HBox hbox2, ArrayList<String> groupNames, ArrayList<Color> groupColors, ArrayList<ArrayList<Hashtable<String, String>>> listOfIslesWithCells, ArrayList<ArrayList<String>> listOfIsleIDs, String cellsToNull)
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
                System.out.println("Grouping: "+listOfIsleIDs.get(i).get(j));
                String[] groupCoords = listOfIslesWithCells.get(i).get(j).get(listOfIsleIDs.get(i).get(j)).split(",");
                for (int k=0; k<groupCoords.length; k=k+2)
                {
                    g.getRNode(Integer.parseInt(groupCoords[k]), Integer.parseInt(groupCoords[k+1])).setHighlighted(true);
                    System.out.println("Highlighting: "+Integer.parseInt(groupCoords[k])+","+Integer.parseInt(groupCoords[k+1]));
                }
                if (g.isleGroupList.containsKey(groupNames.get(i)))
                {
                    System.out.println("Adding "+listOfIsleIDs.get(i).get(j)+" to existing isle group");
                    g.makeIsle(listOfIsleIDs.get(i).get(j), groupNames.get(i), groupColors.get(i), g.isleGroupList.get(groupNames.get(i)), true);
                }
                else
                {
                    System.out.println("New isle group for "+listOfIsleIDs.get(i).get(j));
                    g.makeIsle(listOfIsleIDs.get(i).get(j), groupNames.get(i), groupColors.get(i), null, false);
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
            if (node.isIsle())
            {
                r.setFill(node.getColor());
                r.setOpacity(0.5);
            }
            else if (!node.isNulled())
            {
                r.setFill(Color.GRAY);
                r.setStroke(Color.GRAY);
                r.setOpacity(0.5);
            }
        });
        r.setOnMouseExited(mouseEvent ->
        {
            if (node.isHighlighted())
            {
                r.setFill(Color.GRAY);
                r.setOpacity(1.0);
            }
            else if (node.isIsle())
            {
                r.setFill(node.getColor());
                r.setOpacity(1.0);
            }
            else if (!node.isNulled())
            {
                r.setFill(Color.TRANSPARENT);
                r.setStroke(Color.TRANSPARENT);
                r.setOpacity(0.5);
            }
        });
        r.setOnMouseClicked(mouseEvent ->
        {
            if (mouseEvent.getButton() == MouseButton.SECONDARY)
            {
                if (node.isHighlighted())
                {
                    rightClick.show(r, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    contextMenuShowing = true;
                }
                else if (node.isIsle())
                {
                    rightClick2.show(r, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    editGroupNode = node;
                }
                else
                    contextMenuShowing = false;
            }
            else if (mouseEvent.getButton() == MouseButton.PRIMARY)
            {
                if (!contextMenuShowing)
                {
                    System.out.println(node.getX()+","+node.getY());
                    //System.out.println(node.printSCoords());
                    g.resetHighlighted();
                    System.out.print("Group: ");
                    if (node.isIsle())
                    {
                        System.out.println("Isle ID: "+node.getIsle().getIsleID());
                        System.out.println("Isle Group: "+node.getIsleGroup().getName());
                    }
                    else
                    {
                        System.out.println("null");
                        node.setHighlighted(true);
                    }
                }
                else
                    contextMenuShowing = false;
            }
        });
        r.setOnMouseDragged(mouseEvent ->
        {
            if (mouseEvent.getButton() == MouseButton.PRIMARY && !node.isNulled())
            {
                contextMenuShowing = false;
                g.highlight(node, mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });

        r.setLayoutX(0);
        r.setLayoutY(0);
        r.setWidth(z-1);
        r.setHeight(z-1);
    }

    private VBox makeIsleMenu(Stage s, GridData3 g)
    {
        VBox v = new VBox();

        v.setAlignment(Pos.CENTER);
        v.setSpacing(5);
        Label title = new Label("New Isle Menu");
        title.setStyle("-fx-font-size: 20;");
        HBox hboxA = new HBox();
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
                groupSelect.initModality(Modality.APPLICATION_MODAL);
                groupSelect.initOwner(stage);
                VBox groupSelectVbox = setupGroupCreation(groupSelect, g, isleID);
                Scene groupSelectScene = new Scene(groupSelectVbox);
                groupSelect.setScene(groupSelectScene);
                groupSelect.show();
            }
        });
        v.getChildren().addAll(title, addToExisting, makeNew);

        return v;
    }

    /**
     * Setups popup window for group creation for a highlighted area
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
        Label title = new Label("Select Isle Group Name and Color");
        title.setStyle("-fx-font-size: 20;");
        HBox hboxA = new HBox();
        Label name = new Label("Input Name: ");
        name.setStyle("-fx-font-size: 16;");
        TextField nameT = new TextField();
        hboxA.getChildren().addAll(name, nameT);
        HBox hboxB = new HBox();
        Label color = new Label("Select Color: ");
        color.setStyle("-fx-font-size: 16;");
        ColorPicker colorP = new ColorPicker();
        hboxB.getChildren().addAll(color, colorP);
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
                g.makeIsle(isleID, nameT.getText(), colorP.getValue(), null, false);
            }
        });
        v.getChildren().addAll(title, hboxA, hboxB, submit);

        return v;
    }

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
            group.setOnAction(actionEvent12 ->
            {
                g.makeIsle(isleID, g.isleGroupList.get(key).getName(), g.isleGroupList.get(key).getColor(), g.isleGroupList.get(key), true);
                s.hide();
            });
            v.getChildren().add(group);
        }

        return v;
    }

    private void saveFile(File file)
    {
        try
        {
            PrintStream fileStream = new PrintStream(file);

            fileStream.println(cols);
            fileStream.println(rows);
            fileStream.println(finalSizeOfCells);
            fileStream.println(cellSizeInFeet);

            Set<String> groups = g.isleGroupList.keySet();
            for (String key : groups)
            {
                GridData3.IsleGroup isleGroup = g.isleGroupList.get(key);
                fileStream.println(isleGroup.getName());
                Color color = isleGroup.getColor();
                fileStream.println(color.getRed()+" "+color.getGreen()+" "+color.getBlue());
                fileStream.println(isleGroup.getIsleIDList().size());

                Set<String> isleIDS = isleGroup.getIsleIDList().keySet();
                for (String idKey : isleIDS)
                {
                    fileStream.println(isleGroup.getIsleIDList().get(idKey).getIsleID());
                    GridData3.IsleCellList isleCellList = isleGroup.getIsleIDList().get(idKey).getIsleCellList();
                    GridData3.IsleCellList.IsleCellNode curr = isleCellList.first;
                    while (curr != null)
                    {
                        fileStream.print(curr.rNode.getX()+","+curr.rNode.getY());

                        if (curr.next != null)
                            fileStream.print(",");
                        else
                            fileStream.print("\n");

                        curr = curr.next;
                    }
                }
            }

            fileStream.println("Nulls");
            GridData3.NullList.NullNode curr = g.nullList.first;
            while (curr != null)
            {
                fileStream.print(curr.rNode.getX()+","+curr.rNode.getY());

                if (curr.next != null)
                    fileStream.print(",");
                else
                    fileStream.print("\n");

                curr = curr.next;
            }

            fileStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Saved new layout to "+file);
    }

    /**
     * Displays mouse coordinates for debugging
     *
     * @param x mouse x coord
     * @param y mouse y coord
     */
    public void sendMouse(double x, double y)
    {
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
        //hbox1.getChildren().clear();
        //hbox2.getChildren().clear();

        initOutsidePanes(x, y);

        //drawPluses(x1, hbox1);

        initInsidePanes(finalSizeOfCells);

        //drawCells(x1, hbox2);

        g.adjust(finalSizeOfCells, xRemainderSize1, yRemainderSize1);
    }

    /**
     * Displays screen x dimension for debuggind
     *
     * @param x screen x dimension
     */
    public void sendScreenX(double x)
    {
        sX = x;
        m3.setText("ScreenX: " + x);
    }

    /**
     * Displays screen y dimension for debuggind
     *
     * @param y screen y dimension
     */
    public void sendScreenY(double y)
    {
        sY = y;
        m4.setText("ScreenY: " + y);
    }
}