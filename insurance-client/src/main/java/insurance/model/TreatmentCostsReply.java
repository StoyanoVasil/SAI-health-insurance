package insurance.model;

import java.text.DecimalFormat;

public class TreatmentCostsReply {

    private double hospitalPrice;
    private double transportPrice;
    private String hospitalName;

    public TreatmentCostsReply() {
        super();
    }

    public TreatmentCostsReply(double hospitalPrice, double transportPrice, String hospitalName) {
        super();
        this.hospitalPrice = hospitalPrice;
        this.transportPrice = transportPrice;
        this.hospitalName = hospitalName;
    }

    public double getHospitalPrice() {
        return hospitalPrice;
    }

    public void setHospitalPrice(double hospitalPrice) {
        this.hospitalPrice = hospitalPrice;
    }

    public double getTransportPrice() {
        return transportPrice;
    }

    public void setTransportPrice(double transportPrice) {
        this.transportPrice = transportPrice;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("####0.00");
        return "["+df.format(hospitalPrice) + "e]-[" + df.format(transportPrice) + "e]-[" + hospitalName+"]";
    }
}