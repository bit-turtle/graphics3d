package graphics3d;

public class Flag {
	private int value;
	public Flag(int value) {
		this.value = value;
	}
	public Flag(boolean value) {
		setValue(value);
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public void setValue(boolean value) {
		this.value = (value) ? 1 : 0;
	}
	public boolean enabled() {
		return (this.value != 0);
	}
	public void toggleValue() {
		value = (this.value != 0) ? 0 : 1;
	}
}
