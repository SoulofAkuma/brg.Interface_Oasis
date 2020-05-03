package gui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MessageObject {
	
	public final MessageType type;
	public final MessageOrigin origin;
	public final String timePrefix;
	public final String message;
	public final String cause;
	public final ArrayList<String> idTrace;
	
	public MessageObject(MessageType type, MessageOrigin origin, String errorMessage, String cause, ArrayList<String> idTrace) {
		this.type = type;
		this.origin = origin;
		this.message = errorMessage;
		this.cause = cause;
		this.idTrace = idTrace;
		this.timePrefix = "[" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()) + "]";
	}
	
	public String print(boolean printTrace) {
		String message = "";
		String startSpacing = genSpacing(this.timePrefix.length());
		switch (this.type) {
			case Information:
				message += timePrefix + " " + this.origin.name() + ":";
				message += " " + this.message;
			break;
			case Warning:
				message += timePrefix + " WARINING " + this.origin.name() + ":";
				message += " " + this.message + " caused by " + this.cause;
			break;
			case Error:
				message += timePrefix + "Error " + this.origin.name() + ":";
				message += " " + this.message + this.cause;
			break;
		}
		if (printTrace) {
			message += "\r\n" + startSpacing + " Trace: " + String.join(" < ", this.idTrace.toArray(new String[this.idTrace.size()]));
		}
		return message;
	}
	
	private String genSpacing(int count) {
		String spaces = "";
		for (int i = 0; i < count; i++) {
			spaces += " ";
		}
		return spaces;
	}

}
