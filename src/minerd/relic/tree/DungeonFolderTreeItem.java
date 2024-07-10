package minerd.relic.tree;

import javafx.beans.value.ObservableValue;
import minerd.relic.data.Cache;
import minerd.relic.data.Text;
import minerd.relic.data.dungeon.Dungeon;

public class DungeonFolderTreeItem extends FolderTreeItem<Dungeon> {
	public DungeonFolderTreeItem() {
		super("Dungeons", "This section lets you edit dungeons in the game.", Dungeon.class, 98);
	}

	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded){
			getChildren().remove(0);
			for(int i = 0; i<98; i++) {
				String name = Text.getText("Dungeons", i);
				if(name.equals(""))
					name =  "Dungeon" + i;
				getChildren().add(new DungeonDataTreeItem(name, i));
			}
			loaded = true;
			Cache.alloc("Floor", 1764);
			Cache.alloc("EncounterList", 839);
			Cache.alloc("LootList", 176);
			Cache.alloc("TrapList", 148);
		}
	}
}