package group.responder;

import java.util.ArrayList;
import java.util.HashMap;

import constant.Constant;

public class Body {

	ArrayList<Constant> content = new ArrayList<Constant>();

	public Body(ArrayList<Constant> content) {
		this.content = content;
	}
	
	public String getBody(HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody) {
		String body = "";
		for (Constant constant : this.content) {
			body += constant.getConstant(parsedHeader, parsedBody);
		}
		return body;
	}
}
