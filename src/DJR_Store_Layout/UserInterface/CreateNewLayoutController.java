/**
 * CreateNewLayoutController class for project DJR_Store_Layout
 * Lets user dictate store dimensions for layout setup
 * @author David Roberts
 */

package DJR_Store_Layout.UserInterface;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateNewLayoutController {
    /** Launching variables */
    private Stage stage;
    private final Scene scene;

    /** FXML linked variables */
    public BorderPane bP;
    public Button submit;
    public TextField floorsText, lengthText, widthText;
    public Label floorsLabel, lengthLabel, widthLabel;
    public Pane topP, botP, rightP, leftP;
    public MenuItem back;

    /** Variables need for transition to store layout */
    private int floors, length, width;

    /** Constructor loads fxml and connects this controller */
    public CreateNewLayoutController() throws RuntimeException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("createNewLayout.fxml"));
        loader.setController(this);
        try
        {
            Parent parent = loader.load();
            scene = new Scene(parent, 800, 450);
        }
        catch (IOException ex)
        {
            System.out.println("Error displaying login window");
            throw new RuntimeException(ex);
        }
    }

    /** Launches scene */
    public void launchScene(Stage stage)
    {
        this.stage = stage;
        stage.setTitle("Set Store Layout");
        stage.setScene(scene);
        stage.setResizable(true);

        stage.hide();
        stage.show();

        setHPanes((stage.getWidth()-16));
        setVPanes((stage.getHeight()-39));
    }

    /** Basic initializer, setups button functionalities */
    public void initialize()
    {
        bP.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        //vbox.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null , null)));

        back.setOnAction(actionEvent -> new LoadOrCreateController().launchScene(stage));

        submit.setOnAction(actionEvent ->
        {
            try
            {
                floors = Integer.parseInt(floorsText.getText());
                floorsLabel.setText("");
            } catch (Exception e)
            {
                floorsLabel.setText("Please Enter a Valid Integer");
            }

            if (floors > 1)
                System.out.println("Hmmm more than one floor?");

            try
            {
                length = Integer.parseInt(lengthText.getText());
                lengthLabel.setText("");
            } catch (Exception e)
            {
                lengthLabel.setText("Please Enter a Valid Integer");
            }

            try
            {
                width = Integer.parseInt(widthText.getText());
                if (width < 1)
                    widthLabel.setText("Width cannot be less than 1");
                widthLabel.setText("");
            } catch (Exception e)
            {
                widthLabel.setText("Please Enter a Valid Integer");
            }

            try
            {
                Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                double x = screenBounds.getWidth();
                double y = screenBounds.getHeight();
                new IsleLayoutController(floors, length, width, x, y-63).launchScene(stage, false);
            }
            catch (ArithmeticException e)
            {
                lengthLabel.setText("Please Enter an Integer > 0");
                widthLabel.setText("Please Enter an Integer > 0");
            }
        });
    }

    /** Setups horizontal panes to appropriate width */
    public void setHPanes(double x)
    {
        leftP.setPrefWidth((x-400)/2);
        rightP.setPrefWidth((x-400)/2);
    }

    /** Setups vertical panes to appropriate width */
    public void setVPanes(double y)
    {
        topP.setPrefHeight((y-225)/2-25);
        botP.setPrefHeight((y-225)/2);
    }

}