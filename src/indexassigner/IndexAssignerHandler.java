package indexassigner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cc.Pair;
import gui.IndexAssignerGUIPanel;
import settings.Setting;
import settings.SettingHandler;

public class IndexAssignerHandler {
	
	private static ConcurrentHashMap<String, IndexAssigner> indexAssigners = new ConcurrentHashMap<String, IndexAssigner>();
	private static Setting indexAssingerMastserSetting;
	
	private static final String IDNAME = "id";
	private static final String NAMENAME = "name";
	private static final String RMMATCHNAME = "rmMatch";
	private static final String POSITIONNAME = "position";
	private static final String KEYNAME = "key";
	private static final String REGEXNAME = "regex";
	private static final String KEYSNAME = "keys";
	private static final String DEFINDNAME = "defInd";
	private static final String IORDERNAME = "iorder";
	private static final String RORDERNAME = "rorder";
	
	private static final String INDEXESNAME = "Indexes";
	private static final String INDEXNAME = "Index";
	private static final String REGEXESNAME = "Regexes";
	private static final String SREGEXNAME = "Regex";
	private static final String SETTINGNAME = "IndexAssigner";
	
	public static void init(Setting indexAssignerMasterSetting) {
		IndexAssignerHandler.indexAssingerMastserSetting = indexAssignerMasterSetting;
		for (Setting indexAssignerSetting : indexAssignerMasterSetting.getSubsettings()) {
			if (!indexAssignerSetting.isEnabled()) {
				continue;
			}
			String name = indexAssignerSetting.getAttribute(IndexAssignerHandler.NAMENAME);
			String id = indexAssignerSetting.getAttribute(IndexAssignerHandler.IDNAME);
			ArrayList<String> iorder = (indexAssignerSetting.getAttribute(IndexAssignerHandler.IORDERNAME).isBlank()) ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(indexAssignerSetting.getAttribute(IndexAssignerHandler.IORDERNAME).split(",")));
			ArrayList<String> rorder = (indexAssignerSetting.getAttribute(IndexAssignerHandler.RORDERNAME).isBlank()) ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(indexAssignerSetting.getAttribute(IndexAssignerHandler.RORDERNAME).split(",")));
			boolean rmMatch = Boolean.parseBoolean(indexAssignerSetting.getAttribute(IndexAssignerHandler.RMMATCHNAME));
			Setting indexesSetting = indexAssignerSetting.getSettings(IndexAssignerHandler.INDEXESNAME).get(0);
			ConcurrentHashMap<String, Pair<Integer, String>> indexes = new ConcurrentHashMap<String, Pair<Integer, String>>();
			if (indexesSetting.hasSubsettings()) {
				for (Setting indexSetting : indexesSetting.getSettings(IndexAssignerHandler.INDEXNAME)) {
					if (!indexSetting.isEnabled()) {
						continue;
					}
					indexes.put(indexSetting.getAttribute(IndexAssignerHandler.IDNAME), new Pair<Integer, String>(Integer.parseInt(indexSetting.getAttribute(IndexAssignerHandler.POSITIONNAME)), indexSetting.getAttribute(IndexAssignerHandler.KEYNAME)));
				}
			}
			Setting regexesSetting = indexAssignerSetting.getSettings(IndexAssignerHandler.REGEXESNAME).get(0);
			ConcurrentHashMap<String, Pair<String, String[]>> regexes = new ConcurrentHashMap<String, Pair<String, String[]>>();
			ConcurrentHashMap<String, Integer> defInd = new ConcurrentHashMap<String, Integer>();
			if (regexesSetting.hasSubsettings()) {
				for (Setting regexSetting : regexesSetting.getSettings(IndexAssignerHandler.SREGEXNAME)) {
					if (!regexSetting.isEnabled()) {
						continue;
					}
					String[] keys = (regexSetting.getAttribute(IndexAssignerHandler.KEYSNAME).isBlank()) ? new String[] {} : regexSetting.getAttribute(IndexAssignerHandler.KEYSNAME).split(",");
					regexes.put(regexSetting.getAttribute(IndexAssignerHandler.IDNAME), new Pair<String, String[]>(regexSetting.getAttribute(IndexAssignerHandler.REGEXNAME), keys));
					defInd.put(regexSetting.getAttribute(IndexAssignerHandler.IDNAME), Integer.parseInt(regexSetting.getAttribute(IndexAssignerHandler.DEFINDNAME)));
				}
			}
			IndexAssignerHandler.indexAssigners.put(id, new IndexAssigner(id, name, indexes, regexes, defInd, rmMatch, Collections.synchronizedList(iorder), Collections.synchronizedList(rorder)));
		}
	}
	
	public static HashMap<String, String> assign(String indexAssignerID, ArrayList<String> input) {
		return IndexAssignerHandler.indexAssigners.get(indexAssignerID).assign(input);
	}

	public static void close() {
		for (Setting indexAssignerSetting : IndexAssignerHandler.indexAssingerMastserSetting.getSettings(IndexAssignerHandler.SETTINGNAME)) {
			if (!indexAssignerSetting.isEnabled()) {
				continue;
			}
			String id = indexAssignerSetting.getAttribute(IndexAssignerHandler.IDNAME);
			if (IndexAssignerHandler.indexAssigners.containsKey(id)) {
				HashMap<String, String> newAttributes = new HashMap<String, String>();
				IndexAssigner indexAssigner = IndexAssignerHandler.indexAssigners.get(id);
				newAttributes.put(IndexAssignerHandler.IDNAME, id);
				newAttributes.put(IndexAssignerHandler.NAMENAME, indexAssigner.getName());
				newAttributes.put(IndexAssignerHandler.RMMATCHNAME, String.valueOf(indexAssigner.getRmMatch()));
				newAttributes.put(IndexAssignerHandler.IORDERNAME, SettingHandler.alts(indexAssigner.getIorder()));
				newAttributes.put(IndexAssignerHandler.RORDERNAME, SettingHandler.alts(indexAssigner.getRorder()));
				indexAssignerSetting.addReplaceAttributes(newAttributes);
				Setting indexesSetting = indexAssignerSetting.getSettings(IndexAssignerHandler.INDEXESNAME).get(0);
				Setting regexesSetting = indexAssignerSetting.getSettings(IndexAssignerHandler.REGEXESNAME).get(0);
				ConcurrentHashMap<String, Pair<Integer, String>> indexes = indexAssigner.getIndexes();
				ConcurrentHashMap<String, Pair<String, String[]>> regexes = indexAssigner.getRegexes();
				HashMap<String, Boolean> indexesMatches = SettingHandler.getMatchList(indexes.keySet(), false);
				HashMap<String, Boolean> regexesMatches = SettingHandler.getMatchList(regexes.keySet(), false);
				ConcurrentHashMap<String, Integer> defInds = indexAssigner.getDefInd();
				for (Setting indexSetting : indexesSetting.getSettings(IndexAssignerHandler.INDEXNAME)) {
					if (!indexSetting.isEnabled()) {
						continue;
					}
					String indexID = indexSetting.getAttribute(IndexAssignerHandler.IDNAME);
					if (indexes.containsKey(indexID)) {
						HashMap<String, String> newIndexAttributes = new HashMap<String, String>();
						Pair<Integer, String> index = indexes.get(indexID);
						newIndexAttributes.put(IndexAssignerHandler.POSITIONNAME, String.valueOf(index.getKey()));
						newIndexAttributes.put(IndexAssignerHandler.KEYNAME, index.getValue());
						indexSetting.addReplaceAttributes(newIndexAttributes);
						indexesMatches.put(indexID, true);
					}
				}
				for (Setting regexSetting : regexesSetting.getSettings(IndexAssignerHandler.SREGEXNAME)) {
					if (!regexSetting.isEnabled()) {
						continue;
					}
					String regexID = regexSetting.getAttribute(IndexAssignerHandler.IDNAME);
					if (regexes.containsKey(regexID) && defInds.containsKey(regexID)) {
						HashMap<String, String> newRegexAttributes = new HashMap<String, String>();
						Pair<String, String[]> regex = regexes.get(regexID);
						newRegexAttributes.put(IndexAssignerHandler.IDNAME, regexID);
						newRegexAttributes.put(IndexAssignerHandler.REGEXNAME, regex.getKey());
						newRegexAttributes.put(IndexAssignerHandler.KEYSNAME, String.join(",", regex.getValue()));
						newRegexAttributes.put(IndexAssignerHandler.DEFINDNAME, String.valueOf(defInds.get(regexID)));
						regexSetting.addReplaceAttributes(newRegexAttributes);
						regexesMatches.put(regexID, true);
					}
				}
				ArrayList<String> missingIndexes = SettingHandler.getConditionalList(indexesMatches, false);
				ArrayList<String> missingRegexes = SettingHandler.getConditionalList(regexesMatches, false);
				for (String indexID : missingIndexes) {
					Pair<Integer, String> index = indexes.get(indexID);
					HashMap<String, String> indexAttributes = new HashMap<String, String>();
					indexAttributes.put(IndexAssignerHandler.IDNAME, indexID);
					indexAttributes.put(IndexAssignerHandler.POSITIONNAME, String.valueOf(index.getKey()));
					indexAttributes.put(IndexAssignerHandler.KEYNAME, index.getValue());
					indexesSetting.addSetting(IndexAssignerHandler.INDEXNAME, null, indexAttributes);
				}
				for (String regexID : missingRegexes) {
					Pair<String, String[]> regex = regexes.get(regexID);
					HashMap<String, String> regexAttributes = new HashMap<String, String>();
					regexAttributes.put(IndexAssignerHandler.IDNAME, regexID);
					regexAttributes.put(IndexAssignerHandler.REGEXNAME, regex.getKey());
					regexAttributes.put(IndexAssignerHandler.KEYSNAME, String.join(",", regex.getValue()));
					regexAttributes.put(IndexAssignerHandler.DEFINDNAME, String.valueOf(defInds.get(regexID)));
					regexesSetting.addSetting(IndexAssignerHandler.SREGEXNAME, null, regexAttributes);
				}
			}
		}
	}
	
	public static void addIndexAssigner(IndexAssigner indexAssigner) {
		IndexAssignerHandler.indexAssigners.put(indexAssigner.getId(), indexAssigner);
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put(IndexAssignerHandler.IDNAME, indexAssigner.getId());
		attributes.put(IndexAssignerHandler.NAMENAME, indexAssigner.getName());
		attributes.put(IndexAssignerHandler.RMMATCHNAME, String.valueOf(indexAssigner.getRmMatch()));
		attributes.put(IndexAssignerHandler.IORDERNAME, SettingHandler.alts(indexAssigner.getIorder()));
		attributes.put(IndexAssignerHandler.RORDERNAME, SettingHandler.alts(indexAssigner.getRorder()));
		Setting indexAssignerSetting = IndexAssignerHandler.indexAssingerMastserSetting.addSetting(IndexAssignerHandler.SETTINGNAME, null, attributes);
		Setting indexesSetting = indexAssignerSetting.addSetting(IndexAssignerHandler.INDEXESNAME, null, null);
		Setting regexesSetting = indexAssignerSetting.addSetting(IndexAssignerHandler.REGEXESNAME, null, null);
		ConcurrentHashMap<String, Pair<Integer, String>> indexes = indexAssigner.getIndexes();
		ConcurrentHashMap<String, Pair<String, String[]>> regexes = indexAssigner.getRegexes();
		ConcurrentHashMap<String, Integer> defInds = indexAssigner.getDefInd();
		for (Entry<String, Pair<Integer, String>> index : indexes.entrySet()) {
			HashMap<String, String> indexAttributes = new HashMap<String, String>();
			indexAttributes.put(IndexAssignerHandler.IDNAME, index.getKey());
			indexAttributes.put(IndexAssignerHandler.POSITIONNAME, String.valueOf(index.getValue().getKey()));
			indexAttributes.put(IndexAssignerHandler.KEYNAME, index.getValue().getValue());
			indexesSetting.addSetting(IndexAssignerHandler.INDEXNAME, null, indexAttributes);
		}
		for (Entry<String, Pair<String, String[]>> regex : regexes.entrySet()) {
			HashMap<String, String> regexAttributes = new HashMap<String, String>();
			regexAttributes.put(IndexAssignerHandler.IDNAME, regex.getKey());
			regexAttributes.put(IndexAssignerHandler.REGEXNAME, regex.getValue().getKey());
			regexAttributes.put(IndexAssignerHandler.KEYSNAME, String.join(",", regex.getValue().getValue()));
			regexAttributes.put(IndexAssignerHandler.DEFINDNAME, String.valueOf(defInds.get(regex.getKey())));
			regexesSetting.addSetting(IndexAssignerHandler.SREGEXNAME, null, regexAttributes);
		}
	}
	
	public static IndexAssigner getIndexAsssigner(String id) {
		return (IndexAssignerHandler.indexAssigners.containsKey(id)) ? IndexAssignerHandler.indexAssigners.get(id) : null;
	}
	
	public static void removeIndexAssigner(String id) {
		if (SettingHandler.removeParent(id, IndexAssignerHandler.IDNAME, IndexAssignerHandler.SETTINGNAME, IndexAssignerHandler.indexAssingerMastserSetting)) {
			IndexAssignerHandler.indexAssigners.remove(id);
		}
	}
	
	public static void removeRegex(String indexAssignerID, String regexID) {
		SettingHandler.removeElement(indexAssignerID, regexID, IndexAssignerHandler.IDNAME, IndexAssignerHandler.SETTINGNAME, IndexAssignerHandler.REGEXESNAME, IndexAssignerHandler.SREGEXNAME, IndexAssignerHandler.indexAssingerMastserSetting);
	}
	
	public static void removeIndex(String indexAssignerID, String indexID) {
		SettingHandler.removeElement(indexAssignerID, indexID, IndexAssignerHandler.IDNAME, IndexAssignerHandler.SETTINGNAME, IndexAssignerHandler.INDEXESNAME, IndexAssignerHandler.INDEXNAME, IndexAssignerHandler.indexAssingerMastserSetting);
	}
	
	public static String getAssignerName(String id) {
		return IndexAssignerHandler.indexAssigners.get(id).getName();
	}
	
	public static String getRIDIfExists() {
		if (IndexAssignerHandler.indexAssigners.size() > 0 ) {
			for (Entry<String, IndexAssigner> val : IndexAssignerHandler.indexAssigners.entrySet()) {
				return val.getKey();
			}
		}
		return null;
	}
	
	public static List<IndexAssignerGUIPanel> getAssignerPanels() {
		ArrayList<IndexAssignerGUIPanel> panels = new ArrayList<IndexAssignerGUIPanel>();
		for (Entry<String, IndexAssigner> assigners : IndexAssignerHandler.indexAssigners.entrySet()) {
			IndexAssignerGUIPanel panel = new IndexAssignerGUIPanel();
			panel.init(assigners.getValue());
			panels.add(panel);
		}
		return panels;
	}

}
