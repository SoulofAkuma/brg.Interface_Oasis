package constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import cc.Pair;

public class Constant {
	
	private String id;
	private String name;
	private ArrayList<String> order;
	private ConcurrentHashMap<String, Value> values;
	
	public Constant(String id, String name, ArrayList<String> order, ConcurrentHashMap<String, Value> values) {
		this.id = id;
		this.name = name;
		this.order = order;
		this.values = values;
	}

	public void insertValue(int index, Value value) {
		if (index < order.size()) {
			order.add(index, value.id);
		} else {
			order.add(value.id);
		}
		values.put(value.id, value);
	}
	
	public void removeValue(int index) {
		this.values.remove(order.get(index));
		this.order.remove(index);
	}
	
	public String getConstant(HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody) {
		String reVal = "";
		for (String valueID : this.order) {
			reVal += this.values.get(valueID).getString(parsedHeader, parsedBody);
		}
		return reVal;
	}

}
