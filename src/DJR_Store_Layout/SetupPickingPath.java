package DJR_Store_Layout;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SetupPickingPath
{
    private Stage stage;
    public TextField text1, text2, text3, text4, text5, text6, text7, text8, text9, text10, text11, text12, text13, text14, text15, text16, text17, text18, text19, text20;
    public Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12, button13, button14, button15, button16, button17, button18, button19, button20, done, clearPath;
    public ChoiceBox choice1, choiceA;

    private TextField[] textArr;
    private Button[] buttonArr;
    private GridData3 grid;
    private GraphOfTheGrid graph;
    private String[] cellPath;
    private ArrayList<String> vertexPath;

    public SetupPickingPath()
    {

    }

    public void setInfo(GridData3 g, GraphOfTheGrid g1, Stage s)
    {
        grid = g;
        graph = g1;
        stage = s;
    }

    public void initialize()
    {
        choiceA.setItems(FXCollections.observableArrayList("Coordinates", "Locations"));
        choice1.setItems(FXCollections.observableArrayList("OPU Regular", "OPU Grocery", "Regular"));
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
                Isle isle = grid.isleGroupList.get(isleGroup).getIsleIDList().get(isleID);
                buttonArr[finalI].setText(isle.getNumberOfSubsectionsForEachSection().get(isleSection)+"");
            });
        }
    }

    public void done()
    {
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

                GraphOfTheGrid.FindingPathReturn path = graph.findPickingPath2(list, choice1.getValue().toString());
                System.out.println("Location Path: "+path.getLocationPath());
                System.out.println("Vertex Path: "+path.getVertexPath());
                vertexPath = path.getVertexPath();
                String[] coords = path.getCellPath().split(" ");
                cellPath = coords;

                AtomicInteger i = new AtomicInteger(1);
                PauseTransition drawPath = new PauseTransition(Duration.millis(25));
                drawPath.setOnFinished(actionEvent ->
                {
                    String[] coord = coords[i.get()].split(",");
                    try
                    {
                        int x = Integer.parseInt(coord[0]);
                        int y = Integer.parseInt(coord[1]);
                        Rectangle r = grid.getRNode(x, y).getR();
                        r.setFill(Color.RED);
                        r.setOpacity(0.5);
                    }
                    catch (NumberFormatException ignored) {}
                    if (i.get() < coords.length-1)
                    {
                        i.getAndIncrement();
                        drawPath.play();
                    }
                });
                drawPath.play();

                for (String s : path.getVertexPath())
                {
                    String[] coord = s.split(",");
                    int x = Integer.parseInt(coord[0]);
                    int y = Integer.parseInt(coord[1]);
                    Rectangle r = grid.getRNode(x, y).getR();
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

                GraphOfTheGrid.FindingPathReturn path = graph.findPickingPath2(list, choice1.getValue().toString());
                System.out.println("Location Path: "+path.getLocationPath());
                System.out.println("Vertex Path: "+path.getVertexPath());
                vertexPath = path.getVertexPath();
                String[] coords = path.getCellPath().split(" ");
                cellPath = coords;

                AtomicInteger i = new AtomicInteger(1);
                PauseTransition drawPath = new PauseTransition(Duration.millis(25));
                drawPath.setOnFinished(actionEvent ->
                {
                    String[] coord = coords[i.get()].split(",");
                    try
                    {
                        int x = Integer.parseInt(coord[0]);
                        int y = Integer.parseInt(coord[1]);
                        Rectangle r = grid.getRNode(x, y).getR();
                        r.setFill(Color.RED);
                        r.setOpacity(0.5);
                    }
                    catch (NumberFormatException ignored) {}
                    if (i.get() < coords.length-1)
                    {
                        i.getAndIncrement();
                        drawPath.play();
                    }
                });
                drawPath.play();

                for (String s : path.getVertexPath())
                {
                    String[] coord = s.split(",");
                    int x = Integer.parseInt(coord[0]);
                    int y = Integer.parseInt(coord[1]);
                    Rectangle r = grid.getRNode(x, y).getR();
                    r.setFill(Color.RED);
                    r.setOpacity(1.0);
                }
            }
        }
        else
        {

            //Make grocery OPU algorithm
        }
    }

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
            GridData3.RNode rNode = grid.getRNode(x, y);
            if (rNode.isIsle())
                rNode.getR().setFill(rNode.getColor());
            else
            {
                rNode.getR().setFill(Color.RED);
                rNode.getR().setOpacity(1.0);
            }
        }
    }
}
