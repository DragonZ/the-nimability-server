
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class VOIPServer {

	final static int MAX_INTERVAL = 10;
	
	HashMap<String, Client> clientList = new HashMap<String, Client>();
	int port = 7777;
	ServerSocket server;

	public VOIPServer() {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Failure on starting server on port 7777;" + e);
			// e.printStackTrace();

			new CheckStatus().start();
			new AcceptRequest().start();

		}
		System.err.println("Starting server on port 7777");

	}

	class CheckStatus extends Thread {

		public void run() 
		{
			while(true)
			{
				for(String key: clientList.keySet() )
				{
					if (clientList.get(key).getInterval() < MAX_INTERVAL)
					{
						clientList.remove(key);
					}
					
				}
			}
		}
	}

	class AcceptRequest extends Thread {
		final static int CASE_GETLIST = 0;
		final static int CASE_REGISTER = 1;
		final static int CASE_DECLINE = 2;
		final static int CASE_ACEEPTCALL = 3;
		final static int CASE_ONGOINGCALL = 4;
		final static int CASE_DROPCALL = 5;
		final static int CASE_EXIT = 6;

		public void run() {
			
			Socket slaveSocket;
			InputStream input;
			OutputStream output;
			Scanner scanner;
			String srcName; 
			int cases;
			String objName;
			
			while (true) {
				try {
					slaveSocket = server.accept();
					input = slaveSocket.getInputStream();
					output = slaveSocket.getOutputStream();
					scanner = new Scanner(input);
				} catch (IOException e) {
					System.err.println("Error accepting connection:" + e);
					continue;
				}
				System.err.printf("Opened connection with "
						+ slaveSocket.getInetAddress() + " on port "
						+ slaveSocket.getLocalPort());
				
				srcName = scanner.nextLine();
				cases = Integer.parseInt(scanner.nextLine());
				
				switch(cases){
				case CASE_GETLIST : getList(srcName); break;
				case CASE_REGISTER : register(srcName,slaveSocket.getInetAddress()); break;
				case CASE_DECLINE : decline(objName,srcName); break;//(caller callee
				case CASE_ACEEPTCALL : acceptCall(objName,srcName);break;//caller callee
				case CASE_ONGOINGCALL : dial(srcName, objName); break;//caller callee
				case CASE_DROPCALL : dropCall(srcName, objName); break;//caller callee
				case CASE_EXIT: exit(srcName); break;
				}
			}// end while
		}

		private void register(String srcName, InetAddress inetAddress) {
			// TODO Auto-generated method stub
			
		}

		private void getList(String srcName) {
			// TODO Auto-generated method stub
			
		}

	}

	public static void main(String[] args) {

	}

}
