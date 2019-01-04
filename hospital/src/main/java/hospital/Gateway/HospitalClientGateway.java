package hospital.Gateway;

import hospital.model.HospitalCostsReply;
import hospital.model.HospitalCostsRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that is responsible for delegating
 * production and consumption of JSM messages
 */
public class HospitalClientGateway {

    /**
     * Declare Consumer and Producer to delegate
     * consumption and production of messages respectively
     */
    private Consumer consumer;
    private Producer producer;

    /**
     * Declare HospitalCostsSerializer for (de)serializing HospitalCost objects
     */
    private HospitalCostsSerializer hospitalCostsSerializer;

    /**
     * Map HospitalCostsRequest to an ID in order to distinguish which
     * HospitalCostsRequest corresponds to a received HospitalCostsReply
     */
    private Map<HospitalCostsRequest, String> hospitalCostsRequestToCorrelationMap;

    /**
     * Map correlation ID to an aggregation ID
     */
    private Map<String, Integer> correlationToAggregationMap;

    /**
     * Constructor that initializes the consumer, producer, hospitalCostsRequestToCorrelationMap,
     * hospitalCostsSerializer and sets a consumer message listener
     *
     * @param producerQueueName the queue name for initializing the producer
     * @param consumerQueueName the queue name for initializing the consumer
     * @throws JMSException if something goes wrong with JMS
     */
    public HospitalClientGateway(String producerQueueName, String consumerQueueName) throws JMSException {
        // initialize all properties
        this.consumer = new Consumer(consumerQueueName);
        this.producer = new Producer(producerQueueName);
        this.hospitalCostsSerializer = new HospitalCostsSerializer();
        this.hospitalCostsRequestToCorrelationMap = new HashMap<>();
        this.correlationToAggregationMap = new HashMap<>();

        /*
          Event listener that receives the JMS message, deserializes the body to HospitalCostsReply,
          finds corresponding HospitalCostsRequest and pushes both to parent class through a callback
         */
        this.consumer.setConsumerMessageListener(message -> {
            try {
                // typecast received message to TextMessage
                TextMessage msg = (TextMessage) message;
                // get the HospitalCostsRequest from the message body
                HospitalCostsRequest hospitalCostsRequest =
                        this.hospitalCostsSerializer.deserializeHospitalCostsRequestJSON(msg.getText());
                // map necessary information
                this.hospitalCostsRequestToCorrelationMap.put(
                        hospitalCostsRequest,
                        msg.getJMSMessageID()
                );
                this.correlationToAggregationMap.put(msg.getJMSMessageID(), msg.getIntProperty("aggregationID"));
                // push the received HospitalCostsRequest
                onHospitalCostsRequestArrived(hospitalCostsRequest);
            } catch (JMSException e) { e.printStackTrace(); }
        });
    }

    /**
     * Method that prepares the JMS message, sets all necessary field
     * (JMSCorrelationID and int property aggregationID) and then sends the message
     *
     * @param hospitalCostsRequest that is being responded to
     * @param hospitalCostsReply related to the HospitalCostsRequest
     * @throws JMSException if something goes wrong with JMS
     */
    public void replyOnHospitalCostsRequest(
            HospitalCostsRequest hospitalCostsRequest,
            HospitalCostsReply hospitalCostsReply
    ) throws JMSException {
        // serialize to JSON string the HospitalCostsReply
        String hospitalCostsReplyJSON = this.hospitalCostsSerializer.serializeHospitalCostsReply(hospitalCostsReply);
        // create the message
        Message message = this.producer.createMessage(hospitalCostsReplyJSON);
        // get necessary information from maps
        String correlationId = this.hospitalCostsRequestToCorrelationMap.get(hospitalCostsRequest);
        Integer aggregationId = this.correlationToAggregationMap.get(correlationId);
        // include necessary information in message
        message.setJMSCorrelationID(correlationId);
        message.setIntProperty("aggregationID", aggregationId);
        // send the message
        this.producer.sendMessage(message);
    }

    /**
     * This method is a callback that has to be overwritten when initializing
     * an instance of this class to be able to handle the
     * received HospitalCostsRequest
     *
     * @param hospitalCostsRequest the received HospitalCostsRequest
     */
    public void onHospitalCostsRequestArrived(HospitalCostsRequest hospitalCostsRequest) {}
}
