package edu.purdue.cs.voip.server;

import com.google.gson.annotations.SerializedName;

public class ClientRequest {
  @SerializedName("request_type")
  public String requestType;
  
  @SerializedName("request_target")
  public String requestTarget;

  
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
}
