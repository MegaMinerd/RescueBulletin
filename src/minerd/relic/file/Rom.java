package minerd.relic.file;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public class Rom{
	private FileChannel file;
	private String filename;
	private static Rom instance;
	
	public static void load(File fileIn) throws IOException {
		instance = new Rom(fileIn);
	}
	
	public static boolean isLoaded() {
		return instance != null;
	}
	
	private Rom(File fileIn) throws IOException{
		Set<StandardOpenOption> options = new HashSet<>();
		options.add(StandardOpenOption.READ);
		options.add(StandardOpenOption.WRITE);
		file = FileChannel.open(fileIn.toPath(), options);
		filename = fileIn.getName();
	}
	
	public static String getFilename() {
		return instance.filename;
	}
	
	public static RomFile getAll() throws IOException{
		instance.file.position(0);
		ByteBuffer buffer = ByteBuffer.allocate(0x2000000);
		instance.file.read(buffer);
		return new RomFile(buffer);
	}
	
	public static void saveAll(RomFile data) throws IOException{
		instance.file.position(0);
		data.seek(0);
		instance.file.write(data.getBuffer());
	}
}

