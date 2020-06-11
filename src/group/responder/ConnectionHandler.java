package group.responder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import group.RequestType;
import gui.Logger;

public class ConnectionHandler implements Runnable {
	
	private final int responseID;
	private final String parentID;
	private Socket socket;
	private RequestType requestType;
	
	public ConnectionHandler(int responseID, String parentID, Socket socket, RequestType requestType) {
		this.responseID = responseID;
		this.parentID = parentID;
		this.socket = socket;
		this.requestType = requestType;
	}
	
	@Override
	public void run() {
		BufferedReader in = null;
		boolean success = false;
		String request = "";
		String body = null;
		try {
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			String inputLine;
			int i = 0;
			boolean hasBody = false;
			boolean watchForContentLength = false;
			int contentLength = -1;
			while ((inputLine = in.readLine()) != null) {
				
			}
		} catch (Exception e) {
			Logger.reportException(this.getClass().getName(), "run", e);
		}
	}

}
