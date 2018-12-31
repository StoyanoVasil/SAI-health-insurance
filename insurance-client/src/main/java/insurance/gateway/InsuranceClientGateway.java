package insurance.gateway;

import insurance.model.TreatmentCostsReply;
import insurance.model.TreatmentCostsRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that is responsible for delegating
 * production and consumption of JSM messages
 */
public class InsuranceClientGateway {

    /**
     * Declare Consumer and Producer to delegate
     * consumption and production of messages respectively
     */
    private Consumer consumer;
    private Producer producer;

    /**
     * Declare TreatmentCostsSerializer for (de)serializing TreatmentCosts objects
     */
    private TreatmentCostsSerializer treatmentCostsSerializer;

    /**
     * Map TreatmentCostsRequest to an ID in order to distinguish which
     * TreatmentCostsRequest corresponds to a received TreatmentCostsReply
     */
    private Map<String, TreatmentCostsRequest> correlationToTreatmentRequestMap;

    /**
     * Constructor that initializes the consumer, producer, correlationToTreatmentRequestMap,
     * treatmentSerialized and sets a consumer message listener
     *
     * @param producerQueueName the queue name for initializing the producer
     * @param consumerQueueName the queue name for initializing the consumer
     * @throws JMSException if something goes wrong with JMS
     */
    public InsuranceClientGateway(String producerQueueName, String consumerQueueName) throws JMSException {
        // initialize all properties
        this.producer = new Producer(producerQueueName);
        this.consumer = new Consumer(consumerQueueName);
        this.treatmentCostsSerializer = new TreatmentCostsSerializer();
        this.correlationToTreatmentRequestMap = new HashMap<>();

        /*
          Event listener that receives the JMS message, deserializes the body to TreatmentCostsReply,
          finds corresponding TreatmentCostsRequest and pushes both to parent class through a callback
         */
        this.consumer.setConsumerMessageListener(message -> {
            try {
                // typecast message to TextMessage
                TextMessage msg = (TextMessage) message;
                // deserialize the body of the message to TreatmentCostsReply
                TreatmentCostsReply treatmentCostsReply =
                        this.treatmentCostsSerializer.deserializeTreatmentCostsReplyJSON(msg.getText());
                // get TreatmentCostsRequest mapped to the JMSCorrelationID of the message
                TreatmentCostsRequest treatmentCostsRequest =
                        this.correlationToTreatmentRequestMap.get(msg.getJMSCorrelationID());
                // push TreatmentCostsRequest and TreatmentCostsReply to parent class through callback
                onTreatmentCostsReplyArrived(treatmentCostsRequest, treatmentCostsReply);
            } catch (JMSException e) { e.printStackTrace(); }
        });
    }

    /**
     * Method that serializes a TreatmentCostsRequest, creates the JMS message,
     * sends the message and maps the JMSMessageID to the TreatmentCostsRequest
     *
     * @param treatmentCostsRequest TreatmentCostsRequest to be send
     * @throws JMSException if something goes wrong with JMS
     */
    public void requestTreatmentCostApproximation(TreatmentCostsRequest treatmentCostsRequest) throws JMSException {
        // serialize the TreatmentCostsRequest to JSON string
        String treatmentJson = this.treatmentCostsSerializer.serializeTreatmentCostsRequest(treatmentCostsRequest);
        // create the message
        Message message = this.producer.createMessage(treatmentJson);
        // send the message
        this.producer.sendMessage(message);
        // save necessary information in map
        this.correlationToTreatmentRequestMap.put(message.getJMSMessageID(), treatmentCostsRequest);
    }

    /**
     * This method is a callback that has to be overwritten when initializing
     * an instance of this class to be able to handle the
     * received TreatmentCostsReply with its paired TreatmentCostsRequest
     *
     * @param treatmentCostsRequest TreatmentCostsRequest corresponding to the TreatmentCostsReply
     * @param treatmentCostsReply TreatmentCostsReply received from JMS
     */
    public void onTreatmentCostsReplyArrived(
            TreatmentCostsRequest treatmentCostsRequest,
            TreatmentCostsReply treatmentCostsReply
    ) {}
}
