package minerd.relic.file;

import java.io.IOException;
import java.util.ArrayList;

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
	
	public void printTree() throws IOException {
		System.out.println("\t\tSiro format: " + layout);
		if(head != null) {	
			head.printTree(2, "children"); 
		}
	}
	
	public enum SiroLayout{
		BASIC("basic"),
		VARIABLE_LENGTH_TABLE("variable length table"),
		
		ITEM("item"),
		POKEMON("pokemon"),
		MOVE("move"),
		DUNGEON("dugeon"),
		
		GRAPHIC_LIST("graphic list"),
		GRAPHIC_TABLE("graphic table"),
		GLYPH_TABLE("glyph table"),
		PALETTE_TABLE("palette table"),
		SIMPLE_SPRITE("simple sprite"),
		COMPOSITE_SPRITE("composite sprite"),
		
		BANFONT_TABLE("banfont unknown data");
		

		public final String name;
		
	    private SiroLayout(String name) {
	        this.name = name;
	    }
	}
}