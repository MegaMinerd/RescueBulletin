package minerd.relic.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SiroSegment {
	private int offset;
	private BufferedDataHandler data;
	//Mainly used for debug for now, may be useful later
	private DataType type;
	private HashMap<String, SiroSegment> children;

	public SiroSegment(Pointer p) {
		this(p.getOffset());
	}

	public SiroSegment(int offsetIn) {
		this(offsetIn, DataType.UNKNOWN);
	}

	public SiroSegment(Pointer p, BufferedDataHandler dataIn) {
		this(p.getOffset(), dataIn, DataType.UNKNOWN);
	}

	public SiroSegment(int offsetIn, BufferedDataHandler dataIn) {
		this(offsetIn, dataIn, DataType.UNKNOWN);
	}
	
	public SiroSegment(int offsetIn, DataType typeIn) {
		this.offset = offsetIn;
		this.children = new HashMap<String, SiroSegment>();
		this.type = typeIn;
	}

	public SiroSegment(Pointer p, BufferedDataHandler dataIn, DataType typeIn) {
		this(p.getOffset(), dataIn, typeIn);
	}

	public SiroSegment(int offsetIn, BufferedDataHandler dataIn, DataType typeIn) {
		this.offset = offsetIn;
		this.children = new HashMap<String, SiroSegment>();
		this.data = dataIn;
		this.type = typeIn;
	}

	public int getOffset() {
		return offset;
	}

	public void addChild(String name, SiroSegment child) {
		children.put(name, child);
	}

	public void removeChild(String name) {
		children.remove(name);
	}

	public void setData(BufferedDataHandler dataIn) {
		this.data = dataIn;
	}

	public HashMap<String, SiroSegment> getChildren() {
		return children;
	}

	public SiroSegment getChild(String name) {
		return children.get(name);
	}

	public SiroSegment getDescendant(String path) {
		String[] parts = path.split("/", 2);
		return parts.length==1 ? children.get(parts[0]) : children.get(parts[0]).getDescendant(parts[1]);
	}

	public BufferedDataHandler getData() {
		try{
			data.seek(0);
		} catch(IOException e){
			e.printStackTrace();
		}
		return data;
	}

	public void load(BufferedDataHandler rom) {
	}
	
	public void printTree(int tablevel, String name) throws IOException {
		for(int i=0; i<tablevel; i++)
			System.out.print("\t");
		if(data==null)
			System.out.println(String.format("%s (0x%h): ", name, offset));
		else {
			System.out.println(String.format("%s (0x%h): %d bytes (%s)", name, offset, data.length(), type));
			if(type.equals(DataType.STRING)) {
				for(int i=0; i<=tablevel; i++)
					System.out.print("\t");
				System.out.println((new BufferedDataHandler(data.getBuffer())).readString());
			}
		}
		
		if(!children.isEmpty()) {
			ArrayList<String> names = new ArrayList<String>();
			names.addAll(children.keySet());
			names.sort(null);
			for(String childname : names) {
				children.get(childname).printTree(tablevel+1, childname);
			}
		}
	}
	
	public void setType(DataType typeIn) {
		this.type = typeIn;
	}
	
	public enum DataType{
		GENERIC,
		STRING,
		
		PALETTE,
		GRAPHICS,
		ARRANGEMENT,
		GLYPH,
		
		ITEM,
		LEVELMAP,
		POKEMON,
		LEARNSET,
		MOVE,
		
		DUNGEON_MAIN,
		FLOOR_LAYOUT,
		LOOT_TABLE,
		SPAWN_TABLE,
		TRAP_LIST,
		
		METADATA,
		UNKNOWN;
	}
}