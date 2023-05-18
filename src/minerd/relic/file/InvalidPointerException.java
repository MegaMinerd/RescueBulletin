package minerd.relic.file;

//@SuppressWarnings("serial")
public class InvalidPointerException extends RuntimeException {
    private int offset;
    
    public InvalidPointerException() {
        super("Invalid pointer");
    }
    
    public InvalidPointerException(int offsetIn) {
        super("Invalid pointer at 0x" + Integer.toHexString(offsetIn));
        this.offset = offsetIn;
    }
    
    public int getOffset() {
        return offset;
    }
}
