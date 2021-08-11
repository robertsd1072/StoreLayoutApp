/**
 * LoadOrCreateController class for project DJR_Store_Layout
 * Gives user a choice between loading a previously made layout or creating a new layout
 * @author David Roberts
 */

package DJR_Store_Layout;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class LoadOrCreateController {
    /**
     * Launching variables
     */
    private Stage stage;
    private final Scene scene;

    /**
     * Base javafx controller for the scene
     */
    public BorderPane bP;

    /**
     * Constructor loads fxml and connects this controller
     */
    public LoadOrCreateController() throws RuntimeException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loadOrCreate.fxml"));
        loader.setController(this);
        try
        {
            Parent parent = loader.load();
            scene = new Scene(parent, 600, 325);
        }
        catch (IOException ex)
        {
            System.out.println("Error displaying login window");
            throw new RuntimeException(ex);
        }
    }

    /**
     * Actually launches the scene
     */
    public void launchScene(Stage stage)
    {
        this.stage = stage;
        stage.setTitle("ePick Store Layout");
        stage.setScene(scene);

        stage.hide();
        stage.show();
    }

    /**
     * Basic initializer
     */
    public void initialize()
    {
        bP.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
    }

    /**
     * Launches new scene for the creation of a new layout
     */
    public void create()
    {
        new CreateNewLayoutController().launchScene(stage);
    }

    /**
     * Launches new scene for the loading of an existing layout
     */
    public void load()
    {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double x = screenBounds.getWidth();
        double y = screenBounds.getHeight();

        final JFileChooser fc = new JFileChooser();

        File f = new File("src\\Saves");

        fc.setCurrentDirectory(f);

        int returnVal = fc.showOpenDialog(fc.getParent());

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();
            new IsleLayoutController(file, x, y).launchScene(stage, true);
        }
    }
}
