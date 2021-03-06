package broker.gateway;

import com.google.gson.Gson;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * A class that is responsible for creating JMS messages
 * and sending them to a set JMS message queue
 */
public class Producer {

    /**
     * String that holds the JMS provider url
     */
    private static final String JMS_CONNECTION = "tcp://localhost:61616";

    /**
     * Declare variable that will hold the objects necessary
     * for making the connection to JMS, creating JMS messages
     * and sending JMS messages
     */
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer producer;

    /**
     * Constructor that initializes the connection,
     * the session, the destination and an empty producer
     * that will sendMessage messages with a given queueName later on
     */
    public Producer() {
        try {
            // set properties
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, JMS_CONNECTION);

            // create connection and session
            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
                    .lookup("ConnectionFactory");
            this.connection = connectionFactory.createConnection();
            this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // create an empty producer
            this.producer = this.session.createProducer(null);

        } catch (JMSException | NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor that initializes the connection,
     * the session, the destination and the producer
     * for a given queue name
     *
     * @param queueName String that holds the queue name of the queue
     *                  which the Producer is going to communicate with
     */
    public Producer(String queueName) {
        try {
            // set properties
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, JMS_CONNECTION);
            props.put(("queue." + queueName), queueName);

            // create connection and session
            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
                    .lookup("ConnectionFactory");
            this.connection = connectionFactory.createConnection();
            this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // create brokerDestination and producer
            this.destination = (Destination) jndiContext.lookup(queueName);
            this.producer = session.createProducer(this.destination);
        } catch (JMSException | NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that creates the JMS message
     * containing a given message body
     *
     * @param messageBody String contents to be wrapped with a JMS message
     * @return Message object containing the message body
     * @throws JMSException if something goes wrong with JMS
     */
    public Message createMessage(String messageBody) throws JMSException{
        return this.session.createTextMessage(messageBody);
    }

    /**
     * Method that sends a given message to the
     * queue destination initialized in the constructor
     *
     * @param message to be sendMessage
     * @throws JMSException if something goes wrong with JMS
     */
    public void sendMessage(Message message) throws JMSException {
        this.producer.send(message);
    }

    /**
     * Method that sends a given message to a
     * destination created from the passed as
     * a parameter queue name
     *
     * @param message to be sendMessage
     * @param queueName
     * @throws JMSException if something goes wrong with JMS
     */
    public void sendMessage(Message message, String queueName) throws JMSException {
        this.producer.send(this.session.createQueue(queueName), message);
    }

    public void sendMessage(Message message, Destination destination) throws JMSException {
        this.producer.send(destination, message);
    }
}
