package hospital.application;

import hospital.model.HospitalCostsReply;
import hospital.model.HospitalCostsRequest;

public class HospitalListLine {
	
	private HospitalCostsRequest request;
	private HospitalCostsReply reply;
	
	public HospitalListLine(HospitalCostsRequest request, HospitalCostsReply reply) {
            this.reply = reply;
            this.request = request;
	}	
	
	public HospitalCostsRequest getRequest() {
		return request;
	}
	
	private void setRequest(HospitalCostsRequest request) {
		this.request = request;
	}
	
	public HospitalCostsReply getReply() {
		return reply;
	}
	
	public void setReply(HospitalCostsReply reply) {
		this.reply = reply;
	}
	
	@Override
	public String toString() {
	   return request.toString() + "  --->  " + ((reply!=null)?reply.toString():"waiting...");
	}
	
}
