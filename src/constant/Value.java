package constant;

import java.util.HashMap;

public class Value {

	public String id;
	public String value;
	public boolean isDynamic;
	public boolean useHeader;
	
	public Value(String id, String value, boolean isDynamic, boolean useHeader) {
		this.id = id;
		this.value = value;
		this.isDynamic = isDynamic;
		this.useHeader = useHeader;
	}
	
	public String getString(HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody) {
		if (this.isDynamic) {
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
			return value;
		}
	}

}
