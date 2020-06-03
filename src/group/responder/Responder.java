package group.responder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import constant.Constant;
import group.RequestType;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;
import parser.ParserHandler;

public class Responder {
	
	private String responderID;
	private String name;
	private boolean log;
	private String groupID;
	private String groupName;
	private Header header;
	private Body body;
	
	public Responder(String responderID, String name, boolean log, String groupID, String groupName, Header header, Body body) {
		this.responderID = responderID;
		this.name = name;
		this.log = log;
		this.groupID = groupID;
		this.groupName = groupName;
		this.header = header;
		this.body = body;
	}

	public void repond() {
		// TODO Auto-generated method stub
		
	}

	public void repond(HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody) {
		String bodyString = this.body.getBody(parsedHeader, parsedBody);
		String headerString = this.header.getHeader(parsedHeader, parsedBody, bodyString.length());
		String response = headerString + "\r\n\r\n" + bodyString;
		sendResponse(header., response);
	}
	
	//TODO: Add header Class to allow custom header values. 
	public void sendResponse(String url, String response) {
		try {
			Socket responder = new Socket();
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
			Logger.reportException("Responder", "sendResponse", e);
		} catch (InterruptedException e) {
			reportError("Interrupted responder response", e.getMessage());
			Logger.reportException("Responder", "sendResponse", e);
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
