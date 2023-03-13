package minerd.relic.graphics;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class Chunk {
	private int rows, cols;
	private Palette[] palettes;
	private Tile[][] tiles;
	private TileProperties[][] props;
	
	public Chunk(int rows, int cols){
		this.rows = rows;
		this.cols = cols;
		this.tiles = new Tile[rows][cols];
	}
	
	public Chunk(int rows, int cols, Tile[] tiles){
		this(rows, cols);
		addTiles(0, tiles);
	}
	
	public Chunk(int rows, int cols, int displace, Tile[] tiles){
		this(rows, cols);
		addTiles(displace, tiles);
	}
	
	public void addTiles(int displace, Tile[] tiles){
		for(int i=0; i<tiles.length; i++){
			int row = (i+displace)/cols; //Integer division, should give floor
			int col = (i+displace)%cols;
			this.tiles[row][col] = tiles[i];
		}
	}
	
	public void setTileProperties(int row, int col, int pal, boolean hor, boolean ver){
		props[row][col] = new TileProperties(pal, hor, ver);
	}
	
	//Version used for backgrounds
	public WritableImage renderGraphics(){
		WritableImage chunkImg = new WritableImage(cols*8, rows*8);
		PixelWriter writer = chunkImg.getPixelWriter();
		for(int row=0; row<rows; row++) {
			for(int col=0; col<cols; col++) {
				WritableImage tileImg = tiles[row][col].render(
					palettes[props[row][col].getPal()], 
					props[row][col].getHor(),
					props[row][col].getVer()
				);
				writer.setPixels(col*8, row*8, 8, 8, tileImg.getPixelReader(), 0, 0);
			}
		}
		return chunkImg;
	}
	
	//Version used for item sprites
	public WritableImage renderGraphics(Palette pal) {
		WritableImage chunkImg = new WritableImage(cols*8, rows*8);
		PixelWriter writer = chunkImg.getPixelWriter();
		for(int row=0; row<rows; row++) {
			for(int col=0; col<cols; col++) {
				WritableImage tileImg = tiles[row][col].render(pal);
				writer.setPixels(col*8, row*8, 8, 8, tileImg.getPixelReader(), 0, 0);
			}
		}
		return chunkImg;
	}
	   
	
	public void addGraphics(Object transform, int row, int col) {
		// TODO Auto-generated method stub
	}
	
	private class TileProperties{
		private final boolean hor, ver;
		private final int pal;
		
		public TileProperties(int pal, boolean hor, boolean ver){
			this.pal = pal;
			this.hor = hor;
			this.ver = ver;
		}
		
		public int getPal(){
			return pal;
		}
		
		public boolean getHor(){
			return hor;
		}
		
		public boolean getVer(){
			return ver;
		}
	}
}