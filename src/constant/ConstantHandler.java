package constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import settings.Setting;

public class ConstantHandler {
	
	private ConcurrentHashMap<String, Constant> constants;
	
	public static void init(Setting constantMasterSetting) {
		ArrayList<Setting> constantSettings = constantMasterSetting.getSettings("Constant");
		ConcurrentHashMap<String, Constant> constants = new ConcurrentHashMap<String, Constant>();
		for (Setting constantSetting : constantSettings) {
			String id = constantSetting.getAttribute("id");
			String name = constantSetting.getAttribute("name");
			ArrayList<String> order = new ArrayList<String>(Arrays.asList(constantSetting.getAttribute("order").split(",")));
			ArrayList<Setting> valueSettings = constantSetting.getSettings("Values").get(0).getSettings("Value");
			ConcurrentHashMap<String, Value> values = new ConcurrentHashMap<String, Value>();
			for (Setting valueSetting : valueSettings) {
				String vID = valueSetting.getAttribute("id");
				String value = valueSetting.getValue();
				boolean isDynamic = Boolean.parseBoolean(valueSetting.getAttribute("isDynamic"));
				boolean useHeader = Boolean.parseBoolean(valueSetting.getAttribute("useHeader"));
				values.put(vID, new Value(vID, value, isDynamic, useHeader));
			}
			constants.put(id, new Constant(id, name, order, values));
		}
	}

}
