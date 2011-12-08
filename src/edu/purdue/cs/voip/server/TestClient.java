package edu.purdue.cs.voip.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class TestClient {
  Socket socket;
  private InputStream in;
  private OutputStream out;
  private BufferedReader incoming;
  private PrintStream outgoing;

  public TestClient(String host, int port) {
    try {
      socket = new Socket(host, port);
      in = socket.getInputStream();
      out = socket.getOutputStream();
      incoming =new BufferedReader(new InputStreamReader(in));
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
    try {
      while (true) {
        String jsonString;
        while ((jsonString = incoming.readLine()) != null) {
          System.out.format("Received server response json:%s\n", jsonString);
          ServerResponse response = gson.fromJson(jsonString.toString(), ServerResponse.class);
          for (String s : response.getListOfClients()) {
            System.out.println(s);
          }
        }
      }
    } catch (JsonSyntaxException e) {
      System.out.println("Failed to parse JSON response from server.");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("Failed to read data from socket.");
      e.printStackTrace();
    }

  }

  public static void main(String[] args) {
    TestClient testClient = new TestClient(args[0], Integer.parseInt(args[1]));
    testClient.start();
  }
}
