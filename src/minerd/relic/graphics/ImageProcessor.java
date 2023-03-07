package minerd.relic.graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import minerd.relic.InvalidPointerException;
import minerd.relic.RomManipulator;
import minerd.relic.util.FileSystem;

public class ImageProcessor{
	//Todo: make a class to wrap the variables and return that instead of BufferedImage
	//And then handle layers and animation elsewhere in the way that's needed contextually
	private static Palette[] palettes;
	private static Tile[] stills;
	private static Tile[][] anims;
	private static Chunk[] chunks;
	private static Chunk[][] animChunks;
	private static BufferedImage[] layers;
	private static int palPointer, tileDefPointer, chunkDefPointer, imgDefPointer, animDefPointer, extrasDefPointer;
	private static int chunkWidth, chunkHeight, stillCount, chunkCount, animCount, frameCount, rows, cols;
	private static boolean hasAnim, hasExtras;
	
	//Cache for common and shared sprites
	private static Tile[][] itemTiles;
	private static Palette[] itemPalettes;
	
	
	public static BufferedImage getItemSprite(int sprite, int palette) throws IOException, InvalidPointerException {
		if(itemTiles == null) {
			//Load sprites
			RomManipulator.seek(0x01E76170);
			//Navigate SIRO footer
			RomManipulator.skip(4);
			RomManipulator.seek(RomManipulator.parsePointer());
			RomManipulator.skip(12);
			RomManipulator.seek(RomManipulator.parsePointer());
			int itemPointer = RomManipulator.getFilePointer();
			itemTiles = new Tile[24][4];
			for(int i=0; i<24; i++) {
				RomManipulator.seek(itemPointer+4*i);
				RomManipulator.seek(RomManipulator.parsePointer());
				RomManipulator.seek(RomManipulator.parsePointer());
				for(int j=0; j<4; j++)
					itemTiles[i][j] = new Tile(8, 8);
			}
			RomManipulator.seek(0x016BD42C);
			itemPalettes = new Palette[13];
			for(int i=0; i<13; i++)
				itemPalettes[i] = new Palette(false);
		}
		
		BufferedImage itemSprite = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics g = itemSprite.getGraphics();
		g.drawImage(itemTiles[sprite][0].render(itemPalettes[palette], false, false), 0, 0, null);
		g.drawImage(itemTiles[sprite][1].render(itemPalettes[palette], false, false), 8, 0, null);
		g.drawImage(itemTiles[sprite][2].render(itemPalettes[palette], false, false), 0, 8, null);
		g.drawImage(itemTiles[sprite][3].render(itemPalettes[palette], false, false), 8, 8, null);
		
		return itemSprite;
	}
	
	//Todo: untested port from precursor tool
	public static TiledImage extractBackground(int offset){
		try{
			loadPointers(offset);
			System.out.println("Palette: " + Integer.toHexString(palPointer).toUpperCase());
			System.out.println("Tiles: " + Integer.toHexString(tileDefPointer).toUpperCase());
			System.out.println("Mapping: " + Integer.toHexString(imgDefPointer).toUpperCase());
			
			buildPalettes(false);
			
			//Parse animation metadata
			if(hasAnim) {
				RomManipulator.seek(animDefPointer);
				int animWidth = RomManipulator.readUnsignedShort();
				int animHeight = RomManipulator.readUnsignedShort();
				animCount = animWidth*animHeight;
			}else {
				animCount = 0;
			}
			
			//Parse tile/chunk metadata
			RomManipulator.seek(tileDefPointer);
			chunkWidth = RomManipulator.readUnsignedShort();
			chunkHeight = RomManipulator.readUnsignedShort();
			stillCount = RomManipulator.readUnsignedShort()-1;
			RomManipulator.skip(8);        //Todo: The use of these bytes is unknown
			chunkCount = RomManipulator.readUnsignedShort()-1;
			
			//Build still tiles
			buildStills();
			
			chunkDefPointer = RomManipulator.getFilePointer();
			
			//Build animated tiles, if any
			if(hasAnim) 
				buildAnims();
			
			//Build chunks
			RomManipulator.seek(chunkDefPointer);
			buildChunks();
			
			//Parse image metadata
			RomManipulator.seek(imgDefPointer+4);
			cols = RomManipulator.readUnsignedByte();
			rows = RomManipulator.readUnsignedByte();
			RomManipulator.skip(6);
			
			//buildImage
			buildChunks();
			
			//Todo: finish
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private static void loadPointers(int offset) throws InvalidPointerException, IOException{
		RomManipulator.seek(offset);
		String name = RomManipulator.readStringAndReturn(RomManipulator.parsePointer());
		palPointer = FileSystem.lookup(name);
		name = RomManipulator.readStringAndReturn(RomManipulator.parsePointer());
		tileDefPointer = FileSystem.lookup(name);
		name = RomManipulator.readStringAndReturn(RomManipulator.parsePointer());
		imgDefPointer = FileSystem.lookup(name);
		try{
			name = RomManipulator.readStringAndReturn(RomManipulator.parsePointer());
			animDefPointer = FileSystem.lookup(name);
			hasAnim = true;
		}catch(InvalidPointerException e){
			hasAnim = false;
		}
		RomManipulator.skip(4);
		try{
			name = RomManipulator.readStringAndReturn(RomManipulator.parsePointer());
			extrasDefPointer = FileSystem.lookup(name);
			hasExtras = true;
		}catch(InvalidPointerException e){
			hasExtras = false;
		}
	}
	
	private static void buildPalettes(boolean color0IsAlpha) throws IOException{
		RomManipulator.seek(palPointer);
		short palCount = RomManipulator.readShort();
		palettes = new Palette[palCount];
		for(int i=0; i<palCount; i++) {
			palettes[i] = new Palette(color0IsAlpha);
		}
	}
	
	private static Tile buildTile() throws IOException{
		int[][] tileData = new int[8][8];
		for(int row=0; row<8; row++) {
			for(int col=0; col<8; col+=2) {
				int[] pair = RomManipulator.readMask(1, 4, 4);
				tileData[row][col] = pair[0];
				tileData[row][col+1] = pair[1];
			}
		}
		return new Tile(tileData);
	}
	
	private static void buildStills() throws IOException{
		stills = new Tile[stillCount];
		
		for(int tile=0; tile<(stillCount); tile++)
			stills[tile] = buildTile();
	}
	
	private static void buildAnims() throws IOException{
		//Todo: I forgot what im skipping with the next 2 lines
		RomManipulator.seek(animDefPointer + 2);
		RomManipulator.skip((RomManipulator.readUnsignedShort())*4);
		//Todo: Find the correct frame count
		for(int frame=0; frame<frameCount; frame++)
			for(int tile=0; tile<animCount; tile++)
				anims[frame][tile] = buildTile();
	}
	
	private static void buildChunks() throws IOException{
		chunks = new Chunk[chunkCount];
		System.out.println("Offset: " + Integer.toHexString(RomManipulator.getFilePointer()));
		for(int i=0; i<chunkCount; i++) {
			Chunk[] chunk = new Chunk[frameCount];
			boolean usesAnim = false;
			for(int row=0; row<chunkHeight; row++) {
				for(int col=0; col<chunkWidth; col++){
					int meta[] = RomManipulator.readMask(2, 10, 1, 1, 4);
					int id = meta[0];
					boolean hor = meta[1]==1;
					boolean ver = meta[2]==1;
					int pal = meta[3];
					//Todo: code the methods used here
					if(id<stillCount){
						chunk[0].addGraphics(stills[id].render(palettes[pal], hor, ver), row, col);
					} else {
						for(int frame=0; frame<frameCount; frame++)
							chunk[frame].addGraphics(anims[frame][id-stillCount].render(palettes[pal], hor, ver), row, col);
						usesAnim = true;
					}
				}
			}
			//Todo: can 2d arrays do this?
			if(usesAnim)
				animChunks[i] = chunks;
			else
				chunks[i] = chunk[0];
		}
	}
	
	private static Integer[] buildRow(Integer[] lastIds) throws IOException {
		ArrayList<Integer> data = new ArrayList<Integer>();
		
		//Decode Pair-24 NRL
		while(data.size()<cols) {
			int control = RomManipulator.readUnsignedByte();
			int len;
			//Null run
			if(control < 0x80) {
				len=(control+1)*2;
				for(int i=0; i<len; i++)
					data.add(0);
			}
			//Repeating run
			else if(control < 0xC0) {
				len = control - 0x80 + 1;
				int[] pair = RomManipulator.readMask(3, 12, 12);
				for(int i=0; i<len; i++) {
					data.add(pair[0]);
					data.add(pair[1]);
				}
			}
			//Literal run
			else {
				len = control - 0xC0 + 1;
				int[] pair = RomManipulator.readMask(3, 12, 12);
				for(int i=0; i<len; i++) {
					data.add(pair[0]);
					data.add(pair[1]);
				}
			}
		}
		
		//XOR with previous row for Reasons
		Integer[] out = data.toArray(new Integer[cols]);
		for(int i=0; i<out.length; i++)
			out[i] = lastIds[i]^out[i];
		return out;
	}
	
	//Todo: support 2 layers in addition to animation
	private static void buildImage() throws IOException{
		Graphics[] g = new Graphics[frameCount];
		for(int i=0; i<frameCount; i++)
			layers[i] = new BufferedImage(cols*chunkWidth*8, rows*chunkHeight*8, BufferedImage.TYPE_INT_RGB);
		
		//Build first row
		Integer[] blank = new Integer[(int)(Math.ceil(cols/2.0)*2)];
		for(int i=0; i<blank.length; i++)
			blank[i] = 0;
		Integer[] ids = buildRow(blank);
		for(int i=0; i<cols; i++) {
			try {
				g[0].drawImage(chunks[ids[i]-1].getGraphics(), i*chunkWidth*8, chunkHeight*8*rows, null);
			}
			//In theory, this will occur iff there was an animated tile in the chunk
			catch(NullPointerException | ArrayIndexOutOfBoundsException e) {
				for(int frame=0; frame<frameCount; frame++)
					g[frame].drawImage(animChunks[frame][ids[i]-1].getGraphics(), i*chunkWidth*8, chunkHeight*8*rows, null);
			}
		}
		
		//Build subsequent rows
		for(int i=1; i<rows; i++) {
			ids = buildRow(ids);
			for(int j=0; j<cols; j++){
				try {
					g[0].drawImage(chunks[ids[i]-1].getGraphics(), i*chunkWidth*8, chunkHeight*8*rows, null);
				}
				//In theory, this will occur iff there was an animated tile in the chunk
				catch(NullPointerException | ArrayIndexOutOfBoundsException e) {
					for(int frame=0; frame<frameCount; frame++)
						g[frame].drawImage(animChunks[frame][ids[i]-1].getGraphics(), i*chunkWidth*8, chunkHeight*8*rows, null);
				}
			}
		}
		
		System.out.println("End: " + Integer.toHexString(RomManipulator.getFilePointer()));
	}
}