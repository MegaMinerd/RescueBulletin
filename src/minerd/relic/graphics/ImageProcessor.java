package minerd.relic.graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
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
	private static WritableImage blankSprite;
	private static Chunk[] itemSprites;
	private static Palette[] commonPalettes;
	
	static {
		try {
			//Load palettes
			RomManipulator.seek(0x016BD42C);
			commonPalettes = new Palette[13];
			for(int i=0; i<13; i++)
				commonPalettes[i] = new Palette(false);
			
			//Load item sprites
			//Todo: make this go through readSpriteSiro
			//Goto SIRO file
			RomManipulator.seek(0x01E76170);
			//Navigate SIRO footer
			RomManipulator.skip(4);
			RomManipulator.seek(RomManipulator.parsePointer());
			RomManipulator.skip(12);
			RomManipulator.seek(RomManipulator.parsePointer());
			int itemPointer = RomManipulator.getFilePointer();
			//Load sprites
			itemSprites = new Chunk[24];
			for(int i=0; i<24; i++) {
				RomManipulator.seek(itemPointer+4*i);
				RomManipulator.seek(RomManipulator.parsePointer());
				itemSprites[i] = buildSprite(2, 2);
			}
			
			//Create a blank sprite used to fill in backgrounds
			blankSprite = new WritableImage(64, 64);
			PixelWriter writer = blankSprite.getPixelWriter();
			Color bgColor = commonPalettes[0].getColor(0);
			for(int i=0; i<64; i++) {
				for(int j=0; j<64; j++) {
					writer.setColor(i, j, bgColor);
				}
			}
		} catch (IOException | InvalidPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static WritableImage getItemSprite(int sprite, int palette) throws IOException, InvalidPointerException {
		return itemSprites[sprite].renderGraphics(commonPalettes[palette]);
	}
	
	public static Palette getCommonPalette(int id) {
		return commonPalettes[id];
	}
	
	//Todo: figure out suitable return type
	public static Sprite buildSprite(int offset) throws IOException, InvalidPointerException {
		RomManipulator.seek(offset);
		RomManipulator.skip(4); //Ignore magic bytes
		int footerPointer = RomManipulator.parsePointer();
		RomManipulator.seek(footerPointer); //Go to footer
		int animationPointers = RomManipulator.parsePointer();
		int directionPointers = RomManipulator.parsePointer();
		int animCount = RomManipulator.readInt();
		int spritesPointer = RomManipulator.parsePointer();
		int displacePointers = RomManipulator.parsePointer();
		
		//Load animations
		DirectionalAnimation[] dirAnims = new DirectionalAnimation[animCount];
		for(int animId=0; animId<animCount; animId++) {
			RomManipulator.seek(directionPointers + animId*4);
			int animPointer = RomManipulator.parsePointer();
			Animation[] anims = new Animation[8];
			for(int dir=0; dir<8; dir++) {
				RomManipulator.seek(animPointer + dir*4);
				RomManipulator.seek(RomManipulator.parsePointer());
				//System.out.println("Animation " + dir + " data: " + Integer.toHexString(RomManipulator.getFilePointer()));
				anims[dir] = buildAnimation(animationPointers, spritesPointer);
			}
			dirAnims[animId] = new DirectionalAnimation(anims);
		}
		
		return new Sprite(dirAnims);
	}
	
	public static Animation buildAnimation(int animationPointers, int spritesPointer) throws IOException, InvalidPointerException {
		ArrayList<WritableImage> frames = new ArrayList<WritableImage>(0);
		ArrayList<Integer> durations = new ArrayList<Integer>(0);
		int loopPointer;
		do {
			//Read timing data
			int duration = RomManipulator.readUnsignedShort();
			int animId = RomManipulator.readUnsignedShort();
			short horDisp = RomManipulator.readShort();
			short verDisp = RomManipulator.readShort();
			//There's 2 more shorts afterward for shadow displacement, but those aren't important right now.
			RomManipulator.skip(4);
			//To come back later
			loopPointer = RomManipulator.getFilePointer();
			
			//Detect a null entry terminator
			if(duration==0)
				break;
			
			//Create frame
			RomManipulator.seek(animationPointers + animId*4);
			RomManipulator.seek(RomManipulator.parsePointer());
			//System.out.println("Frame " + animId + " data: " + Integer.toHexString(RomManipulator.getFilePointer()));
			WritableImage frameImage = buildFrame(spritesPointer);
			int width = (int) frameImage.getWidth();
			int height = (int) frameImage.getHeight();
			WritableImage shiftedImage = new WritableImage((int)frameImage.getWidth(), (int)frameImage.getHeight());
			PixelWriter writer = shiftedImage.getPixelWriter();
			PixelReader reader = frameImage.getPixelReader();
			writer.setPixels(0, 0, width, height, blankSprite.getPixelReader(), 0, 0);
			writer.setPixels(Math.max(horDisp, 0),	  Math.max(verDisp, 0), 
							 width-Math.abs(horDisp), height-Math.abs(verDisp), 
							 reader, 
							 Math.max(0-horDisp, 0),  Math.max(0-verDisp, 0));
			frames.add(shiftedImage);
			durations.add(duration);
			
			RomManipulator.seek(loopPointer);
		} while(true);

		return new Animation(frames.toArray(new WritableImage[frames.size()]), durations.toArray(new Integer[durations.size()]));
	}
	
	public static WritableImage buildFrame(int spritesPointer) throws IOException, InvalidPointerException{
		//Read frame data		
		int index = RomManipulator.readUnsignedShort();
		int unkPalEff = RomManipulator.readUnsignedShort();
		
		//Attribute 0
		int[] tileData = RomManipulator.readMask(2, 8, 1, 1, 2, 1, 1, 2);
		byte yDisp = (byte)tileData[0];
		boolean rotateScale = tileData[1]==1;				//Always off?
		boolean doubled = rotateScale && tileData[2]==1;	//Always off?
		boolean disabled = !rotateScale && tileData[2]==1;
		int mode = tileData[3];								//Always the same?
		boolean mosaic = tileData[4]==1;					//Always off?
		boolean singlePal = tileData[5]==1;					//Always off?
		int shape = tileData[6];
		
		//Attribute 1
		int xDisp, rotateParam, size;
		boolean horFlip, verFlip;
		if(rotateScale){
			tileData = RomManipulator.readMask(2, 9, 5, 2);
			xDisp = (byte)tileData[0];
			rotateParam = tileData[1];
			size = tileData[2];
		} else {
			tileData = RomManipulator.readMask(2, 9, 3, 1, 1, 2);
			xDisp = (byte)tileData[0];
			horFlip = tileData[1]==0;
			verFlip = tileData[2]==0;
			size = tileData[3];
		}
		//System.out.println(xDisp + ", " + yDisp);
		int[][] widthTable = {{8,  16, 8},
							  {16, 32, 8},
							  {32, 32, 16},
							  {64, 64, 32}};
		int width = widthTable[size][shape];
		int[][] heightTable = {{8,  8,  16},
							   {16, 8,  32},
							   {32, 16, 32},
							   {64, 32, 64}};
		int height = heightTable[size][shape];
		
		//Attribute 2
		tileData = RomManipulator.readMask(2, 10, 2, 4);
		int tileNum = tileData[0];
		int priority= tileData[1];
		int pal = tileData[2];
		
		//Create frame for real this time
		RomManipulator.seek(spritesPointer + index*4);
		RomManipulator.seek(RomManipulator.parsePointer());
		//System.out.println("Chunk " + index + " data: " + Integer.toHexString(RomManipulator.getFilePointer()));
		Chunk frameChunk = buildSprite(height/8, width/8);
		WritableImage frameImage = frameChunk.renderGraphics(commonPalettes[pal]);
		WritableImage shiftedImage = new WritableImage(width+16, height+16);
		//System.out.println(String.format("%s, %d-%d, %d-%d", doubled, width, xDisp, height, yDisp));
		xDisp += width/2;
		yDisp += height/2;
		PixelWriter writer = shiftedImage.getPixelWriter();
		PixelReader reader = frameImage.getPixelReader();
		int x0 = Math.max(8+xDisp, 0);
		int y0 = Math.max(8-yDisp, 0);
		int w = width-Math.max(0, Math.abs(xDisp)-8);
		int h = height-Math.max(0, Math.abs(yDisp)-8);
		int xf = 0-Math.min(xDisp+8, 0);
		int yf = Math.max(yDisp-8, 0);
		writer.setPixels(0, 0, width+16, height+16, blankSprite.getPixelReader(), 0, 0);
		writer.setPixels(x0, y0, w,  h, reader, xf, yf);
		//TODO: There seems to be a bug that causes some sprites to render 8 pixels too low.
		//For now, I made the canvas bigger (to test if this would make more of the sprite visible)
		//Below is the code for shifting the image prior to this test
		//writer.setPixels(Math.max(xDisp, 0), Math.max(0-yDisp, 0), width-Math.abs(xDisp), height-Math.abs(yDisp), reader, Math.max(0-xDisp, 0), Math.max(yDisp, 0));
		return shiftedImage;
	}
	
	public static Chunk buildSprite(int rows, int cols) throws IOException, InvalidPointerException{
		int totalSize = 0;
		int endSize = rows*cols;
		ArrayList<Tile> tiles = new ArrayList<Tile>(0);
		while(totalSize<endSize){
			int pointer;
			try {
				pointer = RomManipulator.parsePointer();
			} catch(InvalidPointerException e) {
				break;
			}
			int size = RomManipulator.readInt()/32;
			if(pointer==-1) {
				for(int i=0; i<size; i++)
					tiles.add(Tile.empty());
			} else {
				int here = RomManipulator.getFilePointer();
				RomManipulator.seek(pointer);
				for(int i=0; i<size; i++)
					tiles.add(new Tile(8, 8));
				RomManipulator.seek(here);
			}
			totalSize += size;
			//System.out.println("\t" + totalSize + " tiles processed at " + Integer.toHexString(RomManipulator.getFilePointer()));
		}
		
		return new Chunk(rows, cols, tiles.toArray(new Tile[tiles.size()]));
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
			RomManipulator.skip(8);		//Todo: The use of these bytes is unknown
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
	
	private static void buildStills() throws IOException{
		stills = new Tile[stillCount];
		
		for(int tile=0; tile<(stillCount); tile++)
		stills[tile] = new Tile(8, 8);
	}
	
	private static void buildAnims() throws IOException{
		//Todo: I forgot what im skipping with the next 2 lines
		RomManipulator.seek(animDefPointer + 2);
		RomManipulator.skip((RomManipulator.readUnsignedShort())*4);
		//Todo: Find the correct frame count
		for(int frame=0; frame<frameCount; frame++)
		for(int tile=0; tile<animCount; tile++)
		anims[frame][tile] = new Tile(8, 8);
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
				//g[0].drawImage(chunks[ids[i]-1].renderGraphics(), i*chunkWidth*8, chunkHeight*8*rows, null);
			}
			//In theory, this will occur iff there was an animated tile in the chunk
			catch(NullPointerException | ArrayIndexOutOfBoundsException e) {
				//for(int frame=0; frame<frameCount; frame++)
				//g[frame].drawImage(animChunks[frame][ids[i]-1].renderGraphics(), i*chunkWidth*8, chunkHeight*8*rows, null);
			}
		}
		
		//Build subsequent rows
		for(int i=1; i<rows; i++) {
			ids = buildRow(ids);
			for(int j=0; j<cols; j++){
				try {
					//g[0].drawImage(chunks[ids[i]-1].renderGraphics(), i*chunkWidth*8, chunkHeight*8*rows, null);
				}
				//In theory, this will occur iff there was an animated tile in the chunk
				catch(NullPointerException | ArrayIndexOutOfBoundsException e) {
					//for(int frame=0; frame<frameCount; frame++)
					//g[frame].drawImage(animChunks[frame][ids[i]-1].renderGraphics(), i*chunkWidth*8, chunkHeight*8*rows, null);
				}
			}
		}
		
		System.out.println("End: " + Integer.toHexString(RomManipulator.getFilePointer()));
	}
}