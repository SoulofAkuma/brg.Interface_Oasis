package group.responder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import constant.ConstantHandler;

public class Body {

	private List<String> content;
	private String separator;

	public Body(List<String> content, String separator) {
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

	public List<String> getContent() {
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
