package minerd.relic.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class SbinFile extends RomFile {
	private HashMap<String, Pointer> contents;

	public SbinFile(ByteBuffer bufferIn) {
		super(bufferIn);
		try{
			seek(8);
			long count = readUnsignedInt();
			for(int i = 0; i<count; i++)
				contents.put(readString(parsePointer()), parsePointer());
		} catch(IOException | InvalidPointerException e){

		}
	}

	// Todo contents should be a buffer collection
	public ByteBuffer getSection(int size, int... path)
			throws IOException, IndexOutOfBoundsException, InvalidPointerException {
		// seek(footer);
		for(int sectId : path){
			skip(4*sectId);
			seek(parsePointer());
		}
		byte[] data = new byte[size];
		// subBuffer.put(0, buffer, getFilePointer(), size);
		return buffer.slice(getFilePointer(), size);
	}
}