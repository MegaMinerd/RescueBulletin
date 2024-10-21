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
			//kanji_a: undocumented siro
			//kanji_b: undocumented siro
			//lvmp###: too many to do by default
			system.buildSiroSubfile("monspara", SiroLayout.POKEMON);
			system.buildSiroSubfile("wazapara", SiroLayout.MOVE);
			sbinCache.put("system", system);
		}
		return system;
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

	public BufferedDataHandler getDungeonData(int index) throws IOException {
		int start = 0x109D30 + index*0x10;
		return new BufferedDataHandler(get(start, start + 0x10));
	}
}
