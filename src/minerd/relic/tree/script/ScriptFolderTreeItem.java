package minerd.relic.tree.script;

import javafx.beans.value.ObservableValue;
import minerd.relic.fxml.script.Script;
import minerd.relic.tree.FolderTreeItem;

public class ScriptFolderTreeItem extends FolderTreeItem<Script> {

	public ScriptFolderTreeItem(String name, String desc, int number, int offset) {
		super(name, desc, FolderTreeItem.class, number, false);
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