package minerd.relic.file;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class Rom {
	private static Rom instance;

	public static void load(Rom i) {
		instance = i;
	}
	
	public static Rom getInstance() {
		return instance;
	}

	public abstract String getFilename();

	public abstract BufferedDataHandler getAll() throws IOException;

	public abstract ByteBuffer get(int start, int end) throws IOException;
	
	public abstract SbinFile getSystemSbin() throws IOException;
	
	public abstract SbinFile getDungeonSbin() throws IOException;

	public abstract void saveAll(BufferedDataHandler data) throws IOException;

}