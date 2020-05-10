package group.responder;

import java.util.ArrayList;

public class Constant {
	
	private ArrayList<String> values;
	private ArrayList<String> dynamicValues;
	private boolean isDynamic;
	
	public Constant(ArrayList<String> values, ArrayList<String> dynamicValues, boolean isDynamic) {
		this.values = values;
		this.dynamicValues = dynamicValues;
		this.isDynamic = isDynamic;
	}
	
	public void insertValue(int index, String value) {
		if (values.size() > index) {
			this.values.add(index, value);
		}
	}
	
	public void insertDynamic(int index, String value) {
		int dynToIndex = 0;
		for (int i = 0; i < index; i++) {
			if (this.values.get(i) == null) {
				dynToIndex++;
			}
		}
		this.dynamicValues.add(dynToIndex, value);
		this.values.add(index, null);
	}

}
