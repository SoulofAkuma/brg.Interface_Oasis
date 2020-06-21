package constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import gui.ConstantGUIPanel;
import gui.ListElement;
import settings.Setting;
import settings.SettingHandler;

public class ConstantHandler {
	
	private static ConcurrentHashMap<String, Constant> constants; //All constants stored by id constant
	private static Setting constantMasterSetting;
	
	private static final String IDNAME = "id";
	private static final String NAMENAME = "name";
	private static final String ORDERNAME = "order";
	private static final String ISKEYNAME = "isKey";
	private static final String BACKREFERENCENAME = "backReference";
	private static final String USEHEADERNAME = "useHeader";
	private static final String VALUESNAME = "Values";
	private static final String VALUENAME = "Value";
	private static final String SETTINGNAME = "Constant";
	
	public static void init(Setting constantMasterSetting) {
		ConstantHandler.constantMasterSetting = constantMasterSetting;
		ArrayList<Setting> constantSettings = constantMasterSetting.getSettings(ConstantHandler.SETTINGNAME);
		ConcurrentHashMap<String, Constant> constants = new ConcurrentHashMap<String, Constant>();
		for (Setting constantSetting : constantSettings) {
			if (!constantSetting.isEnabled()) {
				continue;
			}
			String id = constantSetting.getAttribute(ConstantHandler.IDNAME);
			String name = constantSetting.getAttribute(ConstantHandler.NAMENAME);
			ArrayList<String> order = (constantSetting.getAttribute(ConstantHandler.ORDERNAME).isBlank()) ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(constantSetting.getAttribute(ConstantHandler.ORDERNAME).split(",")));
			ArrayList<Setting> valueSettings = constantSetting.getSettings(ConstantHandler.VALUESNAME).get(0).getSettings(ConstantHandler.VALUENAME);
			ConcurrentHashMap<String, Value> values = new ConcurrentHashMap<String, Value>();
			for (Setting valueSetting : valueSettings) {
				if (!valueSetting.isEnabled()) {
					continue;
				}
				String vID = valueSetting.getAttribute("id");
				String value = valueSetting.getValue();
				boolean isKey = Boolean.parseBoolean(valueSetting.getAttribute(ConstantHandler.ISKEYNAME));
				boolean useHeader = Boolean.parseBoolean(valueSetting.getAttribute(ConstantHandler.USEHEADERNAME));
				boolean backReference = Boolean.parseBoolean(valueSetting.getAttribute(ConstantHandler.BACKREFERENCENAME));
				values.put(vID, new Value(vID, value, isKey, useHeader, backReference));
			}
			constants.put(id, new Constant(id, name, Collections.synchronizedList(order), values));
		}
		ConstantHandler.constants = constants;
	}
	
	public static String getConstant(String id, HashMap<String, String>  parsedHeader, HashMap<String, String> parsedBody) {
		return ConstantHandler.constants.get(id).getConstant(parsedHeader, parsedBody);
	}

	public static String identification(String id) {
		return ConstantHandler.constants.get(id).identification();
	}

	public static void close() {
		for (Setting constantSetting : ConstantHandler.constantMasterSetting.getSettings(ConstantHandler.SETTINGNAME)) {
			if (!constantSetting.isEnabled()) {
				continue;
			}
			String id = constantSetting.getAttribute(ConstantHandler.IDNAME);
			if (ConstantHandler.constants.containsKey(id)) {
				HashMap<String, String> newAttributes = new HashMap<String, String>();
				Constant constant = ConstantHandler.constants.get(id);
				newAttributes.put(ConstantHandler.IDNAME, id);
				newAttributes.put(ConstantHandler.NAMENAME, constant.getName());
				newAttributes.put(ConstantHandler.ORDERNAME, SettingHandler.alts(constant.getOrder()));
				constantSetting.addReplaceAttributes(newAttributes);
				ConcurrentHashMap<String, Value> values = constant.getValues();
				HashMap<String, Boolean> valueMatches = SettingHandler.getMatchList(values.keySet(), false);
				Setting valuesSetting = constantSetting.getSettings(ConstantHandler.VALUESNAME).get(0);
				for (Setting valueSetting : valuesSetting.getSettings(ConstantHandler.VALUENAME)) {
					if (!valueSetting.isEnabled()) {
						continue;
					}
					String vID = valueSetting.getAttribute(ConstantHandler.IDNAME);
					if (values.containsKey(vID)) {
						HashMap<String, String> newValueAttributes = new HashMap<String, String>();
						Value value = values.get(vID);
						newValueAttributes.put(ConstantHandler.IDNAME, vID);
						newValueAttributes.put(ConstantHandler.ISKEYNAME, String.valueOf(value.isKey()));
						newValueAttributes.put(ConstantHandler.BACKREFERENCENAME, String.valueOf(value.isBackReference()));
						newValueAttributes.put(ConstantHandler.USEHEADERNAME, String.valueOf(value.isUseHeader()));
						valueSetting.addReplaceAttributes(newValueAttributes);
						valueSetting.setValue(value.getValue());
						valueMatches.put(vID, true);
					}
				}
				ArrayList<String> missingValues = SettingHandler.getConditionalList(valueMatches, false);
				for (String valueID : missingValues) {
					Value value = values.get(valueID);
					HashMap<String, String> valueAttributes = new HashMap<String, String>();
					valueAttributes.put(ConstantHandler.IDNAME, valueID);
					valueAttributes.put(ConstantHandler.ISKEYNAME, String.valueOf(value.isKey()));
					valueAttributes.put(ConstantHandler.BACKREFERENCENAME, String.valueOf(value.isBackReference()));
					valueAttributes.put(ConstantHandler.USEHEADERNAME, String.valueOf(value.isUseHeader()));
					String valueValue = value.getValue();
					valuesSetting.addSetting(ConstantHandler.VALUENAME, valueValue, valueAttributes);
				}
			}
		}
	}
	
	public static void addConstant(Constant constant) {
		ConstantHandler.constants.put(constant.getId(), constant);
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put(ConstantHandler.IDNAME, constant.getId());
		attributes.put(ConstantHandler.NAMENAME, constant.getName());
		attributes.put(ConstantHandler.ORDERNAME, SettingHandler.alts(constant.getOrder()));
		Setting constantSetting = ConstantHandler.constantMasterSetting.addSetting(ConstantHandler.SETTINGNAME, null, attributes);
		ConcurrentHashMap<String, Value> values = constant.getValues();
		Setting valuesSetting = constantSetting.addSetting(ConstantHandler.VALUESNAME, null, null);
		for (Entry<String, Value> value : values.entrySet()) {
			HashMap<String, String> valueAttributes = new HashMap<String, String>();
			valueAttributes.put(ConstantHandler.IDNAME, value.getKey());
			valueAttributes.put(ConstantHandler.ISKEYNAME, String.valueOf(value.getValue().isKey()));
			valueAttributes.put(ConstantHandler.BACKREFERENCENAME, String.valueOf(value.getValue().isBackReference()));
			valueAttributes.put(ConstantHandler.USEHEADERNAME, String.valueOf(value.getValue().isUseHeader()));
			String valueValue = value.getValue().getValue();
			valuesSetting.addSetting(ConstantHandler.VALUENAME, valueValue, valueAttributes);
		}
	}
	
	public static Constant getConstant(String id) {
		return (ConstantHandler.constants.containsKey(id)) ? ConstantHandler.constants.get(id) : null;
	}
	
	public static void removeValue(String constantID, String valueID) {
		SettingHandler.removeElement(constantID, valueID, ConstantHandler.IDNAME, ConstantHandler.SETTINGNAME, ConstantHandler.VALUESNAME, ConstantHandler.VALUENAME, ConstantHandler.constantMasterSetting);
	}
	
	public static void removeConstant(String id) {
		if (SettingHandler.removeParent(id, ConstantHandler.IDNAME, ConstantHandler.SETTINGNAME, ConstantHandler.constantMasterSetting)) {
			ConstantHandler.constants.remove(id);
		}
	}
	
	public static ListElement[] getConstantElements() {
		ArrayList<ListElement> elements = new ArrayList<ListElement>();
		for (Entry<String, Constant> constant : ConstantHandler.constants.entrySet()) {
			elements.add(new ListElement(constant.getKey(), constant.getValue().getName()));
		}
		return elements.toArray(new ListElement[elements.size()]);
	}
	
	public static HashMap<String, String> getConstantNames() {
		HashMap<String, String> constants = new HashMap<String, String>();
		for (Entry<String, Constant> constant : ConstantHandler.constants.entrySet()) {
			constants.put(constant.getKey(), constant.getValue().getName());
		}
		return constants;
	}

	public static List<ConstantGUIPanel> getConstantPanels() {
		ArrayList<ConstantGUIPanel> panels = new ArrayList<ConstantGUIPanel>();
		for (Entry<String, Constant> constant : ConstantHandler.constants.entrySet()) {
			ConstantGUIPanel panel = new ConstantGUIPanel();
			panel.init(constant.getValue());
			panels.add(panel);
		}
		return panels;
	}
	
	public static String getRConstant() {
		for (Entry<String, Constant> entry : ConstantHandler.constants.entrySet()) {
			return entry.getKey();
		}
		return null;
	}
}
