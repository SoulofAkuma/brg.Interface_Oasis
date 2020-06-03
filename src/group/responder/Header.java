package group.responder;

import java.util.ArrayList;
import java.util.HashMap;

import cc.Pair;
import group.RequestType;
import constant.Constant;

public class Header {
	//TODO: Returns a dynamic header string. Header values may consist of constants
	private RequestType requestType;
	private Constant url;
	private String[] reserved = new String[] {"Host","Connection", "Content-Type", "User-Agent","Content-Length"};
	private Constant host;
	private String connection = "Close";
	private Constant contentType;
	private Constant userAgent;
	private ArrayList<Constant> customArgs;
	private String urlVal;
	private String portVal;
	
	public Header(RequestType requestType, Constant url, String[] reserved, Constant host, String connection, Constant contentType, Constant userAgent, ArrayList<Constant> customArgs) {
		this.requestType = requestType;
		this.url = url;
		this.reserved = reserved;
		this.host = host;
		this.connection = connection;
		this.contentType = contentType;
		this.userAgent = userAgent;
		this.customArgs = customArgs;
	}

	public String getHeader(HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody, int contentLength) {
		String header = "";
		//TODO: generate header string from class and args
		return header;
	}
	
	
}
