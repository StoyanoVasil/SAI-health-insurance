package broker.gateway;

import broker.model.hospital.HospitalCostsReply;
import broker.model.hospital.HospitalCostsRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that is responsible for delegating
 * production and consumption of JSM messages
 */
public class BrokerHospitalClientGateway {

    /**
     * Declare Consumer and Producer to delegate
     * consumption and production of messages respectively
     */
    private Consumer consumer;
    private Producer producer;

    /**
     * Declare HospitalCostsSerializer for (de)serializing HospitalCosts objects
     */
    private HospitalCostsSerializer hospitalCostsSerializer;

    /**
     * Map ID to a HospitalCostsRequest in order to distinguish which
     * HospitalCostsRequest corresponds to a received HospitalCostsReply
     */
    private Map<String, HospitalCostsRequest> correlationToHospitalCostsRequestMap;

    /**
     * Constructor that initialized the consumer, the producer, the
     * correlationToHospitalCostsRequestMap, the HospitalCostsSerializer
     * and sets consumer event listener
     *
     * @param consumerQueueName the name of the queue the consumer listens to
     */
    public BrokerHospitalClientGateway(String consumerQueueName) throws JMSException {
        this.consumer = new Consumer(consumerQueueName);
        this.producer = new Producer();
        this.hospitalCostsSerializer = new HospitalCostsSerializer();
        this.correlationToHospitalCostsRequestMap = new HashMap<>();

        this.consumer.setConsumerMessageListener(message -> {
            try {
                // typecast received message to TextMessage
                TextMessage msg = (TextMessage) message;
                // get the HospitalCostsReply from the message
                HospitalCostsReply hospitalCostsReply =
                        this.hospitalCostsSerializer.deserializeHospitalCostsReplyJSON(msg.getText());
                // get the HospitalCostsRequest from map with JMSCorrelationID
                HospitalCostsRequest hospitalCostsRequest =
                        this.correlationToHospitalCostsRequestMap.get(msg.getJMSCorrelationID());
                // push necessary information
                onHospitalCostsReplyReceived(
                        hospitalCostsRequest,
                        hospitalCostsReply,
                        msg.getIntProperty("aggregationID"));
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Method that prepares the JMS message, sets all necessary field
     * (aggregationID), then sends the message and maps the JMSMessageID
     * to HospitalCostsRequest
     *
     * @param hospitalCostsRequest to be sent
     * @param aggregationId Integer property to be set in the created JMS message
     * @param hospitalQueueName where the HospitalCostsRequest will be sent
     * @throws JMSException if something goes wrong with JMS
     */
    public void requestApproximation(
            HospitalCostsRequest hospitalCostsRequest,
            Integer aggregationId,
            String hospitalQueueName
    ) throws JMSException {
        // serialize to JSON string the HospitalCostsRequest
        String hospitalCostsRequestJSON =
                this.hospitalCostsSerializer.serializeHospitalCostsRequest(hospitalCostsRequest);
        // create the message
        Message message = this.producer.createMessage(hospitalCostsRequestJSON);
        // set aggregationId int property in the message
        message.setIntProperty("aggregationID", aggregationId);
        // send the message to a given queue
        this.producer.sendMessage(message, hospitalQueueName);
        // map the JMSMessageID to HospitalCostsRequest
        this.correlationToHospitalCostsRequestMap.put(message.getJMSMessageID(), hospitalCostsRequest);
    }

    /**
     * This method is a callback that has to be overwritten when initializing
     * an instance of this class to be able to handle the
     * received HospitalCostsReply
     *
     * @param hospitalCostsRequest the corresponding HospitalCostsRequest
     * @param hospitalCostsReply the received HospitalCostsReply
     * @param aggregationId Integer property that is set in the message
     */
    public void onHospitalCostsReplyReceived(
            HospitalCostsRequest hospitalCostsRequest,
            HospitalCostsReply hospitalCostsReply,
            Integer aggregationId) {}
}
