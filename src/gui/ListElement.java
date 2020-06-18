package gui;

public class ListElement {
	
	private String elementID;
	private String elementName;
	private Object elementObject = null;
	
	public ListElement(String id, String name) {
		this.elementID = id;
		this.elementName = name;
	}
	
	public ListElement(String id, String name, Object object) {
		this.elementID = id;
		this.elementName = name;
		this.elementObject = object;
	}
	
	@Override
	public String toString() {
		return this.elementName;
	}
	
	public String getID() {
		return this.elementID;
	}
	
	public Object getObject() {
		return this.elementObject;
	}

}
