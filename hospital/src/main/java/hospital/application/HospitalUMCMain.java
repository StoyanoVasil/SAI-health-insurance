package hospital.application;

import hospital.model.Address;

public class HospitalUMCMain extends HospitalMain {

    public HospitalUMCMain() throws IllegalArgumentException {
        super(new Address("Heidelberglaan", 100, "Utrecht"), "University Medical Center (UMC)", "umcRequestQueue");
    }

    public static void main(String[] args) {
        launch(args);
    }
}