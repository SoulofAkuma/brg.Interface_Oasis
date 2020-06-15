package constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import settings.Setting;
import settings.SettingHandler;

public class ConstantHandler {
	
	private static ConcurrentHashMap<String, Constant> constants; //All constants stored by id constant
	private static Setting constantMasterSetting;
	
	private static final String IDNAME = "id";
	private static final String NAMENAME = "name";
	private static final String ORDERNAME = "order";
	private static final String ISKEYNAME = "isKey";
	private static final String BACKREFERENCENAME = "backRefernce";
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
			ArrayList<String> order = new ArrayList<String>(Arrays.asList(constantSetting.getAttribute(ConstantHandler.ORDERNAME).split(",")));
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
			constants.put(id, new Constant(id, name, order, values));
		}
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
				Setting valuesSetting = constantSetting.getSettings(ConstantHandler.VALUESNAME).get(0);
				for (Setting valueSetting : valuesSetting.getSettings(ConstantHandler.VALUENAME)) {
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
					}
				}
			}
		}
	}
	
	public static void addConstant(Constant constant) {
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
}
