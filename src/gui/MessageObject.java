package gui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageObject {
	
	public final MessageType type;
	public final MessageOrigin origin;
	public final String time;
	public final String message;
	public final String id;
	
	public MessageObject(MessageType type, MessageOrigin origin, String message, String id) {
		this.type = type;
		this.origin = origin;
		this.message = message;
		this.id = id;
		this.time = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
	}
	
	public String print() {
		String message = "";
		String timePrefix = "[" + this.time + "]";
		switch (this.type) {
			case Information:
				message += timePrefix + " " + this.origin.name() + " " + this.id +": " + this.message;
			break;
			case Warning:
				message += timePrefix + " WARINING " + this.origin.name() + " " + this.id + ": " + this.message;
			break;
			case Error:
				message += timePrefix + "Error " + this.origin.name() + " " + this.id + ": " + this.message;
			break;
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
