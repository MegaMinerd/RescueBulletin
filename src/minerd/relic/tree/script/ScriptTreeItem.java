package minerd.relic.tree.script;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import minerd.relic.data.GameData;
import minerd.relic.tree.FolderTreeItem;

public class ScriptTreeItem extends FolderTreeItem<GameData> {

	public ScriptTreeItem(String name, int index, int number, int offset) {
		super(name, "", FolderTreeItem.class, number, false);
	}

	@Override
	public Node select() {
		return null;
	}

	//User expanded the folder
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded){
			getChildren().remove(0);
			for(int i = 0; i<number; i++){
				//getChildren().add(new FolderTreeItem("Call " + i, "", null, 0, false));
			}
			loaded = true;
		}
	}
}