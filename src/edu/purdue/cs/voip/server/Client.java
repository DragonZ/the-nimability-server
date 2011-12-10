/*
 * 
 */

package edu.purdue.cs.voip.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Client extends Thread {
	// This constants should keep the same as the constants in VoipConstant
	public final static String REQUEST_LIST_ALL = "REQUEST_LIST_ALL";
	public final static String RESPONSE_LIST_ALL = "RESPONSE_LIST_ALL";
	public final static String OP_REQUEST_DECLINE = "REQUEST_DECLINE";
	public final static String OP_REQUEST_ACCEPT = "REQUEST_ACCEPT";
	public final static String OP_RESPONSE_ACCEPT_FAILURE = "RESPONSE_ACCEPT_FAILURE";
	public final static String OP_REQUEST_DROP = "REQUEST_DROP";
	public final static String OP_REQUEST_DROP_SUCCESSFUL = "REQUEST_DROP_SUCCESSFUL ";
	public final static String OP_RESPONSE_DROP = "RESPONSE_DROP";
	public final static String OP_RESPONSE_DROP_SUCCESSFUL = "RESPONSE_DROP_SUCCESSFUL";
	public final static String OP_REQUEST_EXIT = "REQUEST_EXIT";
	public final static String OP_RESPONSE_EXIT = "RESPONSE_EXIT";
	public final static String OP_REQUEST_CALL = "REQUEST_CALL";
	public final static String OP_RESPONSE_CALL = "RESPONSE_CALL";// the
																	// response
																	// of a call
																	// request
	public final static String OP_REACH_CALLEE = "REACH_CALLEE";// notify callee
																// there is a
																// caller..
	public final static String OP_REQUEST_CONNECTED = "REQUEST_CONNECTED";// if
																			// current
																			// caller
																			// and
																			// callee
																			// is
																			// connected
	public final static String OP_REQUEST_SENDMESSAGE = "REQUEST_SENDMESSAGE";
	public final static String OP_REQUEST_SETREALLOCALIP = "REQUEST_SETREALLOCALIP";
	public final static String OP_REACH_SENDMESSAGE = "REACH_SENDMESSAGE";
	// the possible value in tag OP_RESPONSE_CALL and the current client's
	// status
	public final static int CALLEE_STATUS_BUSY = 0;
	public final static int CALLEE_STATUS_READY = 1;// call is ready/ accepted
	private final static int CALLEE_STATUS_FREE = 2;
	private final static int CALLER_STATUS_CALLING = 4;
	// the possible value used only in tag OP_RESPONSE_CALL(to caller)
	public final static int CALLEE_STATUS_NOT_EXIST = 3; // callee not exist
	public final static int CALLEE_STATUS_DECLINE = 5; // call is declined

	private VOIPServer server;
	private Socket socket;
	private String clientName;
	private int status;

	private InputStream in;
	private OutputStream out;
	private BufferedReader incoming;
	private PrintStream outgoing;

	private String realLocalIP;

	public Client(VOIPServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
			incoming = new BufferedReader(new InputStreamReader(in));
			outgoing = new PrintStream(out);
			this.status = CALLEE_STATUS_FREE;
		} catch (IOException e) {
			System.out.format("Failed to get I/O stream from the socket\n");
		}

	}

	@Override
	public void run() {
		while (true) {
			String jsonString;
			if (socket.isClosed()) {
				break;
			}
			try {
				while ((jsonString = incoming.readLine()) != null) {
					System.out.format("Request JSON: %s\n", jsonString);
					Gson gson = new Gson();
					ClientRequest request = gson.fromJson(jsonString,
							ClientRequest.class);
					if (request.requestType.equals(REQUEST_LIST_ALL)) {
						processRequestList(request);
					} else if (request.requestType.equals(OP_REQUEST_CALL)) {
						processCallRequest(request);
					} else if (request.requestType.equals(OP_REQUEST_DECLINE)) {
						processDecline(request);
					} else if (request.requestType.equals(OP_REQUEST_ACCEPT)) {
						processAccept(request);
					} else if (request.requestType.equals(OP_REQUEST_DROP)) {
						processDrop(request);
					} else if (request.requestType
							.equals(OP_REQUEST_DROP_SUCCESSFUL)) {
						processDropSuccessful(request);
					} else if (request.requestType.equals(OP_REQUEST_CONNECTED)) {
						processConnected(request);
					} else if (request.requestType
							.equals(OP_REQUEST_SENDMESSAGE)) {
						processSendMessage(request);
					} else if (request.requestType.equals(OP_REQUEST_EXIT)) {
						//this.socket.close();
						server.logout(this);
					} else if (request.requestType
							.equals(OP_REQUEST_SETREALLOCALIP)) {
						processSetRealLocalIp(request);
					}

				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void processSetRealLocalIp(ClientRequest request) {
		this.realLocalIP = request.getRequestRealIp();

	}

	private void processRequestList(ClientRequest request) {
		ServerResponse response = new ServerResponse();
		response.responseType = RESPONSE_LIST_ALL;
		response.listOfClients = server.getClientList(this);
		sendResponseToClient(response);
	}

	private void processSendMessage(ClientRequest request) {
		String calleeIp = request.getRequestTarget();
		String messageContent = request.getRequestMessage();
		Client callee = server.getClientByIp(calleeIp);
		if (callee == null) {
			return;
		}
		ServerResponse response = new ServerResponse();
		response.setResponseType(OP_REACH_SENDMESSAGE);
		response.setRequestTarget(getRealLocalIP());
		response.setReachMessage(messageContent);
		callee.sendResponseToClient(response);
	}

	private void processConnected(ClientRequest request) {
		this.status = CALLEE_STATUS_BUSY;

		String calleeIp = request.getRequestTarget();
		Client callee = server.getClientByIp(calleeIp);
		if (callee == null) {
			this.status = CALLEE_STATUS_FREE;
			return;

		}
		callee.setStatus(CALLEE_STATUS_BUSY);
	}

	private void processDropSuccessful(ClientRequest request) {
		this.status = CALLEE_STATUS_FREE;
	}

	private void processDrop(ClientRequest request) {
		ServerResponse response = new ServerResponse();
		String calleeIp = request.getRequestTarget();
		Client callee = server.getClientByIp(calleeIp);
		this.status = CALLEE_STATUS_FREE;
		if (callee == null) {
			// send back failure
			return;
		}
		callee.setStatus(CALLEE_STATUS_FREE);
		response.setResponseType(OP_RESPONSE_DROP);
		response.setRequestTarget(getRealLocalIP());
		callee.sendResponseToClient(response);

	}

	private void processAccept(ClientRequest request) {
		// callee set to ready, send accept to caller
		ServerResponse response = new ServerResponse();
		response.setResponseType(OP_RESPONSE_CALL);
		String callerIp = request.getRequestTarget();
		Client caller = server.getClientByIp(callerIp);
		if (caller == null) {
			// tell callee the caller not exist error.
			response.setResponseType(OP_RESPONSE_ACCEPT_FAILURE);
			response.setRequestTarget(callerIp);
			sendResponseToClient(response);
			return;
		}
		this.status = CALLEE_STATUS_BUSY;
		response.setRequestTarget(getRealLocalIP());
		response.setCalleeStatus(CALLEE_STATUS_READY);
		caller.sendResponseToClient(response);
		System.out.format("caller ip: %s\n", caller.getRealLocalIP());
		response.setRequestTarget(caller.getRealLocalIP());
		response.setCalleeStatus(CALLEE_STATUS_READY);
		sendResponseToClient(response);
		caller.setStatus(CALLEE_STATUS_BUSY);
		System.out.format("callee ip: %s\n", this.getRealLocalIP());
	}

	private void processDecline(ClientRequest request) {
		ServerResponse response = new ServerResponse();
		response.setResponseType(OP_RESPONSE_CALL);
		// set caller to free. tell caller decline
		String callerIp = request.getRequestTarget();
		Client caller = server.getClientByIp(callerIp);
		if (caller == null) {
			return;
		}
		caller.setStatus(CALLEE_STATUS_FREE);
		response.setCalleeStatus(CALLEE_STATUS_DECLINE);
		response.setRequestTarget(getRealLocalIP());
		caller.sendResponseToClient(response);
	}

	private void processCallRequest(ClientRequest request) {
		ServerResponse response = new ServerResponse();
		response.setResponseType(OP_RESPONSE_CALL);
		this.status = CALLER_STATUS_CALLING;

		String targetIp = request.getRequestTarget();
		Client targetClient = server.getClientByIp(targetIp);
		if (targetClient == null) {
			response.setCalleeStatus(CALLEE_STATUS_NOT_EXIST);
			sendResponseToClient(response);
			return;
		} else if (targetClient.getStatus() != CALLEE_STATUS_FREE) {
			response.setCalleeStatus(CALLEE_STATUS_BUSY);
			sendResponseToClient(response);
			return;
		} else {
			response.setResponseType(OP_REACH_CALLEE);
			response.setRequestTarget(getRealLocalIP());
			targetClient.sendResponseToClient(response);
			return;
		}
	}

	private void sendResponseToClient(ServerResponse response) {
		Gson gson = new Gson();
		System.out.format("Response JSON: %s\n", gson.toJson(response));
		outgoing.println(gson.toJson(response));
		outgoing.flush();

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

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRealLocalIP() {
		return realLocalIP;
	}
}