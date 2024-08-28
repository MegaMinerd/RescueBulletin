package minerd.relic.file;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class Rom {

	public abstract String getFilename();

	public abstract BufferedDataHandler getAll() throws IOException;

	public abstract ByteBuffer get(int start, int end) throws IOException;

	public abstract void saveAll(BufferedDataHandler data) throws IOException;

}