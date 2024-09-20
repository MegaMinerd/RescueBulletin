package minerd.relic.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class SbinFile extends BufferedDataHandler {
	private String name;
	private int offset; //The offset of this sbin within the full rom
	private HashMap<String, BufferedDataHandler> contents;
	private HashMap<String, Integer> offsets;
	private HashMap<String, String> aliases;

	public SbinFile(ByteBuffer bufferIn, String nameIn, int offsetIn) {
		super(bufferIn);
		this.name = nameIn;
		this.offset = offsetIn;
		System.out.println(offset);

		contents = new HashMap<String, BufferedDataHandler>();
		offsets = new HashMap<String, Integer>();
		aliases = new HashMap<String, String>();
		try{
			seek(8);
			int count = (int) readUnsignedInt();
			System.out.println(count + " files");
			seek(24);
			ArrayList<String> names = new ArrayList<String>(count);
			ArrayList<Pointer> pointers = new ArrayList<Pointer>(count);
			//Read the table of contents
			for(int i = 0; i<count; i++){
				names.add(readString(parsePointer().relativeTo(offset)));
				pointers.add(parsePointer());
			}
			//Detect aliases
			int i = 0;
			while(i<pointers.size()){
				Pointer p = pointers.get(i);
				if(i==pointers.indexOf(p)){
					//This is the first occurrence of this pointer, move on.
					i++;
				} else{
					//This a repeat occurrence of this pointer, register an alias and remove it
					aliases.put(names.get(i), names.get(pointers.indexOf(p)));
					names.remove(i);
					pointers.remove(i);
				}
			}
			//Build the content table in a separate loop
			for(i = 0; i<pointers.size() - 1; i++){
				addSubfile(names.get(i), pointers.get(i), pointers.get(i + 1));
			}
			addSubfile(names.get(pointers.size() - 1), pointers.get(pointers.size() - 1), Pointer.fromInt(length()));
		} catch(IOException | InvalidPointerException e){
			e.printStackTrace();
		}
	}

	//Helper function used by constructor to avoid code repetition
	private void addSubfile(String filename, Pointer start, Pointer end) throws IOException {
			System.out.println(filename + ": " + start.relativeTo(offset).getOffset() + " - " + end.relativeTo(offset).getOffset());
				(end.relativeTo(offset).getOffset() - start.relativeTo(offset).getOffset())));
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

	public ByteBuffer save() throws IOException {
		int size = 32 + 8*contents.size();
		int[] pointers = new int[contents.size()*2];

		//First pass to poll for changes and tally total size
		for(String filename : contents.keySet()){
			//RomFile file = FileSystem.getCachedFile(name + "/" + filename);
			//ByteBuffer data = file==null ? contents.get(filename) : file.save();
			//size += Math.ceil((filename.length() + 1)/4.0)*4 + data.length();
		}

		//Reason for first pass: capacity assurance
		if(buffer.capacity()<size)
			buffer = ByteBuffer.allocate(size);

		//Write header
		seek(0);
		writeString("pksdir0");
		writeUnsignedInt(contents.size());
		writeUnsignedInt(offset + 0x18);
		writeString("pksdir0");
		skip(8*contents.size());

		//Second pass to write filenames
		int i = 0;
		for(String filename : contents.keySet()){//Arrays.sort(contents.keySet())){
			pointers[i] = offset + getFilePointer();
			i += 2;

			writeString(filename);
			while(getFilePointer()%4!=0)
				writeByte((byte) 0);
		}
		writeString("pksdir0");

		//Third pass to write data
		i = 1;
		for(String filename : contents.keySet()){//Arrays.sort(contents.keySet())){
			pointers[i] = offset + getFilePointer();
			i += 2;

			//write(contents.get(filename).array());
		}

		//Write pointers
		seek(0x18);
		for(i = 0; i<contents.size()*2; i++)
			writeUnsignedInt(pointers[i]);

		//Pad any extra
		while(getFilePointer()<length())
			writeByte((byte) -1);

		return buffer;
	}
}