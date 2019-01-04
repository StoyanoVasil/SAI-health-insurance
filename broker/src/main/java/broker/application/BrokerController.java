package broker.application;

import broker.gateway.BrokerInsuranceClientGateway;
import broker.gateway.HospitalClientScatterGather;
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
     * Declare BrokerInsuranceClientGateway and Scatter-Gather
     */
    private BrokerInsuranceClientGateway brokerInsuranceClientGateway;
    private HospitalClientScatterGather hospitalClientScatterGather;

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
                    //todo: maybe change BrokerListLine to use TreatmentCostsRequest
                    BrokerListLine brokerListLine = new BrokerListLine(hospitalCostsRequest, null);
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
                        HospitalCostsRequest hospitalCostsRequest,
                        HospitalCostsReply hospitalCostsReply) {
                    TreatmentCostsRequest treatmentCostsRequest =
                            hospitalCostsReqToTreatmentCostsReq.get(hospitalCostsRequest);
                    //todo: transport service call here
                    TreatmentCostsReply treatmentCostsReply = new TreatmentCostsReply(
                            hospitalCostsReply.getPrice(),
                            0,
                            hospitalCostsReply.getHospitalName()
                    );
                    try {
                        brokerInsuranceClientGateway.replyOnTreatmentCostsRequest(
                                treatmentCostsRequest,treatmentCostsReply);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                    BrokerListLine brokerListLine = findBrokerListLineByHospitalCostsRequest(hospitalCostsRequest);
                    brokerListLine.setReply(hospitalCostsReply);
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
     * @param hospitalCostsRequest the search parameter
     * @return BrokerListLine if found, otherwise null
     */
    private BrokerListLine findBrokerListLineByHospitalCostsRequest(HospitalCostsRequest hospitalCostsRequest) {
        for(BrokerListLine bll : this.lvRequestReply.getItems()) {
            if(hospitalCostsRequest.equals(bll.getRequest())) return bll;
        }
        return null;
    }
}
