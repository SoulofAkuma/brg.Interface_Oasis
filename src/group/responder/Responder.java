package group.responder;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import group.GroupHandler;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;

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
			sendResponse(header.getHost(), header.getUrl(), header.getPort(), header.getRequestType(), response);
		}
	}
	
	//The response here is an http request. However, everything is considered a response here because it happens in response to a trigger
	public void sendResponse(String host, String url, int port, String requestType, String response) {
		try {
			Socket responder = new Socket(host, port);
			PrintWriter out = new PrintWriter(responder.getOutputStream());
			InputStream in = responder.getInputStream();

			if (!responder.isConnected()) {
				reportError("Socket connection failed", "Unknown");
				responder.close();
				return;
			}
			if (this.log) {
				Logger.logResponderRequest(response, this.responderID, this.name);
			}
			out.write(response);
			out.flush();
			
			Thread.sleep(1000);
			
			String timeoutID = GroupHandler.addSocketTimeout(responder, 10);
			Runnable handler = new ConnectionHandler(this.responderID, this.name, this.log, in, url, requestType, port, timeoutID);
			Thread handlerThread = new Thread(handler);
			handlerThread.start();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public String getResponderID() {
		return responderID;
	}

	public String getGroupID() {
		return groupID;
	}
	
}
