package jsonhandler;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cc.Pair;
import parser.ParserHandler;
import parser.Rule;

public class Trace implements Rule {
	
	//Trace types for given strings
	private static final int OBJECTKEY = 0; //The string is the key of an object in the current object
	private static final int ARRAYINDEX = 1; //The string is an integer which indicates the index of an element in an array
	private static final int ARRAYQUERY = 2; //The string is a key value pair separated by an = sign which can be contained by an object in an array
	private static final int ARRAYQUERYREGEX = 3; //The string is a key value pair separated by an = sign which can be contained by an object in an array
	//If an array is the last index this rule will return a boolean whether the last search was successful
	
	private ArrayList<Pair<Integer, String>> path; //The list of the JSON parameter names to trace stored by name - trace
	private String defVal; //Default value to add when this rule has failed
	private ArrayList<String> log = new ArrayList<String>(); 

	public Trace(ArrayList<Pair<Integer, String>> path, String defVal) {
		this.path = path;
		this.defVal = defVal;
	}

	@Override
	public Rule genRule(HashMap<String, String> constructorArgs) {
		String pathStrings[] = ParserHandler.returnStringArrayIfExists(constructorArgs, "path");
		if (pathStrings == null || pathStrings.length / 2 != Math.round(pathStrings.length / 2)) {
			ParserHandler.reportGenRuleError("path", this.getClass().getName());
			return null;
		}
		ArrayList<Pair<Integer, String>> path = new ArrayList<Pair<Integer, String>>();
		for (int i = 0; i < pathStrings.length; i += 2) {
			if (!ParserHandler.isInt(pathStrings[i])) {
				ParserHandler.reportGenRuleError("path", this.getClass().getName());
				return null;
			} else if (Integer.parseInt(pathStrings[i]) != Trace.OBJECTKEY && Integer.parseInt(pathStrings[i]) != Trace.ARRAYINDEX && Integer.parseInt(pathStrings[i]) != Trace.ARRAYQUERY && Integer.parseInt(pathStrings[i]) != Trace.ARRAYQUERYREGEX) {
				ParserHandler.reportGenRuleError("path", this.getClass().getName());
				return null;
			} else if (Integer.parseInt(pathStrings[i]) == Trace.ARRAYINDEX && !ParserHandler.isInt(pathStrings[i + 1])) {
				ParserHandler.reportGenRuleError("path", this.getClass().getName());
				return null;
			} else if (Integer.parseInt(pathStrings[i]) == Trace.ARRAYQUERY && !pathStrings[i + 1].contains("=")) {
				ParserHandler.reportGenRuleError("path", this.getClass().getName());
				return null;
			} else {
				path.add(new Pair<Integer, String>(Integer.parseInt(pathStrings[i]), pathStrings[i + 1]));
			}
		}
		String defVal = ParserHandler.returnStringIfExists(constructorArgs, "defVal");
		if (defVal == null) {
			ParserHandler.reportGenRuleError("defVal", this.getClass().getName());
		}
		return new Trace(path, defVal);
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
		boolean breakOut = false;
		String result = "";
		for (int i = 0; i < this.path.size(); i++) {
			int searchType = this.path.get(i).getKey();
			String searchString = this.path.get(i).getValue();
			Object obj = jObj.get(searchString);
			boolean isLast = (i == this.path.size() - 1);
			switch (searchType) {
				case Trace.OBJECTKEY:
					if (wasArray) {
						errorLog("Cannot seach for an object key in an array");
						breakOut = true;
						break;
					}
					if (jObj.containsKey(searchString)) {
						if (obj instanceof JSONObject) {
							if (isLast) {
								result = "true";
								this.log.add("Found object of object \"" + searchString + "\"");
							} else {
								wasArray = false;
								jObj = (JSONObject) obj;
								this.log.add("Found another object of object \"" + searchString + "\"");
							}
						} else if (obj instanceof JSONArray) {
							if (isLast) {
								if (isValueArray(obj)) {
									result = arrayToString(obj);
									this.log.add("Found array of values \"" + result + "\" of object \"" + searchString + "\"");
								} else {
									result = "true";
									this.log.add("Found mixed array of object \"" + searchString + "\". Returning true");
								}
							} else {
								wasArray = true;
								jArr = (JSONArray) obj;
								this.log.add("Found array of object \"" + searchString + "\"");
							}
						} else {
							if (isLast) {
								result = obj.toString();
								this.log.add("Found object value \"" + result + "\" of \"" + searchString + "\"");
							} else {
								errorLog("Found a non-tracable value \"" + obj.toString() + "\"");
								breakOut = true;
							}
						}
					} else {
						if (isLast) {
							result = "false";
							this.log.add("Did not find object key \"" + searchString + "\". Returning false");
						} else {
							errorLog("Did not find object key \"" + searchString + "\"");
						}
						breakOut = true;
					}
				break;
				case Trace.ARRAYINDEX:
					if (!wasArray) {
						errorLog("Cannot search for an array index inside of an object");
						breakOut = true;
						break;
					}
					int index = Integer.parseInt(searchString);
					if (index >= jArr.size()) {
						errorLog("The given index of " + index + " was not in the array range 0 - " + jArr.size());
						breakOut = true;
						break;
					}
					Object foundObj = jArr.get(index);
					if (foundObj instanceof JSONObject) {
						if (isLast) {
							result = "true";
							this.log.add("Found object of array at index " + searchString + ". Returning true");
						} else {
							wasArray = false;
							jObj = (JSONObject) foundObj;
							this.log.add("Found another object of array at index " + searchString);
						}
					} else if (foundObj instanceof JSONArray) {
						if (isLast) {
							if (isValueArray(foundObj)) {
								result = arrayToString(foundObj);
								this.log.add("Found array of values \"" + result + "\" of array at index " + searchString);
							} else {
								result = "true";
								this.log.add("Found mixed array of array at index " + searchString + ". Returning true");
							}
						} else {
							wasArray = true;
							jArr = (JSONArray) foundObj;
							this.log.add("Found array of array at index " + searchString);
						}
					} else {
						if (isLast) {
							result = foundObj.toString();
							this.log.add("Found value \"" + result + "\" at array index " + searchString);
						} else {
							errorLog("Found a non-tracable value \"" + foundObj.toString() + "\" at array index \"" + obj.toString() + "\"");
							breakOut = true;
						}
					}
				break;
				default:
					if (!wasArray) {
						errorLog("Cannot do an array query inside of an object");
						breakOut = true;
						break;
					}
					JSONObject objMatch = null;
					String key = searchString.substring(0, searchString.indexOf("=") + 1);
					String value = (searchString.indexOf("=") == searchString.length() -1) ? "" : searchString.substring(searchString.indexOf("=") + 1, searchString.length());
					int successInd = -1;
					for (int j = 0; j < jArr.size(); i++) {
						if (jArr.get(j) instanceof JSONObject) {
							JSONObject itObj = (JSONObject) jArr.get(j);
							if (itObj.containsKey(key) && itObj.get(key) instanceof JSONObject) {
								if (searchType == Trace.ARRAYQUERYREGEX) {
									if (value.isEmpty() || itObj.get(key).toString().matches(value)) {
										objMatch = itObj;
										successInd = j;
										break;
									}
								} else {
									if (value.isEmpty() || itObj.get(key).toString().equals(value)) {
										objMatch = itObj;
										successInd = j;
										break;
									}
								}
							}
						}
					}
					if (objMatch == null) {
						if (isLast) {
							result = "false";
							this.log.add("The array query of key:\"" + key + "\" value:\"" + value + "\" was unsuccessful. Returning false");
						} else {
							errorLog("The array query of key:\"" + key + " value:\"" + value + "\" was unsuccessful");
							breakOut = true;
						}
					} else {
						if (isLast) {
							result = "true";
							this.log.add("Found object of array at index " + successInd + " which matches key:\"" + key + "\" value:\"" + value + "\". Returning true");
						} else {
							this.log.add("Found object of array at index " + successInd + " which matches key:\"" + key + "\" value:\"" + value + "\"");
							jObj = objMatch;
						}
					}
				break;
			}
			if (breakOut) {
				result = this.defVal;
				this.log.add("Returning default value \"" + this.defVal + "\"");
				break;
			}
		}
		input.add(result);
		return input;
	}
	
	private boolean isValueArray(Object jArray) {
		JSONArray array = (JSONArray) jArray;
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) instanceof JSONArray || array.get(i) instanceof JSONObject) {
				return false;
			}
		}
		return true;
	}
	
	private String arrayToString(Object jArray) {
		JSONArray array = (JSONArray) jArray;
		String asString = "";
		for (int i = 0 ; i < array.size(); i++) {
			asString += array.get(i) + ",";
		}
		if (asString.length() > 0) {
			asString = asString.substring(0, asString.length() - 1);
		}
		return asString;
	}

	@Override
	public String printElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	private void errorLog(String message) {
		this.log.add(message + " - Quitting jsonparser");
	}

	@Override
	public HashMap<String, String> storeRule() {
		HashMap<String, String> rule = new HashMap<String, String>();
		String pathString = "";
		for (Pair<Integer, String> pathElement : this.path) {
			pathString += pathElement.getKey() + "," + pathElement.getValue() + ",";
		}
		if (pathString.length() > 0) {
			pathString = pathString.substring(0, pathString.length() - 1);
		}
		rule.put("path", pathString);
		return rule;
	}

}
