package insurance.model;

public class TreatmentCostsRequest {

    private int ssn; // social security number
    private int age;

    private String treatmentCode;
    private int transportDistance;

    public TreatmentCostsRequest() {
        super();
    }

    public TreatmentCostsRequest(int ssn, int age, String treatmentCode, int transportDistance) {
        super();
        this.ssn = ssn;
        this.age = age;
        this.treatmentCode = treatmentCode;
        this.transportDistance = transportDistance;
    }

    public TreatmentCostsRequest(int ssn, String treatmentCode) {
        super();
        this.ssn = ssn;
        this.treatmentCode = treatmentCode;
        this.transportDistance = 0;
    }

    public int getSsn() {
        return ssn;
    }

    public void setSsn(int ssn) {
        this.ssn = ssn;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public String getTreatmentCode() {
        return treatmentCode;
    }

    public void setTreatmentCode(String treatmentCode) {
        this.treatmentCode = treatmentCode;
    }

    public int getTransportDistance() {
        return transportDistance;
    }

    public void setTransportDistance(int transportDistance) {
        this.transportDistance = transportDistance;
    }

    @Override
    public String toString() {
        return "["+ssn + "]-[" + age + "]-[" +treatmentCode + "]-[" + transportDistance + "km]";
    }

}
