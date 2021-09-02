/**
 * UI menu for setting up isle info
 * @author David Roberts
 */

package DJR_Store_Layout.UserInterface;

import DJR_Store_Layout.GridData.CellList;
import DJR_Store_Layout.GridData.GridData3;
import DJR_Store_Layout.GridData.Isle;
import DJR_Store_Layout.HelperClasses.MyPopup;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class SetupIsleInfoController
{
    /** Launching/FXML variable */
    private Stage stage;
    public VBox theV, vboxOfTexts;
    public Button addSection, done, help, removeSection;
    public HBox hbox0, hboxEnd;
    public TextField text0, text1;
    public ChoiceBox choiceA, choiceB;
    public HBox addAndRemoveHBox;
    public Label endLabel;
    /** Data variables */
    private GridData3 g;
    private Isle isle;
    private int numberOfIsleSections;
    private final Hashtable<Integer, Integer> numberOfSubsectionsForEachSection;
    private final ArrayList<TextField> subsectionTextfields, sectionNumberTextfields;
    private final ArrayList<HBox> hboxArr;
    private IsleLayoutController ilc;

    /** Basic Constructor */
    public SetupIsleInfoController()
    {
        g = null;
        numberOfIsleSections = 2;
        numberOfSubsectionsForEachSection = new Hashtable<>();
        subsectionTextfields = new ArrayList<>();
        sectionNumberTextfields = new ArrayList<>();
        hboxArr = new ArrayList<>();
    }

    /** Basic Initializer */
    public void initialize()
    {
        subsectionTextfields.add(text0);
        subsectionTextfields.add(text1);
        choiceA.setItems(FXCollections.observableArrayList("north", "south", "east", "west"));
        choiceB.setItems(FXCollections.observableArrayList("up", "down", "left", "right", "diagonal right", "diagonal left"));
        help.setOnAction(actionEvent -> ilc.displayIsleInfoInstructions());
        addAndRemoveHBox.getChildren().remove(removeSection);
    }

    /**
     * Connects various info that is important for functionality
     * @param grid GridData3
     * @param i isle
     * @param s stage
     * @param isleLayoutController IsleLayoutController
     */
    public void setImportantInfo(GridData3 grid, Isle i, Stage s, IsleLayoutController isleLayoutController)
    {
        g = grid;
        isle = i;
        stage = s;
        ilc = isleLayoutController;

        text0.setText(1+"");
        text1.setText((isle.getNumberOfCellsInIsle()-1)+"");

        if (isle.getIsleGroup().getBackOrFloor().compareTo("back") == 0)
        {
            vboxOfTexts.getChildren().removeAll(hbox0);
            vboxOfTexts.setPrefHeight(25);
            theV.getChildren().removeAll(addAndRemoveHBox, hboxEnd, endLabel);
            stage.setHeight(160);
        }

        stage.setOnCloseRequest(windowEvent ->
        {
            CellList.CellNode curr = isle.getIsleCellList().getFirst();
            while (curr != null)
            {
                curr.getrNode().getR().setFill(isle.getIsleGroup().getColor());
                curr = curr.getNext();
            }
        });
    }

    /** Adds necesarry UI elements to input another section */
    public void addSection()
    {
        HBox hbox = new HBox();
        Label label1 = new Label("Isle Section ");
        label1.setStyle("-fx-font-size: 16;");
        TextField numOfSection = new TextField();
        numOfSection.setPrefWidth(30);
        Label label2 = new Label("      ");
        label2.setStyle("-fx-font-size: 16;");
        Label label3 = new Label("# of Subsections: ");
        label3.setStyle("-fx-font-size: 16;");
        TextField textFieldNew = new TextField();
        hbox.getChildren().addAll(label1, numOfSection, label2, label3, textFieldNew);
        vboxOfTexts.getChildren().add(hbox);
        stage.setHeight(stage.getHeight()+25);
        subsectionTextfields.add(textFieldNew);
        sectionNumberTextfields.add(numOfSection);
        hboxArr.add(hbox);

        if (subsectionTextfields.size() == 3)
            addAndRemoveHBox.getChildren().add(removeSection);
    }

    /** Removes necesarry UI elements to remove section */
    public void removeSection()
    {
        vboxOfTexts.getChildren().remove(hboxArr.get(hboxArr.size()-1));
        stage.setHeight(stage.getHeight()-25);
        subsectionTextfields.remove(subsectionTextfields.get(subsectionTextfields.size()-1));
        sectionNumberTextfields.remove(sectionNumberTextfields.get(sectionNumberTextfields.size()-1));
        hboxArr.remove(hboxArr.get(hboxArr.size()-1));

        if (subsectionTextfields.size() <= 2)
            addAndRemoveHBox.getChildren().remove(removeSection);
    }

    /** Called if isle info is already setup and someone wants to edit that info */
    public void editingInfo()
    {
        ArrayList<Integer> isleSectionsInOrder = new ArrayList<>(isle.getNumberOfSubsectionsForEachSection().keySet());
        Collections.sort(isleSectionsInOrder);

        text0.setText(isle.getNumberOfSubsectionsForEachSection().get(0)+"");
        text1.setText(isle.getNumberOfSubsectionsForEachSection().get(1)+"");

        int numberOfAdditionalIsleSections = isle.getNumberOfIsleSections()-2;
        for (int i=0; i<numberOfAdditionalIsleSections; i++)
        {
            addSection();
            sectionNumberTextfields.get(i).setText(isleSectionsInOrder.get(i+2)+"");
            subsectionTextfields.get(i+2).setText(isle.getNumberOfSubsectionsForEachSection().get(isleSectionsInOrder.get(i+2))+"");
        }

        choiceA.setValue(isle.getEndCapLocation());
        choiceB.setValue(isle.getDirectionOfIncreasingIsleSections());
    }

    /** Sets info of isle based on inputted info */
    public void done()
    {
        if (isle.getIsleGroup().getBackOrFloor().compareTo("back") == 0)
        {
            numberOfSubsectionsForEachSection.put(0, 1);
            numberOfSubsectionsForEachSection.put(1, Integer.parseInt(text1.getText()));
        }
        else
        {
            try
            {
                numberOfSubsectionsForEachSection.put(0, Integer.parseInt(subsectionTextfields.get(0).getText()));
                numberOfSubsectionsForEachSection.put(1, Integer.parseInt(subsectionTextfields.get(1).getText()));
            }
            catch (NumberFormatException e)
            {
                ArrayList<Node> list = new ArrayList<>();
                Label label = new Label("Enter Valid Numbers for Each Isle Section Field");
                list.add(label);
                new MyPopup(list, stage).getStage(true).show();
            }

            for (int i=2; i<subsectionTextfields.size(); i++)
            {
                try
                {
                    numberOfSubsectionsForEachSection.put(Integer.parseInt(sectionNumberTextfields.get(i-2).getText()), Integer.parseInt(subsectionTextfields.get(i).getText()));
                    numberOfIsleSections++;
                }
                catch (NumberFormatException e)
                {
                    ArrayList<Node> list = new ArrayList<>();
                    Label label = new Label("Enter Valid Numbers for Each Isle Section Field");
                    list.add(label);
                    new MyPopup(list, stage).getStage(true).show();
                }
            }
        }

        if (isle.getIsleGroup().getBackOrFloor().compareTo("back") == 0)

            isle.setupIsleInfo(numberOfIsleSections, numberOfSubsectionsForEachSection, "none", choiceB.getValue().toString());
        else
            isle.setupIsleInfo(numberOfIsleSections, numberOfSubsectionsForEachSection, choiceA.getValue().toString(), choiceB.getValue().toString());

        CellList.CellNode curr = isle.getIsleCellList().getFirst();
        while (curr != null)
        {
            curr.getrNode().getR().setFill(isle.getIsleGroup().getColor());
            curr = curr.getNext();
        }
        stage.hide();
    }
}