package minerd.relic.item;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import minerd.relic.InvalidPointerException;
import minerd.relic.RomManipulator;
import minerd.relic.tree.FolderTreeItem;

public class ItemFolderTreeItem extends FolderTreeItem {

	public ItemFolderTreeItem(int offset) {
		super("Items", "This section lets you edit data for items in the game.", offset);
	}
	
	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded) {
			getChildren().remove(0);
			try {
				RomManipulator.seek(offset);
				RomManipulator.skip(4);
				int dataStart = RomManipulator.parsePointer();
				for(int i=0; i<240; i++) {
					int itemStart = dataStart + 0x20 * i;
					RomManipulator.seek(itemStart);
					String itemName = RomManipulator.readString(RomManipulator.parsePointer());
					getChildren().add(new ItemDataTreeItem(itemName, itemStart));
				}
				//RescueRelicWindow.updateTree();
				loaded = true;
			} catch (IOException | InvalidPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
