package minerd.relic.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class SbinFile extends BufferedDataHandler {
	private String name;
	private int offset; //The offset of this sbin within the full rom
	private HashMap<String, BufferedDataHandler> contents;
	private HashMap<String, Integer> offsets;
	private HashMap<String, ArrayList<String>> aliases;

	public SbinFile(ByteBuffer bufferIn, String nameIn, int offsetIn) {
		super(bufferIn);
		this.name = nameIn;
		this.offset = offsetIn;

		contents = new HashMap<String, BufferedDataHandler>();
		offsets = new HashMap<String, Integer>();
		aliases = new HashMap<String, ArrayList<String>>();
		try{
			seek(8);
			int count = (int) readUnsignedInt();
			seek(24);
			ArrayList<String> names = new ArrayList<String>(count);
			ArrayList<Pointer> pointers = new ArrayList<Pointer>(count);
			//Read the table of contents
			for(int i = 0; i<count; i++){
				names.add(readString(parsePointer().relativeTo(offset)));
				pointers.add(parsePointer());
			}
			//Detect aliases
			for(int i = 0; i<pointers.size(); i++){
				Pointer p = pointers.get(i);
				if(i==pointers.indexOf(p)){
					//This is the first occurrence of this pointer, create a new alias list
					aliases.put(names.get(i), new ArrayList<String>());
					aliases.get(names.get(i)).add(names.get(i));
				} else{
					//This a repeat occurrence of this pointer, grab the existing alias list
					//System.out.println(names.get(i) + " is an alias of " + names.get(pointers.indexOf(p)));
					aliases.put(names.get(i), aliases.get(names.get(pointers.indexOf(p))));
				}
				aliases.get(names.get(i)).add(names.get(i));
				Collections.sort(aliases.get(names.get(i)));
			}
			//Build the content table in a separate loop
			for(int i = 0; i<pointers.size() - 1; i++){
				addSubfile(names.get(i), pointers.get(i), pointers.get(i + 1));
			}
			addSubfile(names.get(pointers.size() - 1), pointers.get(pointers.size() - 1), Pointer.fromInt(length()));
		} catch(IOException | InvalidPointerException e){
			e.printStackTrace();
		}
	}

	//Helper function used by constructor to avoid code repetition
	private void addSubfile(String filename, Pointer start, Pointer end) throws IOException {
		//System.out.println(String.format("%s: %x - %x = %x", filename, start.relativeTo(offset).getOffset(), end.relativeTo(offset).getOffset(), (end.relativeTo(offset).getOffset() - start.relativeTo(offset).getOffset())));
		byte[] segment = new byte[end.relativeTo(offset).getOffset() - start.relativeTo(offset).getOffset()];
		seek(start.relativeTo(offset));
		read(segment);
		contents.put(filename, new BufferedDataHandler(ByteBuffer.wrap(segment)));
		offsets.put(filename, start.getOffset());
	}

	public void updateSubfile(String filename, BufferedDataHandler data) throws IOException {
		contents.put(filename, data);
	}

	public void buildSiroSubfile(String filename, SiroFile.SiroLayout layout, int... args) throws IOException {
		switch(layout){
			case BASIC:
				updateSubfile(filename, SiroFactory.buildBasicSiro(getSubfile(filename), getOffset(filename)));
				break;
			case ITEM:
				updateSubfile(filename, SiroFactory.buildItemSiro(getSubfile(filename), getOffset(filename)));
				break;
			case POKEMON:
				updateSubfile(filename, SiroFactory.buildPokemonSiro(getSubfile(filename), getOffset(filename)));
				break;
			case MOVE:
				updateSubfile(filename, SiroFactory.buildMoveSiro(getSubfile(filename), getOffset(filename)));
				break;
			case DUNGEON:
				updateSubfile(filename, SiroFactory.buildDungeonSiro(getSubfile(filename), getOffset(filename)));
				break;
			case GRAPHIC_LIST:
				updateSubfile(filename, SiroFactory.buildGraphicListSiro(getSubfile(filename), getOffset(filename)));
				break;
			case GRAPHIC_TABLE:
				updateSubfile(filename, SiroFactory.buildGraphicTableSiro(getSubfile(filename), getOffset(filename), args[0], args[1]));
				break;
		}

	}

	public BufferedDataHandler getSubfile(String name) {
		return contents.get(name);
	}

	public int getOffset(String filename) {
		Integer off = offsets.get(filename);
		if(off==null)
			System.out.println(filename + " returned null offset");
		return off;
	}

	public ByteBuffer saveToRom() throws IOException {
		BufferedDataHandler rom = Rom.getInstance().getAll();
		int[] pointers = new int[contents.size()*2];

		//Write header
		rom.seek(offset);
		rom.writeString("pksdir0");
		rom.writeUnsignedInt(contents.size());
		rom.writeUnsignedInt(offset + 0x18);
		rom.writeString("pksdir0");
		//Leave space for pointers
		rom.skip(8*contents.size());

		//First pass to write filenames
		int i = 0;
		String[] filenames = contents.keySet().toArray(new String[contents.keySet().size()]);
		Arrays.sort(filenames);
		for(String filename : filenames){
			pointers[i] = rom.getFilePointer();
			i += 2;

			rom.writeString(filename);
			while(getFilePointer()%4!=0)
				rom.writeByte((byte) 0);
		}
		rom.writeString("pksdir0");

		//Second pass to write data
		i = 1;
		for(String filename : filenames){
			if(aliases.get(filename).get(0).equals(filename)){
				rom.write(contents.get(filename).save());
				pointers[i] = rom.getFilePointer();
			} else{
				pointers[i] = pointers[Arrays.binarySearch(filenames, filename)*2 + 1];
			}
			i += 2;
		}

		//Write pointers
		seek(offset + 0x18);
		for(i = 0; i<filenames.length*2; i++)
			writeUnsignedInt(pointers[i]);

		//Pad any extra
		while(getFilePointer()<offset + length())
			writeByte((byte) -1);

		return buffer;
	}
}