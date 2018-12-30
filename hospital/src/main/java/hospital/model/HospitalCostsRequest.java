package hospital.model;

public class HospitalCostsRequest {

    private int ssn; // social security number
    private int age;
    private String treatmentCode;

    public HospitalCostsRequest() {
        super();
    }

    public HospitalCostsRequest(int ssn, String treatmentCode, int age) {
        super();
        this.ssn = ssn;
        this.treatmentCode = treatmentCode;
        this.age = age;
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

    @Override
    public String toString() {
        return "["+ssn + "]-[" + treatmentCode+"]";
    }

}
