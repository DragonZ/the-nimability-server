package edu.purdue.cs.voip.server;

import com.google.gson.annotations.SerializedName;

public class ClientRequest {
	@SerializedName("request_type")
	public String requestType;

	@SerializedName("request_target")
	public String requestTarget;

	@SerializedName("request_sendMessage")
	public String requestMessage;

	@SerializedName("request_RealIp")
	public String request_RealIp;
	
	public String getRequestType() {
		return requestType;
	}

	public String getRequestTarget() {
		return requestTarget;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public void setRequestTarget(String requestTarget) {
		this.requestTarget = requestTarget;
	}

	public void setRequestMessage(String message) {
		requestMessage = message;
	}

	public String getRequestMessage() {
		return requestMessage;
	}
	
	public String getRequest_RealIp(){
		return request_RealIp;
	}
	
	public void setRequest_RealIp(String ip){
		request_RealIp = ip;
	}
}
