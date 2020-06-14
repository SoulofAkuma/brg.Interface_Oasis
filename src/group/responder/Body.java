package group.responder;

import java.util.ArrayList;
import java.util.HashMap;

import constant.Constant;
import constant.ConstantHandler;

public class Body {

	private ArrayList<String> content = new ArrayList<String>();
	private String seperator;

	public Body(ArrayList<String> content, String seperator) {
		this.content = content;
		this.seperator = seperator;
	}
	
	public String getBody(HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody) {
		String body = "";
		for (int i = 0; i < this.content.size(); i++) {
			body += ConstantHandler.getConstant(this.content.get(i), parsedHeader, parsedBody);
			if (i != this.content.size() - 1) {
				body += this.seperator;
			}
		}
		return body;
	}
}
