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

public class TestClient2 {
	Socket socket;
	BufferedReader in;
	private DataOutputStream out;
	private Scanner incoming;
	private PrintStream outgoing;

	public TestClient2(String host, int port) {
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
		ClientRequest request = new ClientRequest();
		request.setRequestType(VOIPConstant.OP_REQUEST_SENDMESSAGE);
		request.setRequestTarget("/128.10.25.222");
		request.setRequestMessage("HEllo, world\n haha");
		Gson gson = new Gson();

		outgoing.println(gson.toJson(request));
		outgoing.flush();

		while (true) {
			if (incoming.hasNextLine()) {
				String jsonString = incoming.nextLine();
				System.out.format("Received server response json:%s\n",
						jsonString);
				ServerResponse response = gson.fromJson(jsonString.toString(),
						ServerResponse.class);
				if (response.getResponseType().equals(VOIPConstant.RESPONSE_LIST_ALL)) {
					System.out.println("Entered LIST_ALL");
					for (String s : response.getListOfClients()) {
						System.out.println(s);
					}
				}else if(response.getResponseType().equals(VOIPConstant.OP_REACH_CALLEE)){
					System.out.println("Entered REACH_CALLEE");
					String callerIp = response.getRequestTarget();
					ClientRequest requestTmp = new ClientRequest();
					requestTmp.setRequestType(VOIPConstant.OP_REQUEST_DECLINE);
					requestTmp.setRequestTarget(callerIp);
					outgoing.println(gson.toJson(requestTmp));
					outgoing.flush();
				} else if(response.getResponseType().equals( VOIPConstant.OP_RESPONSE_CALL)){
					System.out.println("Entered OP_RESPONSE_CALL");
			/*		if(response.getCalleeStatus()==(VOIPConstant.CALLEE_STATUS_BUSY)) System.out.println("CALLEE IS BUSY");
					else if(response.getCalleeStatus()==(VOIPConstant.CALLEE_STATUS_DECLINE)) System.out.println("CALLEE DECLINE");*/
					 if( ((Integer)response.getCalleeStatus()).equals(VOIPConstant.CALLEE_STATUS_READY)) {
						System.out.println("CALLEE READY, connecting"); 
						String callerIp = response.getRequestTarget();
						ClientRequest requestTmp = new ClientRequest();
						requestTmp.setRequestType(VOIPConstant.OP_REQUEST_CONNECTED);
						requestTmp.setRequestTarget(callerIp);
						outgoing.println(gson.toJson(requestTmp));
						outgoing.flush();
					}
					 else{System.out.println("FUCK");}
					/*else if(response.getCalleeStatus()==(VOIPConstant.CALLEE_STATUS_NOT_EXIST)) System.out.println("CALLEE NOT EXIST");*/
				}
			}

		}

	}

	public static void main(String[] args) {
		TestClient2 testClient = new TestClient2(args[0],
				Integer.parseInt(args[1]));
		testClient.start();
	}
}
