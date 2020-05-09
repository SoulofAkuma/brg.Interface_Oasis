package trigger;

import java.util.ArrayList;
import java.util.HashMap;

import group.listener.Listener;
import group.responder.Responder;
import settings.Setting;

public class TriggerHandler {
	
	private static ArrayList<Trigger> triggers = new ArrayList<Trigger>(); //List of all the triggers which will be swept through if their group is active
	private static HashMap<String, Responder> responders = new HashMap<String, Responder>();
	private static HashMap<String, Listener> listeners = new HashMap<String, Listener>();
	
	public static void init(Setting triggerMasterSetting) {
		for (Setting trigger : triggerMasterSetting.getSubsettings()) {
			
		}
	}
	
	protected static Responder getResponder(String id) {
		return TriggerHandler.responders.get(id);
	}
}
