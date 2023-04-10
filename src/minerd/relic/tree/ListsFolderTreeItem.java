package minerd.relic.tree;

import javafx.beans.value.ObservableValue;
import minerd.relic.data.GameData;
import minerd.relic.data.Starters;

public class ListsFolderTreeItem extends FolderTreeItem<GameData> {
	
	public ListsFolderTreeItem() {
		super("Lists", "This section lets you edit various lists.");
	}
	
	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded) {
			getChildren().remove(0);
			getChildren().add(new DataTreeItem<Starters>("Starters", Starters.class, 0, 0x00F278E, 0x000F4264));
	        loaded = true;
		}
	}
}
