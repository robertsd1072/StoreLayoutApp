package DJR_Store_Layout;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
    public HBox hbox0, hboxEnd;
    public TextField text0, text1;
    public ChoiceBox choiceA, choiceB;
    public Label endLabel;
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
        choiceA.setItems(FXCollections.observableArrayList("north", "south", "east", "west"));
        choiceB.setItems(FXCollections.observableArrayList("up", "down", "left", "right", "diagonal right", "diagonal left"));
    }

    public void setImportantInfo(GridData3 grid, Isle i, Stage s)
    {
        g = grid;
        isle = i;
        stage = s;

        if (isle.getIsleGroup().getBackOrFloor().compareTo("back") == 0)
        {
            vboxOfTexts.getChildren().removeAll(hbox0);
            vboxOfTexts.setPrefHeight(25);
            theV.getChildren().removeAll(addSection, hboxEnd, endLabel);
            stage.setHeight(146);
        }

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
                if (isle.getIsleGroup().getBackOrFloor().compareTo("floor") == 0)
                    numberOfSubsectionsForEachSection.add(Integer.parseInt(textField.getText()));
                else
                {
                    numberOfSubsectionsForEachSection.add(1);
                    numberOfSubsectionsForEachSection.add(Integer.parseInt(text1.getText()));
                    break;
                }
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

        if (isle.getIsleGroup().getBackOrFloor().compareTo("back") == 0)

            isle.setupIsleInfo(numberOfIsleSections, numberOfSubsectionsForEachSection, "none", choiceB.getValue().toString());
        else
            isle.setupIsleInfo(numberOfIsleSections, numberOfSubsectionsForEachSection, choiceA.getValue().toString(), choiceB.getValue().toString());

        Isle.IsleCellList.IsleCellNode curr = isle.getIsleCellList().getFirst();
        while (curr != null)
        {
            curr.getrNode().getR().setOpacity(1.0);
            curr = curr.getNext();
        }
        stage.hide();
    }
}
