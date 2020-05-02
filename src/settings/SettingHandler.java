package settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.Pair;
import filehandler.Manager;

public class SettingHandler {
	
	private static int fileID;
	private static final String FILENAME = "settings.xml";
	private static final String PATH = System.getProperty("user.home") + Manager.SEPERATOR + "Interface Oasis";
	private static final String BASESETTING = "Interface Oasis";
	private static Setting masterSetting;
	
	public final static char CR  = (char) 0x0D;
	public final static char LF  = (char) 0x0A;
	public final static String CRLF  = "" + CR + LF; 
	public static Socket socket = null;;
	
	public static void handle() {
		
		String test = "Accept-Language: en-US,en;q=0.9,de-DE;q=0.8,de;q=0.7";
		System.out.println(test.length());
		
		ServerSocket server;
		try {
			server = new ServerSocket(1234);
			socket = server.accept();
			run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			
		
		//until here
		
		fileID = Manager.newFile(PATH + Manager.SEPERATOR + FILENAME);
		
		masterSetting = Setting.parseSetting(Manager.readFile(fileID), 1);
		
		if (masterSetting.isCorrupt()) {
			masterSetting.resetSetting(BASESETTING);
		}
	}
	
	public static void createSetting(String name, String value, ArrayList<Pair<String, String>> attributes) {
		masterSetting.addSetting(name, value, attributes);
	}
	
	public static ArrayList<Setting> getSettings(String name) {
		return masterSetting.getSettings(name);
	}
	
	public static void replaceSetting(Setting setting) {
		masterSetting.replaceID(setting.getID(), setting);
	}
	
	public static void run() {
		PrintWriter out = null;
		BufferedReader in = null;
		boolean success;
		try {
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
			System.out.println(request);
		} catch(IOException e) {
			success = false;
			//errorReport
		}
	}
}
