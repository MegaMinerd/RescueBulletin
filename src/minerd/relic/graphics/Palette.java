package minerd.relic.graphics;

import java.io.IOException;
import javafx.scene.paint.Color;

import minerd.relic.RomManipulator;

public class Palette {
	private final Color[] colors;
	
	public Palette(boolean color0IsAlpha) throws IOException {
		colors = new Color[16];
		colors[0] = color0IsAlpha ? Color.TRANSPARENT : loadColor();
		for(int i=1; i<16; i++) {
			colors[i]=loadColor();
		}
	}
	
	private Color loadColor() throws IOException {
		int red = (RomManipulator.readUnsignedByte());
		int green = (RomManipulator.readUnsignedByte());
		int blue = (RomManipulator.readUnsignedByte());
		int alpha = (RomManipulator.readUnsignedByte());
		//System.out.println(String.format("Color: %f, %f, %f, %f", red/255.0, green/255.0, blue/255.0, alpha/127.0));
		return new Color(red/255.0, green/255.0, blue/255.0, alpha/128.0);
	}
	
	public Color getColor(int id) {
		return colors[id];
	}
}