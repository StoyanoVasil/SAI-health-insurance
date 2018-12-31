package hospital.Gateway;

import com.google.gson.Gson;
import hospital.model.HospitalCostsReply;
import hospital.model.HospitalCostsRequest;

/**
 * A class that is responsible for serializing to a JSON sting and
 * deserializing  from a JSON string HospitalCostsRequest and HospitalCostsReply objects
 */
public class HospitalCostsSerializer {

    // Declare Gson object that does the (de)serializing
    private Gson serializer;

    /**
     * Constructor that initializes the Gson object
     */
    public HospitalCostsSerializer() {
        this.serializer = new Gson();
    }

    /**
     * Serializes HospitalCostsRequest to a JSON string by using the Gson object
     * then returns the serialized string
     *
     * @param hospitalCostsRequest HospitalCostsRequest object to be serialized
     * @return String hospitalCostsRequest serialized to a JSON string
     */
    public String serializeHospitalCostsRequest(HospitalCostsRequest hospitalCostsRequest) {
        return this.serializer.toJson(hospitalCostsRequest);
    }

    /**
     * Serializes HospitalCostsReply to a JSON string by using the Gson object
     * then returns the serialized string
     *
     * @param hospitalCostsReply HospitalCostsRequest object to be serialized
     * @return String hospitalCostsReply serialized to a JSON string
     */
    public String serializeHospitalCostsReply(HospitalCostsReply hospitalCostsReply) {
        return this.serializer.toJson(hospitalCostsReply);
    }

    /**
     * Deserializes a JSON string to a HospitalCostsRequest object by using the Gson object,
     * then returns the deserialized object
     *
     * @param json JSON string to be deserialized
     * @return HospitalCostsRequest the deserialized object
     */
    public HospitalCostsRequest deserializeHospitalCostsRequestJSON(String json) {
        return this.serializer.fromJson(json, HospitalCostsRequest.class);
    }

    /**
     * Deserializes a JSON string to a HospitalCostsReply object by using the Gson object,
     * then returns the deserialized object
     *
     * @param json JSON string to be deserialized
     * @return HospitalCostsReply the deserialized object
     */
    public HospitalCostsReply deserializeHospitalCostsReplyJSON(String json) {
        return this.serializer.fromJson(json, HospitalCostsReply.class);
    }
}
