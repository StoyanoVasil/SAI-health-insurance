package hospital.application;

import hospital.model.Address;

public class HospitalMaximaMain extends HospitalMain {

    public HospitalMaximaMain() throws IllegalArgumentException {
        super(new Address("Dominee Theodor Fliednerstraat", 1, "Veldhoven"), "Máxima Medisch Centrum", "maximaRequestQueue");
    }

    public static void main(String[] args) {
        launch(args);
    }
}