package group.listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import group.GroupHandler;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;

public class Listener implements Runnable {
	/*
	 * This class reads content and responds 
	 */
	
	private String portString; //String value of port
	private String name; //Name of Listener
	private int port; //int value of port
	private boolean canRun; //indicates whether the listener can launch (is false if no valid port is provided)
	private String groupID; //the id of the group the listener is in
	private String groupName; //The name of the group the listener is part of
	private String listenerID; //the id of this listener to uniquely identify it
	private boolean isActive = false; //indicates whether the listener thread is currently listening to the port
	private ServerSocket serverSocket = null;
	
	private ArrayList<Thread> connections = new ArrayList<Thread>(); //The threads of ConnectionHandlers to enable multiple requests at once
	
	protected Listener(String portString, String name, String groupID, String listenerID, String groupName) {
		this.name = name;
		this.portString = portString;
		this.groupID = groupID;
		this.groupName = groupName;
		this.listenerID = listenerID;
		ListenerHandler.inputs.put(this.groupID, new ArrayList<String[]>());
		int tempPort = -1;
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
				reportError("Could not close the ServerSocket on port" + this.port, e.getMessage());
			}
		}
	}
	
	@Override
	public void run() {
		if (!this.canRun) {
			return;
		}
		this.isActive = true;
		try {
			this.serverSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			this.isActive = false;
			String listeningID = ListenerHandler.isListening(this.portString);
			if (listeningID != null) {
				reportError("Port " + this.port + " is already occupied by listener " + listeningID + " \"" + ListenerHandler.getListenerName(listeningID) + "\"", e.getMessage());
			}
			reportError("Could not bind ServerSocket to port" + this.port, e.getMessage());
		}
		while (this.isActive) {
			try {
				Socket clientSocket = this.serverSocket.accept();
				int myID = ListenerHandler.inputs.get(this.listenerID).size();
				ListenerHandler.inputs.get(this.listenerID).add(null);
				Runnable connectionHandler = new ConnectionHandler(myID, this.listenerID, clientSocket);
				this.connections.add(new Thread(connectionHandler));
				this.connections.get(this.connections.size() - 1).start();
				ListenerHandler.timerController.get(this.groupID).getKey().addSocket(clientSocket, 10);
			} catch (IOException e) {
				reportError("Could not accept ServerSocket connection on port " + this.port, e.getMessage());
			}
		}
		GroupHandler.getListenerHandler(this.groupID).changeStatus(this.listenerID, false);
	}

	private void reportError(String causes, String errorMessage) {
		String message = this.name + " in " + this.groupName + " reported " + causes + " caused by " + errorMessage;
		String elements[] = {"GroupName", "GroupID", "ListenerName", "ListenerID", "ListenerPort", "Causes", "ErrorMessage"};
		String values[] = {this.groupName, this.groupID, this.name, this.listenerID, this.portString, causes, errorMessage};
		Logger.addMessage(MessageType.Error, MessageOrigin.Listener, message, this.listenerID, elements, values, false);
	}


}
