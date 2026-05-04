package minerd.relic.file;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import minerd.relic.file.SiroFile.SiroLayout;

/**
 * Write a description of class RedRom here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class RedRom extends Rom {
	private FileChannel file;
	private String filename;
	private HashMap<String, SbinFile> sbinCache;

	public RedRom(File fileIn) throws IOException {
		Set<StandardOpenOption> options = new HashSet<>();
		options.add(StandardOpenOption.READ);
		options.add(StandardOpenOption.WRITE);
		file = FileChannel.open(fileIn.toPath(), options);
		filename = fileIn.getName();
		sbinCache = new HashMap<String, SbinFile>();
	}

	public String getFilename() {
		return filename;
	}

	public BufferedDataHandler getAll() throws IOException {
		file.position(0);
		ByteBuffer buffer = ByteBuffer.allocate(0x2000000);
		file.read(buffer);
		return new BufferedDataHandler(buffer);
	}

	public ByteBuffer get(int start, int end) throws IOException {
		file.position(start);
		ByteBuffer buffer = ByteBuffer.allocate(end - start);
		file.read(buffer);
		return buffer;
	}

	public void saveAll(BufferedDataHandler data) throws IOException {
		file.position(0);
		data.seek(0);
		file.write(data.getBuffer());
	}

	public SbinFile getSystemSbin() throws IOException {
		SbinFile system = sbinCache.get("system");
		if(system==null){
			file.position(0x300500);
			ByteBuffer buffer = ByteBuffer.allocate(0x07FB00);
			file.read(buffer);
			system = new SbinFile(buffer, "system", 0x300500);
			//font: non-siro, count=0x88, tiles
			//fontd8x8: non-siro, count=0x180, tiles
			//fontpal: non-siro palettes
			//fontsp: non-siro, count=0x10, tiles
			//fontsppa: non-siro palette
			system.buildSiroSubfile("itempara", SiroLayout.ITEM);
			//12x12 pixel tiles
			system.buildSiroSubfile("kanji_a", SiroLayout.GLYPH_TABLE);
			system.buildSiroSubfile("kanji_b", SiroLayout.GLYPH_TABLE);
			for(int i=1; i<=421; i++) {
				String lvmp = String.format("lvmp%03d", i);
				if(!system.isAlias(lvmp))
					system.buildSiroSubfile(lvmp, SiroLayout.BASIC, "LEVELMAP");
			}	
			system.buildSiroSubfile("monspara", SiroLayout.POKEMON);
			system.buildSiroSubfile("wazapara", SiroLayout.MOVE);
			sbinCache.put("system", system);
		}
		return system;
	}

	public SbinFile getTitleMenuSbin() throws IOException {
		SbinFile titlemenu = sbinCache.get("titlemenu");
		if(titlemenu==null){
			file.position(0x380000);
			ByteBuffer buffer = ByteBuffer.allocate(0x030000);
			file.read(buffer);
			titlemenu = new SbinFile(buffer, "titlemenu", 0x380000);
			titlemenu.buildSiroSubfile("clmkpat", SiroLayout.GRAPHIC_LIST);
			//commun0:  non-siro compressed tiles
			//commun0p: non-siro palette
			//subdef:   non-siro compressed image
			//subdefp:  non-siro palette
			//titlen0:  non-siro compressed image
			//titlen0p: non-siro palette
			//titlen1:  non-siro compressed image
			//titlen1p: non-siro palette
			//titlen2:  non-siro compressed image
			//titlen2p: non-siro palette
			titlemenu.buildSiroSubfile("tmrkpat", SiroLayout.GRAPHIC_LIST);
			titlemenu.buildSiroSubfile("wlicpat", SiroLayout.GRAPHIC_LIST);
			titlemenu.buildSiroSubfile("wmapcani", SiroLayout.PALETTE_TABLE);
			//wmapfont: non-siro compressed image
			//compressed tiling
			titlemenu.buildSiroSubfile("wmapmcc", SiroLayout.BASIC, "ARRANGEMENT");
			//wmappal:  non-siro palette
			titlemenu.buildSiroSubfile("wmapspr", SiroLayout.SIMPLE_SPRITE);
			titlemenu.buildSiroSubfile("wmp2cani", SiroLayout.PALETTE_TABLE);
			//wmp2font: non-siro compressed image
			titlemenu.buildSiroSubfile("wmp2mcc", SiroLayout.BASIC, "ARRANGEMENT");
			//wmp2pal:  non-siro palette
			sbinCache.put("titlemenu", titlemenu);
		}
		return titlemenu;
	}

	public SbinFile getDungeonSbin() throws IOException {
		SbinFile dungeon = sbinCache.get("dungeon");
		if(dungeon==null){
			file.position(0x3B0000);
			ByteBuffer buffer = ByteBuffer.allocate(0x160000);
			file.read(buffer);
			dungeon = new SbinFile(buffer, "dungeon", 0x3B0000);
			for(int i=0; i<76; i++) {
				if(dungeon.getSubfile(String.format("b%02dcanm", i)) != null)
					dungeon.buildSiroSubfile(String.format("b%02dcanm", i), SiroLayout.PALETTE_TABLE);
				if(dungeon.getSubfile(String.format("b%02dcex", i)) != null)
					dungeon.buildSiroSubfile(String.format("b%02dcex", i), SiroLayout.BASIC, "UNKNOWN");
				if(dungeon.getSubfile(String.format("b%02demap", i)) != null)
					dungeon.buildSiroSubfile(String.format("b%02demap", i), SiroLayout.BASIC, "UNKNOWN");
			}
			//siro unknown data
			dungeon.buildSiroSubfile("banfont", SiroLayout.BANFONT_TABLE);
			//banrpal: non-siro palette
			//siro unknown data
			dungeon.buildSiroSubfile("colvec", SiroLayout.BASIC, "UNKNOWN");
			//etcfont: non-siro dungeon ui images
			//siro dungeon shadow/ripple images
			dungeon.buildSiroSubfile("etcfonta", SiroLayout.BASIC, "GRAPHICS");
			dungeon.buildSiroSubfile("fixedmap", SiroLayout.VARIABLE_LENGTH_TABLE, "UNKNOWN");
			//hp5font: non-siro int count and tiles 
			//item icons; null palette
			dungeon.buildSiroSubfile("itempat", SiroLayout.GRAPHIC_LIST, "0C00", "1");
			dungeon.buildSiroSubfile("jyochu", SiroLayout.SIMPLE_SPRITE);
			//floor number font
			dungeon.buildSiroSubfile("levfont", SiroLayout.BASIC, "GRAPHICS");
			dungeon.buildSiroSubfile("mapparam", SiroLayout.DUNGEON);
			
			for(int i=0; i<43; i++) {
				if(dungeon.getSubfile(String.format("b%dtalk", i)) != null)
					dungeon.buildSiroSubfile(String.format("b%dtalk", i), SiroLayout.PALETTE_TABLE);
			}
			//talk0-talk42: siro string table
			//talkp0-talkp42: siro string table
			dungeon.buildSiroSubfile("trappat", SiroLayout.GRAPHIC_LIST);
			dungeon.buildSiroSubfile("zmappat", SiroLayout.GRAPHIC_TABLE, "40", "300");
		}
		return dungeon;
	}
	
	public SbinFile getMonsterSbin() throws IOException {
		SbinFile monster = sbinCache.get("monster");
		if(monster==null){
			file.position(0x510000);
			ByteBuffer buffer = ByteBuffer.allocate(0x01230000);
			file.read(buffer);
			monster = new SbinFile(buffer, "monster", 0x510000);
			//ax001-ax423: siro sprites
			for(int i=1; i<424; i++)
				monster.buildSiroSubfile(String.format("ax%03d", i), SiroLayout.COMPOSITE_SPRITE);
			//73 siro palettes and compressed images
			for(int i=1; i<424; i++)
				if(monster.getSubfile(String.format("kao%03d", i))!=null)
					monster.buildSiroSubfile(String.format("kao%03d", i), SiroLayout.PORTRAIT);
			//palet: non-siro general use palettes
		}
		return monster;
	}
	
	public SbinFile getEffectSbin() throws IOException {
		SbinFile effect = sbinCache.get("effect");
		if(effect==null){
			file.position(0x01740000);
			ByteBuffer buffer = ByteBuffer.allocate(0x150000);
			file.read(buffer);
			effect = new SbinFile(buffer, "effect", 0x01740000);
			//efbg000-efbg007: siro full screen animation
			//efob000-efob138: siro sprite
		}
		return effect;
	}

	public BufferedDataHandler getDungeonData(int index) throws IOException {
		int start = 0x109D30 + index*0x10;
		return new BufferedDataHandler(get(start, start + 0x10));
	}
}
