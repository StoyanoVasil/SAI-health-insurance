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
public class Gateway {

    /**
     * Declare Consumer and Producer to delegate
     * consumption and production of message respectively
     */
    private Consumer consumer;
    private Producer producer;

    /**
     * Declare TreatmentSerializer for (de)serializing TreatmentCost objects
     */
    private TreatmentSerializer treatmentSerializer;

    /**
     * Map TreatmentCostRequest to an ID in order to distinguish which
     * TreatmentCostRequest corresponds to a received TreatmentCostReply
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
    public Gateway(String producerQueueName, String consumerQueueName) throws JMSException{
        this.producer = new Producer(producerQueueName);
        this.consumer = new Consumer(consumerQueueName);
        this.treatmentSerializer = new TreatmentSerializer();
        this.correlationToTreatmentRequestMap = new HashMap<>();

        /**
         * Event listener that receives the JMS message, deserializes the body to TreatmentCostReply,
         * finds corresponding TreatmentCostRequest and pushes both to parent class through a callback
         */
        this.consumer.setConsumerMessageListener(message -> {
            try {
                // typecast message to TextMessage
                TextMessage msg = (TextMessage) message;
                // deserialize the body of the message to TreatmentCostReply
                TreatmentCostsReply treatmentCostsReply =
                        this.treatmentSerializer.deserializeTreatmentCostReplyJSON(msg.getText());
                // get TreatmentCostRequest mapped to the JMSCorrelationID of the message
                TreatmentCostsRequest treatmentCostsRequest =
                        this.correlationToTreatmentRequestMap.get(msg.getJMSCorrelationID());
                // push TreatmentCostRequest and TreatmentCostReply to parent class through callback
                onTreatmentCostReplyArrived(treatmentCostsRequest, treatmentCostsReply);
            } catch (JMSException e) { e.printStackTrace(); }
        });
    }

    /**
     * Method that serializes a TreatmentCostRequest, creates the JMS message,
     * sends the message and maps the JMSMessageID to the TreatmentCostRequest
     *
     * @param treatmentCostsRequest TreatmentCostRequest to be send
     * @throws JMSException if something goes wrong with JMS
     */
    public void requestTreatmentCostApproximation(TreatmentCostsRequest treatmentCostsRequest) throws JMSException {
        String treatmentJson = this.treatmentSerializer.serializeTreatmentCostRequest(treatmentCostsRequest);
        Message message = this.producer.createMessage(treatmentJson);
        this.producer.sendMessage(message);
        this.correlationToTreatmentRequestMap.put(message.getJMSMessageID(), treatmentCostsRequest);
    }

    /**
     * This method is a callback that has to be overwritten when initializing
     * an instance of this class to be able to handle the
     * received TreatmentCostReply with its paired TreatmentCostRequest
     *
     * @param treatmentCostsRequest TreatmentCostRequest corresponding to the TreatmentCostReply
     * @param treatmentCostsReply TreatmentCostReply received from JMS
     */
    public void onTreatmentCostReplyArrived(
            TreatmentCostsRequest treatmentCostsRequest,
            TreatmentCostsReply treatmentCostsReply
    ) {}
}
