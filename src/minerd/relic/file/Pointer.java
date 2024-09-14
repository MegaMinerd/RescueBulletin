package minerd.relic.file;

public class Pointer {
	private final int offset;
	private final boolean absolute;

	public Pointer(int off, boolean abs) {
		offset = off;
		absolute = abs;
	}

	public int getOffset() {
		return offset;
	}

	public boolean isAbsolute() {
		return absolute;
	}

	//Todo: use this everywhere
	public static Pointer fromInt(int in) throws InvalidPointerException {
		int abs = ((in & 0x08000000) >> 27);
		int val = in & 0x07FFFFFF;
		if(abs>2)
			throw new InvalidPointerException();
		return (abs==0 && val==0) ? null : new Pointer(val, abs==1);
	}

	public Pointer relativeTo(int off) {
		return absolute ? new Pointer(offset - off, false) : this;
	}

	public Pointer absoluteFrom(int off) {
		return absolute ? this : new Pointer(offset - off, true);
	}
}