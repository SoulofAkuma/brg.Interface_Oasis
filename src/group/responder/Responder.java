package group.responder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import cc.Pair;
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

	public void respond(HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody) {
		String bodyString = this.body.getBody(parsedHeader, parsedBody);
		String headerString = this.header.getHeader(parsedHeader, parsedBody, bodyString.getBytes().length);
		if (headerString != null) {
			String response = headerString + "\r\n" + bodyString;
			sendResponse(header.getHost(), header.getPort(), response);
		}
	}
	
	//The response here is an http request. However, everything is considered a response here because it happens in response to a trigger
	public void sendResponse(String host, int port, String response) {
		try {
			Socket responder = new Socket(host, port);
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
	
	private void reportError(String causes, String errorMessage) {
		String message = this.name + " in " + this.groupName + " reported " + causes + " caused by " + errorMessage;
		String elements[] = {"GroupName", "GroupID", "ResponderName", "ResponderID", "Causes", "ErrorMessage"};
		String values[] = {this.groupName, this.groupID, this.name, this.responderID, causes, errorMessage};
		Logger.addMessage(MessageType.Error, MessageOrigin.Responder, message, this.responderID, elements, values, false);
	}
	
	private void reportError(String causes, String errorMessage, String url) {
		String message = this.name + " in " + this.groupName + " reported " + causes + " caused by " + errorMessage;
		String elements[] = {"GroupName", "GroupID", "ResponderName", "ResponderID", "Url", "Causes", "ErrorMessage"};
		String values[] = {this.groupName, this.groupID, this.name, this.responderID, url, causes, errorMessage};
		Logger.addMessage(MessageType.Error, MessageOrigin.Responder, message, this.responderID, elements, values, false);
	}

}
