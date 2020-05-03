package gui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import cc.Pair;

public class Logger {
	
	private static ArrayList<MessageObject> messages = new ArrayList<MessageObject>(); //Messages to be viewable in log
	
	public static void reportMessage(String groupID, String groupName, String source, String cause, String errorMessage, MessageType type, MessageOrigin origin) {
		if (type == MessageType.Warning) {
			messages.add(new Pair<MessageType, String>(MessageType.Warning, "[" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()) + "] WARNING Group " + groupID + " \"" + String.valueOf(groupName) + "\" reported a warning from \"" + String.valueOf(source) + "\" caused by \"" + String.valueOf(cause) + "\" with the error message \"" + errorMessage + "\""));
		} else if (type == MessageType.Error) {			
			messages.add(new Pair<MessageType, String>(MessageType.Error, "[" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()) + "] Group " + groupID + " \"" + String.valueOf(groupName) + "\" reported an error from \"" + String.valueOf(source) + "\" caused by \"" + String.valueOf(cause) + "\" with the error message \"" + errorMessage + "\""));
			xmlLog(new String[] {"GroupID","GroupName,Source,Cause","ErrorMessage,Time"}, new String[] {String.valueOf(groupID), groupName, source, cause, errorMessage, DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now())});			
		} else if (type == MessageType.Information) {
			messages.add(new Pair<Pair<MessageOrigin, MessageType>, String>())
		}
	}

	public static void reportMessage(String groupName, String source, String cause, boolean isWarning) {
		if (isWarning) {
			messages.add(new Pair<MessageType, String>(MessageType.Warning, "[" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()) + "] WARNING Group \"" + String.valueOf(groupName) + "\" reported a warning from \"" + String.valueOf(source) + "\" caused by \"" + String.valueOf(cause) + "\""));
		} else {			
			messages.add(new Pair<MessageType, String>(MessageType.Error, "[" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()) + "] Group \"" + String.valueOf(groupName) + "\" reported an error from \"" + String.valueOf(source) + "\" caused by \"" + String.valueOf(cause) + "\""));
			xmlLog(new String[] {"GroupName,Source,Cause,Time"}, new String[] {groupName, source, cause, DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now())});			
		}
	}
	
	public static void reportMessage(String groupID, String groupName, String source, String cause, boolean isWarning) {
		if (isWarning) {
			messages.add(new Pair<MessageType, String>(MessageType.Warning, "[" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()) + "] WARNING Group " + groupID + " \"" + String.valueOf(groupName) + "\" reported a warning from \"" + String.valueOf(source) + "\" caused by \"" + String.valueOf(cause) + "\""));
		} else {			
			messages.add(new Pair<MessageType, String>(MessageType.Error, "[" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()) + "] Group " + groupID + " \"" + String.valueOf(groupName) + "\" reported an error from \"" + String.valueOf(source) + "\" caused by \"" + String.valueOf(cause) + "\""));
			xmlLog(new String[] {"GroupID","GroupName","Source","Cause,Time"}, new String[] {String.valueOf(groupID), groupName, source, cause, DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now())});			
		}
	}

	public static void xmlLog(String[] elements, String[] values) {
		String errorString = "<Error>";
		for (int i = 0; i < elements.length; i++) {
			errorString += "\r\n\t<" + elements[i] + ">" + values[i] + "</" + elements[i] + ">";
		}
		errorString += "\r\n</Error>";
		xmlErrors.add(errorString);
	}

}
