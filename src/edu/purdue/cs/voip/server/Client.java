package edu.purdue.cs.voip.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;

public class Client extends Thread {
  private VOIPServer server;
  private Socket socket;
  private String clientName;
  private int status;

  private BufferedReader in;
  private DataOutputStream out;
  private Scanner incoming;
  private PrintStream outgoing;

  public final static String REQUEST_LIST_ALL = "REQUEST_LIST_ALL";
  public final static String RESPONSE_LIST_ALL = "RESPONSE_LIST_ALL";

  public Client(VOIPServer server, Socket socket) {
    this.server = server;
    this.socket = socket;
    try {
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new DataOutputStream(socket.getOutputStream());
      incoming = new Scanner(in);
      outgoing = new PrintStream(out);
    } catch (IOException e) {
      System.out.format("Failed to get I/O stream from the socket\n");
    }

  }

  @Override
  public void run() {
    while (true) {
      if (incoming.hasNextLine()) {
        String jsonString = incoming.nextLine();
        System.out.format("Received client request json:%s\n", jsonString);

        Gson gson = new Gson();

        ClientRequest request = gson.fromJson(jsonString, ClientRequest.class);

        if (request.requestType.equals(REQUEST_LIST_ALL)) {
          ServerResponse response = new ServerResponse();
          response.responseType = RESPONSE_LIST_ALL;
          List<String> mockClients = new ArrayList<String>();
          mockClients.add("aaaa");
          response.listOfClients = mockClients;

          System.out.format("Send server response to client: %s\n", gson.toJson(response));
          outgoing.println(gson.toJson(response));
          outgoing.flush();
        }
      }
    }

  }

  public VOIPServer getServer() {
    return server;
  }

  public Socket getSocket() {
    return socket;
  }

  public String getClientName() {
    return clientName;
  }

  public int getStatus() {
    return status;
  }
}
