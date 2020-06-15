package indexassigner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cc.Pair;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;

public class IndexAssigner {
	
	private String id;
	private String name;
	private ConcurrentHashMap<String, Pair<Integer, String>> indexes = new ConcurrentHashMap<String, Pair<Integer, String>>(); //All the indexes which will be assigned to the list stored by list index - Key
	private ConcurrentHashMap<String, Pair<String, String[]>> regexes = new ConcurrentHashMap<String, Pair<String, String[]>>(); //All the regexes which will be assigned to the list stored by regex - Keys (will be assigned in ascending order of matches)
	private boolean rmMatch; //Indicates whether a regex match should be removed from the original list after it has been successfully matched
	private ConcurrentHashMap<String, Integer> defInd; //Defines the default indexes for an overflow of results for the regex list. If not given the default is the last index of the array. Cannot be greater than the array length stored by regex - index
	private List<String> iorder;
	private List<String> rorder;
	
	public IndexAssigner(String id, String name, ConcurrentHashMap<String, Pair<Integer, String>> indexes, ConcurrentHashMap<String, Pair<String, String[]>> regexes, ConcurrentHashMap<String, Integer> defInd, boolean rmMatch, List<String> iorder, List<String> rorder) {
		this.id = id;
		this.indexes = indexes;
		this.regexes = regexes;
		this.rmMatch = rmMatch;
		this.defInd = defInd;
		this.name = name;
		this.iorder = iorder;
		this.rorder = rorder;
	}
	
	public HashMap<String, String> assign(ArrayList<String> results) {
		HashMap<String, String> assigned = new HashMap<String, String>();
		int matchIndex = 0;
		for (String regexID : this.rorder) {
			if (this.regexes.get(regexID).getValue().length == 0) {
				Logger.addMessage(MessageType.Warning, MessageOrigin.IndexAssigner, "Index Assginer contais the regex " + this.regexes.get(regexID).getKey() + " without any keys to assign. Skipping regex", this.id, null, null, false);
				continue;
			}
			ArrayList<String> matches = new ArrayList<String>();
			for (Iterator<String> ite = results.iterator(); ite.hasNext();) {
				String result = ite.next();
				if (!result.replaceAll(this.regexes.get(regexID).getKey(), "dummy").equals(result)) {
					matches.add(result);
					if (rmMatch) {
						ite.remove();
					}
				}
			}
			for (int i = 0; i < matches.size(); i++) {
				if (i < this.regexes.get(regexID).getValue().length) {
					assigned.put(this.regexes.get(regexID).getValue()[i], matches.get(i));					
				} else if (this.regexes.get(regexID).getValue().length > 0 && this.regexes.get(regexID).getValue().length <= i) {
					int defInd = this.regexes.get(regexID).getValue().length - 1;
					if (this.defInd.containsKey(this.regexes.get(regexID).getKey()) && this.defInd.get(this.regexes.get(regexID).getKey()) < this.regexes.get(regexID).getValue().length) {
						defInd = this.defInd.get(this.regexes.get(regexID).getKey());
					}
					assigned.put(this.regexes.get(regexID).getValue()[defInd], matches.get(i));
					continue;
				}
			}
		}
		for (String indexID : this.iorder) {
			if (this.indexes.get(indexID).getKey() < results.size()) {
				assigned.put(this.indexes.get(indexID).getValue(), results.get(this.indexes.get(indexID).getKey()));
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

	public boolean getRmMatch() {
		return rmMatch;
	}

	public void setRmMatch(boolean rmMatch) {
		this.rmMatch = rmMatch;
	}

	public ConcurrentHashMap<String, Integer> getDefInd() {
		return defInd;
	}

	public void setDefInd(ConcurrentHashMap<String, Integer> defInd) {
		this.defInd = defInd;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ConcurrentHashMap<String, Pair<Integer, String>> getIndexes() {
		return indexes;
	}

	public void setIndexes(ConcurrentHashMap<String, Pair<Integer, String>> indexes) {
		this.indexes = indexes;
	}

	public ConcurrentHashMap<String, Pair<String, String[]>> getRegexes() {
		return regexes;
	}

	public void setRegexes(ConcurrentHashMap<String, Pair<String, String[]>> regexes) {
		this.regexes = regexes;
	}

	public List<String> getIorder() {
		return iorder;
	}

	public void setIorder(List<String> iorder) {
		this.iorder = iorder;
	}

	public List<String> getRorder() {
		return rorder;
	}

	public void setRorder(List<String> rorder) {
		this.rorder = rorder;
	}
	
}
