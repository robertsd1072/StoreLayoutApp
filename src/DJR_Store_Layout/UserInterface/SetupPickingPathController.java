/**
 * Controller for inputting list of locations and getting those locations back in the correct order
 * @author
 */

package DJR_Store_Layout.UserInterface;

import DJR_Store_Layout.GraphsAndHelpers.FindingPathReturn;
import DJR_Store_Layout.GraphsAndHelpers.GraphOfTheGrid;
import DJR_Store_Layout.GridData.GridData3;
import DJR_Store_Layout.GridData.Aisle;
import DJR_Store_Layout.GridData.RNode;
import DJR_Store_Layout.HelperClasses.Coords;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SetupPickingPathController
{
    /** Launching/FXML variable */
    private Stage stage;
    public TextField text1, text2, text3, text4, text5, text6, text7, text8, text9, text10, text11, text12, text13, text14, text15, text16, text17, text18, text19, text20;
    public Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12, button13, button14, button15, button16, button17, button18, button19, button20, done, clearPath;
    public ChoiceBox choice1, choiceA;
    public VBox theV;
    /** Data variables */
    private TextField[] textArr;
    private Button[] buttonArr;
    private GridData3 grid;
    private GraphOfTheGrid graph;
    private String[] cellPath;
    private ArrayList<String> vertexPath;

    /** I can't put anything in the Constructor cuz i'll get errors so it's empty */
    public SetupPickingPathController() {}

    /**
     * Connects various info that is important for functionality
     * @param g GridData3
     * @param g1 GraphOfTheGrid
     * @param s Stage
     */
    public void setInfo(GridData3 g, GraphOfTheGrid g1, Stage s)
    {
        grid = g;
        graph = g1;
        stage = s;
    }

    /** For finding picking path without UI of store layout */
    public void setInfoWithFile(String file, Stage s)
    {
        stage = s;
        grid = new GridData3(file);
        graph = new GraphOfTheGrid(grid);
        theV.getChildren().remove(clearPath);
        stage.setHeight(stage.getHeight()-25);
    }

    /** Basic Initializer */
    public void initialize()
    {
        choiceA.setItems(FXCollections.observableArrayList("Coordinates", "Locations"));
        choice1.setItems(FXCollections.observableArrayList("OPU Regular", "OPU Grocery", "Standard"));
        textArr = new TextField[]{text1, text2, text3, text4, text5, text6, text7, text8, text9, text10, text11, text12, text13, text14, text15, text16, text17, text18, text19, text20};
        buttonArr = new Button[]{button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12, button13, button14, button15, button16, button17, button18, button19, button20};

        for (int i=0; i<buttonArr.length; i++)
        {
            int finalI = i;
            buttonArr[i].setOnAction(actionEvent ->
            {
                String string = textArr[finalI].getText();
                String isleGroup = string.charAt(0)+"";
                String[] arr = string.split("\\(");
                String isleID = arr[0];
                int isleSection = Integer.parseInt(arr[1].charAt(0)+"");
                Aisle isle = grid.getAisleGroupList().get(isleGroup).getAisleIDList().get(isleID);
                if (isle == null || !isle.hasSetupInfo())
                    buttonArr[finalI].setText("Any");
                else
                    buttonArr[finalI].setText(isle.getNumberOfSubsectionsForEachSection().get(isleSection)+"");
            });
        }
    }

    /** Gets relevant info from inputted strings, finds picking path, and reorganizes locations based on order of picking path */
    public void done()
    {
        Long start = System.nanoTime();
        ArrayList<String> finalLocationList = null;
        if (!choice1.getValue().toString().equals("OPU Grocery"))
        {
            if (choiceA.getValue().toString().compareTo("Coordinates") == 0)
            {
                Hashtable<String, String> list = new Hashtable<>();

                for (TextField tf : textArr)
                {
                    if (!tf.getText().trim().isEmpty())
                    {
                        list.put(tf.getText(), tf.getText());
                    }
                }

                FindingPathReturn path = graph.findPickingPath2(list, choice1.getValue().toString());
                System.out.println("Location Path: "+path.getLocationPath());
                System.out.println("Vertex Path: "+path.getVertexPath());
                vertexPath = path.getVertexPath();
                String[] coordsOfCellPath = path.getCellPath().split(" ");
                cellPath = coordsOfCellPath;

                AtomicInteger i = new AtomicInteger(1);
                PauseTransition drawPath = new PauseTransition(Duration.millis(25));
                drawPath.setOnFinished(actionEvent ->
                {
                    Coords coords = new Coords(coordsOfCellPath[i.get()]);
                    Rectangle r = grid.getRNode(coords.getX(), coords.getY()).getR();
                    r.setFill(Color.RED);
                    r.setOpacity(0.5);

                    if (i.get() < coordsOfCellPath.length-1)
                    {
                        i.getAndIncrement();
                        drawPath.play();
                    }
                });
                drawPath.play();

                for (String s : path.getVertexPath())
                {
                    Coords coords = new Coords(s);
                    Rectangle r = grid.getRNode(coords.getX(), coords.getY()).getR();
                    r.setFill(Color.RED);
                    r.setOpacity(1.0);
                }
            }
            else //Locations
            {
                Hashtable<String, String> list = new Hashtable<>();

                for (TextField tf : textArr)
                {
                    if (!tf.getText().trim().isEmpty())
                    {
                        String loc = tf.getText();

                        list.put(grid.getCoordsGivenLocation(loc), loc);
                    }
                }

                System.out.println("List:");
                Set<String> set = list.keySet();
                for (String s : set)
                {
                    System.out.println(list.get(s)+" at "+s);
                }

                FindingPathReturn path = graph.findPickingPath2(list, choice1.getValue().toString());
                finalLocationList = path.getLocationPath();
                System.out.println("Location Path: "+finalLocationList);
                System.out.println("Vertex Path: "+path.getVertexPath());
                vertexPath = path.getVertexPath();
                String[] coordsOfCellPath = path.getCellPath().split(" ");
                cellPath = coordsOfCellPath;

                AtomicInteger i = new AtomicInteger(1);
                PauseTransition drawPath = new PauseTransition(Duration.millis(25));
                drawPath.setOnFinished(actionEvent ->
                {
                    Coords coords = new Coords(coordsOfCellPath[i.get()]);
                    Rectangle r = grid.getRNode(coords.getX(), coords.getY()).getR();
                    r.setFill(Color.RED);
                    r.setOpacity(0.5);

                    if (i.get() < coordsOfCellPath.length-1)
                    {
                        i.getAndIncrement();
                        drawPath.play();
                    }
                });
                drawPath.play();

                for (String s : path.getVertexPath())
                {
                    Coords coords = new Coords(s);
                    Rectangle r = grid.getRNode(coords.getX(), coords.getY()).getR();
                    r.setFill(Color.RED);
                    r.setOpacity(1.0);
                }
            }
        }
        else
        {
            /**
             * I should make a grocery OPU algorithm but the one that is current implemented I found no problems with
             */
        }

        for (int i=0; i<finalLocationList.size(); i++)
            textArr[i].setText(finalLocationList.get(i));

        Long end = System.nanoTime();
        System.out.println("Time: "+(end-start));
        theV.getChildren().add(new Label("Time: "+(end-start)));
        stage.setHeight(stage.getHeight()+20);
    }

    /** Resets cellPath that was drawn on story layout UI */
    public void clearCellPath()
    {
        for (int i=1; i<cellPath.length; i++)
        {
            String[] coord = cellPath[i].split(",");
            try
            {
                int x = Integer.parseInt(coord[0]);
                int y = Integer.parseInt(coord[1]);
                grid.getRNode(x, y).setHighlighted(false);
            }
            catch (NumberFormatException ignored) {}
        }
        for (String coords : vertexPath)
        {
            String[] coord = coords.split(",");
            int x = Integer.parseInt(coord[0]);
            int y = Integer.parseInt(coord[1]);
            RNode rNode = grid.getRNode(x, y);
            if (rNode.isAisle())
                rNode.getR().setFill(rNode.getColor());
            else
            {
                rNode.getR().setFill(Color.RED);
                rNode.getR().setOpacity(1.0);
            }
        }
    }
}