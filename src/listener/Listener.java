package listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import connectionhandler.Handler;

public class Listener implements Runnable {
	/*
	 * This class reads content and responds 
	 */
	
	private final String portString; //String value of port
	private final String name; //Name of Listener
	private final int port; //int value of port
	private boolean canRun; //indicates whether the listener can launch (is false if no valid port is provided)
	private String groupID; //the id of the group the listener is in (for potential backtracking of the corresponding handler class)
	private String listenerID; //the id of this listener to uniquely identify it
	private boolean isActive = false; //indicates whether the listener thread is currently listening to the port
	private ServerSocket serverSocket = null;
	
	private ArrayList<Thread> connections = new ArrayList<Thread>(); //The threads of ConnectionHandlers to enable multiple requests at once
	private static HashMap<String, ArrayList<String[]>> inputs = new HashMap<String, ArrayList<String[]>>(); //A List of the received requests[0] and request bodies[1] to the corresponding listener IDs 
	
	protected Listener(String portString, String name, String groupID, String listenerID) {
		this.name = name;
		this.portString = portString;
		this.groupID = groupID;
		this.listenerID = listenerID;
		ListenerHandler.inputs.put(this.groupID, new ArrayList<String[]>());
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
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
		if (!isActive) {
			try {
				this.serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		this.isActive = true;
		try {
			this.serverSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			this.isActive = false;
			reportError("Could not bind ServerSocket to port " + this.port, e.getMessage());
		}
		while (this.isActive) {
			try {
				Socket clientSocket = this.serverSocket.accept();
				int myID = ListenerHandler.inputs.get(this.listenerID).size();
				ListenerHandler.inputs.get(this.listenerID).add(new String[2]);
				Runnable connectionHandler = new ConnectionHandler(myID, this.listenerID, clientSocket);
				this.connections.add(new Thread(connectionHandler));
				this.connections.get(this.connections.size() - 1).start();
				ListenerHandler.timerController.get(this.groupID).getKey().addSocket(clientSocket, 10);
			} catch (IOException e) {
				reportError("Could not accept ServerSocket connection on port " + this.port, e.getMessage());
			}
		}
		Handler.getListenerHandler(this.groupID).changeStatus(this.listenerID, false);
	}

	private void reportError(String cause, String errorMessage) {
		Handler.getGroup(this.groupID).getKey().reportError(this.name, this.portString, this.listenerID, cause, errorMessage);
	}
	
	private void reportError(String cause) {
		Handler.getGroup(this.groupID).getKey().reportError(this.name, this.portString, this.listenerID, cause);
	}


}
