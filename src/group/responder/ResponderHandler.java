package group.responder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import constant.Constant;
import constant.ConstantHandler;
import group.GroupHandler;
import gui.ListElement;
import gui.ResponderGUIPanel;
import settings.Setting;
import settings.SettingHandler;
import trigger.TriggerHandler;

public class ResponderHandler {

	private Setting responderMasterSetting;
	private String groupID;
	private String groupName;
	private ConcurrentHashMap<String, Responder> responders = new ConcurrentHashMap<String, Responder>();
	
	private static final String LOGNAME = "log";
	private static final String IDNAME = "id";
	private static final String NAMENAME = "name";
	private static final String URLNAME = "url";
	private static final String USERAGENTNAME = "userAgent";
	private static final String CONTENTTYPENAME = "contentType";
	private static final String REQUESTTYPENAME = "requestType";
	private static final String CONSTANTSNAME = "constants";
	private static final String CUSTOMARGSNAME = "customArgs";
	private static final String SEPARATORNAME = "separator";
	private static final String HEADERNAME = "Header";
	private static final String BODYNAME = "Body";
	private static final String SETTINGNAME = "Responder";
	
	public ResponderHandler(Setting responderMasterSetting, String groupID, String groupName) {
		this.responderMasterSetting = responderMasterSetting;
		this.groupID = groupID;
		this.groupName = groupName;
	}
	
	public void init() {
		for (Setting responder : this.responderMasterSetting.getSettings(ResponderHandler.SETTINGNAME)) {
			if (!responder.isEnabled()) {
				continue;
			}
			String name = responder.getAttribute(ResponderHandler.NAMENAME);
			String id = responder.getAttribute(ResponderHandler.IDNAME);
			boolean log = Boolean.parseBoolean(responder.getAttribute(ResponderHandler.LOGNAME));
			Setting headerSetting = responder.getSettings(ResponderHandler.HEADERNAME).get(0);
			Setting bodySetting = responder.getSettings(ResponderHandler.BODYNAME).get(0);
			String url = headerSetting.getAttribute(ResponderHandler.URLNAME);
			String requestType = headerSetting.getAttribute(ResponderHandler.REQUESTTYPENAME);
			String userAgent = headerSetting.getAttribute(ResponderHandler.USERAGENTNAME);
			String contentType = headerSetting.getAttribute(ResponderHandler.CONTENTTYPENAME);
			ArrayList<String> customArgs = (headerSetting.hasAttribute(ResponderHandler.CUSTOMARGSNAME) && !headerSetting.getAttribute(ResponderHandler.CUSTOMARGSNAME).isBlank()) ? new ArrayList<String>(Arrays.asList(headerSetting.getAttribute(ResponderHandler.CUSTOMARGSNAME).split(","))) : new ArrayList<String>();
			ArrayList<String> constants = (bodySetting.getAttribute(ResponderHandler.CONSTANTSNAME).isBlank()) ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(bodySetting.getAttribute(ResponderHandler.CONSTANTSNAME).split(",")));
			String separator = bodySetting.getAttribute(ResponderHandler.SEPARATORNAME);
			Header header = new Header(requestType, url, contentType, userAgent, Collections.synchronizedList(customArgs), id, name);
			Body body = new Body(Collections.synchronizedList(constants), separator);
			this.responders.put(id, new Responder(id, name, log, this.groupID, this.groupName, header, body));
			TriggerHandler.registerResponder(id);
			GroupHandler.registerResponder(id, this.groupID);
		}
	}
	
	public void respond(String id, HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody) {
		this.responders.get(id).respond(parsedHeader, parsedBody);
	}
	
	public void close() {
		for (Setting responderSetting : this.responderMasterSetting.getSettings(ResponderHandler.SETTINGNAME)) {
			if (!responderSetting.isEnabled()) {
				continue;
			}
			String id = responderSetting.getAttribute(ResponderHandler.IDNAME);
			if (this.responders.containsKey(id)) {
				HashMap<String, String> newAttributes = new HashMap<String, String>();
				Responder responder = this.responders.get(id);
				newAttributes.put(ResponderHandler.IDNAME, id);
				newAttributes.put(ResponderHandler.NAMENAME, responder.getName());
				newAttributes.put(ResponderHandler.LOGNAME, String.valueOf(responder.getLog()));
				responderSetting.addReplaceAttributes(newAttributes);
				Setting headerSetting = responderSetting.getSettings(ResponderHandler.HEADERNAME).get(0);
				Header header = responder.getHeader();
				HashMap<String, String> newHeaderAttributes = new HashMap<String, String>();
				newHeaderAttributes.put(ResponderHandler.URLNAME, header.getUrl());
				newHeaderAttributes.put(ResponderHandler.REQUESTTYPENAME, header.getRequestType());
				newHeaderAttributes.put(ResponderHandler.USERAGENTNAME, header.getUserAgent());
				newHeaderAttributes.put(ResponderHandler.CONTENTTYPENAME, header.getContentType());
				newHeaderAttributes.put(ResponderHandler.CUSTOMARGSNAME, SettingHandler.alts(header.getCustomArgs()));
				headerSetting.addReplaceAttributes(newHeaderAttributes);
				Setting bodySetting = responderSetting.getSettings(ResponderHandler.BODYNAME).get(0);
				Body body = responder.getBody();
				HashMap<String, String> newBodyAttributes = new HashMap<String, String>();
				newBodyAttributes.put(ResponderHandler.SEPARATORNAME, body.getSeparator());
				newBodyAttributes.put(ResponderHandler.CONSTANTSNAME, SettingHandler.alts(body.getContent()));
				bodySetting.addReplaceAttributes(newBodyAttributes);
			}
		}
	}
	
	public void addResponder(Responder responder) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put(ResponderHandler.IDNAME, responder.getResponderID());
		attributes.put(ResponderHandler.NAMENAME, responder.getName());
		attributes.put(ResponderHandler.LOGNAME, String.valueOf(responder.getLog()));
		Header header = responder.getHeader();
		HashMap<String, String> headerAttributes = new HashMap<String, String>();
		headerAttributes.put(ResponderHandler.URLNAME, header.getUrl());
		headerAttributes.put(ResponderHandler.REQUESTTYPENAME, header.getRequestType());
		headerAttributes.put(ResponderHandler.USERAGENTNAME, header.getUserAgent());
		headerAttributes.put(ResponderHandler.CONTENTTYPENAME, header.getContentType());
		headerAttributes.put(ResponderHandler.CUSTOMARGSNAME, SettingHandler.alts(header.getCustomArgs()));
		Body body = responder.getBody();
		HashMap<String, String> bodyAttributes = new HashMap<String, String>();
		bodyAttributes.put(ResponderHandler.SEPARATORNAME, body.getSeparator());
		bodyAttributes.put(ResponderHandler.CONSTANTSNAME, SettingHandler.alts(body.getContent()));
		Setting newRes = this.responderMasterSetting.addSetting(ResponderHandler.SETTINGNAME, null, attributes);
		newRes.addSetting(ResponderHandler.HEADERNAME, null, headerAttributes);
		newRes.addSetting(ResponderHandler.BODYNAME, null, bodyAttributes);
		this.responders.put(responder.getResponderID(), responder);
	}
	
	public void removeResponder(String id) {
		int sIDmatch = -1;
		for (Setting responderSetting : this.responderMasterSetting.getSettings(ResponderHandler.SETTINGNAME)) {
			if (responderSetting.isEnabled() && responderSetting.getAttribute(ResponderHandler.IDNAME).equals(id)) {
				sIDmatch = responderSetting.getSID();
				break;
			}
		}
		if (sIDmatch != -1) {
			this.responders.remove(id);
			this.responderMasterSetting.removeSetting(sIDmatch);
			TriggerHandler.removeResponderReferences(id);
		}
	}
	
	public Responder genResponder(String responderID, String name, boolean log, Header header, Body body) {
		return new Responder(responderID, name, log, this.groupID, this.groupName, header, body);
	}
	
	public String getGroupName() {
		return this.groupName;
	}
	
	public Responder getResponder(String id) {
		return (this.responders.containsKey(id)) ? this.responders.get(id) : null;
	}

	public ArrayList<ListElement> getResponderElements() {
		ArrayList<ListElement> elements = new ArrayList<ListElement>();
		for (Entry<String, Responder> kvp : this.responders.entrySet()) {
			elements.add(new ListElement(kvp.getKey(), kvp.getValue().getName()));
		}
		return elements;
	}
	
	public HashMap<String, String> getResponderNames() {
		HashMap<String, String> responders = new HashMap<String, String>();
		for (Entry<String, Responder> responder : this.responders.entrySet()) {
			responders.put(responder.getKey(), responder.getValue().getName());
		}
		return responders;
	}
	
	public ArrayList<ListElement> getResponderElementsDetail() {
		ArrayList<ListElement> elements = new ArrayList<ListElement>();
		for (Entry<String, Responder> kvp : this.responders.entrySet()) {
			String bodyConst = "";
			for (String constant : kvp.getValue().getBody().getContent()) {
				bodyConst += ConstantHandler.getConstantNames().get(constant) + ", ";
			}
			bodyConst = (bodyConst.length() > 0) ? bodyConst.substring(0, bodyConst.length() - 1) : "";
			elements.add(new ListElement(kvp.getKey(), kvp.getValue().getName() + " - " + kvp.getValue().getHeader().getRequestType() + ", " + ConstantHandler.getConstantNames().get(kvp.getValue().getHeader().getUrl()) + "; " + bodyConst));
		}
		return elements;
	}

	public String getGroupID() {
		return this.groupID;
	}

	public List<ResponderGUIPanel> getResponderPanels() {
		ArrayList<ResponderGUIPanel> panels = new ArrayList<ResponderGUIPanel>();
		for (Entry<String, Responder> responder : this.responders.entrySet()) {
			ResponderGUIPanel panel = new ResponderGUIPanel();
			panel.init(responder.getValue());
			panels.add(panel);
		}
		return panels;
	}
}
