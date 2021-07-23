package DJR_Store_Layout;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MyPopup
{
    private final String message;
    private final Stage stage;

    public MyPopup(String s, Stage st)
    {
        message = s;
        stage = st;
    }

    public Stage getStage()
    {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(stage);
        VBox popupStageVbox = new VBox();
        popupStageVbox.setSpacing(5);
        popupStageVbox.setAlignment(Pos.CENTER);
        Label messageLabel = new Label(message);
        Button ok = new Button("Ok");
        ok.setOnAction(actionEvent -> popupStage.hide());
        popupStageVbox.getChildren().addAll(messageLabel, ok);
        Scene popupScene = new Scene(popupStageVbox);
        popupStage.setScene(popupScene);

        return popupStage;
    }
}
