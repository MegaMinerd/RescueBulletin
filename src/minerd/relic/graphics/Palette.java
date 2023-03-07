package minerd.relic.graphics;

import java.io.IOException;

import minerd.relic.RomManipulator;

public class Palette {
	private final int[] rgb;
	
	public Palette(boolean color0IsAlpha) throws IOException {
		rgb = new int[16];
		rgb[0] = color0IsAlpha ? 0: loadColor();
		for(int i=1; i<16; i++) {
			rgb[i]=loadColor();
		}
	}
	
	private int loadColor() throws IOException {
		int temp = 0;
		temp += (RomManipulator.readUnsignedByte())<<16;
		temp += (RomManipulator.readUnsignedByte())<<8;
		temp += (RomManipulator.readUnsignedByte());
		RomManipulator.skip(1);
		return temp;
	}
	
	public int getRgb(int id) {
		return rgb[id];
	}
}
