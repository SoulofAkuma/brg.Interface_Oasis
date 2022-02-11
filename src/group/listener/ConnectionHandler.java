package group.listener;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import group.GroupHandler;
import group.RequestType;
import gui.Logger;
import gui.Main;
import gui.MessageOrigin;
import gui.MessageType;
import trigger.TriggerHandler;

public class ConnectionHandler implements Runnable {
	
	private final String parentID;
	private Socket socket;
	private RequestType requestType;
	private static final String[] stdCharsets = new String[] {"US-ASCII","ISO-8859-1","UTF-8","UTF-16BE","UTF-16LE","UTF-16"};
	private final String timeoutID;
	private String parentName;
	private boolean log;
	
	public ConnectionHandler(String parentID, String parentName, boolean log, Socket socket, String timeoutID) {
		this.parentID = parentID;
		this.socket = socket;
		this.timeoutID = timeoutID;
		this.parentName = parentName;
		this.log = log;
	}
	
	@Override
	public void run() {
		PrintWriter out = null;
		InputStream in = null;
		boolean success = false;
		String request = "";
		String body = null;
		String response = null;
		try {
			out = new PrintWriter(this.socket.getOutputStream());
			in = this.socket.getInputStream();
			String inputLine;
			int i = 0;
			boolean hasBody = false;
			boolean watchForContentLength = false;
			int contentLength = -1;
			do {
				inputLine = readLine(in);
				request += inputLine;
				if (i == 0) {
					String[] requestBaseInformation = request.split(" ");
					if (requestBaseInformation.length == 3) {
						String requestType = requestBaseInformation[0];
						String protocol = requestBaseInformation[2].strip();
						if (requestType.equals("GET")) {
							this.requestType = RequestType.GET;
							hasBody = false;
						} else if (requestType.equals("POST")) {
							this.requestType = RequestType.POST;
							hasBody = true;
							watchForContentLength = true;
						} else if (requestType.equals("HEAD")) {
							this.requestType = RequestType.HEAD;
							hasBody = false;
						} else {
							response = getResponse(500, "Not Implemented");
							reportInformation(getResponse(500, "Not Implemented"), true);
							out.write(getResponse(500, "Not Implemented"));
							out.flush();
							break;
						}
						if (!(protocol.equals("HTTP/1.1") || !protocol.equals("HTTP"))) {
							response = getResponse(400, "Bad Request");
							reportInformation(getResponse(400, "Bad Request"), true);
							out.write(getResponse(400, "Bad Request"));
							out.flush();
							break;
						}
					} else {
						String.join(", ", requestBaseInformation);
						response = getResponse(400, "Bad Request");
						reportInformation(getResponse(400, "Bad Request"), true);
						out.write(getResponse(400, "Bad Request"));
						out.flush();
						break;
					}
				}
				if (watchForContentLength) {
					Pattern pattern = Pattern.compile("(?<=Content-(L|l)ength: )[0-9]+");
					Matcher matcher = pattern.matcher(inputLine);
					if (matcher.find()) {
						try {
							contentLength = Integer.parseInt(matcher.group());		
						} catch (Exception e) {
							response = getResponse(400, "Bad Request");
							reportInformation(getResponse(400, "Bad Request"), true);
							out.write(getResponse(400, "Bad Request"));
							out.flush();
							break;
						}
						watchForContentLength = false;
					}
				}
				if (inputLine.isBlank()) {
					if (hasBody) {
						if (contentLength != -1) {
							Pattern pattern = Pattern.compile("(?<=Content-(T|t)ype: [a-z]+\\/[a-z0-9.-]+; *charset=)[a-zA-Z0-9-]+");
							Matcher matcher = pattern.matcher(request);
							String charset = "UTF-8";
							if (matcher.find()) {
								charset = matcher.group();
							}
							Charset stdC;
							try {
								stdC = Charset.forName(charset);
							} catch (Exception e) {
								response = getResponse(200, "OK", "text/json", "{\"Status\":\"Charset Error\", \"Valid-Charsets\":[\"" + String.join("\",\"", ConnectionHandler.stdCharsets) + "\"]}");
								reportInformation(getResponse(200, "OK", "text/json", "{\"Status\":\"Charset Error\", \"Valid-Charsets\":[\"" + String.join("\",\"", ConnectionHandler.stdCharsets) + "\"]}"), true);
								out.write(getResponse(200, "OK", "text/json", "{\"Status\":\"Charset Error\", \"Valid-Charsets\":[\"" + String.join("\",\"", ConnectionHandler.stdCharsets) + "\"]}"));
								out.flush();
								break;
							}
							byte[] bodyBuffer = new byte[contentLength];
							in.read(bodyBuffer);
							body = new String(bodyBuffer, stdC);
							response = getResponse(200, "OK", "text/json",  "{\"Status\":\"Received Request\"}");
							reportInformation(getResponse(200, "OK", "text/json",  "{\"Status\":\"Received Request\"}"), false);
							out.write(getResponse(200, "OK", "text/json",  "{\"Status\":\"Received Request\"}"));
							out.flush();
							success = true;
							break;
						} else {
							response = getResponse(400, "Bad Request");
							reportInformation(getResponse(400, "Bad Request"), false);
							out.write(getResponse(400, "Bad Request"));
							out.flush();
							break;
						}
					} else {
						if (this.requestType == RequestType.HEAD) {
							response = getResponse(200, "OK");
							reportInformation(getResponse(200, "OK"), false);
							out.write(getResponse(200, "OK"));
						} else {
							response = getResponse(200, "OK", "text/json", "{\"Status\":\"Received Request\"}");
							reportInformation(getResponse(200, "OK", "text/json", "{\"Status\":\"Received Request\"}"), false);	
							out.write(getResponse(200, "OK", "text/json", "{\"Status\":\"Received Request\"}"));
						}
						out.flush();
						success = true;
						break;
					}
				}
				i++;
			} while (!inputLine.isBlank());
			in.close();
			out.close();
			this.socket.close();
		} catch(Exception e) {
			Logger.reportException("ConnectionHandler", "run", e);
		}
		if (response != null && this.log) {
			Logger.logListenerResponse(response, this.parentID, this.parentName);
		}
		if (success) {
			Logger.addMessage(MessageType.Information, MessageOrigin.Listener, "Listener successfully parsed received request - Reporting to trigger", this.parentID, null, null, false);
			TriggerHandler.reportListener(this.parentID, request, body);
			if (this.log) {
				Logger.logListenerRequest(request + body, this.parentID, this.parentName);
			}
		} else {
			Logger.addMessage(MessageType.Warning, MessageOrigin.Listener, "Listener parsing of received request failed - Aborting trigger report", this.parentID, null, null, false);
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
	
	private String getResponse(int responseCode, String responseMessage, String contentType, String content) {
		String response = "";
		response += "HTTP/1.1 " + responseCode + " " + responseMessage + "\r\n";
		response += "Connection: Closed\r\n";
		response += "Server: InterfaceOasis/" + Main.versionNumber + "\r\n";
		response += "Content-Length: " + content.getBytes().length + "\r\n";
		response += "Content-Type: " + contentType + "; charset=utf-8\r\n";
		response += "\r\n" + content;
		return response;
	}
	
	private String getResponse(int responseCode, String responseMessage) {
		String response = "";
		response += "HTTP/1.1 " + responseCode + " " + responseMessage + "\r\n";
		response += "Connection: Closed\r\n";
		response += "Server: InterfaceOasis/" + Main.versionNumber + "\r\n\r\n";
		return response;
	}
	
	private void reportInformation(String send, boolean isError) {
		MessageType type = (isError) ? MessageType.Error : MessageType.Information;
		Logger.addMessage(type, MessageOrigin.Listener, "Sent response to " + this.requestType.name() + " request", this.parentID, new String[] {"RequestType", "Sent"}, new String[] {this.requestType.name(), send}, false);
	}
		

}