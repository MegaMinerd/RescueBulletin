package minerd.relic.item;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
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
				RomFile rom = Rom.getAll();
				rom.seek(offset);
				rom.skip(4);
				int dataStart = rom.parsePointer();
				for(int i=0; i<240; i++) {
					int itemStart = dataStart + 0x20 * i;
					rom.seek(itemStart);
					String itemName = rom.readString(rom.parsePointer());
					getChildren().add(new ItemDataTreeItem(itemName, itemStart));
				}
				loaded = true;
			} catch (IOException | InvalidPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
