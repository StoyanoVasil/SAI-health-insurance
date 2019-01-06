package broker.application;

import broker.gateway.BrokerInsuranceClientGateway;
import broker.gateway.HospitalClientScatterGather;
import broker.gateway.TransportServiceClient;
import broker.model.client.TreatmentCostsReply;
import broker.model.client.TreatmentCostsRequest;
import broker.model.hospital.HospitalCostsReply;
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
    private static final String JMS_BROKER_INSURANCE_CLIENT_QUEUE_NAME = "broker-insurance-client-queue";
    private static final String JMS_BROKER_HOSPITAL_CLIENT_QUEUE_NAME = "broker-hospital-client-queue";
    private static final String JMS_INSURANCE_CLIENT_QUEUE_NAME = "insurance-client-queue";

    /**
     * Declare BrokerInsuranceClientGateway, Scatter-Gather and TransportServiceClient
     */
    private BrokerInsuranceClientGateway brokerInsuranceClientGateway;
    private HospitalClientScatterGather hospitalClientScatterGather;
    private TransportServiceClient transportServiceClient;

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
     * implementing the callbacks for the gateway,
     * the scatter-gather and transport service client
     */
    public BrokerController() {
        this.hospitalCostsReqToTreatmentCostsReq = new HashMap<>();
        this.transportServiceClient = new TransportServiceClient();

        try {
            // initialize BrokerInsuranceClientGateway
            this.brokerInsuranceClientGateway = new BrokerInsuranceClientGateway(
                    JMS_INSURANCE_CLIENT_QUEUE_NAME,
                    JMS_BROKER_INSURANCE_CLIENT_QUEUE_NAME
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
                    BrokerListLine brokerListLine = new BrokerListLine(treatmentCostsRequest, null);
                    addBrokerListLineToListView(brokerListLine);
                    try {
                        hospitalClientScatterGather.requestApproximation(hospitalCostsRequest);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            };

            //initialize HospitalClientScatterGather and implement callback
            this.hospitalClientScatterGather = new HospitalClientScatterGather(JMS_BROKER_HOSPITAL_CLIENT_QUEUE_NAME) {
                public void onHospitalCostsReplyReceived(
                        HospitalCostsRequest hospitalCostsRequest, HospitalCostsReply hospitalCostsReply) {
                    // get TreatmentCostsRequest from map
                    TreatmentCostsRequest treatmentCostsRequest =
                            hospitalCostsReqToTreatmentCostsReq.get(hospitalCostsRequest);
                    // calculate transport costs and set in TreatmentCostsReply
                    double transportCosts = calculateTransportPrice(treatmentCostsRequest.getTransportDistance());
                    TreatmentCostsReply treatmentCostsReply = new TreatmentCostsReply(
                            hospitalCostsReply.getPrice(),
                            transportCosts,
                            hospitalCostsReply.getHospitalName()
                    );
                    // send the TreatmentCostsRequest and TreatmentCostsReply to the insurance client
                    try {
                        brokerInsuranceClientGateway.replyOnTreatmentCostsRequest(
                                treatmentCostsRequest,treatmentCostsReply);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                    // update ListView
                    BrokerListLine brokerListLine = findBrokerListLineByTreatmentCostsRequest(treatmentCostsRequest);
                    brokerListLine.setReply(treatmentCostsReply);
                    lvRequestReply.refresh();
                }
            };
        } catch (JMSException | NullPointerException e) {
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

    /**
     * Method that searches for BrokerListLine
     * containing the HospitalCostsRequest given
     *
     * @param treatmentCostsRequest the search parameter
     * @return BrokerListLine if found, otherwise null
     */
    private BrokerListLine findBrokerListLineByTreatmentCostsRequest(TreatmentCostsRequest treatmentCostsRequest) {
        for(BrokerListLine bll : this.lvRequestReply.getItems()) {
            if(treatmentCostsRequest.equals(bll.getRequest())) return bll;
        }
        return null;
    }

    /**
     * Method that gets the price per kilometer from the TransportService and
     * calculates the transport cost by multiplying the distance by the price
     * per kilometer. If the distance is 0 or less, the method returns 0 without
     * making he call to the TransportService.
     *
     * @param transportDistance the number of kilometers
     * @return the calculated transport price as double or 0
     */
    private double calculateTransportPrice(Integer transportDistance) {
        if (transportDistance > 0) {
            double price = this.transportServiceClient.getTransportPricePerKilometer();
            return price * transportDistance;
        }
        return 0;
    }
}
