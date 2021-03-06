package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Discard implements Rule {
	
	//Default values
	public static final boolean INVERTED = false;
	public static final boolean REGEX = false;
	public static final char FLAGS[] = new char[0];
	
	private String findValue; //String to find in the input
	private String find;
	private boolean inverted; //If inverted the rule will discard all strings which match the condition
	private boolean regex;
	private ArrayList<String> flags; //Flags for the rule s: remove all subsequent elements, p: remove all preceding elements, z: push discarded elements to the end of the result list instead of deleting them, a: push discarded elements to the beginning of the result list instead of deleting them
	private boolean useHeader;
	private List<String> log = Collections.synchronizedList(new ArrayList<String>()); //Log for the rule
	
	public Discard() {}
	
	public Discard(String find, boolean inverted, boolean regex, String flags[], boolean useHeader) {
		this.find = find;
		this.inverted = inverted;
		this.regex = regex;
		this.flags = new ArrayList<String>(Arrays.asList(flags));
		this.useHeader = useHeader;
	}
	
	@Override
	public Rule genRule(HashMap<String, String> constructorArgs) {
		String id = constructorArgs.get("id");
		String find = ParserHandler.returnStringIfExists(constructorArgs, "find");
		Boolean inverted = ParserHandler.returnBooleanIfExists(constructorArgs, "inverted");
		Boolean regex = ParserHandler.returnBooleanIfExists(constructorArgs, "regex");
		String[] flags = ParserHandler.returnStringArrayIfExists(constructorArgs, "flags");
		Boolean useHeader = ParserHandler.returnBooleanIfExists(constructorArgs, "useHeader");
		if (find == null) {
			ParserHandler.reportGenRuleError("find", this.getClass().getName(), id);
			return null;
		} else if (inverted == null) {
			ParserHandler.reportGenRuleError("inverted", this.getClass().getName(), id);
			return null;
		} else if (regex == null) {
			ParserHandler.reportGenRuleError("regex", this.getClass().getName(), id);
			return null;
		} else if (flags == null) {
			ParserHandler.reportGenRuleError("flags", this.getClass().getName(), id);
			return null;
		} else if ((Arrays.asList(flags).contains("s") && Arrays.asList(flags).contains("p")) || (Arrays.asList(flags).contains("a") && Arrays.asList(flags).contains("z"))) {
			ParserHandler.reportGenRuleError("flags (a && z || p && s is not allowed)", this.getClass().getName(), id);
			return null;
		} else if (useHeader == null) {
			ParserHandler.reportGenRuleError("useHeader", this.getClass().getName(), id);
			return null;
		} else {
			return new Discard(find, inverted, regex, flags, useHeader);
		}
	}
	
	
	@Override
	public ArrayList<String> apply(ArrayList<String> input, HashMap<String, String> parsedHeader) {
		this.log.clear();
		if (this.useHeader) {
			if (parsedHeader.containsKey(this.find)) {
				this.findValue = parsedHeader.get(this.find);
			} else {
				this.log.add("Value \"" + this.find +"\" not found in header");
				return input;
			}
		} else {
			this.findValue = this.find;
		}
		ArrayList<String> output = new ArrayList<String>();
		ArrayList<String> discarded = new ArrayList<String>();
		boolean s = false;
		for (String element : input) {
			if (element == null) {
				if (this.findValue.isEmpty()) {
					this.log.add("Element Empty, discarding");
				} else {
					this.log.add("Element Empty");
				}
			}
			this.log.add("Applying Rule on \"" + element.substring(0, 10) + "...\"");
			if (s) {
				this.log.add("Discarding element due to discard subsequent flag");
				discarded.add(element);
				continue;
			}
			boolean match = (regex) ? hasRegexMatch(element) : element.contains(this.findValue);
			if (match) {
				this.log.add("Match found");
				for (String flag : this.flags) {
					switch (flag) {
						case "s":
							this.log.add("Applying discard subsequent flag");
							s = true;
						break;
						case "p":
							this.log.add("Applying discard preceding flag");
							for (String outputElement : output) {
								this.log.add("Discarded " + outputElement);
								discarded.add(outputElement);
							}
							output = new ArrayList<String>();
						break;
					}
				}
			}
			if (!inverted && match) {
				this.log.add("Adding " + element + " to the result list");
				output.add(element);
			} else if (inverted && !match) {
				this.log.add("Adding " + element + " to the result list");
				output.add(element);
			}
		}
		if (this.flags.contains("z")) {
			for (String element : discarded) {
				output.add(element);
			}
		} else if(this.flags.contains("a")) {
			ArrayList<String> dummy = new ArrayList<String>();
			for (String element : discarded) {
				dummy.add(element);
			}
			for (String element : output) {
				dummy.add(element);
			}
			output = dummy;
		}
		return output;
	}
	
	@Override
	public List<String> printLog() {
		return this.log;
	}
	
	public boolean hasRegexMatch(String input) {
		Pattern pattern = Pattern.compile(this.findValue);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public HashMap<String, String> storeRule() {
		HashMap<String, String> rule = new HashMap<String, String>();
		rule.put("find", this.find);
		rule.put("inverted", String.valueOf(this.inverted));
		rule.put("regex", String.valueOf(this.regex));
		rule.put("flags", String.join(",", this.flags));
		rule.put("useHeader", String.valueOf(this.useHeader));
		return rule;
	}
	
	@Override
	public String printRuleLRP() {
		String useHeader = (this.useHeader) ? "useHeader; " : "";
		String inverted = (this.inverted) ? "reEval; " : "";
		String regex = (this.regex) ? "regex; " : "";
		String flags = (this.flags.size() > 0) ? String.join(",", this.flags.toArray(new String[this.flags.size()])) + "; " : "";
		return "Discard; " + useHeader + inverted + regex + flags + this.find;
	}

	public String getFind() {
		return find;
	}

	public void setFind(String find) {
		this.find = find;
	}

	public boolean isInverted() {
		return inverted;
	}

	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}

	public boolean isRegex() {
		return regex;
	}

	public void setRegex(boolean regex) {
		this.regex = regex;
	}

	public ArrayList<String> getFlags() {
		return flags;
	}

	public void setFlags(ArrayList<String> flags) {
		this.flags = flags;
	}

	public boolean isUseHeader() {
		return useHeader;
	}

	public void setUseHeader(boolean useHeader) {
		this.useHeader = useHeader;
	}
	
	public void changeFlagState(String flag) {
		if (this.flags.contains(flag)) {
			this.flags.remove(flag);
		} else {
			this.flags.add(flag);
		}
	}
	
}