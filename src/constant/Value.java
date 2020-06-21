package constant;

import java.util.HashMap;

public class Value {

	private String id;
	private String value;
	private boolean isKey;
	private boolean useHeader;
	private boolean backReference;
	
	public Value(String id, String value, boolean isKey, boolean useHeader, boolean backReference) {
		this.id = id;
		this.value = (value == null) ? "" : value;
		this.isKey = isKey;
		this.useHeader = useHeader;
		this.backReference = backReference;
	}
	
	public String getString(HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody) {
		if (this.backReference) {
			return ConstantHandler.getConstant(value, parsedHeader, parsedBody);
		} else if (this.isKey) {
			if (this.useHeader) {
				if (parsedHeader.containsKey(this.value)) {
					return parsedHeader.get(this.value);
				} else {
					return "";
				}
			} else {
				if (parsedBody.containsKey(this.value)) {
					return parsedBody.get(this.value);
				} else {
					return "";
				}
			}
		} else {
			return this.value;
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isKey() {
		return isKey;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}

	public boolean isUseHeader() {
		return useHeader;
	}

	public void setUseHeader(boolean useHeader) {
		this.useHeader = useHeader;
	}

	public boolean isBackReference() {
		return backReference;
	}

	public void setBackReference(boolean backReference) {
		this.backReference = backReference;
	}

	public String getId() {
		return id;
	}
	
	public String getValueString() {
		String key = (this.isKey) ? "isKey" : "raw";
		String header = (this.useHeader) ? ", useHeader" : "";
		String backReference = (this.backReference) ? ", backReference" : "";
		String valueString = (this.backReference) ? ConstantHandler.getConstantNames().get(this.value) : this.value;
		return valueString + " - " + key + header + backReference;
	}
	
	

}
