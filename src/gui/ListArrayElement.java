package gui;

public class ListArrayElement {

	private String[] elementIDs;
	private String[] elementNames;
	private Object[] objects = null;
	
	public ListArrayElement(String[] elementIDs, String[] elementNames) {
		this.elementIDs = elementIDs;
		this.elementNames = elementNames;
	}

	public ListArrayElement(String[] elementIDs, String[] elementNames, Object[] objects) {
		this.elementIDs = elementIDs;
		this.elementNames = elementNames;
		this.objects = objects;
	}
	
	@Override
	public String toString() {
		return String.join(", ", this.elementNames);
	}

	public String[] getElementIDs() {
		return elementIDs;
	}

	public String[] getElementNames() {
		return elementNames;
	}

	public Object[] getObjects() {
		return objects;
	}
}
