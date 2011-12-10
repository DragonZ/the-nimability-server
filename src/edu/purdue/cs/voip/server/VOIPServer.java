package edu.purdue.cs.voip.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class VOIPServer {
	ServerSocket serverSocket;
	List<Client> listClient = new ArrayList<Client>();
	final static int defaultPort = 8888;
	long currentTime;
	public void start(int port) {
		currentTime = System.currentTimeMillis();
		try {
			serverSocket = new ServerSocket(port);
			System.out.format("Listening on IP: %s\nListening on port: %d\n",
					InetAddress.getLocalHost().getHostAddress(),
					serverSocket.getLocalPort());
		} catch (IOException ioeport) {
			System.out.format("Port %d already in use\n", port);
		}

		if (serverSocket == null) {
			try {
				serverSocket = new ServerSocket(0);
				System.out.format(
						"Listening on Ip: %s\n Listening on port: %d.\n",
						InetAddress.getLocalHost().getHostAddress(),
						serverSocket.getLocalPort());
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

				System.out.println("Incoming connection from: "
						+ newClient.getSocket().getInetAddress()
								.getHostAddress());
				
				checkClient();
			} catch (IOException e) {
				System.out.format("Failed to accept client connection.\n");
			}
			
		}
	}

	public void checkClient() {
		if(System.currentTimeMillis()-  currentTime >= 5000){
			System.out.println("Current ClientList:" + getClientIpList().toString());
			currentTime=System.currentTimeMillis();
		}
			
		for (Client client : listClient) {
			if (!client.getSocket().isBound()) {
				logout(client);
			}
		}
	}

	public List<String> getClientIpList(){
		List<String> listClientIP = new ArrayList<String>();
		for (Client client : listClient) {
			listClientIP.add(client.getRealLocalIP());
		}
		return listClientIP;
	}
	public synchronized void logout(Client c) {
		listClient.remove(c);
	}

	public List<String> getClientList(Client c) {
		List<String> listClientIP = new ArrayList<String>();
		for (Client client : listClient) {
			if (client.getRealLocalIP().equals(c.getRealLocalIP()))
				continue;
			listClientIP.add(client.getRealLocalIP());
		}
		return listClientIP;
	}

	public Client getClientByIp(String ip) {
		for (Client client : listClient) {
			if (client.getRealLocalIP().equals(ip))
				return client;
		}
		return null;
	}

	public static void main(String args[]) {
		VOIPServer voipServer = new VOIPServer();
		voipServer.start(defaultPort);
	}
}