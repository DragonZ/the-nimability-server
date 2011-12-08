package edu.purdue.cs.voip.server;

import com.google.gson.annotations.SerializedName;

public class ClientRequest {
	@SerializedName("request_type")
	public String requestType;

	@SerializedName("request_target")
	public String requestTarget;

	@SerializedName("request_sendEmail")
	public String requestEmail;

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

	public void setRequestEmail(String email) {
		requestEmail = email;
	}

	public String getRequestEmail() {
		return requestEmail;
	}
}
