package group.responder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;
import parser.ParserHandler;

public class Responder {
	
	private String parserID;
	private ArrayList<Constant> constants = new ArrayList<Constant>();
	private int port;
	private String portString;
	private String name;
	private Constant url;
	private boolean canRespond;
	private String groupName;
	private String groupID;
	private String responderID;
	
	public Responder(String responderID, String parserID, ArrayList<Constant> constants, String portString, Constant url, String groupName, String groupID) {
		this.responderID = responderID;
		this.groupID = groupID;
		this.parserID = parserID;
		this.constants = constants;
		this.portString = portString;
		int tempPort = -1;
		boolean canRespond;
		try {
			tempPort = Integer.parseInt(portString);
			canRespond = true;
		} catch (NullPointerException en) {
			reportError("Unset port value", en.getMessage());
			canRespond = false;
		} catch (NumberFormatException ef) {
			reportError("Invalid port value \"" + this.portString + "\"", ef.getMessage());
			canRespond = false;
		}
		this.canRespond = canRespond;
		this.port = tempPort;
		this.url = url;
		this.groupName = groupName;
	}

	public void repond() {
		// TODO Auto-generated method stub
		
	}

	public void repond(String[] responseParams) {
		HashMap<String, String> parsedHeader = transformHeader(responseParams[1]);
		HashMap<String, String> parsedBody = ParserHandler.getParser(this.parserID).parse(responseParams[0]);
		String response = "";
		
		for (Constant constant : this.constants) {
			if (constant.usesHeader()) {
				response += constant.getConstant(parsedHeader);
			} else {
				response += constant.getConstant(parsedBody);
			}
		}
		
		String url = "";
		if (this.url.usesHeader()) {
			url = this.url.getConstant(parsedHeader);
		} else {
			url = this.url.getConstant(parsedBody);
		}
		sendResponse(url, response);
	}
	
	public void sendResponse(String url, String response) {
		if (!this.canRespond) {
			return;
		}
		try {
			Socket responder = new Socket(url, this.port);
			PrintWriter out = new PrintWriter(responder.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(responder.getInputStream()));
			
			out.write(response);
			out.flush();
			
			Thread.sleep(1000);
			
			String line = "";
			while ((line = in.readLine()) != null) {
				//TODO: Timeout in case of reading error;
				//TODO: Find a way to store responses of responder
			}
		} catch (IOException e) {
			reportError("Couldn't connect socket", e.getMessage());
		} catch (InterruptedException e) {
			reportError("Interrupted responder response", e.getMessage());
		}
	}
	
	public static HashMap<String, String> transformHeader(String input) {
		String[] lines = input.split("\r\n");
		HashMap<String, String> returnVal = new HashMap<String, String>();
		for (int i = 1; i < lines.length; i++) {
			String line = lines[i];
			int splitIndex = line.indexOf(":", 0);
			String name = line.substring(0, splitIndex);
			String value = line.substring(splitIndex + 1, line.length());
			returnVal.put(name, value);
		}
		return returnVal;
	}
	
	private void reportError(String causes, String errorMessage) {
		String message = this.name + " in " + this.groupName + " reported " + causes + " caused by " + errorMessage;
		String elements[] = {"GroupName", "GroupID", "ResponderName", "ResponderID", "ResponderPort", "Causes", "ErrorMessage"};
		String values[] = {this.groupName, this.groupID, this.name, this.responderID, this.portString, causes, errorMessage};
		Logger.addMessage(MessageType.Error, MessageOrigin.Responder, message, this.responderID, elements, values, false);
	}
	
	private void reportError(String causes, String errorMessage, String url) {
		String message = this.name + " in " + this.groupName + " reported " + causes + " caused by " + errorMessage;
		String elements[] = {"GroupName", "GroupID", "ResponderName", "ResponderID", "ResponderPort", "Url", "Causes", "ErrorMessage"};
		String values[] = {this.groupName, this.groupID, this.name, this.responderID, this.portString, url, causes, errorMessage};
		Logger.addMessage(MessageType.Error, MessageOrigin.Responder, message, this.responderID, elements, values, false);
	}

}
