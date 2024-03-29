package minerd.relic.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class SbinFile extends BufferedDataHandler {
	private String name;
	private int offset; // The offset of this sbin within the full rom
	private HashMap<String, ByteBuffer> contents;

	public SbinFile(ByteBuffer bufferIn, String nameIn, int offsetIn) {
		super(bufferIn);
		this.name = nameIn;
		this.offset = offsetIn;

		contents = new HashMap<String, ByteBuffer>();
		try{
			seek(8);
			int count = (int) readUnsignedInt();
			seek(24);
			String[] names = new String[count];
			Pointer[] pointers = new Pointer[count];
			// Read the table of contents
			for(int i = 0; i<count; i++){
				names[i] = readString(parsePointer());
				pointers[i] = parsePointer();
			}
			// Build the content table in a separate loop
			// Doing this as a single loop would be complicated due to referencing i+i
			for(int i = 0; i<count - 1; i++)
				addSegment(names[i], pointers[i].getOffset(), pointers[i + 1].getOffset());
			addSegment(names[count], pointers[count].getOffset(), length());
		} catch(IOException | InvalidPointerException e){

		}
	}

	// Helper function used by constructor to avoid code repetition
	private void addSegment(String filename, int start, int end) throws IOException {
		byte[] segment = new byte[start - end];
		seek(start);
		read(segment);
		contents.put(filename, ByteBuffer.wrap(segment));
	}

	public ByteBuffer save() throws IOException {
		int size = 32 + 8*contents.size();
		int[] pointers = new int[contents.size()*2];

		// First pass to poll for changes and tally total size
		for(String filename : contents.keySet()){
			// RomFile file = FileSystem.getCachedFile(name + "/" + filename);
			// ByteBuffer data = file==null ? contents.get(filename) : file.save();
			// size += Math.ceil((filename.length() + 1)/4.0)*4 + data.length();
		}

		// Reason for first pass: capacity assurance
		if(buffer.capacity()<size)
			buffer = ByteBuffer.allocate(size);

		// Write header
		seek(0);
		writeString("pksdir0");
		writeUnsignedInt(contents.size());
		writeUnsignedInt(offset + 0x18);
		writeString("pksdir0");
		skip(8*contents.size());

		// Second pass to write filenames
		int i = 0;
		for(String filename : contents.keySet()){// Arrays.sort(contents.keySet())){
			pointers[i] = offset + getFilePointer();
			i += 2;

			writeString(filename);
			while(getFilePointer()%4!=0)
				writeByte((byte) 0);
		}
		writeString("pksdir0");

		// Third pass to write data
		i = 1;
		for(String filename : contents.keySet()){// Arrays.sort(contents.keySet())){
			pointers[i] = offset + getFilePointer();
			i += 2;

			write(contents.get(filename).array());
		}

		// Write pointers
		seek(0x18);
		for(i = 0; i<contents.size()*2; i++)
			writeUnsignedInt(pointers[i]);

		// Pad any extra
		while(getFilePointer()<length())
			writeByte((byte) -1);

		return buffer;
	}

	public ByteBuffer getSubfile(String name) {
		return contents.get(name);
	}
}