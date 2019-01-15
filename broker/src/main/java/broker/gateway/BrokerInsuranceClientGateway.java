package broker.gateway;

import broker.model.client.TreatmentCostsReply;
import broker.model.client.TreatmentCostsRequest;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that is responsible for delegating
 * production and consumption of JSM messages
 */
public class BrokerInsuranceClientGateway {

    /**
     * Declare Consumer and Producer to delegate
     * consumption and production of messages respectively
     */
    private Consumer consumer;
    private Producer producer;

    /**
     * Declare TreatmentCostsSerializer for (de)serializing TreatmentCost objects
     */
    private TreatmentCostsSerializer treatmentCostsSerializer;

    /**
     * Map TreatmentCostsRequest to an ID in order to distinguish which
     * TreatmentCostsRequest corresponds to a received TreatmentCostsReply
     */
    private Map<TreatmentCostsRequest, String> treatmentCostsRequestToCorrelationMap;
    private Map<TreatmentCostsRequest, Destination> treatmentCostsRequestDestinationMap;


    /**
     * Constructor that initializes the consumer, producer, treatmentCostsRequestToCorrelationMap,
     * treatmentCostsSerializer and sets a consumer message listener
     *
     * @param producerQueueName the queue name for initializing the producer
     * @param consumerQueueName the queue name for initializing the consumer
     * @throws JMSException if something goes wrong with JMS
     */
    public BrokerInsuranceClientGateway(String producerQueueName, String consumerQueueName) throws JMSException {
        // initialize all properties
        this.consumer = new Consumer(consumerQueueName);
        this.producer = new Producer();
        this.treatmentCostsSerializer = new TreatmentCostsSerializer();
        this.treatmentCostsRequestToCorrelationMap = new HashMap<>();
        this.treatmentCostsRequestDestinationMap = new HashMap<>();

        /*
          Event listener that receives the JMS message, deserializes the body to TreatmentCostsReply,
          finds corresponding TreatmentCostsRequest and pushes both to parent class through a callback
         */
        this.consumer.setConsumerMessageListener(message -> {
            try {
                // typecast received message to TextMessage
                TextMessage msg = (TextMessage) message;
                // get the TreatmentCostsRequest from the message body
                TreatmentCostsRequest treatmentCostsRequest =
                        this.treatmentCostsSerializer.deserializeTreatmentCostsRequestJSON(msg.getText());
                // map necessary information
                this.treatmentCostsRequestToCorrelationMap.put(
                        treatmentCostsRequest,
                        msg.getJMSMessageID()
                );
                this.treatmentCostsRequestDestinationMap.put(treatmentCostsRequest, message.getJMSReplyTo());
                // push the received TreatmentCostsRequest
                onTreatmentCostsRequestArrived(treatmentCostsRequest);
            } catch (JMSException e) { e.printStackTrace(); }
        });
    }

    /**
     * Method that prepares the JMS message, sets all necessary field
     * (JMSCorrelationID) and then sends the message
     *
     * @param treatmentCostsRequest that is being responded to
     * @param treatmentCostsReply related to the TreatmentCostsRequest
     * @throws JMSException if something goes wrong with JMS
     */
    public void replyOnTreatmentCostsRequest(
            TreatmentCostsRequest treatmentCostsRequest,
            TreatmentCostsReply treatmentCostsReply
    ) throws JMSException {
        // serialize to JSON string the TreatmentCostsReply
        String treatmentCostsReplyJSON = this.treatmentCostsSerializer.serializeTreatmentCostsReply(treatmentCostsReply);
        // create the message
        Message message = this.producer.createMessage(treatmentCostsReplyJSON);
        // get necessary information from maps
        String correlationId = this.treatmentCostsRequestToCorrelationMap.get(treatmentCostsRequest);
        Destination returnDestination = this.treatmentCostsRequestDestinationMap.get(treatmentCostsRequest);
        // include necessary information in message
        message.setJMSCorrelationID(correlationId);
        // sendMessage the message
        this.producer.sendMessage(message, returnDestination);
    }

    /**
     * This method is a callback that has to be overwritten when initializing
     * an instance of this class to be able to handle the
     * received TreatmentCostsRequest
     *
     * @param treatmentCostsRequest the received TreatmentCostsRequest
     */
    public void onTreatmentCostsRequestArrived(TreatmentCostsRequest treatmentCostsRequest) {}
}
