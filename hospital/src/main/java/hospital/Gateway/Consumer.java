package hospital.Gateway;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * A class that is responsible for consuming
 * messages send to a set JMS message queue
 */
public class Consumer {

    /**
     * String that holds the JMS provider url
     */
    private static final String JMS_PROVIDER_URL = "tcp://localhost:61616";

    /**
     * Declare variable that will hold the objects necessary
     * for making the connection to JMS, creating JMS messages
     * and sending JMS messages
     */
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageConsumer consumer;

    /**
     * Constructor that initializes the connection,
     * the session, the destination and the consumer
     * for a given queue name
     *
     * @param queueName String that holds the queue name of the queue
     *                  from which the Consumer is going to consume messages
     */
    public Consumer(String queueName) {
        try {
            // set properties
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, JMS_PROVIDER_URL);
            props.put(("queue." + queueName), queueName);

            // create connection and session
            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
                    .lookup("ConnectionFactory");
            this.connection = connectionFactory.createConnection();
            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // create clientDestination and consumer
            this.destination = (Destination) jndiContext.lookup(queueName);
            this.consumer = session.createConsumer(this.destination);

            // start connection
            this.connection.start();
        } catch (JMSException | NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * A method that sets in the consumer a message listener
     * that is going to handle messages when they arrive
     *
     * @param messageListener the message listener to be set
     * @throws JMSException if something goes wrong with JMS
     */
    public void setConsumerMessageListener(MessageListener messageListener) throws JMSException {
        this.consumer.setMessageListener(messageListener);
    }
}
