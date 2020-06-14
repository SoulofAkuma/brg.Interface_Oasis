package indexassigner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import settings.Setting;

public class IndexAssignerHandler {
	
	private static ConcurrentHashMap<String, IndexAssigner> indexAssigners = new ConcurrentHashMap<String, IndexAssigner>();
	private static Setting indexAssingerMastserSetting;
	
	public static void init(Setting indexAssignerMasterSetting) {
		IndexAssignerHandler.indexAssingerMastserSetting = indexAssignerMasterSetting;
		for (Setting indexAssignerSetting : indexAssignerMasterSetting.getSubsettings()) {
			if (!indexAssignerSetting.isEnabled()) {
				continue;
			}
			String name = indexAssignerSetting.getAttribute("name");
			String id = indexAssignerSetting.getAttribute("id");
			boolean rmMatch = Boolean.parseBoolean(indexAssignerSetting.getAttribute("rmMatch"));
			Setting indexesSetting = indexAssignerSetting.getSettings("Indexes").get(0);
			ConcurrentHashMap<Integer, String> indexes = new ConcurrentHashMap<Integer, String>();
			if (indexesSetting.hasSubsettings()) {
				for (Setting indexSetting : indexesSetting.getSubsettings()) {
					if (!indexSetting.isEnabled()) {
						continue;
					}
					indexes.put(Integer.parseInt(indexSetting.getAttribute("position")), indexSetting.getAttribute("key"));
				}
			}
			Setting regexesSetting = indexAssignerSetting.getSettings("Regexes").get(0);
			ConcurrentHashMap<String, String[]> regexes = new ConcurrentHashMap<String, String[]>();
			ConcurrentHashMap<String, Integer> defInd = new ConcurrentHashMap<String, Integer>();
			if (regexesSetting.hasSubsettings()) {
				for (Setting regexSetting : regexesSetting.getSubsettings()) {
					if (!regexSetting.isEnabled()) {
						continue;
					}
					regexes.put(regexSetting.getAttribute("regex"), regexSetting.getAttribute("keys" ).split(","));
					defInd.put(regexSetting.getAttribute("regex"), Integer.parseInt(regexSetting.getAttribute("defInd")));
				}
			}
			IndexAssignerHandler.indexAssigners.put(id, new IndexAssigner(id, indexes, regexes, defInd, rmMatch));
		}
	}
	
	public static HashMap<String, String> assign(String indexAssignerID, ArrayList<String> input) {
		return IndexAssignerHandler.indexAssigners.get(indexAssignerID).assign(input);
	}

	public static void close() {
		
	}

}
