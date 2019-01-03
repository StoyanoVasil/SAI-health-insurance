package broker.application;

import broker.gateway.BrokerInsuranceClientGateway;
import broker.model.client.TreatmentCostsRequest;
import broker.model.hospital.HospitalCostsRequest;
import javafx.application.Platform;
import javafx.fxml.FXML;


import javafx.scene.control.ListView;

import javax.jms.JMSException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for controlling all the
 * interactions to the gui, communicating with hospital
 * and insurance-client gateways and administration
 */
public class BrokerController {

    /**
     * Store broker and insurance client queue name
     */
    private static final String JMS_BROKER_CLIENT_QUEUE_NAME = "broker-client-queue";
    private static final String JMS_INSURANCE_CLIENT_QUEUE_NAME = "insurance-client-queue";

    /**
     * Declare BrokerInsuranceClientGateway and Scatter-Gather
     */
    private BrokerInsuranceClientGateway brokerInsuranceClientGateway;
    //todo: declare the scatter-gather

    /**
     * Declare mapping to map HospitalCostsRequest to TreatmentCostsRequest
     */
    private Map<HospitalCostsRequest, TreatmentCostsRequest> hospitalCostsReqToTreatmentCostsReq;

    /**
     * Declare JavaFX objects
     */
    @FXML
    public ListView<BrokerListLine> lvRequestReply;

    /**
     * Constructor that initializes all properties and
     * implementing the callbacks for the gateway and
     * the scatter-gather
     */
    public BrokerController() {
        this.hospitalCostsReqToTreatmentCostsReq = new HashMap<>();

        // initialize BrokerInsuranceClientGateway
        try {
            this.brokerInsuranceClientGateway = new BrokerInsuranceClientGateway(
                    JMS_INSURANCE_CLIENT_QUEUE_NAME,
                    JMS_BROKER_CLIENT_QUEUE_NAME
            ) {
                public void onTreatmentCostsRequestArrived(TreatmentCostsRequest treatmentCostsRequest) {
                    // create HospitalCostsRequest from the received TreatmentCostsRequest
                    HospitalCostsRequest hospitalCostsRequest = new HospitalCostsRequest(
                            treatmentCostsRequest.getSsn(),
                            treatmentCostsRequest.getTreatmentCode(),
                            treatmentCostsRequest.getAge());
                    // map HospitalCostsRequest t
                    hospitalCostsReqToTreatmentCostsReq.put(hospitalCostsRequest, treatmentCostsRequest);
                    // create BrokerListLine and add to ListView TreatmentCostsRequest
                    //todo: maybe change BrokerListLine to use TreatmentCostsRequest
                    BrokerListLine brokerListLine = new BrokerListLine(hospitalCostsRequest, null);
                    addBrokerListLineToListView(brokerListLine);
                    //todo: send to hospital
                }
            };
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that adds BrokerListLine to the lvRequestsReply
     *
     * @param brokerListLine to be added to lvRequestsReply
     */
    private void addBrokerListLineToListView(BrokerListLine brokerListLine) {
        Platform.runLater(() -> {
            this.lvRequestReply.getItems().add(brokerListLine);
        });
    }
}
