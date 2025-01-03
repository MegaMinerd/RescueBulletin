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
			//kanji_a: undocumented basic siro
			//kanji_b: undocumented basic siro
			//lvmp###: too many to do by default
			system.buildSiroSubfile("monspara", SiroLayout.POKEMON);
			//system.buildSiroSubfile("wazapara", SiroLayout.MOVE);
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
			//wmapcani: siro palette
			//wmapfont: non-siro compressed image
			//wmapmcc:  siro compressed tiling
			//wmappal:  non-siro palette
			//wmapspr:  non-siro unknown data
			//wmp2cani: siro palette
			//wmp2font: non-siro compressed image
			//wmp2mcc:  siro compressed tiling
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
			dungeon.buildSiroSubfile("mapparam", SiroLayout.DUNGEON);
			//talk0-talk42: siro string table
			//talkp0-talkp42: siro string table
			dungeon.buildSiroSubfile("trappat", SiroLayout.GRAPHIC_LIST);
			dungeon.buildSiroSubfile("zmappat", SiroLayout.GRAPHIC_TABLE, 0x40, 0x300);
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
			//ax001-ax423:   siro sprite
			//kao001-kao423: 73x siro palette and compressed images
			//palet:         non-siro general use palettes
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
