package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import filehandler.Manager;

public class Logger extends JFrame implements Runnable{
	
	private static Logger console;
	private static JTextPane consoleText;
	
	public Logger() {
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			Logger.reportException(Main.class.getName(), "main", e);			
		}
		
		setBounds(new Rectangle(0, 0, 600, 300));
		getContentPane().setBounds(new Rectangle(0, 0, 600, 300));
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		
		consoleText = new JTextPane();
		consoleText.setFont(new Font("Tahoma", Font.PLAIN, 12));
		consoleText.setBackground(Color.DARK_GRAY);
		consoleText.setBorder(null);
		consoleText.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(consoleText);
		consoleText.setPreferredSize(new Dimension(586, 263));
		scrollPane.setBounds(0, 0, 586, 263);
		getContentPane().add(scrollPane);
		addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
		        Logger.showGUI = false;
		    }
		});
	}
	
	private static ArrayList<MessageObject> messages = new ArrayList<MessageObject>(); //Messages to be viewable in log
	private static ArrayList<String> errorElements = new ArrayList<String>();
	private static Thread myThread;
	private static final String BASEFOLDER = Manager.PATH + Manager.SEPARATOR + "Log";
	private static final String SESSIONFOLDER = Logger.BASEFOLDER + Manager.SEPARATOR + "Log " + Main.SESSIONTIME;
	private static final String ERRORLOGFILEPATH = Logger.SESSIONFOLDER + Manager.SEPARATOR + "ErrorLog_Session" + Main.SESSIONTIME + ".xml";
	private static final String EXCEPTIONLOGFILEPATH = Logger.SESSIONFOLDER + Manager.SEPARATOR + "ExceptionLog_Session" + Main.SESSIONTIME + ".xml";
	private static final String LISTENERLOGFILEPATH = Logger.SESSIONFOLDER + Manager.SEPARATOR + "ListenerLog" + Main.SESSIONTIME + ".xml";
	private static final String RESPONDERLOGFILEPATH = Logger.SESSIONFOLDER + Manager.SEPARATOR + "ResponderLog" + Main.SESSIONTIME + ".xml";
	private static int fileID;
	private static int exceptionFileID;
	private static int listenerLogID;
	private static int responderLogID;
	
	public static boolean showGUI = false;
	
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
	
	public static void logListenerRequest(String request, String id, String listenerName) {
		String logString = "<ListenerRequest>\r\n"
				+ "\t<ListenerName>" + listenerName + "</ListenerName>\r\n"
				+ "\t<ListenerID>" + id + "</ListenerID>\r\n"
				+ "\t<Request>\r\n" + paragraphTab(request, 2) + "\r\n\t</Request>\r\n"
				+ "</ListenerRequest>\r\n";
		Manager.writeFile(Logger.listenerLogID, logString, true);
	}
	
	public static void logListenerResponse(String response, String id, String listenerName) {
		String logString = "<ListenerResponse>\r\n"
				+ "\t<ListenerName>" + listenerName + "</ListenerName>\r\n"
								+ "\t<ListenerID>" + id + "</ListenerID>\r\n"
				+ "\t<Response>\r\n" + paragraphTab(response, 2) + "\r\n\t</Response>\r\n"
				+ "</ListenerResponse>\r\n";
		Manager.writeFile(Logger.listenerLogID, logString, true);
	}
	
	public static void logResponderRequest(String request, String id, String responderName) {
		String logString = "<ResponderRequest>\r\n"
				+ "\t<ResponderName>" + responderName + "</ResponderName>\r\n"
				+ "\t<ResponderID>" + id + "</ResponderID>\r\n"
				+ "\t<Request>\r\n" + paragraphTab(request, 2) + "\r\n\t</Request>\r\n"
				+ "</ResponderRequest>\r\n";
		Manager.writeFile(Logger.responderLogID, logString, true);
	}
	
	public static void logResponderResponse(String response, String id, String responderName) {
		String logString = "<ResponderResponse>\r\n"
				+ "\t<ResponderName>" + responderName + "</ResponderName>\r\n"
				+ "\t<ResponderID>" + id + "</ResponderID>\r\n"
				+ "\t<Response>\r\n" + paragraphTab(response, 2) + "\r\n\t</Response>\r\n"
				+ "</ResponderResponse>\r\n";
		Manager.writeFile(Logger.responderLogID, logString, true);
	}
	
	public static String paragraphTab(String paragraph, int tabCount) {
		String[] lines = paragraph.split("\r\n");
		String tabString = "";
		for (int i = 0; i < tabCount; i++) {
			tabString += "\t";
		}
		String newParagraph = "";
		for (String line : lines) {
			newParagraph += tabString + line + "\r\n";
		}
		return newParagraph;
	}
	
	public static void init() {
		Manager.checkPath(Logger.BASEFOLDER);
		Manager.checkPath(Logger.SESSIONFOLDER);
		Logger.fileID = Manager.newFile(Logger.ERRORLOGFILEPATH);
		Logger.exceptionFileID = Manager.newFile(Logger.EXCEPTIONLOGFILEPATH);
		Logger.listenerLogID = Manager.newFile(Logger.LISTENERLOGFILEPATH);
		Logger.responderLogID = Manager.newFile(Logger.RESPONDERLOGFILEPATH);
		Logger.myThread = new Thread(new Logger());
		Logger.runMe = true;
		Logger.myThread.start();
		Logger.console = new Logger();
	}
	
	@Override
	public void run() {
		int size = 0;
		while (Logger.runMe) {
			if (messages.size() > size) {
				for (int i = size; i < messages.size(); i++) {
					System.out.println(messages.get(i).print());
					Logger.consoleText.setEditable(true);
					switch(messages.get(i).type) {
						case Information:
							colorAppend(messages.get(i).print(), Color.WHITE);
						break;
						case Error:
							colorAppend(messages.get(i).print(), Color.RED);
						break;
						case Warning:
							colorAppend(messages.get(i).print(), Color.YELLOW);
						break;
					}
					colorAppend("\n\r", Color.WHITE);
					Logger.consoleText.setEditable(false);
				}
				size = messages.size();
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			
			if (Logger.showGUI) {
				Logger.console.setVisible(true);
			} else {
				Logger.console.setVisible(false);
			}
		}
		Logger.console.dispose();
	}
	
	private static void colorAppend(String text, Color color) {
		
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = Logger.consoleText.getDocument().getLength();
        Logger.consoleText.setCaretPosition(len);
        Logger.consoleText.setCharacterAttributes(aset, false);
        Logger.consoleText.replaceSelection(text);
	}

}
