package minerd.relic.data.dungeon;

import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.layout.Region;
import minerd.relic.data.GameData;
import minerd.relic.file.RomFile;

public class EncounterList extends GameData {
	ArrayList<Encounter> entries;

	public EncounterList(RomFile rom) throws IOException {
		short lastProb = 0;
		short lastHouseProb = 0;
		entries = new ArrayList<Encounter>();
		while(rom.read(8)!=0){
			rom.seek(rom.getFilePointer() - 8);
			int[] mon = rom.readMask(2, 9, 7);
			int prob = rom.readUnsignedShort();
			prob = Math.max(prob - lastProb, 0);
			lastProb += prob;
			int houseProb = rom.readUnsignedShort();
			houseProb = Math.max(houseProb - lastHouseProb, 0);
			lastHouseProb += houseProb;
			Encounter entry = new Encounter(mon[0], mon[1], prob, houseProb);
			rom.skip(2);
			entries.add(entry);
		}
	}

	public Region load() throws IOException {
		return null;
	}

	@Override
	public void save(RomFile rom) {
		try {
		short lastProb = 0;
		short lastHouseProb = 0;
		for(Encounter mon : entries) {
			int[] data = { mon.getId(), mon.getLevel() };
			rom.writeMask(data, 2, 9, 7);
			if(mon.getFloorProb()==0) {
				rom.writeUnsignedShort(0);
			} else {
				lastProb += mon.getFloorProb();
				rom.writeUnsignedShort(lastProb);
			}
			if(mon.getHouseProb()==0) {
				rom.writeUnsignedShort(0);
			} else {
				lastHouseProb += mon.getHouseProb();
				rom.writeUnsignedShort(lastHouseProb);
			}
			rom.writeUnsignedShort(0);
		}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return "Encounter List";
	}

	public ArrayList<Encounter> getEntries() {
		return entries;
	}
}