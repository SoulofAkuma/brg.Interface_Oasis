package parser;

import java.util.ArrayList;
import java.util.HashMap;

public class StdGetParser implements Parser {
	
	private ArrayList<String> log = new ArrayList<String>();
	
	@Override
	public String getName() {
		return "StdGetParser";
	}
	
	@Override
	public HashMap<String, String> parse(String input, HashMap<String, String> parsedHeader) {
		this.log.clear();
		if (!parsedHeader.containsKey("ULR")) {
			this.log.add("Error - the header is not a request header and hence contains no url");
			return new HashMap<String, String>();
		}
		String url = parsedHeader.get("URL");
		this.log.add("Applying GET Parser on url \"" + url + "\"");
		String query = url.substring(url.indexOf("?") + 1, url.length());
		if (query == null) {
			return new HashMap<String, String>();
		}
		HashMap<String, String> res = new HashMap<String, String>();
		String[] parts = query.split("\\&");
		for (String part : parts) {
			String[] params = part.split("=");
			if (params.length != 2) {
				this.log.add("Malformed Element - Skipping Element \"" + part + "\"");
				continue;
			}
			String key = params[0];
			String value = (res.containsKey(key)) ? res.get(key) + params[1] : params[1];
			if (res.containsKey(key)) {
				res.put(key, res.get(key) + "," + value);
			}
			res.put(key, value);
		}
		return res;
	}

	@Override
	public ArrayList<String> printLog() {
		return log;
	}
	
	

}
