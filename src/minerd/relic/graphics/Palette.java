package minerd.relic.graphics;

import java.io.IOException;

import javafx.scene.paint.Color;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

public class Palette {
	private final Color[] colors;
	
	public Palette(RomFile rom, boolean color0IsAlpha) throws IOException {
		colors = new Color[16];
		colors[0] = color0IsAlpha ? Color.TRANSPARENT : loadColor(rom);
		for(int i=1; i<16; i++) {
			colors[i]=loadColor(rom);
		}
	}
	
	private Color loadColor(RomFile rom) throws IOException {
		int red = (rom.readUnsignedByte());
		int green = (rom.readUnsignedByte());
		int blue = (rom.readUnsignedByte());
		int alpha = (rom.readUnsignedByte());
		return new Color(red/255.0, green/255.0, blue/255.0, alpha/128.0);
	}
	
	public Color getColor(int id) {
		return colors[id];
	}
}