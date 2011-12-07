package edu.purdue.cs.voip.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class VOIPServer {
  ServerSocket serverSocket;
  List<Client> listClient = new ArrayList<Client>();
  final static int defaultPort = 8888;

  public void start(int port) {
    try {
      serverSocket = new ServerSocket(port);
      System.out.format("Listening on port: %d\n", serverSocket.getLocalPort());
    } catch (IOException ioeport) {
      System.out.format("Port %d already in use\n", port);
    }
    if (serverSocket == null) {
      try {
        serverSocket = new ServerSocket(0);
        System.out.format("Listening on port: %d.\n", serverSocket.getLocalPort());
      } catch (IOException ioe) {
        System.out.format("No available ports\n Exiting...\n");
        System.exit(-1);
      }
    }

    while (true) {
      try {
        Socket slaveSocket = serverSocket.accept();
        Client newClient = new Client(this, slaveSocket);
        newClient.start();
        listClient.add(newClient);
      } catch (IOException e) {
        System.out.format("Failed to accept client connection.\n");
      }
    }
  }

  public synchronized void logout(Client c) {
    listClient.remove(c);
  }

  public List<Client> getClientList(Client c) {
    List<Client> listClient = this.listClient;
    listClient.remove(c);
    return listClient;
  }

  public void call(Client caller, Client callee) {

  }

  public static void main(String args[]) {
    VOIPServer voipServer = new VOIPServer();
    voipServer.start(defaultPort);
  }
}