package hospital.model;

import java.text.DecimalFormat;

public class HospitalCostsReply {

   private double price;
   private String hospitalName;
   private Address address;

    public HospitalCostsReply() {
        super();

    }

    public HospitalCostsReply(double price, String hospitalName, Address address) {
        super();
        this.price = price;
        this.hospitalName = hospitalName;
        this.address = address;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("####0.00");
        return "["+df.format(price) + "e]-[" + address.toString()+"]";
    }
}
