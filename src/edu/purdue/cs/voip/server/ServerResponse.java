package edu.purdue.cs.voip.server;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ServerResponse {
  @SerializedName("response_type")
  public String responseType;
  
  @SerializedName("list_of_clients")
  public List<String> listOfClients;

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
  
  
}
