package insurance.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;

public class InsuranceClientMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        String fxml = "insurance_client.fxml";
        URL url  = getClass().getClassLoader().getResource( fxml );
        if (url != null) {
            Parent root = FXMLLoader.load(url);
            primaryStage.setTitle("Hospital Treatment Client");
            primaryStage.setScene(new Scene(root, 473, 384));
            primaryStage.setOnCloseRequest(new EventHandler<>() {
                @Override
                public void handle(WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
            });

            primaryStage.show();
        } else {
            System.err.println("Error: Could not load frame from "+ fxml);
        }

    }


    public static void main(String[] args) {
        launch(args);
    }

}
