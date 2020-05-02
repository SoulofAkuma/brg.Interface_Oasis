package listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import connectionhandler.Handler;

public class Listener implements Runnable {
	/*
	 * This class reads content and responds 
	 */
	
	private final String portString;
	private final String name;
	private final int port;
	private boolean canRun;
	private String groupID;
	private String listenerID;
	private boolean isActive = false;
	
	private ArrayList<Thread> connections = new ArrayList<Thread>();
	
	protected Listener(String portString, String name, String groupID, String listenerID) {
		this.name = name;
		this.portString = portString;
		this.groupID = groupID;
		this.listenerID = listenerID;
		int tempPort = -1; //This is necessary because the compiler throws an error if the port is modified after it could have been modified in the try/catch phrase; 
		boolean isValid = false;
		try {
			tempPort = Integer.parseInt(portString);
			isValid = true;
		} catch (NullPointerException en) {
			reportError("Unset port value", en.getMessage());
		} catch (NumberFormatException ef) {
			reportError("Invalid port value \"" + this.portString + "\"", ef.getMessage());
		}
		if (isValid) {
			this.port = tempPort;
			this.canRun = true;
		} else {
			port = -1;
			this.canRun = false;
		}
	}
	
	protected String getName() {
		return this.name;
	}
	
	private boolean isActive() {
		return this.isActive;
	}
	
	@Override
	public void run() {
		this.isActive = true;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			this.isActive = false;
			reportError("Could not bind ServerSocket to port " + this.port, e.getMessage());
		}
		while (this.isActive) {
			try {
				Socket clientSocket = serverSocket.accept();
				Runnable 
			} catch (IOException e) {
				reportError("Could not accept ServerSocket connection on port " + this.port, e.getMessage());
			}
		}
	}

	private void reportError(String cause, String errorMessage) {
		Handler.getGroup(this.groupID).getKey().reportError(this.name, this.portString, this.listenerID, cause, errorMessage);
	}
	
	private void reportError(String cause) {
		Handler.getGroup(this.groupID).getKey().reportError(this.name, this.portString, this.listenerID, cause);
	}


}