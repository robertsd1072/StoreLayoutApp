package DJR_Store_Layout;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

public class SetupPickingPath
{
    private Stage stage;
    public TextField text1, text2, text3, text4, text5, text6, text7, text8, text9, text10, text11, text12, text13, text14, text15, text16, text17, text18, text19, text20, text21, text22, text23, text24, text25, text26, text27, text28, text29, text30;
    public Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12, button13, button14, button15, button16, button17, button18, button19, button20, button21, button22, button23, button24, button25, button26, button27, button28, button29, button30, done;
    public ChoiceBox choice1, choiceA;

    private TextField[] textArr;
    private Button[] buttonArr;
    private GridData3 grid;
    private GraphOfTheGrid graph;

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
        choice1.setItems(FXCollections.observableArrayList("OPU", "Regular"));
        textArr = new TextField[]{text1, text2, text3, text4, text5, text6, text7, text8, text9, text10, text11, text12, text13, text14, text15, text16, text17, text18, text19, text20, text21, text22, text23, text24, text25, text26, text27, text28, text29, text30};
        buttonArr = new Button[]{button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12, button13, button14, button15, button16, button17, button18, button19, button20, button21, button22, button23, button24, button25, button26, button27, button28, button29, button30};
        /*
        for (int i=0; i<buttonArr.length; i++)
        {
            buttonArr[i].setOnAction(actionEvent ->
            {

            });
        }

         */
    }

    public void done()
    {
        if (choiceA.getValue().toString().compareTo("Coordinates") == 0)
        {
            Hashtable<String, String> list = new Hashtable<>();

            for (TextField tf : textArr)
            {
                if (!tf.getText().trim().isEmpty())
                {
                    list.put(tf.getText(), null);
                    //System.out.println("Adding "+tf.getText()+" to list");
                }
            }

            GraphOfTheGrid.FindingPathReturn path = graph.findPickingPath(grid, list, choice1.getValue().toString());
            System.out.println("Location Path: "+path.getLocationPath());
            System.out.println("Vertex Path: "+path.getVertexPath());
            String[] coords = path.getCellPath().split(" ");
            for (int i=1; i<coords.length; i++)
            {
                String[] coord = coords[i].split(",");
                int x = Integer.parseInt(coord[0]);
                int y = Integer.parseInt(coord[1]);
                Rectangle r = grid.getRNode(x, y).getR();
                r.setFill(Color.RED);
                r.setOpacity(0.5);
            }
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
        else
        {
            Hashtable<String, String> list = new Hashtable<>();

            for (TextField tf : textArr)
            {
                if (!tf.getText().trim().isEmpty())
                {
                    String loc = tf.getText();

                    Isle isle;
                    System.out.println();
                    try
                    {
                        int hmm = Integer.parseInt(loc.charAt(0)+"");
                        //System.out.println("Isle in the back");
                        String[] sArr = loc.split(" ");
                        if (grid.isleGroupExists(sArr[0]))
                        {
                            isle = grid.getIsle(sArr[0]+sArr[1]+"", sArr[0]);
                            //System.out.println("IsleID: "+isle.getIsleID());
                            if (isle.hasSetupInfo())
                            {
                                String subsection = sArr[2].charAt(sArr[2].length()-1)+"";
                                //System.out.println("isleSubsection: "+subsection);

                                if (isle.inputingValidIsleLocationInBack(subsection))
                                    list.put(isle.getCoordsGivenLocationInBack(subsection), tf.getText());
                            }
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        //System.out.println("Isle on the floor");
                        String[] loc1 = loc.split("\\(");
                        //System.out.println("IsleID: "+loc1[0]);
                        if (loc1.length > 1)
                        {
                            isle = grid.getIsle(loc1[0], loc.charAt(0)+"");
                            if (isle.hasSetupInfo())
                            {
                                String[] loc2 = loc1[1].split("\\)");
                                int isleSection = Integer.parseInt(loc2[0]);
                                //System.out.println("isleSection: "+isleSection);

                                String[] loc3 = loc2[1].split("-");
                                //System.out.println("isleSubsection: "+loc3[0]);

                                if (isle.inputingValidIsleLocationOnFloor(isleSection, loc3[0]))
                                    list.put(isle.getCoordsGivenLocationOnFloor(isleSection, loc3[0]), tf.getText());
                            }
                        }
                        else
                        {
                            isle = grid.getIsleWithUnknownIG(loc);
                            list.put(isle.getIsleID(), tf.getText());
                        }
                    }
                }
            }

            System.out.println("List:");
            Set<String> set = list.keySet();
            for (String s : set)
            {
                System.out.println(list.get(s)+" at "+s);
            }

            GraphOfTheGrid.FindingPathReturn path = graph.findPickingPath(grid, list, choice1.getValue().toString());
            System.out.println("Location Path: "+path.getLocationPath());
            System.out.println("Vertex Path: "+path.getVertexPath());
            String[] coords = path.getCellPath().split(" ");
            for (int i=1; i<coords.length; i++)
            {
                String[] coord = coords[i].split(",");
                int x = Integer.parseInt(coord[0]);
                int y = Integer.parseInt(coord[1]);
                Rectangle r = grid.getRNode(x, y).getR();
                r.setFill(Color.RED);
                r.setOpacity(0.5);
            }
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
}
