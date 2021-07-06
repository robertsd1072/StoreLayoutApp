package DJR_Store_Layout;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SetupIsleInfoController
{
    private Stage stage;
    public VBox theV, vboxOfTexts;
    public Button addSection, done;
    public TextField text0, text1, textA, textB;
    private GridData3 g;
    private Isle isle;
    private int numberOfIsleSections;
    private final ArrayList<Integer> numberOfSubsectionsForEachSection;
    private final ArrayList<TextField> textFields;

    public SetupIsleInfoController()
    {
        g = null;
        numberOfIsleSections = 2;
        numberOfSubsectionsForEachSection = new ArrayList<>();
        textFields = new ArrayList<>();
    }

    public void initialize()
    {
        textFields.add(text0);
        textFields.add(text1);
    }

    public void setImportantInfo(GridData3 grid, Isle i, Stage s)
    {
        g = grid;
        isle = i;
        stage = s;

        stage.setOnCloseRequest(windowEvent ->
        {
            Isle.IsleCellList.IsleCellNode curr = isle.getIsleCellList().getFirst();
            while (curr != null)
            {
                curr.getrNode().getR().setOpacity(1.0);
                curr = curr.getNext();
            }
        });
    }

    public void addSection()
    {
        HBox hbox = new HBox();
        Label label1;
        if (numberOfIsleSections < 10)
            label1 = new Label("Isle Section "+numberOfIsleSections+"       ");
        else
            label1 = new Label("Isle Section "+numberOfIsleSections+"      ");
        label1.setStyle("-fx-font-size: 16;");
        Label label2 = new Label("# of Subsections: ");
        label2.setStyle("-fx-font-size: 16;");
        TextField textFieldNew = new TextField();
        hbox.getChildren().addAll(label1, label2, textFieldNew);
        vboxOfTexts.getChildren().add(hbox);
        stage.setHeight(stage.getHeight()+25);
        textFields.add(textFieldNew);
        numberOfIsleSections++;
    }

    public void done()
    {
        for (TextField textField : textFields)
        {
            try
            {
                numberOfSubsectionsForEachSection.add(Integer.parseInt(textField.getText()));
            }
            catch (Exception e)
            {
                Stage warningStage = new Stage();
                warningStage.initModality(Modality.APPLICATION_MODAL);
                warningStage.initOwner(stage);
                VBox warningVbox = new VBox();
                warningVbox.setSpacing(5);
                warningVbox.setAlignment(Pos.CENTER);
                Label warningLabel = new Label("Enter Valid Numbers for Each Isle Section Field");
                Button ok = new Button("Ok");
                ok.setOnAction(actionEvent -> warningStage.hide());
                warningVbox.getChildren().addAll(warningLabel, ok);
                Scene cellSizeScene = new Scene(warningVbox);
                warningStage.setScene(cellSizeScene);
                warningStage.show();
            }
        }
        if (textA.getText().compareTo("north") != 0 || textA.getText().compareTo("south") != 0 || textA.getText().compareTo("west") != 0 || textA.getText().compareTo("east") != 0
                || textB.getText().compareTo("north") != 0 || textB.getText().compareTo("south") != 0 || textB.getText().compareTo("west") != 0 || textB.getText().compareTo("east") != 0)
        {
            isle.setupIsleInfo(numberOfIsleSections, numberOfSubsectionsForEachSection, textA.getText(), textB.getText());
            Isle.IsleCellList.IsleCellNode curr = isle.getIsleCellList().getFirst();
            while (curr != null)
            {
                curr.getrNode().getR().setOpacity(1.0);
                curr = curr.getNext();
            }
            stage.hide();
        }
        else
        {
            Stage warningStage = new Stage();
            warningStage.initModality(Modality.APPLICATION_MODAL);
            warningStage.initOwner(stage);
            VBox warningVbox = new VBox();
            warningVbox.setSpacing(5);
            warningVbox.setAlignment(Pos.CENTER);
            Label warningLabel = new Label("Enter Only: \"top\",\"bottom\",\"right\",\"left\"");
            Button ok = new Button("Ok");
            ok.setOnAction(actionEvent -> warningStage.hide());
            warningVbox.getChildren().addAll(warningLabel, ok);
            Scene cellSizeScene = new Scene(warningVbox);
            warningStage.setScene(cellSizeScene);
            warningStage.show();
        }
    }
}
