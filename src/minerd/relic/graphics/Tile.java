
package minerd.relic.graphics;

import java.io.IOException;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;

import minerd.relic.RomManipulator;

public class Tile {
	private final int[][] data;
	private final int height, width;
	
	public Tile(int height, int width) throws IOException {
		this.height = height;
		this.width = width;
		data = new int[height][width];
		for(int row=0; row<height; row++) {
			for(int col=0; col<width; col+=2){
				int[] pair = RomManipulator.readMask(1, 4, 4);
				data[row][col] = pair[0];
				data[row][col+1] = pair[1];
			}
		}
	}
	
	public Tile(int[][] dataIn) {
		this.height = dataIn.length;
		this.width = dataIn[0].length;
		this.data = dataIn;
	}
	
	public static Tile empty() {
		int[][] data = {
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0}
		};
		return new Tile(data);
	}
	
	public WritableImage render(Palette pal) {
		return render(pal, false, false);
	}
	
	public WritableImage render(Palette pal, boolean horFlip, boolean verFlip) {
		WritableImage tileImg = new WritableImage(width, height);
		PixelWriter writer = tileImg.getPixelWriter();
		for(int row=0; row<height; row++) {
			for(int col=0; col<width; col++) {
				writer.setColor(col, row, pal.getColor(data[verFlip?height-row:row][horFlip?width-col:col]));
			}
		}
		return tileImg;
	}
}