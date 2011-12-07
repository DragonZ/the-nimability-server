package edu.purdue.cs.voip.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.google.gson.Gson;

public class TestClient {
  Socket socket;
  BufferedReader in;
  private DataOutputStream out;
  private Scanner incoming;
  private PrintStream outgoing;

  public TestClient(String host, int port) {
    try {
      socket = new Socket(host, port);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new DataOutputStream(socket.getOutputStream());
      incoming = new Scanner(in);
      outgoing = new PrintStream(out);
    } catch (UnknownHostException e) {
      System.out.format("Unknown Host: %s", host);
      e.printStackTrace();
    } catch (IOException e) {
      System.out.format("Failed to create socket.");
      e.printStackTrace();
    }
  }

  public void start() {
    ClientRequest request = new ClientRequest();
    request.setRequestType(Client.REQUEST_LIST_ALL);
    Gson gson = new Gson();

    outgoing.println(gson.toJson(request));
    outgoing.flush();
    
    while (true) {  
      if (incoming.hasNextLine()) {
        String jsonString = incoming.nextLine();
        System.out.format("Received server response json:%s\n", jsonString);
        ServerResponse response = gson.fromJson(jsonString.toString(), ServerResponse.class);
        for (String s : response.getListOfClients()) {
          System.out.println(s);
        }
      }

    }

  }

  public static void main(String[] args) {
    TestClient testClient = new TestClient(args[0], Integer.parseInt(args[1]));
    testClient.start();
  }
}
