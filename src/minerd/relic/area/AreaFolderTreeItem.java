package minerd.relic.area;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import minerd.relic.InvalidPointerException;
import minerd.relic.RomManipulator;
import minerd.relic.tree.FolderTreeItem;

public class AreaFolderTreeItem extends FolderTreeItem {
	int namesPointer;

	public AreaFolderTreeItem(int mainData, int names) {
		super("Friend Areas", "This section lets you edit friend areas in the game.", mainData);
		namesPointer = names;
	}
	
	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded) {
			getChildren().remove(0);
			try {
				RomManipulator.seek(namesPointer);
				for(int i=0; i<58; i++) {
					int areaStart = offset + 0x8 * i;
					String areaName = RomManipulator.readStringAndReturn(RomManipulator.parsePointer());
					getChildren().add(new AreaDataTreeItem(areaName, areaStart));
				}
				loaded = true;
			} catch (IOException | InvalidPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
