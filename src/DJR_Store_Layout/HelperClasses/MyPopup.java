/**
 * Custom screen popup
 * Used for warnings about wrong inputs, general helpful info, and more
 * @author David Roberts
 */

package DJR_Store_Layout.HelperClasses;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.ArrayList;

public class MyPopup
{
    private final ArrayList<Node> listOfNodes;
    private final Stage stage;

    /** Basic Constructor takes a list of nodes to put in popup */
    public MyPopup(ArrayList<Node> list, Stage st)
    {
        listOfNodes = list;
        stage = st;
    }

    public Stage getStage(boolean okButton)
    {
        Stage popupStage = new Stage();
        popupStage.initOwner(stage);
        VBox popupStageVbox = new VBox();
        popupStageVbox.setSpacing(5);
        popupStageVbox.setAlignment(Pos.CENTER);
        if (okButton)
        {
            popupStage.initModality(Modality.APPLICATION_MODAL);

            Button ok = new Button("Ok");
            ok.setOnAction(actionEvent -> popupStage.hide());
            listOfNodes.add(ok);
        }
        for (Node thing : listOfNodes)
            popupStageVbox.getChildren().add(thing);
        Scene popupScene = new Scene(popupStageVbox);
        popupStage.setScene(popupScene);

        return popupStage;
    }
}