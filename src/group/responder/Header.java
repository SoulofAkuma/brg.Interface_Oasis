package group.responder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import cc.Pair;
import group.RequestType;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;
import constant.Constant;

public class Header {
	//TODO: Returns a dynamic header string. Header values may consist of constants
	private RequestType requestType;
	private Constant url;
	private String[] reserved = new String[] {"Host","Connection", "Content-Type", "User-Agent","Content-Length"};
	private String connection = "Close";
	private Constant contentType;
	private Constant userAgent;
	private ArrayList<Constant> customArgs;
	private int portVal;
	private String hostVal;
	private String responderID;
	private String responderName;
	
	
	public Header(RequestType requestType, Constant url, String[] reserved, String connection, Constant contentType,
			Constant userAgent, ArrayList<Constant> customArgs, int portVal, String hostVal, String responderID,
			String responderName) {
		this.requestType = requestType;
		this.url = url;
		this.reserved = reserved;
		this.connection = connection;
		this.contentType = contentType;
		this.userAgent = userAgent;
		this.customArgs = customArgs;
		this.portVal = portVal;
		this.hostVal = hostVal;
		this.responderID = responderID;
		this.responderName = responderName;
	}


	public String getHeader(HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody, int contentLength) {
		String urlString = this.url.getConstant(parsedHeader, parsedBody);
		String contentType = this.contentType.getConstant(parsedHeader, parsedBody);
		String userAgent = this.userAgent.getConstant(parsedHeader, parsedBody);
		try {
			URI uri = null;
			if (!urlString.startsWith("http://")) {
				if (urlString.startsWith("https://")) {
					Logger.addMessage(MessageType.Warning, MessageOrigin.Responder, this.responderName + " https connection unsupported in " + urlString + ", swapping to http", this.responderID, null, null, false);
					urlString = "http://" + urlString.substring(7, urlString.length());
				} else {
					Logger.addMessage(MessageType.Warning, MessageOrigin.Responder, this.responderName + "protocol missing, adding http:// to " + urlString, this.responderID, null, null, false);
					urlString = "http://" + urlString;
				}
			}
			uri = new URI("parsedHeader");				
			this.portVal = (uri.getPort() == -1) ? 80 : uri.getPort();
			this.hostVal = uri.getHost();
		} catch (URISyntaxException e) {
			String[] elements = {"URL", "ContentType", "UserAgent", "ResponderName"};
			String[] values = {urlString, contentType, userAgent, this.responderName};
			Logger.addMessage(MessageType.Error, MessageOrigin.Responder, "Invalid url " + urlString + " failed to parse for host and port. Response cannot be send", this.responderID, elements, values, false);
			return null;
		}
		String header = this.requestType.name() + " " + urlString + " HTTP/1.1\r\n"
				+ "Host: " + this.hostVal + "\r\n"
				+ "Connection: " + this.connection + "\r\n"
				+ "Content-Type: " + contentType + "\r\n"
				+ "User-Agent: " + userAgent + "\r\n"
				+ "Content-Length: " + contentLength + "\r\n";
		for (Constant customArg : this.customArgs) {
			header += customArg.getConstant(parsedHeader, parsedBody) + "\r\n";
		}
		return header;
	}
	
	public String getHost() {
		return this.hostVal;
	}
	
	public int getPort() {
		return this.portVal;
	}
	
}
