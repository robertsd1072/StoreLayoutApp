/**
 * Main class for project DJR_Store_Layout
 * Launches LoadOrCreateController
 * @author David Roberts
 */

package DJR_Store_Layout;

import DJR_Store_Layout.UserInterface.LoadOrCreateController;
import DJR_Store_Layout.UserInterface.SetupPickingPathController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage primaryStage)
    {
        new LoadOrCreateController().launchScene(primaryStage);
    }
    //For setting store layout: new LoadOrCreateController().launchScene(primaryStage);

    //For picking path:
    /*
    FXMLLoader loader = new FXMLLoader(getClass().getResource("UserInterface/setupPickingPath.fxml"));
        Scene newScene;
        try
        {
            newScene = new Scene(loader.load());
        }
        catch (Exception ex)
        {
            System.out.println("Error displaying login window");
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(newScene);
        inputStage.show();

        SetupPickingPathController setupPath = loader.getController();
        setupPath.setFile("src/Saves/PhillyStore.sly");
     */
}