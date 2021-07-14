package DJR_Store_Layout;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class SetupPickingPath
{
    private Stage stage;
    public TextField text1, text2, text3, text4, text5, text6, text7, text8, text9, text10, text11, text12, text13, text14, text15, text16, text17, text18, text19, text20, text21, text22, text23, text24, text25, text26, text27, text28, text29, text30;
    public Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12, button13, button14, button15, button16, button17, button18, button19, button20, button21, button22, button23, button24, button25, button26, button27, button28, button29, button30, done;
    public ChoiceBox choice1;

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
        ArrayList<String> list = new ArrayList<>();

        for (TextField tf : textArr)
        {
            if (!tf.getText().trim().isEmpty())
            {
                list.add(tf.getText());
                System.out.println("Adding "+tf.getText()+" to list");
            }
        }

        stage.hide();

        GraphOfTheGrid itemGraph = new GraphOfTheGrid(grid, graph, list, choice1.getValue().toString());
        System.out.println("Path:"+itemGraph.findPickingPath());

    }
}
