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
  final static int MAX_INTERVAL = 10000;
  
  public void start(int port) {
    try {
      serverSocket = new ServerSocket(port);
      System.out.format("Listening on Ip: %s\n Listening on port: %d\n",serverSocket.getInetAddress().toString(), serverSocket.getLocalPort());
    } catch (IOException ioeport) {
      System.out.format("Port %d already in use\n", port);
    }
    if (serverSocket == null) {
      try {
        serverSocket = new ServerSocket(0);
        System.out.format("Listening on Ip: %s\n Listening on port: %d.\n", serverSocket.getInetAddress().toString(), serverSocket.getLocalPort());
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
      //check status every loop
      checkStatus();
    }
  }

  public void checkStatus(){
	  for(Client client: listClient )
		{
			if (System.currentTimeMillis()-client.getLastQueryTime() < MAX_INTERVAL)
			{
				listClient.remove(client);
			}
			
		}
  }
  public synchronized void logout(Client c) {
    listClient.remove(c);
  }

  public List<Client> getClientList(Client c) {
    List<Client> listClient = this.listClient;
    listClient.remove(c);
    c.updateLastQueryTime();
    return listClient;
  }
	public  Client getClientByIp(String ip){
		for (Client client : listClient){
			if(client.getSocket().getInetAddress().toString().equals(ip))
				return client;
		}
		return null;
	}
  public void call(Client caller, Client callee) {

  }

  public static void main(String args[]) {
    VOIPServer voipServer = new VOIPServer();
    voipServer.start(defaultPort);
  }
}