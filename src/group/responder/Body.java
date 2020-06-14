package group.responder;

import java.util.ArrayList;
import java.util.HashMap;

import constant.Constant;
import constant.ConstantHandler;

public class Body {

	private ArrayList<String> content = new ArrayList<String>();
	private String separator;

	public Body(ArrayList<String> content, String separator) {
		this.content = content;
		this.separator = separator;
	}
	
	public String getBody(HashMap<String, String> parsedHeader, HashMap<String, String> parsedBody) {
		String body = "";
		for (int i = 0; i < this.content.size(); i++) {
			body += ConstantHandler.getConstant(this.content.get(i), parsedHeader, parsedBody);
			if (i != this.content.size() - 1) {
				body += this.separator;
			}
		}
		return body;
	}

	public ArrayList<String> getContent() {
		return content;
	}

	public void setContent(ArrayList<String> content) {
		this.content = content;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}
	
	
}
