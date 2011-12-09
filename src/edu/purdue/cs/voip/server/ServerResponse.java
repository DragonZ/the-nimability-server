package edu.purdue.cs.voip.server;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ServerResponse {
  @SerializedName("response_type")
  public String responseType;
  
  @SerializedName("list_of_clients")
  public List<String> listOfClients;
  
  @SerializedName("callee_status")
  public int  callee_status;
   
  @SerializedName("request_target")
  public String requestTarget;

  @SerializedName("reach_sendMessage")
  public String reachMessage;
  
  public String getResponseType() {
    return responseType;
  }

  public List<String> getListOfClients() {
    return listOfClients;
  }

  public void setResponseType(String responseType) {
    this.responseType = responseType;
  }

  public void setListOfClients(List<String> listOfClients) {
    this.listOfClients = listOfClients;
  }
 
  public void setCalleeStatus(int calleeStatus){
	  this.callee_status = calleeStatus;
  }
  public int getCalleeStatus(){
	  return this.callee_status;
  }
  public void setRequestTarget(String requestTarget) {
	    this.requestTarget = requestTarget;
	  }
  public String getRequestTarget(){
	  return this.requestTarget;
  }
  public void setReachMessage(String message) {
	  reachMessage = message;
	  }

	  public String getReachMessage() {
	    return reachMessage;
	  }
}
