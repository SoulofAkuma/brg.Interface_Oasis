package group.responder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import group.GroupHandler;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;
import trigger.TriggerHandler;

public class ConnectionHandler implements Runnable {
	
	private final String parentID;
	private final InputStream is;
	private final String url;
	private final int port;
	private final String requestType;
	private final String timeoutID;
	private String parentName;
	private boolean log;
	
	public ConnectionHandler(String parentID, String parentName, boolean log, InputStream is, String url, String requestType, int port, String timeoutID) {
		this.parentID = parentID;
		this.is = is;
		this.url = url;
		this.port = port;
		this.requestType = requestType;
		this.timeoutID = timeoutID;
		this.parentName = parentName;
		this.log = log;
	}
	
	@Override
	public void run() {
		boolean success = false;
		String response = "";
		String body = null;
		try {
			String inputLine;
			boolean watchForContentLength = true;
			int contentLength = -1;
			do {
				inputLine = readLine(is);
				response += inputLine;
				if (watchForContentLength) {
					Pattern pattern = Pattern.compile("(?<=Content-Length: )[0-9]+");
					Matcher matcher = pattern.matcher(inputLine);
					if (matcher.find()) {
						try {
							contentLength = Integer.parseInt(matcher.group());					
						} catch (Exception e) {
							contentLength = -1;
						}
						watchForContentLength = false;
					}
				}
				if (inputLine.isBlank()) {
					if (contentLength != -1) {
						Pattern pattern = Pattern.compile("(?<=Content-Type: [a-z]+\\/[a-z0-9.-]+; *charset=)[a-zA-Z0-9-]+");
						Matcher matcher = pattern.matcher(response);
						String charset = "UTF-8";
						if (matcher.find()) {
							charset = matcher.group();
						}
						Charset stdC;
						try {
							stdC = Charset.forName(charset);
						} catch (Exception e) {
							reportInformation(response, "Unknown Charset", true);
							break;
						}
						byte[] bodyBuffer = new byte[contentLength];
						is.read(bodyBuffer);
						body = new String(bodyBuffer, stdC);
						success = true;
					} else {
						success = true;
						break;
					}
				}
			} while(!inputLine.isBlank());
			
		} catch (Exception e) {
			Logger.reportException(this.getClass().getName(), "run", e);
		}
		if (success) {
			Logger.addMessage(MessageType.Information, MessageOrigin.Responder, "Responder successfully parsed received response - Reporting to trigger", this.parentID, null, null, false);
			TriggerHandler.reportResponder(this.parentID, response, body);
			if (this.log) {
				Logger.logResponderResponse(response + body, this.parentID, this.parentName);
			}
		} else {
			Logger.addMessage(MessageType.Information, MessageOrigin.Responder, "Responder parsing of received response failed - Aborting trigger report", this.parentID, null, null, false);
		}
		GroupHandler.removeCooldown(this.timeoutID);
	}
	
	private String readLine(InputStream in) {
		byte[] l2 = new byte[2];
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		do {
			try {
				l2[0] = l2[1];
				byte b = (byte) in.read();
				l2[1] = b;
				bytes.add(b);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} while(!(l2[0] == 0x0d && l2[1] == 0x0a));
		byte[] res = new byte[bytes.size()];
		int ite = 0;
		for (int i = 0; i < bytes.size(); i ++) {
			res[i] = bytes.get(i);
		}
		return new String(res, StandardCharsets.US_ASCII);
	}
	
	private void reportInformation(String received, String message, boolean isError) {
		MessageType type = (isError) ? MessageType.Error : MessageType.Information;
		Logger.addMessage(type, MessageOrigin.Responder, "Recieved response to " + this.requestType + " request of responder to " + this.url + " on port " + this.port + ". " + message, this.parentID, new String[] {"URL", "Port", "RequestType","Received"}, new String[] {this.url, String.valueOf(this.port), this.requestType, received}, false);
	}

}
