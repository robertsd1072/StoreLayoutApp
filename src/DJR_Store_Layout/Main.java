/**
 * Main class for project DJR_Store_Layout
 * Launches LoadOrCreateController
 * @author David Roberts
 */

package DJR_Store_Layout;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        new LoadOrCreateController().launchScene(primaryStage);
    }
}