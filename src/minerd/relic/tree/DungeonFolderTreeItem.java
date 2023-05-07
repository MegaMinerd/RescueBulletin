package minerd.relic.tree;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import minerd.relic.data.Cache;
import minerd.relic.data.Text;
import minerd.relic.data.dungeon.Dungeon;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.util.RrtOffsetList;

public class DungeonFolderTreeItem extends FolderTreeItem<Dungeon> {
	
	public DungeonFolderTreeItem() {
		super("Dungeons", "This section lets you edit dungeons in the game.", Dungeon.class, 98);
	}
	
	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded) {
			try {
				getChildren().remove(0);
				RomFile rom = Rom.getAll();
				
				rom.seek(RrtOffsetList.floorCountOffset);
				for(int i=0; i<98; i++) {
					int floorNum = rom.readUnsignedByte();
					getChildren().add(new DungeonDataTreeItem(Text.getText("Dungeons", i), i, floorNum));
				}
				loaded = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Cache.alloc("Floor", 6302);
			Cache.alloc("EncounterList", 839);
			Cache.alloc("LootList", 178);
			Cache.alloc("TrapList", 148);
		}
	}
}
