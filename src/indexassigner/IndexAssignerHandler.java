package indexassigner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import settings.Setting;

public class IndexAssignerHandler {
	
	private static ConcurrentHashMap<String, IndexAssigner> indexAssigners = new ConcurrentHashMap<String, IndexAssigner>();
	private static Setting indexAssingerMastserSetting;
	
	private static final String IDNAME = "id";
	private static final String NAMENAME = "name";
	private static final String RMMATCHNAME = "rmMatch";
	
	private static final String INDEXESNAME = "Indexes";
	private static final String INDEXNAME = "Index";
	private static final String SETTINGNAME = "IndexAssigner";
	
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
		for (Setting indexAssignerSetting : IndexAssignerHandler.indexAssingerMastserSetting.getSettings(IndexAssignerHandler.SETTINGNAME)) {
			String id = indexAssignerSetting.getAttribute(IndexAssignerHandler.IDNAME);
			if (IndexAssignerHandler.indexAssigners.containsKey(id)) {
				HashMap<String, String> attriubtes = new HashMap<String, String>();
			}
		}
	}

}
