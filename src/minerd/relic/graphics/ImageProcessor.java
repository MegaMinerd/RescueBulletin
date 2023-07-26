package minerd.relic.graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;
import minerd.relic.util.CompressionHandler;
import minerd.relic.util.FileSystem;

public class ImageProcessor {
	private static BufferedDataHandler rom;

	//Todo: make a class to wrap the variables and return that instead of
	//BufferedImage
	//And then handle layers and animation elsewhere in the way that's needed
	//contextually
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
	private static Chunk[] itemSprites;
	private static Palette[] itemPalettes;

	static{
		//Load palettes
		try{
			rom = Rom.getAll();
			rom.seek(0x016BD42C);
			itemPalettes = new Palette[13];
			for(int i = 0; i<13; i++)
				itemPalettes[i] = new Palette(rom, false);

			//Todo: make this go through readSpriteSiro
			//Goto SIRO file
			rom.seek(0x01E76170);
			//Navigate SIRO footer
			rom.skip(4);
			rom.seek(rom.parsePointer());
			rom.skip(12);
			rom.seek(rom.parsePointer());
			int itemPointer = rom.getFilePointer();
			//Load sprites
			itemSprites = new Chunk[24];
			for(int i = 0; i<24; i++){
				rom.seek(itemPointer + 4*i);
				rom.seek(rom.parsePointer());
				itemSprites[i] = buildSprite(2, 2);
			}
		} catch(IOException | InvalidPointerException e){
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static WritableImage getItemSprite(int sprite, int palette) throws IOException, InvalidPointerException {
		return itemSprites[sprite].renderGraphics(itemPalettes[palette]);
	}

	public static void importPortraits(File imgFile, int destOff, int faceFlags, boolean symmetrical) throws IOException {
		Image image = new Image(imgFile.getPath());
		PixelReader reader = image.getPixelReader();
		BufferedDataHandler rom = Rom.getAll();
		rom.seek(destOff + 16);
		ArrayList<Integer> pointers = new ArrayList<Integer>();
		//normal happy pain angry worried sad
		//crying shouting teary-eyed determined joyous inspired
		//surprised dizzy special0/1 sigh stunned special2/3
		for(int facecol = 0; facecol<6; facecol++){
			for(int facerow = 0; facerow<5; facerow++){
				if((faceFlags & (1 << (facerow + 5*facecol)))>0){
					BufferedDataHandler[] faceData = convertPortrait(reader, facerow*40, facecol*40);
					pointers.add(rom.getFilePointer() + 0x8000000);
					rom.write(faceData[0], 0);
					pointers.add(rom.getFilePointer() + 0x8000000);
					rom.write(CompressionHandler.compress(faceData[1], true), 0);
					while(rom.getFilePointer()%4!=0)
						rom.writeUnsignedByte(0);
				}
			}
		}
		int table = rom.getFilePointer() + 0x8000000;
		for(Integer pointer : pointers){
			rom.writeInt(pointer);
		}
		rom.seek(destOff);
		//SIRO
		rom.writeInt(0x4F524953);
		rom.writeInt(table);
		Rom.saveAll(rom);
	}

	private static BufferedDataHandler[] convertPortrait(PixelReader reader, int startX, int startY) throws IOException {
		ArrayList<Color> colors = new ArrayList<Color>();
		colors.add(Color.LIME);
		ArrayList<Integer> pixels = new ArrayList<Integer>();
		//TODO: Double check coordinate system.
		for(int tilecol = 0; tilecol<5; tilecol++){
			for(int tilerow = 0; tilerow<5; tilerow++){
				for(int col = 0; col<8; col++){
					for(int row = 0; row<8; row++){
						Color color = reader.getColor(tilerow*8 + row + startX, tilecol*8 + col + startY);
						if(!colors.contains(color))
							colors.add(color);
						pixels.add(colors.indexOf(color));
					}
				}
			}
		}
		BufferedDataHandler colorData = new BufferedDataHandler(ByteBuffer.allocate(64));
		colorData.seek(0);
		for(Color color : colors){
			colorData.writeUnsignedByte((int) (color.getRed()*255));
			colorData.writeUnsignedByte((int) (color.getGreen()*255));
			colorData.writeUnsignedByte((int) (color.getBlue()*255));
			colorData.writeUnsignedByte(0x80);
		}
		BufferedDataHandler pixelData = new BufferedDataHandler(ByteBuffer.allocate(800));
		pixelData.seek(0);
		for(int i = 0; i<1600; i += 2)
			pixelData.writeMask(new int[] {pixels.get(i), pixels.get(i + 1)}, 1, 4, 4);
		BufferedDataHandler[] result = { colorData, pixelData };
		return result;
	}

	//Todo: figure out suitable return type
	public static void readSpriteSiro(int offset) throws IOException, InvalidPointerException {
		rom.seek(offset);
		rom.skip(4); //Ignore magic bytes
		rom.seek(rom.parsePointer()); //Go to footer
		Pointer animationPointers = rom.parsePointer();
		Pointer directionPointers = rom.parsePointer();
		Pointer displacePointers = rom.parsePointer();
		Pointer spritePointers = rom.parsePointer();

		//Load sprites
		//Todo: generalize
		Chunk[] sprites = new Chunk[24];
		for(int i = 0; i<24; i++){
			rom.seek(spritePointers.getOffset() + 4*i);
			rom.seek(rom.parsePointer());
			rom.seek(rom.parsePointer());
			Tile[] spriteTiles = new Tile[4];
			for(int j = 0; j<4; j++)
				spriteTiles[j] = new Tile(rom, 8, 8);
			itemSprites[i] = new Chunk(2, 2, spriteTiles);
		}
	}

	public static Chunk buildSprite(int rows, int cols) throws IOException, InvalidPointerException {
		int totalSize = 0;
		int endSize = rows*cols;
		Chunk sprite = new Chunk(rows, cols);
		while(totalSize<endSize){
			Pointer pointer = rom.parsePointer();
			int displace = 0;
			if(pointer==null){
				displace = rom.readInt()/32;
				totalSize += displace;
				pointer = rom.parsePointer();
			}
			if(pointer==null)
				break;
			int size = rom.readInt()/32;
			totalSize += size;
			Tile[] tiles = new Tile[size];
			rom.seek(pointer);
			for(int i = 0; i<size; i++)
				tiles[i] = new Tile(rom, 8, 8);
			sprite.addTiles(displace, tiles);
		}
		return sprite;
	}

	//Todo: untested port from precursor tool
	public static TiledImage extractBackground(int offset) {
		try{
			loadPointers(offset);
			System.out.println("Palette: " + Integer.toHexString(palPointer).toUpperCase());
			System.out.println("Tiles: " + Integer.toHexString(tileDefPointer).toUpperCase());
			System.out.println("Mapping: " + Integer.toHexString(imgDefPointer).toUpperCase());

			buildPalettes(false);

			//Parse animation metadata
			if(hasAnim){
				rom.seek(animDefPointer);
				int animWidth = rom.readUnsignedShort();
				int animHeight = rom.readUnsignedShort();
				animCount = animWidth*animHeight;
			} else{
				animCount = 0;
			}

			//Parse tile/chunk metadata
			rom.seek(tileDefPointer);
			chunkWidth = rom.readUnsignedShort();
			chunkHeight = rom.readUnsignedShort();
			stillCount = rom.readUnsignedShort() - 1;
			rom.skip(8); //Todo: The use of these bytes is unknown
			chunkCount = rom.readUnsignedShort() - 1;

			//Build still tiles
			buildStills();

			chunkDefPointer = rom.getFilePointer();

			//Build animated tiles, if any
			if(hasAnim)
				buildAnims();

			//Build chunks
			rom.seek(chunkDefPointer);
			buildChunks();

			//Parse image metadata
			rom.seek(imgDefPointer + 4);
			cols = rom.readUnsignedByte();
			rows = rom.readUnsignedByte();
			rom.skip(6);

			//buildImage
			buildChunks();

			//Todo: finish
			return null;
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	private static void loadPointers(int offset) throws InvalidPointerException, IOException {
		rom.seek(offset);
		String name = rom.readString(rom.parsePointer());
		palPointer = FileSystem.lookup(name);
		name = rom.readString(rom.parsePointer());
		tileDefPointer = FileSystem.lookup(name);
		name = rom.readString(rom.parsePointer());
		imgDefPointer = FileSystem.lookup(name);
		try{
			name = rom.readString(rom.parsePointer());
			animDefPointer = FileSystem.lookup(name);
			hasAnim = true;
		} catch(InvalidPointerException e){
			hasAnim = false;
		}
		rom.skip(4);
		try{
			name = rom.readString(rom.parsePointer());
			extrasDefPointer = FileSystem.lookup(name);
			hasExtras = true;
		} catch(InvalidPointerException e){
			hasExtras = false;
		}
	}

	private static void buildPalettes(boolean color0IsAlpha) throws IOException {
		rom.seek(palPointer);
		short palCount = rom.readShort();
		palettes = new Palette[palCount];
		for(int i = 0; i<palCount; i++){
			palettes[i] = new Palette(rom, color0IsAlpha);
		}
	}

	private static void buildStills() throws IOException {
		stills = new Tile[stillCount];

		for(int tile = 0; tile<(stillCount); tile++)
			stills[tile] = new Tile(rom, 8, 8);
	}

	private static void buildAnims() throws IOException {
		//Todo: I forgot what im skipping with the next 2 lines
		rom.seek(animDefPointer + 2);
		rom.skip((rom.readUnsignedShort())*4);
		//Todo: Find the correct frame count
		for(int frame = 0; frame<frameCount; frame++)
			for(int tile = 0; tile<animCount; tile++)
				anims[frame][tile] = new Tile(rom, 8, 8);
	}

	private static void buildChunks() throws IOException {
		chunks = new Chunk[chunkCount];
		System.out.println("Offset: " + Integer.toHexString(rom.getFilePointer()));
		for(int i = 0; i<chunkCount; i++){
			Chunk[] chunk = new Chunk[frameCount];
			boolean usesAnim = false;
			for(int row = 0; row<chunkHeight; row++){
				for(int col = 0; col<chunkWidth; col++){
					int meta[] = rom.readMask(2, 10, 1, 1, 4);
					int id = meta[0];
					boolean hor = meta[1]==1;
					boolean ver = meta[2]==1;
					int pal = meta[3];
					//Todo: code the methods used here
					if(id<stillCount){
						chunk[0].addGraphics(stills[id].render(palettes[pal], hor, ver), row, col);
					} else{
						for(int frame = 0; frame<frameCount; frame++)
							chunk[frame].addGraphics(anims[frame][id - stillCount].render(palettes[pal], hor, ver), row, col);
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
		while(data.size()<cols){
			int control = rom.readUnsignedByte();
			int len;
			//Null run
			if(control<0x80){
				len = (control + 1)*2;
				for(int i = 0; i<len; i++)
					data.add(0);
			}
			//Repeating run
			else if(control<0xC0){
				len = control - 0x80 + 1;
				int[] pair = rom.readMask(3, 12, 12);
				for(int i = 0; i<len; i++){
					data.add(pair[0]);
					data.add(pair[1]);
				}
			}
			//Literal run
			else{
				len = control - 0xC0 + 1;
				int[] pair = rom.readMask(3, 12, 12);
				for(int i = 0; i<len; i++){
					data.add(pair[0]);
					data.add(pair[1]);
				}
			}
		}

		//XOR with previous row for Reasons
		Integer[] out = data.toArray(new Integer[cols]);
		for(int i = 0; i<out.length; i++)
			out[i] = lastIds[i] ^ out[i];
		return out;
	}

	//Todo: support 2 layers in addition to animation
	private static void buildImage() throws IOException {
		Graphics[] g = new Graphics[frameCount];
		for(int i = 0; i<frameCount; i++)
			layers[i] = new BufferedImage(cols*chunkWidth*8, rows*chunkHeight*8, BufferedImage.TYPE_INT_RGB);

		//Build first row
		Integer[] blank = new Integer[(int) (Math.ceil(cols/2.0)*2)];
		for(int i = 0; i<blank.length; i++)
			blank[i] = 0;
		Integer[] ids = buildRow(blank);
		for(int i = 0; i<cols; i++){
			try{
				//g[0].drawImage(chunks[ids[i]-1].renderGraphics(), i*chunkWidth*8,
				//chunkHeight*8*rows, null);
			}
			//In theory, this will occur iff there was an animated tile in the chunk
			catch(NullPointerException | ArrayIndexOutOfBoundsException e){
				//for(int frame=0; frame<frameCount; frame++)
				//g[frame].drawImage(animChunks[frame][ids[i]-1].renderGraphics(),
				//i*chunkWidth*8, chunkHeight*8*rows, null);
			}
		}

		//Build subsequent rows
		for(int i = 1; i<rows; i++){
			ids = buildRow(ids);
			for(int j = 0; j<cols; j++){
				try{
					//g[0].drawImage(chunks[ids[i]-1].renderGraphics(), i*chunkWidth*8,
					//chunkHeight*8*rows, null);
				}
				//In theory, this will occur iff there was an animated tile in the chunk
				catch(NullPointerException | ArrayIndexOutOfBoundsException e){
					//for(int frame=0; frame<frameCount; frame++)
					//g[frame].drawImage(animChunks[frame][ids[i]-1].renderGraphics(),
					//i*chunkWidth*8, chunkHeight*8*rows, null);
				}
			}
		}

		System.out.println("End: " + Integer.toHexString(rom.getFilePointer()));
	}
}