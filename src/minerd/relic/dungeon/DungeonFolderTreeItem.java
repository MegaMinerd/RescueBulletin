package minerd.relic.dungeon;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import minerd.relic.InvalidPointerException;
import minerd.relic.RomManipulator;
import minerd.relic.tree.FolderTreeItem;

public class DungeonFolderTreeItem extends FolderTreeItem {
	protected int names, floors;
	
	public DungeonFolderTreeItem(int mainOff, int nameOff, int floorCountOff) {
		super("Dungeons", "This section lets you edit dungeons in the game.", mainOff);
		this.names = nameOff;
		this.floors = floorCountOff;
	}
	
	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded) {
			getChildren().remove(0);
			try {
				RomManipulator.seek(names);
				for(int i=0; i<98; i++) {
					int dunStart = offset + 0x10 * i;
					String dunName = RomManipulator.readStringAndReturn(RomManipulator.parsePointer());
					RomManipulator.skip(4);
					getChildren().add(new DungeonDataTreeItem(dunName, i, dunStart, floors));
				}
				loaded = true;
			} catch (IOException | InvalidPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
