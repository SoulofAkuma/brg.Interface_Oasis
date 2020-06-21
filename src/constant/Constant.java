package constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Constant {
	
	private String id;
	private String name;
	private List<String> order;
	private ConcurrentHashMap<String, Value> values;
	
	public Constant(String id, String name, List<String> order, ConcurrentHashMap<String, Value> values) {
		this.id = id;
		this.name = name;
		this.order = order;
		this.values = values;
	}

	public void insertValue(int index, Value value) {
		if (index < order.size()) {
			order.add(index, value.getId());
		} else {
			order.add(value.getId());
		}
		values.put(value.getId(), value);
	}
	
	public String getConstant(HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody) {
		String reVal = "";
		for (String valueID : this.order) {
			reVal += this.values.get(valueID).getString(parsedHeader, parsedBody);
		}
		return reVal;
	}
	
	public String identification() {
		return this.id + " " + this.name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getOrder() {
		return order;
	}

	public void setOrder(ArrayList<String> order) {
		this.order = order;
	}

	public ConcurrentHashMap<String, Value> getValues() {
		return values;
	}

	public void setValues(ConcurrentHashMap<String, Value> values) {
		this.values = values;
	}

	public String getId() {
		return id;
	}
	
	public void addValue(String id, Value value) {
		this.values.put(id, value);
		this.order.add(id);
	}
	
	public void removeValue(String id) {
		this.values.remove(id);
		this.order.remove(id);
		ConstantHandler.removeValue(this.id, id);
	}
	
	public void changeValuePosition(String id, int position) {
		this.order.remove(id);
		this.order.add(position, id);
	}
	
}
