package broker.gateway;

import broker.model.hospital.HospitalCostsReply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A class responsible for administering the received HospitalCostsReply
 */
public class HospitalReplyAggregator {

    /**
     * Map the aggregationId to the number of expected replies
     * Map the aggregationId to the received replies
     */
    private Map<Integer, Integer> aggregationIdToNumberExpectedRepliesMap;
    private Map<Integer, ArrayList<HospitalCostsReply>> aggregationIdToReceivedRepliesMap;

    /**
     * Constructor that initializes both maps
     */
    public HospitalReplyAggregator() {
        this.aggregationIdToNumberExpectedRepliesMap = new HashMap<>();
        this.aggregationIdToReceivedRepliesMap = new HashMap<>();
    }

    /**
     * Create a new aggregation by populating maps
     *
     * @param aggregationId the identifier of the aggregation
     * @param numberOfExpectedReplies the expected number of replies
     */
    public void createAggregation(Integer aggregationId, Integer numberOfExpectedReplies) {
        this.aggregationIdToNumberExpectedRepliesMap.put(aggregationId, numberOfExpectedReplies);
        this.aggregationIdToReceivedRepliesMap.put(aggregationId, new ArrayList<>());
    }

    /**
     * Method that adds a new HospitalCostsReply and
     * checks if all expected replies are received
     *
     * @param hospitalCostsReply to be added to administration
     * @param aggregationId the identifier of the aggregation
     */
    public void newHospitalCostsReplyReceived(HospitalCostsReply hospitalCostsReply, Integer aggregationId) {
        this.aggregationIdToReceivedRepliesMap.get(aggregationId).add(hospitalCostsReply);
        checkAllRepliesReceivedForAggregationId(aggregationId);
    }

    /**
     * Method that checks if all expected replies for a given aggregationId
     * are received, if yes then pushes the one with lowest price through the
     * callback
     *
     * @param aggregationId identification of the aggregation
     */
    private void checkAllRepliesReceivedForAggregationId(Integer aggregationId) {
        ArrayList<HospitalCostsReply> hospitalCostsReplies = this.aggregationIdToReceivedRepliesMap.get(aggregationId);
        if (hospitalCostsReplies.size() == this.aggregationIdToNumberExpectedRepliesMap.get(aggregationId)) {
            double minPrice = Integer.MAX_VALUE;
            HospitalCostsReply bestHospitalCostsReply = null;
            for(HospitalCostsReply reply : hospitalCostsReplies) {
                if (minPrice > reply.getPrice()) bestHospitalCostsReply = reply;
            }
            onAllHospitalCostsRepliesReceived(bestHospitalCostsReply, aggregationId);
        }
    }

    /**
     * A callback that needs to be implemented when
     * creating an instance of this class that pushes
     * a HospitalCostsReply for an aggregationId
     *
     * @param hospitalCostsReply the HospitalCostsReply that is being pushed
     * @param aggregationId the identification of the aggregation to which the HospitalCostsReply belongs
     */
    public void onAllHospitalCostsRepliesReceived(HospitalCostsReply hospitalCostsReply, Integer aggregationId) {}
}
