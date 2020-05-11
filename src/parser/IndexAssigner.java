package parser;

import java.util.ArrayList;
import java.util.HashMap;

import cc.Pair;

public class IndexAssigner {
	
	private ArrayList<String> indexes = new ArrayList<String>();
	private ArrayList<String> regexes = new ArrayList<String>();
	private boolean rmMatch;

	public IndexAssigner(ArrayList<String> indexes, ArrayList<String> regexes, boolean rmMatch) {
		this.indexes = indexes;
		this.regexes = regexes;
		this.rmMatch = rmMatch;
	}
	
	public HashMap<String, String> assign(ArrayList<String> results) {
		HashMap<String, String> assigned = new HashMap<String, String>();
		int matchIndex = 0;
		for (String regex : this.regexes) {
			ArrayList<String> matches = new ArrayList<String>();
			ArrayList<Integer> matchIndexes = new ArrayList<Integer>();
			for (String result : results) {
				if (result.matches(regex)) {
					matches.add(result);
				}
			}
			if (this.rmMatch) {
				for (Integer index : matchIndexes) {
					results.remove(index);
				}
			}
		}
		return assigned;
	}
}
