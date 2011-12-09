package edu.purdue.cs.voip.server;

//This constants should keep the same as the constants in Client
public class VOIPConstant {
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
	public final static String OP_REACH_SENDMESSAGE = "REACH_SENDMESSAGE";
	// the possible value in tag OP_RESPONSE_CALL and the current client's
	// status
	public final static int CALLEE_STATUS_BUSY = 0;
	public final static int CALLEE_STATUS_READY = 1;// call is ready/ accepted
	// the possible value used only in tag OP_RESPONSE_CALL(to caller)
	public final static int CALLEE_STATUS_NOT_EXIST = 3; // callee not exist
	public final static int CALLEE_STATUS_DECLINE = 5; // call is declined
}
