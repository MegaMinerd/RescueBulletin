package minerd.relic.file;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
			system.updateSubfile("itempara", SiroFactory.buildItemSiro(system.getSubfile("itempara"), system.getOffset("itempara")));
			//kanji_a: undocumented siro
			//kanji_b: undocumented siro
			int index = 1;
			while(true){
				String name = String.format("lvmp&3d", index);
				BufferedDataHandler lvmp = system.getSubfile(name);
				if(lvmp==null)
					break;
				//system.updateSubfile(name, SiroFactory.buildCompressedSiro(lvmp, system.getOffset(name)));
			}
			system.updateSubfile("monspara", SiroFactory.buildPokemonSiro(system.getSubfile("monspara"), system.getOffset("monspara")));
			system.updateSubfile("wazapara", SiroFactory.buildMoveSiro(system.getSubfile("wazapara"), system.getOffset("wazapara")));
			sbinCache.put("system", system);
		}
		return system;
	}

	public SbinFile getDungeonSbin() throws IOException {
		SbinFile dungeon = sbinCache.get("system");
		if(dungeon==null){
			file.position(0x3B0000);
			ByteBuffer buffer = ByteBuffer.allocate(0x160000);
			file.read(buffer);
			dungeon = new SbinFile(buffer, "dungeon", 0x3B0000);
			dungeon.updateSubfile("mapparam", SiroFactory.buildDungeonSiro(dungeon.getSubfile("mapparam"), dungeon.getOffset("mapparam")));
		}
		return dungeon;
	}
}
