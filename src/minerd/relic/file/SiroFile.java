package minerd.relic.file;

import java.io.IOException;

public class SiroFile extends BufferedDataHandler {
	private SiroSegment head;
	private SiroLayout layout;
	private int offset;

	/**
	 * @param head   The head element of a tree defining the data structure.
	 * @param offset The location of this file in the full rom
	 */
	public SiroFile(int offset, SiroSegment head, SiroLayout layout) {
		super(null);
		this.offset = offset;
		this.head = head;
		this.layout = layout;
	}

	public SiroSegment getSegment() {
		return head;
	}

	public SiroSegment getSegment(String path) {
		return head.getDescendant(path);
	}

	public int getOffset() {
		return offset;
	}
	
	@Override
	public BufferedDataHandler save() {
		try{
			return SiroPacker.pack(this, layout);
		} catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public enum SiroLayout{
		BASIC,
		ITEM,
		POKEMON,
		MOVE,
		DUNGEON,
		GRAPHIC_LIST,
		GRAPHIC_TABLE;
	}
}