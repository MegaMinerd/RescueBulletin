package minerd.relic.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import minerd.relic.RomManipulator;

public class Tile {
	private final int[][] data;
	
	@Deprecated
	public Tile(int[][] dataIn) {
		this.data = dataIn;
	}
	
	public Tile(int height, int width) throws IOException {
		data = new int[height][width];
		for(int row=0; row<height; row++) {
			for(int col=0; col<width; col+=2) {
				int[] pair = RomManipulator.readMask(1, 4, 4);
				data[row][col] = pair[0];
				data[row][col+1] = pair[1];
			}
		}
	}
	
	public BufferedImage render(Palette pal, boolean hor, boolean ver) {
		BufferedImage tile = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				tile.setRGB(j, i, pal.getRgb(data[ver?7-i:i][hor?7-j:j]));
			}
		}
		return tile;
	}

}
