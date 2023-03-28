package minerd.relic.lists;

import javafx.beans.value.ObservableValue;
import minerd.relic.tree.FolderTreeItem;

public class ListsFolderTreeItem extends FolderTreeItem {
	
	public ListsFolderTreeItem(int offset) {
		super("Lists", "This section lets you edit various lists.", offset);
	}
	
	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded) {
			getChildren().remove(0);
			getChildren().add(new StartersDataTreeNode(0x00F278E, 0x000F4264));
		}
	}
}
