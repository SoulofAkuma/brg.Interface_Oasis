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
			String responderName = responder.getAttribute("name").getValue();
			String responderID = responder.getAttribute("id").getValue();
			String portString = responder.getAttribute("port").getValue();
			String parserID = responder.getAttribute("parserID").getValue();
			Constant url;
			ArrayList<Constant> constants = new ArrayList<Constant>();
			for (Setting constantSetting : responder.getSettings("Constants").get(0).getSettings("Constant")) {
				if (constantSetting.getName().equals("Url")) {
					url = parseConstant(constantSetting);
				}
				constants.add(parseConstant(constantSetting));
			}
			this.responders.put(responderID, new Responder(responderID, parserID, constants, portString, url, responderName, parserID));
			this.idToName.put(responderID, responderName);
		}
	}
	
	private Constant parseConstant(Setting constant) {
		//Acquire information by tags and NOT by attributes. Tags will be named "Value" and "DynamicValue"
		boolean useHeader = Boolean.parseBoolean(constant.getAttribute("useHeader").getValue());
		String name = constant.getAttribute("name").getValue();
		String id = constant.getAttribute("id").getValue();
		return new Constant();
	}
	
	public void stopResponder() {
	}
}
