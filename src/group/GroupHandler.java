package group;

import cc.Pair;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import group.listener.ListenerHandler;
import group.responder.ResponderHandler;
import gui.Logger;
import settings.Setting;

public class GroupHandler {
	
	private static ConcurrentHashMap<String, Pair<ListenerHandler, ResponderHandler>> groups = new ConcurrentHashMap<String, Pair<ListenerHandler, ResponderHandler>>();
	private static ConcurrentHashMap<String, String> ltg = new ConcurrentHashMap<String, String>(); //Converts listener id to group id
	private static ConcurrentHashMap<String, String> rtg = new ConcurrentHashMap<String, String>(); //Converst responder id to group id
	
	private static TimeoutController controllerObj;
	private static Thread controllerThread;
	
	public static void init(Setting handlerMasterSetting) {
		GroupHandler.controllerObj = new TimeoutController();
		GroupHandler.controllerThread = new Thread(GroupHandler.controllerObj);
		GroupHandler.controllerThread.start();
		for (Setting handlerGroup : handlerMasterSetting.getSubsettings()) {
			if (!handlerGroup.isEnabled()) {
				continue;
			}
			String id = handlerGroup.getAttribute("id");
			String name = handlerGroup.getAttribute("name");
			ListenerHandler listenerHandler = new ListenerHandler(handlerGroup.getSettings("Listeners").get(0), id, name);
			ResponderHandler responderHandler = new ResponderHandler(handlerGroup.getSettings("Responders").get(0), id, name);
			listenerHandler.init();
			responderHandler.init();
			groups.put(id, new Pair<ListenerHandler, ResponderHandler>(listenerHandler, responderHandler));
		}
	}
	
	public static String addSocketTimeout(Socket socket, int seconds) {
		return GroupHandler.controllerObj.addSocket(socket, seconds);
	}
	
	public static void close() {
		GroupHandler.controllerObj.stop();
		try {
			GroupHandler.controllerThread.join();
		} catch (InterruptedException e) {
			Logger.reportException(GroupHandler.class.getName(), "close", e);
		}
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
	
	public static void registerListener(String listenerID, String groupID) {
		GroupHandler.ltg.put(listenerID, groupID);
	}
	
	public static void registerResponder(String responderID, String groupID) {
		GroupHandler.rtg.put(responderID, groupID);
	}
	
	public static String ltgID(String listenerID) {
		return GroupHandler.ltg.get(listenerID);
	}
	
	public static String rtgID(String responderID) {
		return GroupHandler.rtg.get(responderID);
	}
}
