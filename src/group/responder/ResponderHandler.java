package group.responder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import constant.Constant;
import group.GroupHandler;
import settings.Setting;
import trigger.TriggerHandler;

public class ResponderHandler {

	private Setting responderMasterSetting;
	private String groupID;
	private String groupName;
	private ConcurrentHashMap<String, Responder> responders = new ConcurrentHashMap<String, Responder>();
	
	public ResponderHandler(Setting responderMasterSetting, String groupID, String groupName) {
		this.responderMasterSetting = responderMasterSetting;
		this.groupID = groupID;
		this.groupName = groupName;
	}
	
	public void init() {
		for (Setting responder : this.responderMasterSetting.getSubsettings()) {
			if (!responder.isEnabled()) {
				continue;
			}
			String name = responder.getAttribute("name");
			String id = responder.getAttribute("id");
			boolean log = Boolean.parseBoolean(responder.getAttribute("log"));
			Setting headerSetting = responder.getSettings("Header").get(0);
			Setting bodySetting = responder.getSettings("Body").get(0);
			String url = headerSetting.getAttribute("url");
			String requestType = headerSetting.getAttribute("requestType");
			String userAgent = headerSetting.getAttribute("userAgent");
			String contentType = headerSetting.getAttribute("contentType");
			ArrayList<String> customArgs = (headerSetting.hasAttribute("customArgs")) ? new ArrayList<String>(Arrays.asList(headerSetting.getAttribute("customArgs").split(","))) : new ArrayList<String>();
			ArrayList<String> constants = new ArrayList<String>(Arrays.asList(bodySetting.getAttribute("constants").split(",")));
			String seperator = bodySetting.getAttribute("seperator");
			Header header = new Header(requestType, url, contentType, userAgent, customArgs, id, name);
			Body body = new Body(constants, seperator);
			this.responders.put(id, new Responder(id, name, log, this.groupID, this.groupName, header, body));
			TriggerHandler.registerResponder(id);
			GroupHandler.registerResponder(id, this.groupID);
		}
	}
	
	public void stopResponder() {
	}
	
	public void respond(String id, HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody) {
		this.responders.get(id).respond(parsedHeader, parsedBody);
	}
}
