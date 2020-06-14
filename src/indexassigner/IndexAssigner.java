package indexassigner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cc.Pair;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;

public class IndexAssigner {
	
	private String id;
	private ConcurrentHashMap<Integer, String> indexes = new ConcurrentHashMap<Integer, String>(); //All the indexes which will be assigned to the list stored by list index - Key
	private ConcurrentHashMap<String, String[]> regexes = new ConcurrentHashMap<String, String[]>(); //All the regexes which will be assigned to the list stored by regex - Keys (will be assigned in ascending order of matches)
	private boolean rmMatch; //Indicates whether a regex match should be removed from the original list after it has been successfully matched
	private ConcurrentHashMap<String, Integer> defInd; //Defines the default indexes for an overflow of results for the regex list. If not given the default is the last index of the array. Cannot be greater than the array length stored by regex - index

	public IndexAssigner(String id, ConcurrentHashMap<Integer, String> indexes, ConcurrentHashMap<String, String[]> regexes, ConcurrentHashMap<String, Integer> defInd, boolean rmMatch) {
		this.id = id;
		this.indexes = indexes;
		this.regexes = regexes;
		this.rmMatch = rmMatch;
		this.defInd = defInd;
	}
	
	public HashMap<String, String> assign(ArrayList<String> results) {
		HashMap<String, String> assigned = new HashMap<String, String>();
		int matchIndex = 0;
		for (Map.Entry<String, String[]> kvp : this.regexes.entrySet()) {
			if (kvp.getValue().length == 0) {
				Logger.addMessage(MessageType.Warning, MessageOrigin.IndexAssigner, "Index Assginer contais the regex " + kvp.getKey() + " without any keys to assign. Skipping regex", this.id, null, null, false);
				continue;
			}
			ArrayList<String> matches = new ArrayList<String>();
			for (Iterator<String> ite = results.iterator(); ite.hasNext();) {
				String result = ite.next();
				if (!result.replaceAll(kvp.getKey(), "dummy").equals(result)) {
					matches.add(result);
					if (rmMatch) {
						ite.remove();
					}
				}
			}
			for (int i = 0; i < matches.size(); i++) {
				if (i < kvp.getValue().length) {
					assigned.put(kvp.getValue()[i], matches.get(i));					
				} else if (kvp.getValue().length > 0 && kvp.getValue().length <= i) {
					int defInd = kvp.getValue().length - 1;
					if (this.defInd.containsKey(kvp.getKey()) && this.defInd.get(kvp.getKey()) < kvp.getValue().length) {
						defInd = this.defInd.get(kvp.getKey());
					}
					assigned.put(kvp.getValue()[defInd], matches.get(i));
					continue;
				}
			}
		}
		for (Map.Entry<Integer, String> kvp : this.indexes.entrySet()) {
			if (kvp.getKey() < results.size()) {
				assigned.put(kvp.getValue(), results.get(kvp.getKey()));
			}
		}
		return assigned;
	}
	
	public static HashMap<String, String> transformHeaderReq(String input) {
		String[] lines = input.split("\r\n");
		HashMap<String, String> returnVal = new HashMap<String, String>();
		if (lines.length > 0) {
			String[] params = lines[0].split(" ");
			returnVal.put("RequestType", params[0]);
			returnVal.put("URI", params[1]);
			returnVal.put("Protocol", params[2]);
		}
		for (int i = 1; i < lines.length; i++) {
			String line = lines[i];
			int splitIndex = line.indexOf(":", 0);
			String name = line.substring(0, splitIndex);
			String value = line.substring(splitIndex + 2, line.length());
			returnVal.put(name, value);
		}
		return returnVal;
	}
	
	public static HashMap<String, String> transformHeaderRes(String input) {
		String[] lines = input.split("\r\n");
		HashMap<String, String> returnVal = new HashMap<String, String>();
		if (lines.length > 0) {
			String[] params = lines[0].split(" ");
			returnVal.put("Protocol", params[0]);
			returnVal.put("StatusCode", params[1]);
			returnVal.put("StatusMessage", params[2]);
		}
		for (int i = 1; i < lines.length; i++) {
			String line = lines[i];
			int splitIndex = line.indexOf(":", 0);
			String name = line.substring(0, splitIndex);
			String value = line.substring(splitIndex + 2, line.length());
			returnVal.put(name, value);
		}
		return returnVal;
		
	}

	public HashMap<String, String> storeIndexAssigner() {
		// TODO Auto-generated method stub
		return null;
	}
}
