package broker.gateway;

import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Class responsible for connecting to the Transport Service
 * and accessing to its endpoints
 */
public class TransportServiceClient {

    /**
     * Store the Transport Service base URL
     */
    private final static String TRANSPORT_SERVICE_BASE_URL = "http://localhost:8080/transport/rest/";

    /**
     * Declare the webTarget that is going to be connected with the service
     */
    private WebTarget webTarget;

    /**
     * Constructor that initializes the WebTarget
     */
    public TransportServiceClient() {
        URI baseUri = UriBuilder.fromUri(TRANSPORT_SERVICE_BASE_URL).build();
        this.webTarget = ClientBuilder.newClient(new ClientConfig()).target(baseUri);
    }

    /**
     * Method that makes a call to the Transport Service API
     * to get the price per kilometer
     *
     * @return the price per kilometer as a double
     */
    public double getTransportPricePerKilometer() {
        Response response = this.webTarget.path("price").request(MediaType.TEXT_PLAIN).get();
        return Double.parseDouble(response.readEntity(String.class));
    }
}
