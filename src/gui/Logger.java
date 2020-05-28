package gui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import filehandler.Manager;

public class Logger implements Runnable {
	
	private static ArrayList<MessageObject> messages = new ArrayList<MessageObject>(); //Messages to be viewable in log
	private static ArrayList<String> errorElements = new ArrayList<String>();
	private static Thread myThread;
	private static final String FILENAME = "ErrorLog_Session" + DateTimeFormatter.ofPattern("HH_mm_ss").format(LocalDateTime.now()) + ".xml";
	private static final String BASEFOLDER = "Error Logs";
	private static int fileID;
	
	public static boolean runMe = false;
	
	public static void addMessage(MessageType type, MessageOrigin origin, String message, String id, String[] elements, String[] values, boolean isFatal) {
		MessageObject messageObject = new MessageObject(type, origin, message, id);
		Logger.messages.add(messageObject);
		if (type == MessageType.Error && elements != null && values != null && values.length == elements.length) { //TODO: Store property in setting handler
			ArrayList<String> elementList = new ArrayList<String>();
			ArrayList<String> valueList = new ArrayList<String>();
			elementList.add("Origin");
			valueList.add(type.name());
			elementList.addAll(Arrays.asList(elements));
			valueList.addAll(Arrays.asList(values));
			elementList.add("Time");
			valueList.add(messageObject.time);
			elementList.add("PrintedMessage");
			valueList.add(message);
			addXMLError(elementList.toArray(new String[elementList.size()]), valueList.toArray(new String[valueList.size()]));
		}
		if (isFatal) {
			if (elements != null && values != null && elements.length == values.length) {
				ArrayList<String> elementList = new ArrayList<String>();
				ArrayList<String> valueList = new ArrayList<String>();
				elementList.add("Origin");
				valueList.add(type.name());
				elementList.addAll(Arrays.asList(elements));
				valueList.addAll(Arrays.asList(values));
				elementList.add("Time");
				elementList.add("PrintedMessage");
				valueList.add(message);
				valueList.add(messageObject.time);
				Main.fatalError(getXMLError(elementList.toArray(new String[elementList.size()]), valueList.toArray(new String[valueList.size()])));
			}
			
		}
	}
	
	public static String getXMLError(String[] elements, String[] values) {
		String errorString = "<Error>";
		for (int i = 0; i < elements.length; i++) {
			errorString += "\r\n\t<" + elements[i] + ">" + values[i] + "</" + elements[i] + ">";
		}
		errorString += "\r\n</Error>";
		return errorString;
	}

	public static void addXMLError(String[] elements, String[] values) {
		String errorString = "<Error>";
		for (int i = 0; i < elements.length; i++) {
			errorString += "\r\n\t<" + elements[i] + ">" + values[i] + "</" + elements[i] + ">";
		}
		errorString += "\r\n</Error>\r\n";
		Manager.writeFile(Logger.fileID, errorString, true);
		errorElements.add(errorString);
	}
	
	public static void init() {
		Logger.fileID = Manager.newFile(Manager.checkPath(Manager.PATH + Manager.SEPERATOR + Logger.BASEFOLDER) + Manager.SEPERATOR + Logger.FILENAME);
		Logger.myThread = new Thread(new Logger());
		Logger.runMe = true;
		Logger.myThread.start();
	}
	
	@Override
	public void run() {
		int size = 0;
		while (Logger.runMe) {
			if (messages.size() > size) {
				for (int i = size; i < messages.size(); i++) {
					System.out.println(messages.get(i).print());
					//TODO: Show in GUI
				}
				size = messages.size();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				break;
			}
		}
	}

}
