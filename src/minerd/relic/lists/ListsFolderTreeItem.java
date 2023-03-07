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
			//Todo: Fill in the pages in this section
		}
	}
}
