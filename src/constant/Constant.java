package constant;

import java.util.ArrayList;
import java.util.HashMap;

public class Constant {
	
	private ArrayList<String> values;
	private ArrayList<String> dynamicValues;
	private boolean isDynamic;
	private boolean useHeader;
	
	public Constant(ArrayList<String> values, ArrayList<String> dynamicValues, boolean useHeader) {
		this.values = values;
		this.dynamicValues = dynamicValues;
		this.isDynamic = (dynamicValues.size() > 0) ? true : false;
		this.useHeader = useHeader;
	}
	
	public boolean usesHeader() {
		return this.useHeader;
	}
	
	public void insertValue(int index, String value) {
		this.values.add(index, value);
	}
	
	public void insertDynamic(int index, String value) {
		if (this.dynamicValues.size() == 0) {
			this.isDynamic = true;
		}
		int dynToIndex = 0;
		for (int i = 0; i < index; i++) {
			if (this.values.get(i) == null) {
				dynToIndex++;
			}
		}
		this.dynamicValues.add(dynToIndex, value);
		this.values.add(index, null);
	}
	
	public void removeValue(int index) {
		if (this.values.get(index) == null) {
			int dynToIndex = 0;
			for (int i = 0; i < index; i++) {
				if (this.values.get(i) == null) {
					dynToIndex++;
				}
			}
			this.dynamicValues.remove(dynToIndex);
			this.values.remove(index);
			if (this.dynamicValues.size() == 0) {
				this.isDynamic = false;
			}
		}
	}
	
	public String getConstant() {
		String reVal = "";
		for (String value : this.values) {
			reVal += value;
		}
		return reVal;
	}
	
	public String getConstant(HashMap<String, String> dynValues) {
		String reVal = "";
		int dynIndex = 0;
		if (this.isDynamic) {
			for (String value : this.values) {
				if (value == null) {
					reVal += dynValues.get(this.dynamicValues.get(dynIndex));
					dynIndex++;
				} else {
					reVal += value;
				}
			}			
		} else {
			for (String value : this.values) {
				reVal += value;
			}
		}
		return reVal;
	}

}
