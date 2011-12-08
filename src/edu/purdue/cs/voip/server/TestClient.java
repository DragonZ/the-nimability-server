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
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
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
		//ClientRequest request = new ClientRequest();
		//request.setRequestType(Client.OP_REQUEST_CALL);
		//request.setRequestTarget(socket.getInetAddress().toString());
		System.out.println(socket.getInetAddress().toString());
		Gson gson = new Gson();

		//outgoing.println(gson.toJson(request));
		//outgoing.flush();

		while (true) {
			if (incoming.hasNextLine()) {
				String jsonString = incoming.nextLine();
				System.out.format("Received server response json:%s\n",
						jsonString);
				ServerResponse response = gson.fromJson(jsonString.toString(),
						ServerResponse.class);
				if (response.getResponseType().equals(Client.RESPONSE_LIST_ALL)) {
					for (String s : response.getListOfClients()) {
						System.out.println(s);
					}
				}else if(response.getResponseType().equals(Client.OP_REACH_CALLEE)){
					String callerIp = response.getRequestTarget();
					ClientRequest requestTmp = new ClientRequest();
					System.out.println("call accepted");
					requestTmp.setRequestType(Client.OP_REQUEST_ACCEPT);
					requestTmp.setRequestTarget(callerIp);
					outgoing.println(gson.toJson(requestTmp));
					outgoing.flush();
				} else if(response.getResponseType().equals( Client.OP_RESPONSE_CALL)){
					
					System.out.println(response.getCalleeStatus());
				}else if(response.getResponseType().equals( Client.OP_REACH_SENDEMAIL)){
					
					System.out.println(response.getReachEmail()+"\nFROM"+response.getRequestTarget());
				}
			}

		}

	}

	public static void main(String[] args) {
		TestClient testClient = new TestClient(args[0],
				Integer.parseInt(args[1]));
		testClient.start();
	}
}
