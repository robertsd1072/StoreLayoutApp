/**
 * SampleController2 class for project DJR_Store_Layout
 * Lets user dictate store dimensions for layout setup
 * @author David Roberts
 */

package DJR_Store_Layout;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class SampleController2 {
    /**
     * Launching variables
     */
    private Stage stage;
    private final Scene scene;

    /**
     * FXML linked variables
     */
    public BorderPane bP;
    public Button submit;
    public TextField floorsText, lengthText, widthText;
    public Label floorsLabel, lengthLabel, widthLabel;
    public Pane topP, botP, rightP, leftP;
    public Menu m1, m2, m3, m4;

    /**
     * Variables need for transition to store layout
     */
    private int floors, length, width;

    /**
     * Constructor loads fxml and connects this controller
     */
    public SampleController2() throws RuntimeException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
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

    /**
     * Launches scene
     */
    public void launchScene(Stage stage)
    {
        this.stage = stage;
        stage.setTitle("Set Store Layout");
        stage.setScene(scene);
        stage.setResizable(true);

        stage.addEventFilter(MouseEvent.MOUSE_MOVED, e ->
                this.sendMouse(e.getSceneX(), e.getSceneY()));
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            this.sendScreenX((stage.getWidth()-16));
            this.setHPanes((stage.getWidth()-16));
        });
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            this.sendScreenY((stage.getHeight()-39));
            this.setVPanes((stage.getHeight()-39));
        });

        stage.hide();
        stage.show();

        m3.setText("ScreenX: " + (stage.getWidth()-16));
        m4.setText("ScreenY: " + (stage.getHeight()-39));

        setHPanes((stage.getWidth()-16));
        setVPanes((stage.getHeight()-39));
    }

    /**
     * Basic initializer
     * Setups submit button functionality
     */
    public void initialize()
    {
        bP.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        //vbox.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null , null)));

        submit.setOnAction(actionEvent -> {
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
                new ActualController3(floors, length, width, x, y-63, null).launchScene(stage);
            }
            catch (ArithmeticException e)
            {
                lengthLabel.setText("Please Enter an Integer > 0");
                widthLabel.setText("Please Enter an Integer > 0");
            }
        });
    }

    /**
     * Displays mouse coordinates
     */
    public void sendMouse(double x, double y)
    {
        m1.setText("MouseX: " + x);
        m2.setText("MouseY: " + y);
    }

    /**
     * Setups horizontal panes to appropriate width
     */
    public void setHPanes(double x)
    {
        leftP.setPrefWidth((x-400)/2);
        rightP.setPrefWidth((x-400)/2);
    }

    /**
     * Setups vertical panes to appropriate width
     */
    public void setVPanes(double y)
    {
        topP.setPrefHeight((y-225)/2-25);
        botP.setPrefHeight((y-225)/2);
    }

    /**
     * Displays screen x dimension
     */
    public void sendScreenX(double x)
    {
        m3.setText("ScreenX: " + x);
    }

    /**
     * Displays screen x dimension
     */
    public void sendScreenY(double y)
    {
        m4.setText("ScreenY: " + y);
    }
}