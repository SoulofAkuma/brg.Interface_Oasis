package group.responder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

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
		if (isIPSyntax(urlString)) {
			if (!isValidIP(urlString)) {
				String[] elements = {"URL", "ContentType", "UserAgent", "ResponderName"};
				String[] values = {urlString, contentType, userAgent, this.responderName};
				Logger.addMessage(MessageType.Error, MessageOrigin.Responder, this.responderName + " Invalid IP Address " + urlString, this.responderID, elements, values, false);
				return null;
			}
		} else {
			try {
				URI uri = null;
				if (!urlString.startsWith("http://")) {
					if (urlString.startsWith("https://")) {
						Logger.addMessage(MessageType.Warning, MessageOrigin.Responder, this.responderName + " Https connection unsupported in " + urlString + ", swapping to http", this.responderID, null, null, false);
						urlString = "http://" + urlString.substring(7, urlString.length());
					} else {
						Logger.addMessage(MessageType.Warning, MessageOrigin.Responder, this.responderName + " Protocol missing, adding http:// to " + urlString, this.responderID, null, null, false);
						urlString = "http://" + urlString;
					}
				}
				uri = new URI("parsedHeader");
				this.portVal = (uri.getPort() == -1) ? 80 : uri.getPort();
				this.hostVal = uri.getHost();
			} catch (URISyntaxException e) {
				String[] elements = {"URL", "ContentType", "UserAgent", "ResponderName"};
				String[] values = {urlString, contentType, userAgent, this.responderName};
				Logger.addMessage(MessageType.Error, MessageOrigin.Responder, this.responderName + " Invalid url " + urlString + " failed to parse for host and port. Response cannot be send", this.responderID, elements, values, false);
				return null;
			}
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
	
	private boolean isIPSyntax(String ip) {
		if (ip.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
			return true;
		}
		return false;
	}
	
	private boolean isValidIP(String ip) {
		try {
			Pattern pb1 = Pattern.compile("[0-9]{1,3}(?=\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");
			Pattern pb2 = Pattern.compile("(?<=[0-9]{1,3}\\.)[0-9]{1,3}(?=\\.[0-9]{1,3}\\.[0-9]{1,3})");
			Pattern pb3 = Pattern.compile("(?<=[0-9]{1,3}\\.[0-9]{1,3}\\.)[0-9]{1,3}(?=\\.[0-9]{1,3})");
			Pattern pb4 = Pattern.compile("(?<=[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)[0-9]{1,3}");
			int b1 = Integer.parseInt(pb1.matcher(ip).group(0));
			int b2 = Integer.parseInt(pb2.matcher(ip).group(0));
			int b3 = Integer.parseInt(pb3.matcher(ip).group(0));
			int b4 = Integer.parseInt(pb4.matcher(ip).group(0));
			if (b1 >= 0 && b2 >= 0 && b3 >= 0 && b3 >= 0 && b1 <= 255 && b2 <= 255 && b3 <= 255 && b4 <= 255) {
				return true;
			}
		} catch (Exception e) {}
		return false;
	}
	
	public String getHost() {
		return this.hostVal;
	}
	
	public int getPort() {
		return this.portVal;
	}
	
}
