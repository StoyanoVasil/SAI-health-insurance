package insurance.application;

import insurance.gateway.InsuranceClientGateway;
import insurance.model.TreatmentCostsReply;
import insurance.model.TreatmentCostsRequest;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javax.jms.JMSException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class InsuranceClientController implements Initializable {
    /**
     * Store the queue names for both broker client queue
     * and for this insurance client queue
     */
    private static final String JMS_INSURANCE_CLIENT_QUEUE_NAME = "insurance-client-queue";
    private static final String JMS_BROKER_CLIENT_QUEUE_NAME = "broker-insurance-client-queue";

    @FXML
    private ListView<ClientListLine> lvRequestsReplies;
    @FXML
    private TextField tfSsn;
    @FXML
    private TextField tfAge;
    @FXML
    private TextField tfTreatmentCode;
    @FXML
    private CheckBox cbTransport;
    @FXML
    private TextField tfKilometers;

    /**
     * Declare the InsuranceClientGateway
     */
    private InsuranceClientGateway insuranceClientGateway;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfSsn.setText("123456");
        tfAge.setText("56");
        tfTreatmentCode.setText("ORT125");
        cbTransport.setSelected(false);
        tfKilometers.setDisable(true);

        try {
            this.insuranceClientGateway = new InsuranceClientGateway(
                    JMS_BROKER_CLIENT_QUEUE_NAME,
                    UUID.randomUUID().toString()) {
                public void onTreatmentCostsReplyArrived(
                        TreatmentCostsRequest treatmentCostsRequest,
                        TreatmentCostsReply treatmentCostsReply
                ) {
                    ClientListLine clientListLine = findClientListLineByTreatmentCostsRequest(treatmentCostsRequest);
                    clientListLine.setReply(treatmentCostsReply);
                    lvRequestsReplies.refresh();
                }
            };
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private ClientListLine getListLine(TreatmentCostsRequest request) {
        for (ClientListLine clientListLine : lvRequestsReplies.getItems()){
            if (clientListLine.getRequest() == request) {
                return clientListLine;
            }
        }
        return null;
    }

    public void transportChanged(){
        System.out.println(cbTransport.isSelected());
        if (!cbTransport.isSelected()){
            tfKilometers.setText("");
        }
        this.tfKilometers.setDisable(!this.cbTransport.isSelected());
    }

    public void btnSendClicked(){
        int ssn = Integer.parseInt(this.tfSsn.getText());
        String treatmentCode = this.tfTreatmentCode.getText();
        int age = Integer.parseInt(this.tfAge.getText());

        int transportDistance = 0;
        if (this.cbTransport.isSelected()) {
            transportDistance = Integer.parseInt(this.tfKilometers.getText());
        }

        TreatmentCostsRequest request = new TreatmentCostsRequest(ssn, age, treatmentCode, transportDistance);

        // request a cost approximation
        try {
            this.insuranceClientGateway.requestTreatmentCostApproximation(request);
        } catch (JMSException e) {
            e.printStackTrace();
        }

        // create ClientListLine and add to ListView
        ClientListLine clientListLine = new ClientListLine(request, null);
        this.addClientListLineToListViewLine(clientListLine);
    }

    /**
     * Method that adds ClientListLine to the lvRequestsReplies
     *
     * @param clientListLine to be added to lvRequestsReplies
     */
    private void addClientListLineToListViewLine(ClientListLine clientListLine) {
        Platform.runLater(() -> {
            this.lvRequestsReplies.getItems().add(clientListLine);
        });
    }

    private ClientListLine findClientListLineByTreatmentCostsRequest(TreatmentCostsRequest treatmentCostsRequest) {
        for(ClientListLine cll : this.lvRequestsReplies.getItems()) {
            if (treatmentCostsRequest.equals(cll.getRequest())) return cll;
        }
        return null;
    }
}
