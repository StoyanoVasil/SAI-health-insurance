package insurance.gateway;

import com.google.gson.Gson;
import insurance.model.TreatmentCostsReply;
import insurance.model.TreatmentCostsRequest;

/**
 * A class that is responsible for serializing to a JSON sting and
 * deserializing  from a JSON string TreatmentCostsRequest and TreatmentCostsReply objects
 */
public class TreatmentSerializer {

    // Declare Gson object that does the (de)serializing
    private Gson serializer;

    /**
     * Constructor that initializes the Gson object
     */
    public TreatmentSerializer() {
        this.serializer = new Gson();
    }

    /**
     * Serializes TreatmentCostRequest to a JSON string by using the Gson object
     * then returns the serialized string
     *
     * @param treatmentCostsRequest TreatmentCostRequest object to be serialized
     * @return String treatmentCostRequest serialized to a JSON string
     */
    public String serializeTreatmentCostRequest(TreatmentCostsRequest treatmentCostsRequest) {
        return this.serializer.toJson(treatmentCostsRequest);
    }

    /**
     * Serializes TreatmentCostReply to a JSON string by using the Gson object
     * then returns the serialized string
     *
     * @param treatmentCostsReply TreatmentCostRequest objext to be serialized
     * @return String treatmentCostReply serialized to a JSON string
     */
    public String serializeTreatmentCostReply(TreatmentCostsReply treatmentCostsReply) {
        return this.serializer.toJson(treatmentCostsReply);
    }

    /**
     * Deserializes a JSON string to a TreatmentCostRequest object by using the Gson object,
     * then returns the deserialized object
     *
     * @param json JSON string to be deserialized
     * @return TreatmentCostRequest the deserialized object
     */
    public TreatmentCostsRequest deserializeTreatmentCostRequestJSON(String json) {
        return this.serializer.fromJson(json, TreatmentCostsRequest.class);
    }

    /**
     * Deserializes a JSON string to a TreatmentCostReply object by using the Gson object,
     * then returns the deserialized object
     *
     * @param json JSON string to be deserialized
     * @return TreatmentCostReply the deserialized object
     */
    public TreatmentCostsReply deserializeTreatmentCostReplyJSON(String json) {
        return this.serializer.fromJson(json, TreatmentCostsReply.class);
    }
}
