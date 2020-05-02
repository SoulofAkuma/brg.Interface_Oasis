package listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

public class ConnectionHandler implements Runnable {
	
	private final int responseID;
	private final String parentID;
	private Socket socket;
	
	public ConnectionHandler(int responseID, String parentID, Socket socket) {
		this.responseID = responseID;
		this.parentID = parentID;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		PrintWriter out = null;
		BufferedReader in = null;
		boolean success;
		try {
			out = new PrintWriter(this.socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			String inputLine;
			String request = "";
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
//						String uri = requestBaseInformation[1];
//						String httpVersion = requestBaseInformation[2];
//						Pattern uriPattern = Pattern.compile(""); //TODO
//						Pattern httpVersionPattern = Pattern.compile("HTTP/(1.1|1.0|2)"); TODO Move somewhere else
//						if (httpVersionPattern.matcher(httpVersion).matches()) {
//							
//						} else {
//							//TODO: Send bad request response							
//						}
						
						if (requestType.equals("GET")) {
							hasBody = false;
						} else if (requestType.equals("POST")) {
							hasBody = true;
							watchForContentLength = true;
						}
					} else {							
						//TODO: Send bad request response
					}
				}
				if (watchForContentLength) {
					if (inputLine.matches("Content-Length: [0-9]+")) {
						try {
							contentLength = Integer.parseInt(inputLine.split(" ")[1]);					
						} catch (Exception e) {
							//This cannot fail
						}
					}
				}
				if (inputLine.length() == 0) {
					if (hasBody) {
						if (contentLength != -1) {
							String body;
							if (contentLength > 2) {
								body = "\r\n";
							} else {
								char[] bodyBuffer = new char[contentLength - 2];
								in.read(bodyBuffer);
								body = "\r\n" +String.valueOf(bodyBuffer);
							}
							request += body;
							break;
						} else {
							//TODO: Send bad request
						}
					} else {
						break;
					}
				}
				i++;
			}
			success = true;
		} catch(IOException e) {
			success = false;
			//errorReport
		}
	}
	
}
