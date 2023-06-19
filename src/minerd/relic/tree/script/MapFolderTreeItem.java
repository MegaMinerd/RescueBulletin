package minerd.relic.tree.script;

import javafx.beans.value.ObservableValue;
import minerd.relic.data.GameData;
import minerd.relic.tree.FolderTreeItem;

//Does not actually hold GameData, but the changed() method requires a type for some reason
public class MapFolderTreeItem extends FolderTreeItem<GameData> {
	private boolean loaded = false;
	
	public MapFolderTreeItem() {
		super("Script Scenes",  "This section will let you edit overworld scenes in the game."
							  + "\nWarning! This section is not complete. The current layout is designed to help with research."
							  + "\nIf you would like to help contact MegaMinerd on discord or PokeCommunity.", MapDataTreeItem.class, 174);
	}

	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded){
			getChildren().remove(0);
			for(int i = 0; i<174; i++){
				getChildren().add(new MapDataTreeItem("Map " + i, i));
				//getChildren().add(new MapDataTreeItem(Text.getText("Map", i), i));
			}
			loaded = true;
		}
	}
}