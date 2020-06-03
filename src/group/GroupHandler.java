package group;

import cc.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import group.listener.ListenerHandler;
import group.responder.ResponderHandler;
import settings.Setting;

public class GroupHandler {
	
	private static ConcurrentHashMap<String, Pair<ListenerHandler, ResponderHandler>> groups = new ConcurrentHashMap<String, Pair<ListenerHandler, ResponderHandler>>();
	
	public static void init(Setting handlerMasterSetting) {
		for (Setting handlerGroup : handlerMasterSetting.getSubsettings()) {
			String id = handlerGroup.getAttribute("id");
			String name = handlerGroup.getAttribute("name");
			ListenerHandler listenerHandler = new ListenerHandler(handlerGroup.getSettings("Listener").get(0), id, name);
			ResponderHandler responderHandler = new ResponderHandler(handlerGroup.getSettings("Responder").get(0), id, name);
			listenerHandler.init();
			responderHandler.init();
			groups.put(id, new Pair<ListenerHandler, ResponderHandler>(listenerHandler, responderHandler));
		}
	}
	
	public static void close() {
		for (Map.Entry<String, Pair<ListenerHandler, ResponderHandler>> kvp : groups.entrySet()) {
			kvp.getValue().getKey().stopListener();
			kvp.getValue().getValue().stopResponder();
		}
	}
	
	public static Pair<ListenerHandler, ResponderHandler> getGroup(String key) {
		return groups.get(key);
	}
	
	public static ListenerHandler getListenerHandler(String groupID) {
		return groups.get(groupID).getKey();
	}
	
	public static ResponderHandler getResponderHandler(String groupID) {
		return groups.get(groupID).getValue();
	}
}
