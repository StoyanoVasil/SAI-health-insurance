package broker.gateway;

import broker.model.hospital.HospitalCostsRequest;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import javax.jms.JMSException;

/**
 * A class responsible for evaluating to which hospitals
 * a HospitalCostsRequest should be sent.
 */
public class HospitalRecipientList {

    /**
     * Store the queue names for all hospitals
     */
    private static final String HOSPITAL_CATHARINA_QUEUE_NAME = "catharinaRequestQueue";
    private static final String HOSPITAL_MAXIMA_QUEUE_NAME = "maximaRequestQueue";
    private static final String HOSPITAL_UMC_QUEUE_NAME = "umcRequestQueue";

    /**
     * Define the rules for all hospital that are used by Jeval
     */
    private static final String HOSPITAL_CATARINA_RULE =
            "startsWith('#{treatmentCode}', 'ORT', 0) && 10 <= #{patientAge}";
    private static final String HOSPITAL_MAXIMA_RULE = "18 <= #{patientAge}";

    /**
     * Declare the BrokerHospitalClientGateway
     */
    private BrokerHospitalClientGateway brokerHospitalClientGateway;

    /**
     * Constructor that sets the BrokerHospitalClientGateway
     *
     * @param brokerHospitalClientGateway to be set in property
     */
    public HospitalRecipientList(BrokerHospitalClientGateway brokerHospitalClientGateway) {
        this.brokerHospitalClientGateway = brokerHospitalClientGateway;
    }

    /**
     * Method that sends a HospitalCostsRequest to hospitals
     * based on the defined rules
     *
     * @param hospitalCostsRequest to be sent
     * @param aggregationId to identify all request that belong to the same aggregation
     * @return Integer the number of banks to which the HospitalCostsRequest was send
     * @throws JMSException if something goes wrong with JMS
     */
    public int sendHospitalCostsRequest(HospitalCostsRequest hospitalCostsRequest, Integer aggregationId)
            throws JMSException {
        // create evaluator
        Evaluator evaluator = createEvaluator(hospitalCostsRequest);
        /* counter that keeps track to how many hospitals the request was sent
         * starts with 1 because one of the hospitals always receives the request */
        int counter = 1;
        try {
            // evaluate Catharina rule, send request and increment counter
            if (("1.0").equals(evaluator.evaluate(HOSPITAL_CATARINA_RULE))) {
                this.brokerHospitalClientGateway.requestApproximation(
                        hospitalCostsRequest,
                        aggregationId,
                        HOSPITAL_CATHARINA_QUEUE_NAME
                );
                counter++;
            }
            // evaluate Maxima rule, send request and increment counter
            if (("1.0").equals(evaluator.evaluate(HOSPITAL_MAXIMA_RULE))) {
                this.brokerHospitalClientGateway.requestApproximation(
                        hospitalCostsRequest,
                        aggregationId,
                        HOSPITAL_MAXIMA_QUEUE_NAME
                );
                counter++;
            }
        } catch (EvaluationException e) {
            e.printStackTrace();
        }
        // always send request to UMC
        this.brokerHospitalClientGateway.requestApproximation(
                hospitalCostsRequest,
                aggregationId,
                HOSPITAL_UMC_QUEUE_NAME
        );
        // return counter
        return counter;
    }

    /**
     * Creates an instance of jeval.Evaluator and puts
     * necessary fields for evaluating hospital rules
     *
     * @param hospitalCostsRequest get necessary field values from here
     * @return Evaluator instance
     */
    private Evaluator createEvaluator(HospitalCostsRequest hospitalCostsRequest) {
        Evaluator evaluator = new Evaluator();
        evaluator.putVariable("treatmentCode", hospitalCostsRequest.getTreatmentCode());
        evaluator.putVariable("patientAge", Integer.toString(hospitalCostsRequest.getAge()));
        return evaluator;
    }
}
