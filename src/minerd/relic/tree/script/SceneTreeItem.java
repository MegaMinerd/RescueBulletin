package minerd.relic.tree.script;

import javafx.beans.value.ObservableValue;
import minerd.relic.data.GameData;
import minerd.relic.file.Pointer;
import minerd.relic.tree.FolderTreeItem;

public class SceneTreeItem extends FolderTreeItem<GameData> {
	int sceneNum;
	Pointer scenePointer, waypointPointer;

	public SceneTreeItem(String name, int index, int offset) {
		super(name, "", FolderTreeItem.class, 0, false);
	}

	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		
	}
}