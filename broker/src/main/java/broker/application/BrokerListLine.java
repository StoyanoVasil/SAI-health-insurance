package broker.application;


import broker.model.client.TreatmentCostsReply;
import broker.model.client.TreatmentCostsRequest;

public class BrokerListLine {

	private TreatmentCostsRequest request;
	private TreatmentCostsReply reply;

	public BrokerListLine(TreatmentCostsRequest request, TreatmentCostsReply reply) {
            this.reply = reply;
            this.request = request;
	}	
	
	public TreatmentCostsRequest getRequest() {
		return request;
	}
	
	private void setRequest(TreatmentCostsRequest request) {
		this.request = request;
	}
	
	public TreatmentCostsReply getReply() {
		return reply;
	}
	
	public void setReply(TreatmentCostsReply reply) {
		this.reply = reply;
	}
	
	@Override
	public String toString() {
	   return request.toString() + "  --->  " + ((reply!=null)?reply.toString():"waiting...");
	}
	
}
