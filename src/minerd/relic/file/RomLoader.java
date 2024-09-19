package minerd.relic.file;

import java.io.File;
import java.io.IOException;

public class RomLoader {
	private static Rom instance;

	public static Rom load(File fileIn) throws IOException {
		instance = new RedRom(fileIn);
		Rom.load(instance);
		//TODO: BlueRom
		return instance;
	}

	public static boolean isLoaded() {
		return instance!=null;
	}

	public Rom getInstance() {
		return instance;
	}
}