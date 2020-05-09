package cc;

//This class provides a variable interface for final variables (the object can be final, the values will be modified afterwards via method)
public class Shell<T> {

	private T value;
	
	public T getValue() {
		return this.value;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
}
