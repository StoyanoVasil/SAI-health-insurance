package hospital.application;

import hospital.model.Address;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;

public class HospitalMain  extends Application {

    private Address address;
    private String hospitalName;
    private String hospitalRequestQueue;

    public HospitalMain(Address address, String hospitalName, String hospitalRequestQueue) throws IllegalArgumentException{
        if (address == null){
            throw new IllegalArgumentException("Hospital address is empty.");
        }
        if (hospitalName == null){
            throw new IllegalArgumentException("Hospital name is empty.");
        }
        if (hospitalRequestQueue == null){
            throw new IllegalArgumentException("Hospital request queue name is empty.");
        }
        this.address = address;
        this.hospitalName = hospitalName;
        this.hospitalRequestQueue = hospitalRequestQueue;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String fxml = "hospital.fxml";
        URL url  = getClass().getClassLoader().getResource( fxml );
        if (url != null) {
            FXMLLoader loader = new FXMLLoader(url);
            HospitalController hospitalController =new HospitalController(this.hospitalName,this.address, this.hospitalRequestQueue);
            loader.setController(hospitalController);
            Parent root = loader.load();
            primaryStage.setTitle(hospitalName);
            primaryStage.setScene(new Scene(root, 388, 295));
            primaryStage.setOnCloseRequest(t -> {
                Platform.exit();
                System.exit(0);
            });
            primaryStage.show();
        } else {
            System.err.println("Error: Could not load frame from "+ fxml);
        }
    }
}
