package minerd.relic.tree;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import minerd.relic.data.Dungeon;
import minerd.relic.data.Text;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

public class DungeonFolderTreeItem extends FolderTreeItem<Dungeon> {
	
	public DungeonFolderTreeItem(int mainOff, int floorCountOff, int floorOff) {
		super("Dungeons", "This section lets you edit dungeons in the game.", Dungeon.class, 98, mainOff, floorCountOff, floorOff);
	}
	
	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded) {
			try {
				getChildren().remove(0);
				RomFile rom;
					rom = Rom.getAll();
				
				rom.seek(offsets[1]);
				for(int i=0; i<98; i++) {
					int dunStart = offsets[0] + 0x10 * i;
					int floorNum = rom.readUnsignedByte();
					getChildren().add(new DungeonDataTreeItem(Text.getText("Dungeons", i), i, floorNum, dunStart, offsets[2]));
				}
				loaded = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
