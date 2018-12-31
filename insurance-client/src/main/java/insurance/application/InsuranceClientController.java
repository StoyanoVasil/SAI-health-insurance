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

public class InsuranceClientController implements Initializable {
    /**
     * Store the queue names for both broker client queue
     * and for this insurance client queue
     */
    private static final String JMS_INSURANCE_CLIENT_QUEUE_NAME = "insurance-client-queue";
    private static final String JMS_BROKER_CLIENT_QUEUE_NAME = "broker-client-queue";

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
                    JMS_INSURANCE_CLIENT_QUEUE_NAME) {
                public void onTreatmentCostsReplyArrived(
                        TreatmentCostsRequest treatmentCostsRequest,
                        TreatmentCostsReply treatmentCostsReply
                ) {
                    //todo: implement callback
                    //lvRequestsReplies.refresh();
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
     * Method that adds ClientListLine to the lvRequestsReplied
     *
     * @param clientListLine to be added to lvRequestsReplied
     */
    private void addClientListLineToListViewLine(ClientListLine clientListLine) {
        Platform.runLater(() -> {
            this.lvRequestsReplies.getItems().add(clientListLine);
        });
    }
}
