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
import java.util.Scanner;
import java.util.Set;

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
    public MenuItem resize, reset, printGroups, saveLayout, setupIsleLayoutMenuItem, addToGroup;
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

        if (ratio < 1.9393)
        {
            rows = (int) (sY-25)/15;
            cols = (int) (ratio*rows);
            if (!setupIsles)
                cellSizeInFeet = (float) w/rows;
            else
            {
                cellSizeInFeet = cellSize*width/rows;
                if (cellSizeInFeet == cellSize)
                    cellSizeInFeet = 1;
            }
        }
        else
        {
            cols = (int) sX/15;
            rows = (int) (cols/ratio);
            if (!setupIsles)
                cellSizeInFeet = (float) l/cols;
            else
            {
                cellSizeInFeet = cellSize*length/cols;
                if (cellSizeInFeet == cellSize)
                    cellSizeInFeet = 1;
            }
        }

        System.out.println(floors);
        System.out.println(ratio);
        System.out.println(cols);
        System.out.println(rows);
        System.out.println(sX);
        System.out.println(sY);

        cellSizeInFeet = Math.round(cellSizeInFeet);

        finalSizeOfCells = 15;
        System.out.println(finalSizeOfCells);

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

    /**
     * Constructor used when viewing previously made isle layout
     *
     * @param grid existing isle layout data
     * @param node used for accessing node data in isle layout view
     */
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
        ArrayList<String> cellsOfGroups = new ArrayList<>();
        ArrayList<String> groupNames = new ArrayList<>();
        ArrayList<Color> groupColors = new ArrayList<>();
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
                String name = scanner.nextLine();
                groupNames.add(name);
                String c1 = scanner.nextLine();
                String[] c2 = c1.split(" ");
                groupColors.add(new Color(Double.parseDouble(c2[0]), Double.parseDouble(c2[1]), Double.parseDouble(c2[2]), 1.0));
                String cells = scanner.nextLine();
                cellsOfGroups.add(cells);
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

        initializeFromFile(groupNames, groupColors, cellsOfGroups);
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

        if (!setupIsleLayout)
            stage.setMaximized(true);

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
        theV.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
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

        saveLayout.setOnAction(actionEvent ->
        {
            final JFileChooser fc = new JFileChooser();

            File f = new File("C:\\Users\\rober\\Desktop\\Other\\JavaShit");

            fc.setCurrentDirectory(f);

            int returnVal = fc.showSaveDialog(fc.getParent());

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = fc.getSelectedFile();
                saveFile(file);
            }
        });

        if (setupIsleLayout)
        {
            MenuItem saveIsles = new MenuItem("Save Isle Layout");
            saveIsles.setOnAction(actionEvent -> {
                setupIslesNode.getGroup().setIsleLayoutBool(true);
                setupIslesNode.getGroup().setIsleLayout(g);
                stage.hide();
            });
            file.getItems().add(saveIsles);
        }

        if (setupIsleLayout)
        {
            menuBar.getMenus().remove(m1);
            menuBar.getMenus().remove(m2);
            menuBar.getMenus().remove(m3);
            menuBar.getMenus().remove(m4);
        }

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
    }

    private void initializeFromFile(ArrayList<String> groupNames, ArrayList<Color> groupColors, ArrayList<String> cellsOfGroups)
    {
        theV.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        sP.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        reset.setOnAction(actionEvent -> g.resetGrid());

        printGroups.setOnAction(actionEvent -> g.printGroups());

        saveLayout.setOnAction(actionEvent ->
        {
            final JFileChooser fc = new JFileChooser();

            File f = new File("C:\\Users\\rober\\Desktop\\Other\\JavaShit");

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

        loadCellsFromFile(finalSizeOfCells, hboxWithTheCells, groupNames, groupColors, cellsOfGroups);
    }

    /**
     * Initializes some right click menus for grouping/ungrouping and other
     * related functions
     */
    private void initRightClickMenus()
    {
        rightClick = new ContextMenu();
        MenuItem group = new MenuItem("Set Isle Group");
        group.setOnAction(actionEvent ->
        {
            Stage groupSelect = new Stage();
            groupSelect.initModality(Modality.APPLICATION_MODAL);
            groupSelect.initOwner(stage);
            VBox groupSelectVbox = setupGroupCreation(groupSelect, g);
            Scene groupSelectScene = new Scene(groupSelectVbox);
            groupSelect.setScene(groupSelectScene);
            groupSelect.show();
            contextMenuShowing = false;
        });
        addToGroup = new MenuItem("Add to Isle Group");
        addToGroup.setOnAction(actionEvent ->
        {
            addToGroupList = new ContextMenu();
            Set<String> groups = g.groupList.keySet();
            for (String key : groups)
            {
                MenuItem groupItem = new MenuItem(g.groupList.get(key).getName());
                groupItem.setOnAction(actionEvent12 -> g.addToGroup(g.groupList.get(key).getName(), g.groupList.get(key).getColor()));
                addToGroupList.getItems().add(groupItem);
            }
            addToGroupList.show(addToGroupNode.getR(), addToGroupMouseX, addToGroupMouseY);
        });
        rightClick.getItems().add(group);

        rightClick2 = new ContextMenu();
        MenuItem ungroup = new MenuItem("Ungroup");
        MenuItem editGroup = new MenuItem("Edit Isle Group");
        ungroup.setOnAction(actionEvent -> g.ungroup(editGroupNode.getGroupName()));
        editGroup.setOnAction(actionEvent ->
        {
            Stage groupSelect = new Stage();
            groupSelect.initModality(Modality.APPLICATION_MODAL);
            groupSelect.initOwner(stage);
            VBox groupSelectVbox = setupGroupCreation(groupSelect, g);
            Scene groupSelectScene = new Scene(groupSelectVbox);
            groupSelect.setScene(groupSelectScene);
            groupSelect.show();
            editGroupBool = true;
        });
        rightClick2.getItems().addAll(ungroup, editGroup);
        setupIsleLayoutMenuItem = new MenuItem();
        if (!setupIsleLayout)
        {
            setupIsleLayoutMenuItem.setText("Setup Isle Group Isle Layout");
            setupIsleLayoutMenuItem.setOnAction(actionEvent ->
            {
                Stage setupIsleLayout = new Stage();
                setupIsleLayout.initModality(Modality.APPLICATION_MODAL);
                int groupCols = Math.round(editGroupNode.getGroup().getMaxWidth()*cellSizeInFeet);
                int groupRows = Math.round(editGroupNode.getGroup().getMaxHeight()*cellSizeInFeet);
                if (groupCols < sX/15 && groupRows < sY/15)
                {
                    System.out.println("Not too big");
                    new ActualController3(1, groupCols, groupRows, groupCols*15, groupRows*15+25, true, cellSizeInFeet, editGroupNode, rightClick2).launchScene(setupIsleLayout);
                }
                else
                {
                    System.out.println("Too big");
                    new ActualController3(1, editGroupNode.getGroup().getMaxWidth(), editGroupNode.getGroup().getMaxHeight(), sX, sY, true, cellSizeInFeet, editGroupNode, rightClick2).launchScene(setupIsleLayout);
                }

            });
            rightClick2.getItems().add(setupIsleLayoutMenuItem);
        }
    }

    /**
     * Initializes padding panes on outside of grid based on screen coords
     *
     * @param x screen x dimension
     * @param y screen y dimension
     */
    private void initOutsidePanes(double x, double y)
    {
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
                lx.setStrokeWidth(2);
                Line ly = new Line(0, (int) -(z/3), 0, (int) (z/3));
                ly.setStrokeWidth(2);
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
                lx.setStrokeWidth(2);
                Line ly = new Line(0, (int) -(z/3), 0, (int) (z/3));
                ly.setStrokeWidth(2);
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

        System.out.println("Drew Cells");
        if (setupIsleLayout && setupIslesNode.getGroup().isIrregular())
        {
            System.out.println("Nulling cells cuz irregular");
            ArrayList<GridData3.RNode> list = setupIslesNode.getGroup().getCellsToRemoveIfIrregular();
            System.out.println("Number of cells to null: "+list.size());
            int otherGridCellSize = setupIslesNode.getGroup().getCellSize();
            System.out.println("Other grid cell size: "+otherGridCellSize);
            int newXOriginCoord = setupIslesNode.getGroup().getLowestX();
            int newYOriginCoord = setupIslesNode.getGroup().getLowestY();
            System.out.println("First node in GroupedList: "+newXOriginCoord+","+newYOriginCoord);
            System.out.println("Cells to Null from Group:");
            for (GridData3.RNode rNode : list)
            {
                System.out.println(rNode.getX()+","+rNode.getY());

                int rNodeX = (rNode.getX()-newXOriginCoord)*otherGridCellSize;
                System.out.println("rNodeX: "+rNodeX);
                for (int i=rNodeX; i<rNodeX+otherGridCellSize; i++)
                {
                    int rNodeY = (rNode.getY()-newYOriginCoord)*otherGridCellSize;
                    System.out.println("rNodeY: "+rNodeY);
                    for (int j=rNodeY; j<rNodeY+otherGridCellSize; j++)
                    {
                        g.setCelltoNull(i, j);
                    }
                }
                System.out.println("Nulled");
            }
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

    private void loadCellsFromFile(double z, HBox hbox2, ArrayList<String> groupNames, ArrayList<Color> groupColors, ArrayList<String> cellsOfGroups)
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

        for (int i=0; i<groupNames.size(); i++)
        {
            String[] groupCoords = cellsOfGroups.get(i).split(",");
            for (int j=0; j<groupCoords.length; j=j+2)
            {
                g.getRNode(Integer.parseInt(groupCoords[j]), Integer.parseInt(groupCoords[j+1])).setHighlighted(true);
            }
            g.group(groupNames.get(i), groupColors.get(i), false, null);
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
        r.setStroke(Color.GRAY);
        r.setOpacity(0.5);
        r.setOnMouseEntered(mouseEvent ->
        {
            if (node.isGrouped())
            {
                r.setFill(node.getColor());
                r.setStroke(node.getColor());
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
                r.setStroke(Color.GRAY);
                r.setOpacity(1.0);
            }
            else if (node.isGrouped())
            {
                r.setFill(node.getColor());
                r.setStroke(node.getColor());
                r.setOpacity(1.0);
            }
            else if (!node.isNulled())
            {
                r.setFill(Color.TRANSPARENT);
                r.setStroke(Color.GRAY);
                r.setOpacity(0.5);
            }
        });
        r.setOnMouseClicked(mouseEvent ->
        {
            if (mouseEvent.getButton() == MouseButton.SECONDARY)
            {
                if (node.isHighlighted())
                {
                    rightClick.getItems().remove(addToGroup);
                    if (g.getNumOfGroups() > 0)
                        rightClick.getItems().add(addToGroup);

                    rightClick.show(r, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    contextMenuShowing = true;
                    addToGroupNode = node;
                    addToGroupMouseX = mouseEvent.getScreenX();
                    addToGroupMouseY = mouseEvent.getScreenY();
                }
                else if (node.isGrouped() && !node.getGroup().hasIsleLayout() && !setupIsleLayout)
                {
                    setupIsleLayoutMenuItem.setText("Setup Group Isle Layout");
                    setupIsleLayoutMenuItem.setOnAction(actionEvent ->
                    {
                        Stage setupIsleLayout = new Stage();
                        setupIsleLayout.initModality(Modality.APPLICATION_MODAL);
                        int groupCols = Math.round(editGroupNode.getGroup().getMaxWidth()*cellSizeInFeet);
                        int groupRows = Math.round(editGroupNode.getGroup().getMaxHeight()*cellSizeInFeet);
                        if (groupCols < sX/15 && groupRows < sY/15)
                        {
                            System.out.println("Not too big");
                            new ActualController3(1, groupCols, groupRows, groupCols*15, groupRows*15+25, true, cellSizeInFeet, editGroupNode, rightClick2).launchScene(setupIsleLayout);
                        }
                        else
                        {
                            System.out.println("Too big");
                            new ActualController3(1, editGroupNode.getGroup().getMaxWidth(), editGroupNode.getGroup().getMaxHeight(), sX, sY, true, cellSizeInFeet, editGroupNode, rightClick2).launchScene(setupIsleLayout);
                        }

                    });
                    rightClick2.show(r, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    contextMenuShowing = true;
                    editGroupNode = node;
                    addToGroupMouseX = mouseEvent.getScreenX();
                    addToGroupMouseY = mouseEvent.getScreenY();
                }
                else if (node.isGrouped() && node.getGroup().hasIsleLayout())
                {
                    editGroupNode = node;
                    setupIsleLayoutMenuItem.setText("View Isle Layout");
                    setupIsleLayoutMenuItem.setOnAction(actionEvent ->
                    {
                        Stage viewIsleLayout = new Stage();
                        viewIsleLayout.initModality(Modality.APPLICATION_MODAL);
                        new ActualController3(editGroupNode.getGroup().getGrid(), editGroupNode).launchScene(viewIsleLayout);
                    });
                    rightClick2.show(r, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    contextMenuShowing = true;
                }
                else
                    contextMenuShowing = false;
            }
            else if (mouseEvent.getButton() == MouseButton.PRIMARY)
            {
                if (!contextMenuShowing)
                {
                    //System.out.println(node.getxCoord()+","+node.getyCoord());
                    //System.out.println(node.printSCoords());
                    g.resetHighlighted();
                    System.out.print("Group: ");
                    if (node.isGrouped())
                    {
                        System.out.println(node.getGroupName());
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

    /**
     * Setups popup window for group creation for a highlighted area
     *
     * @param s stage
     * @param g data used to create and implement new group
     * @return vbox with all necessary elements
     */
    private VBox setupGroupCreation(Stage s, GridData3 g)
    {
        VBox v = new VBox();

        v.setAlignment(Pos.CENTER);
        v.setSpacing(5);
        Label title = new Label("Select Group Name and Color");
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
            s.hide();
            g.group(nameT.getText(), colorP.getValue(), editGroupBool, editGroupNode);
            if (editGroupBool)
            {
                editGroupBool = false;
                editGroupNode = null;
            }
        });
        v.getChildren().addAll(title, hboxA, hboxB, submit);

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

            Set<String> groups = g.groupList.keySet();
            for (String key : groups)
            {
                fileStream.println(g.groupList.get(key).getName());
                Color color = g.groupList.get(key).getColor();
                fileStream.println(color.getRed()+" "+color.getGreen()+" "+color.getBlue());
                GridData3.GroupedList gL = g.groupList.get(key).getGroupedList();
                GridData3.GroupedList.GroupedNode curr = gL.first;
                while(curr != null)
                {
                    fileStream.print(curr.rNode.getX()+","+curr.rNode.getY());

                    if (curr.next != null)
                        fileStream.print(",");
                    else
                        fileStream.print("\n");

                    curr = curr.next;
                }
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