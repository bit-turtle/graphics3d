package graphics3d;

public class Flag {
	private boolean value;
	public Flag(boolean value) {
		this.value = value;
	}
	public boolean getValue() {
		return value;
	}
	public void setValue(boolean value) {
		this.value = value;
	}
	public void toggleValue() {
		value = !value;
	}
}
