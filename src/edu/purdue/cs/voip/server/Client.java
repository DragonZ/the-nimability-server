package edu.purdue.cs.voip.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Client extends Thread {
  private VOIPServer server;
  private Socket socket;
  private String clientName;
  private int status;
  long lastQueryTime;

  private DataInputStream incoming;
  private DataOutputStream outgoing;

  private final static String OP_LIST_ALL = "LIST_ALL";
  private final static String OP_CALL = "CALL";
  private final static String OP_DECLINE = "DECLINE";
  private final static String OP_ACCEPT = "ACCEPT";
  private final static String OP_DROP = "DROP";
  private final static String OP_EXIT = "EXIT";

  private final static String FLAG_START_LIST_ALL = "START_LIST_ALL";
  private final static String FLAG_END_LIST_ALL = "END_LIST_ALL";

  public Client(VOIPServer server, Socket socket) {
	  lastQueryTime  = System.currentTimeMillis();
    this.server = server;
    this.socket = socket;
    try {
      incoming = new DataInputStream(socket.getInputStream());
      outgoing = new DataOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      System.out.format("Failed to get I/O stream from the socket\n");
    }

  }

  @Override
  public void run() {
    Scanner scanner = new Scanner(incoming);
    while (true) {
      try {
        String op = scanner.next("\n");
        if (op.equals(OP_LIST_ALL)) {
          List<Client> listClient = server.getClientList(this);
          outgoing.writeChars(FLAG_START_LIST_ALL);

          for (Client c : listClient) {
            outgoing.writeChars(c.socket.getLocalAddress().toString() + "\n");
          }

          outgoing.writeChars(FLAG_END_LIST_ALL);
        } else if (op.equals(OP_CALL)) {
          
        } else if(op.equals(OP_DECLINE)){
        	
        }else if(op.equals(OP_ACCEPT)){
        	
        }else if(op.equals(OP_DROP)){
        	
        }else if(op.equals(OP_EXIT)){
        	server.logout(this);
        }
      } catch (IOException ioe) {
        System.out.format("Failed to send data to client %s", clientName);
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
  
  public int getInterval()
  {
	  return (int) ((System.currentTimeMillis()- lastQueryTime)/1000);
  }
  
  public void updateLastQueryTime(){
	  lastQueryTime=System.currentTimeMillis();
  }

}
