package jsonhandler;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import parser.ParserHandler;
import parser.Rule;

public class Trace implements Rule {
	
	private String[] path; //The list of the JSON parameter names to trace
	private ArrayList<String> log = new ArrayList<String>();

	public Trace(String[] path) {
		this.path = path;
	}
	
	@Override
	public Rule genRule(HashMap<String, String> contructorArgs) {
		String[] path = ParserHandler.returnStringArrayIfExists(contructorArgs, "path");
		if (path == null) {
			ParserHandler.reportGenRuleError("path", this.getClass().getName());
			return null;
		} else {
			return new Trace(path);
		}
	}

	//This method will only use the first element of the input list for parsing and will not modify it thus making this parser stackable
	@Override
	public ArrayList<String> apply(ArrayList<String> input) {
		String json = input.get(0);
		this.log.add("Applying Rule on \"" + json + "\"");
		JSONParser parser = new JSONParser();
		JSONObject jObj = null;
		try {
			jObj = (JSONObject) parser.parse(json);
		} catch (ParseException e) {
			this.log.add("JSON Parsing failed");
			return input;
		}
		JSONArray jArr = null;
		boolean wasArray = false;
		boolean success = false;
		String result = "";
		for (int i = 0; i < this.path.length; i++) {
			String object = this.path[i];
			if (jObj.containsKey(object)) {
				if (wasArray) {
					boolean arrSuccess = false;
					for (int j = 0; j < jArr.size(); j++) {
						if (jArr.get(j) instanceof JSONArray) {
							
						} else if (jArr.get(j) instanceof JSONObject) {
							jArr.
						} else {
							continue;
						}
					}
				} else {
					if (jObj.get(object) instanceof JSONArray) {	
						if (i == this.path.length - 1) {
							errorLog("The value of the last trace element is invalid (no number or string)");
							break;
						}
						this.log.add("Found next object array \"" + object + "\"");
						jArr = (JSONArray) jObj.get(object);
					} else if (jObj.get(object) instanceof JSONObject) {
						if (i == this.path.length - 1) {
							errorLog("The value of the last trace element is invalid (no number or string)");
							break;
						}
						this.log.add("Found next object \"" + object + "\"");
						jObj = (JSONObject) jObj.get(object);
					} else {
						if (i != this.path.length - 1) {
							errorLog("The value of the trace element is invalid (already a number or string), can't trace further");
							break;
						}
						result = jObj.get(object).toString();
					}
				}
			}
		}
	}

	@Override
	public String printElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> printLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> endProcedure(ArrayList<String> input) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void errorLog(String message) {
		this.log.add(message + " - Quitting jsonparser");
	}

}
