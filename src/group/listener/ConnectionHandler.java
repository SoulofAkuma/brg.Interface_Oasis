package group.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;

public class ConnectionHandler implements Runnable {
	
	private final int responseID;
	private final String parentID;
	private Socket socket;
	private RequestType requestType;
	
	public ConnectionHandler(int responseID, String parentID, Socket socket) {
		this.responseID = responseID;
		this.parentID = parentID;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		PrintWriter out = null;
		BufferedReader in = null;
		boolean success = false;
		String request = "";
		String body = null;
		try {
			out = new PrintWriter(this.socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			String inputLine;
			int i = 0;
			boolean hasBody = false;
			boolean watchForContentLength = false;
			int contentLength = -1;
			while ((inputLine = in.readLine()) != null) {
				request += inputLine + "\r\n";
				if (i == 0) {
					String[] requestBaseInformation = request.split(" ");
					if (requestBaseInformation.length == 3) {
						String requestType = requestBaseInformation[0];
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
							reportInformation(getResponse(500, "Not Implemented"), true);
							out.write(getResponse(500, "Not Implemented"));
							out.flush();
							break;
						}
					} else {	
						reportInformation(getResponse(400, "Bad Request"), true);
						out.write(getResponse(400, "Bad Request"));
						out.flush();
						break;
					}
				}
				if (watchForContentLength) {
					if (inputLine.matches("Content-Length: [0-9]+")) {
						try {
							contentLength = Integer.parseInt(inputLine.split(" ")[1]);					
						} catch (Exception e) {}
						watchForContentLength = false;
					}
				}
				if (inputLine.length() == 0) {
					if (hasBody) {
						if (contentLength != -1) {
							char[] bodyBuffer = new char[contentLength];
							in.read(bodyBuffer);
							body = String.valueOf(bodyBuffer);
							reportInformation(getResponse(200, "OK", "text/json",  "{\"Status\":\"Received Request\",\"Request-Header-Content\":\" " + request + "\",\"Request-Body-Content\":\"" + body + "\"}"), false);
							out.write(getResponse(200, "OK", "text/json",  "{\"Status\":\"Received Request\",\"Request-Header-Content\":\" " + request + "\",\"Request-Body-Content\":\"" + body + "\"}"));
							out.flush();
							success = true;
							break;
						} else {
							reportInformation(getResponse(400, "Bad Request"), false);
							out.write(getResponse(400, "Bad Request"));
							out.flush();
							break;
						}
					} else {
						if (this.requestType == RequestType.HEAD) {
							reportInformation(getResponse(200, "OK"), false);
							out.write(getResponse(200, "OK"));
						} else {
							reportInformation(getResponse(200, "OK", "text/json",  "{\"Status\":\"Received Request\",\"Request-Header-Content\":\" " + request + "\",\"Request-Body-Content\":\"" + body + "\"}"), false);
							out.write(getResponse(200, "OK", "text/json",  "{\"Status\":\"Received Request\",\"Request-Header-Content\":\" " + request + "\",\"Request-Body-Content\":\"" + body + "\"}"));							
						}
						out.flush();
						success = true;
						break;
					}
				}
				i++;
			}
			in.close();
			out.close();
			this.socket.close();
		} catch(IOException e) {

		}
		if (success) {
			ListenerHandler.inputs.get(this.parentID).set(this.responseID, new String[] {request, body});
		} else {
			ListenerHandler.inputs.get(this.parentID).set(this.responseID, new String[] {null, null});
		}
	}
	
	private String getResponse(int responseCode, String responseMessage, String contentType, String content) {
		String response = "";
		response += "HTTP/1.1 " + responseCode + " " + responseMessage + "\r\n";
		response += "Connection: Closed\r\n";
		response += "Server: InterfaceOasis/0.7\r\n";
		response += "Content-Length: " + content.length() + "\r\n";
		response += "Content-Type: " + contentType + "; charset=utf-8";
		response += "\r\n" + content;
		return response;
	}
	
	private String getResponse(int responseCode, String responseMessage) {
		String response = "";
		response += "HTTP/1.1 " + responseCode + " " + responseMessage + "\r\n";
		response += "Connection: Closed\r\n";
		response += "Server: InterfaceOasis/0.7\r\n\r\n";
		return response;
	}
	
	private void reportInformation(String response, boolean isError) {
		MessageType type = (isError) ? MessageType.Error : MessageType.Information;
		Logger.addMessage(type, MessageOrigin.Listener, "Response " + this.responseID + " to " + this.requestType.name() + " request responded with \"" + response + "\"", this.parentID, new String[] {"ResponseID", "RequestType", "Response"}, new String[] {String.valueOf(this.responseID), this.requestType.name(), response}, false);
	}
		

}