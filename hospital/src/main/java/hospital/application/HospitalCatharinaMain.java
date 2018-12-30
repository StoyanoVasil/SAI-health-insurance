package hospital.application;

import hospital.model.Address;

public class HospitalCatharinaMain extends HospitalMain {

    public HospitalCatharinaMain() throws IllegalArgumentException {
        super(new Address("Michelangelolaan", 2, "Eindhoven"), "Catharina Ziekenhuis", "catharinaRequestQueue");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
