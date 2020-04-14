package xmlhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class Trace implements parser.ParserInterface {
	
	private HashMap<Short, String> nodes;
	private short informationType =
	private ArrayList<String> log = new ArrayList<String>();

	@Override
	public ArrayList<String> apply(ArrayList<String> input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String printElement() {
		return "XML Trace - nodes = [\"" + String.join("\",\"", this.nodes) + "\" | ";
	}

	@Override
	public ArrayList<String> printLog() {
		return this.log;
	}

}
