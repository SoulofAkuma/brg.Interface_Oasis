package group.responder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import constant.Constant;
import settings.Setting;

public class ResponderHandler {

	private Setting responderMasterSetting;
	private String groupID;
	private String groupName;
	private HashMap<String, Responder> responders = new HashMap<String, Responder>();
	private HashMap<String, String> idToName = new HashMap<String, String>();
	
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
			String responderName = responder.getAttribute("name");
			String responderID = responder.getAttribute("id");
			String portString = responder.getAttribute("port");
			String parserID = responder.getAttribute("parserID");
			Constant url;
			ArrayList<Constant> constants = new ArrayList<Constant>();
			this.responders.put(responderID, new Responder(responderID));
			this.idToName.put(responderID, responderName);
		}
	}
	
	public void stopResponder() {
	}
	
	public void respond(String id, HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody) {
		this.responders.get(id).respond(parsedHeader, parsedBody);
	}
}
