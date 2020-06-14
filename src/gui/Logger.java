package gui;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import filehandler.Manager;

public class Logger implements Runnable {
	
	private static ArrayList<MessageObject> messages = new ArrayList<MessageObject>(); //Messages to be viewable in log
	private static ArrayList<String> errorElements = new ArrayList<String>();
	private static Thread myThread;
	private static final String BASEFOLDER = Manager.PATH + Manager.SEPARATOR + "Log";
	private static final String SESSIONFOLDER = Logger.BASEFOLDER + Manager.SEPARATOR + "Log " + Main.SESSIONTIME;
	private static final String ERRORLOGFILEPATH = Logger.SESSIONFOLDER + Manager.SEPARATOR + "ErrorLog_Session" + Main.SESSIONTIME + ".xml";
	private static final String EXCEPTIONLOGFILEPATH = Logger.SESSIONFOLDER + Manager.SEPARATOR + "ExceptionLog_Session" + Main.SESSIONTIME + ".xml";
	private static int fileID;
	private static int exceptionFileID;
	
	public static boolean runMe = false;
	
	//Logs all major events for the gui console
	public static void addMessage(MessageType type, MessageOrigin origin, String message, String id, String[] elements, String[] values, boolean isFatal) {
		MessageObject messageObject = new MessageObject(type, origin, message, id);
		Logger.messages.add(messageObject);
		ArrayList<String> elementList = new ArrayList<String>();
		ArrayList<String> valueList = new ArrayList<String>();
		if (type == MessageType.Error && elements != null && values != null && values.length == elements.length) {
			elementList.add("Origin");
			valueList.add(type.name());
			elementList.addAll(Arrays.asList(elements));
			valueList.addAll(Arrays.asList(values));
			elementList.add("Time");
			valueList.add(messageObject.time);
			elementList.add("PrintedMessage");
			valueList.add(message);
			elementList.add("ID");
			valueList.add(id);
			addXMLError(elementList.toArray(new String[elementList.size()]), valueList.toArray(new String[valueList.size()]));
			if (isFatal) {
				Main.fatalError(getXMLError(elementList.toArray(new String[elementList.size()]), valueList.toArray(new String[valueList.size()])));
			}
		}
	}
	
	//Reports all major exceptions to a file
	public static void reportException(String cName, String mName,Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String message = "<Exception>\r\n"
				+ "\t<Class>" + cName + "</Class>\r\n"
				+ "\t<Method>" + mName + "</Method>\r\n"
				+ "\t<Details>"
					+ "\t\t<Message>" + e.getMessage() + "</Message>\r\n"
					+ "\t\t<LocalizedMessage>" + e.getLocalizedMessage() + "</LocalizedMessage>"
					+ "\t\t<StackTrace>" + sw.toString() + "</StackTrace>\r\n"
				+ "\t</Details>"
			+ "\t</Exception>\r\n";
		Manager.writeFile(Logger.exceptionFileID, message, true);
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
		Manager.checkPath(Logger.BASEFOLDER);
		Manager.checkPath(Logger.SESSIONFOLDER);
		Logger.fileID = Manager.newFile(Logger.ERRORLOGFILEPATH);
		Logger.exceptionFileID = Manager.newFile(Logger.EXCEPTIONLOGFILEPATH);
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
