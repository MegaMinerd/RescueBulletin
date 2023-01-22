package minerd.relic;

@SuppressWarnings("serial")
public class InvalidPointerException extends Exception {
	private final int offset;
	
	public InvalidPointerException(int offsetIn) {
		super("Invalid pointer at 0x" + Integer.toHexString(offsetIn));
		this.offset = offsetIn;	
	}
	
	public int getOffset() {
		return offset;
	}
}
