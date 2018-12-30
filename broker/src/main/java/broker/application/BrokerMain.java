package broker.application;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;

import static javafx.application.Application.launch;

public class BrokerMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        String fxml = "broker.fxml";
        URL url  = getClass().getClassLoader().getResource( fxml );
        if (url != null) {
            FXMLLoader loader = new FXMLLoader(url);
            BrokerController brokerController = new BrokerController();
            loader.setController(brokerController);
            Parent root = loader.load();
            primaryStage.setTitle("Insurance broker");
            primaryStage.setScene(new Scene(root, 500, 300));
            primaryStage.setOnCloseRequest(t -> {
                Platform.exit();
                System.exit(0);
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
