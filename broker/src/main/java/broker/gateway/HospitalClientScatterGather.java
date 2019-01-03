package broker.gateway;

import broker.model.hospital.HospitalCostsReply;
import broker.model.hospital.HospitalCostsRequest;

import javax.jms.JMSException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for delegating scattering of HospitalCostsRequest
 * and gathering the corresponding HospitalCostsReplies
 */
public class HospitalClientScatterGather {

    /**
     * Declare the BrokerHospitalClientGateway
     */
    private BrokerHospitalClientGateway brokerHospitalClientGateway;

    /**
     * Declare HospitalRecipientList and HospitalReplyAggregator
     */
    private HospitalRecipientList hospitalRecipientList;
    private HospitalReplyAggregator hospitalReplyAggregator;

    /**
     * Declare and integer that serves as aggregationId generator
     * by incrementing every time a new aggregation is created
     */
    private int aggregationIdGenerator;

    /**
     * Declare mapping for mapping aggregationId to BankInterestRequest
     */
    private Map<Integer, HospitalCostsRequest> aggregationIdToHospitalCostsRequestMap;

    /**
     * Constructor that initializes all properties and implements all callbacks
     */
    public HospitalClientScatterGather(String consumerQueueName) throws JMSException {
        // initialize aggregationIdGenerator and mapping
        this.aggregationIdGenerator = 0;
        this.aggregationIdToHospitalCostsRequestMap = new HashMap<>();
        // initialize BrokerHospitalClientGateway and implement callback
        this.brokerHospitalClientGateway = new BrokerHospitalClientGateway(consumerQueueName) {
            public void onHospitalCostsReplyReceived(
                    HospitalCostsRequest hospitalCostsRequest,
                    HospitalCostsReply hospitalCostsReply,
                    Integer aggregationId
            ) {
                if (!aggregationIdToHospitalCostsRequestMap.containsKey(aggregationId)) {
                    aggregationIdToHospitalCostsRequestMap.put(aggregationId, hospitalCostsRequest);
                }
                hospitalReplyAggregator.newHospitalCostsReplyReceived(hospitalCostsReply, aggregationId);
            }
        };
        // initialize HospitalReplyAggregator
        this.hospitalReplyAggregator = new HospitalReplyAggregator();
        // initialize HospitalRecipientList
        this.hospitalRecipientList = new HospitalRecipientList(this.brokerHospitalClientGateway);
    }

    /**
     * Method that creates a new aggregation in the HospitalReplyAggregation
     * after sending the HospitalCostsRequest via the HospitalRecipientList
     *
     * @param hospitalCostsRequest to be sent
     * @throws JMSException if something goes wrong with JMS
     */
    public void requestApproximation(HospitalCostsRequest hospitalCostsRequest) throws JMSException {
        // send HospitalCostsRequest and store the number of hospitals to which the request was sent
        int sentCount = this.hospitalRecipientList.sendHospitalCostsRequest(
                hospitalCostsRequest, this.aggregationIdGenerator);
        // create the aggregation
        this.hospitalReplyAggregator.createAggregation(this.aggregationIdGenerator, sentCount);
        // increment the generator
        this.aggregationIdGenerator++;
    }

    /**
     * Callback that needs to be implemented when creating an instance of this class
     * that pushes the received HospitalCostsReply and corresponding HospitalCostsRequest
     *
     * @param hospitalCostsRequest corresponding to HospitalCostsReply
     * @param hospitalCostsReply received HospitalCostsReply
     */
    public void onHospitalCostsReplyReceived(
            HospitalCostsRequest hospitalCostsRequest,
            HospitalCostsReply hospitalCostsReply) {}
}
