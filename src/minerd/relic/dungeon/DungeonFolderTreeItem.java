package minerd.relic.dungeon;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import minerd.relic.RomManipulator;
import minerd.relic.tree.FolderTreeItem;

public class DungeonFolderTreeItem extends FolderTreeItem {
	
	public DungeonFolderTreeItem(int offset) {
		super("Dungeons", "This section lets you edit dungeons in the game.", offset);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded) {
			getChildren().remove(0);
			try {
				RomManipulator.seek(offset);
				for(int i=0; i<98; i++) {
					int dunStart = offset + 0x10 * i;
					RomManipulator.seek(dunStart);
					String dunName = "Dungeon " + i;
					getChildren().add(new DungeonDataTreeItem(dunName, dunStart));
				}
				loaded = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
