package minerd.relic.data.dungeon;

import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.layout.Region;
import minerd.relic.data.GameData;
import minerd.relic.data.Text;
import minerd.relic.file.RomFile;

public class TrapList extends GameData {
	ArrayList<Trap> entries;

	public TrapList(RomFile rom) throws IOException {
		entries = new ArrayList<Trap>();
		int lastProb = 0;
		for(int i = 0; i<20; i++){
			int prob = rom.readUnsignedShort();
			entries.add(new Trap(Text.getText("Traps", i), Math.max(prob - lastProb, 0)));
			lastProb = prob==0 ? lastProb : prob;
		}
	}

	public Region load() throws IOException {
		return null;
	}

	@Override
	public void save(RomFile rom) {

	}

	public String getName() {
		return "Trap List";
	}

	public ArrayList<Trap> getEntries() {
		return entries;
	}
}