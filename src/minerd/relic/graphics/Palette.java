package minerd.relic.graphics;

import java.io.IOException;

import javafx.scene.paint.Color;
import minerd.relic.file.BufferedDataHandler;

public class Palette {
	private final Color[] colors;
	
	public Palette(BufferedDataHandler data, boolean color0IsAlpha) throws IOException {
		colors = new Color[16];
		colors[0] = color0IsAlpha ? Color.TRANSPARENT : loadColor(data);
		for(int i=1; i<16; i++) {
			colors[i]=loadColor(data);
		}
	}
	
	private Color loadColor(BufferedDataHandler data) throws IOException {
		int red = (data.readUnsignedByte());
		int green = (data.readUnsignedByte());
		int blue = (data.readUnsignedByte());
		int alpha = (data.readUnsignedByte());
		return new Color(red/255.0, green/255.0, blue/255.0, alpha/128.0);
	}
	
	public Color getColor(int id) {
		return colors[id];
	}
}