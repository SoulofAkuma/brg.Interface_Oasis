package group.responder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import cc.Pair;
import group.RequestType;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;
import constant.Constant;
import constant.ConstantHandler;

public class Header {
	//TODO: Returns a dynamic header string. Header values may consist of constants
	private String requestTypeValue;  
	private String requestType; //The request type to be used by the responder, can be auto (get if body length == 0, otherwise post)
	private String url;
	private String urlVal;
	private ArrayList<String> reserved = new ArrayList<String>(Arrays.asList(new String[] {"Host","Connection", "Content-Type", "User-Agent","Content-Length"}));
	private String connection = "Close";
	private String contentType;
	private String userAgent;
	private ArrayList<String> customArgs;
	private int portVal;
	private String hostVal;
	private String responderID;
	private String responderName;
	
	public Header(String requestType, String url, String contentType, String userAgent,
			ArrayList<String> customArgs, String responderID, String responderName) {
		this.requestType = requestType;
		this.url = url;
		this.contentType = contentType;
		this.userAgent = userAgent;
		this.customArgs = customArgs;
		this.responderID = responderID;
		this.responderName = responderName;
	}

	public String getHeader(HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody, int contentLength) {
		if (this.requestType.equals("auto")) {
			if (contentLength == 0) {
				this.requestTypeValue = "GET";
			} else {
				this.requestTypeValue = "POST";
			}
		}
		this.urlVal = ConstantHandler.getConstant(this.url, parsedHeader, parsedBody);
		String contentType = (this.contentType == null) ? "Content-Type: text/plain" : "Content-Type: " + ConstantHandler.getConstant(this.contentType, parsedHeader, parsedBody) + "\r\n";
		if (!this.requestTypeValue.equals("POST")) {
			contentType = "";
		}
		String userAgent = (this.userAgent == null) ? "" : "User-Agent: " + ConstantHandler.getConstant(this.userAgent, parsedHeader, parsedBody) + "\r\n";
		if (isIPSyntax(this.urlVal)) {
			if (!isValidIP(this.urlVal)) {
				String[] elements = {"URL", "ContentType", "UserAgent", "ResponderName"};
				String[] values = {this.urlVal, contentType, userAgent, this.responderName};
				Logger.addMessage(MessageType.Error, MessageOrigin.Responder, this.responderName + " Invalid IP Address " + this.urlVal, this.responderID, elements, values, false);
				return null;
			}
		} else {
			try {
				URI uri = null;
				if (!this.urlVal.startsWith("http://")) {
					if (this.urlVal.startsWith("https://")) {
						Logger.addMessage(MessageType.Warning, MessageOrigin.Responder, this.responderName + " Https connection unsupported in " + this.urlVal + ", swapping to http", this.responderID, null, null, false);
						this.urlVal = "http://" + this.urlVal.substring(7, this.urlVal.length());
					} else {
						Logger.addMessage(MessageType.Warning, MessageOrigin.Responder, this.responderName + " Protocol missing, adding http:// to " + this.urlVal, this.responderID, null, null, false);
						this.urlVal = "http://" + this.urlVal;
					}
				}
				uri = new URI("parsedHeader");
				this.portVal = (uri.getPort() == -1) ? 80 : uri.getPort();
				this.hostVal = uri.getHost();
			} catch (URISyntaxException e) {
				String[] elements = {"URL", "ContentType", "UserAgent", "ResponderName"};
				String[] values = {this.urlVal, contentType, userAgent, this.responderName};
				Logger.addMessage(MessageType.Error, MessageOrigin.Responder, this.responderName + " Invalid url " + this.urlVal + " failed to parse for host and port. Response cannot be send", this.responderID, elements, values, false);
				return null;
			}
		}
		String header = this.requestTypeValue + " " + this.urlVal + " HTTP/1.1\r\n"
				+ "Host: " + this.hostVal + "\r\n"
				+ "Connection: " + this.connection + "\r\n"
				+ contentType
				+ userAgent
				+ "Content-Length: " + contentLength + "\r\n";
		for (String customArg : this.customArgs) {
			String val = ConstantHandler.getConstant(customArg, parsedHeader, parsedBody);
			if (val.indexOf(":") == -1) {
				Logger.addMessage(MessageType.Warning, MessageOrigin.Responder, "The constant for the header " + ConstantHandler.identification(customArg) + " is in an invalid format \"" + val + "\". Skipping Constant", this.responderID, null, null, false);
			} else if (this.reserved.contains(val.substring(0, val.indexOf(":")))) {
				Logger.addMessage(MessageType.Warning, MessageOrigin.Responder, "The constant for the header " + ConstantHandler.identification(customArg) + " contains a reserved attribute \"" + val.substring(0, val.indexOf(":")) + "\". Skipping Constant", this.responderID, null, null, false);
				
			} else {
				header += val + "\r\n";
			}
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

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public ArrayList<String> getCustomArgs() {
		return customArgs;
	}

	public void setCustomArgs(ArrayList<String> customArgs) {
		this.customArgs = customArgs;
	}

	public int getPort() {
		return portVal;
	}

	public String getHost() {
		return hostVal;
	}
	
}
