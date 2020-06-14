package parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;
import settings.SettingHandler;

public class StdGetParser implements Parser {
	
	private ArrayList<String> log = new ArrayList<String>();
	
	@Override
	public HashMap<String, String> parse(String input, HashMap<String, String> parsedHeader) {
		this.log.clear();
		String url = parsedHeader.get("URL");
		this.log.add("Applying GET Parser on url \"" + url + "\"");
		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			this.log.add("Malformed URL - Returning empty Map");
			Logger.addMessage(MessageType.Error, MessageOrigin.Parser, "Header URL syntax malformed - Cannot transform into uri (\"" + url + "\")", SettingHandler.GETPARSERID, null, null, false);
			return new HashMap<String, String>();
		}
		String query = uri.getQuery();
		if (query == null) {
			return new HashMap<String, String>();
		}
		HashMap<String, String> res = new HashMap<String, String>();
		String[] parts = query.split("\\?");
		for (String part : parts) {
			String[] params = part.split("=");
			if (params.length != 2) {
				this.log.add("Malformed Element - Skipping Element \"" + part + "\"");
				continue;
			}
			String key = params[0];
			String value = (res.containsKey(key)) ? res.get(key) + params[1] : params[1];
			res.put(key, value);
		}
		return res;
	}

	@Override
	public ArrayList<String> printLog() {
		return log;
	}
	
	

}
