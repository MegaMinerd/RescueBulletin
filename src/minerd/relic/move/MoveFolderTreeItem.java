package minerd.relic.move;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.tree.FolderTreeItem;

public class MoveFolderTreeItem extends FolderTreeItem {
	
	public MoveFolderTreeItem(int offset) {
		super("Moves", "This section lets you edit settings related to moves.", offset);
	}
	
	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded) {
			getChildren().remove(0);
			try {
				RomFile rom = Rom.getAll();
				rom.seek(offset);
				rom.skip(4);
				rom.seek(rom.parsePointer());
				int dataStart = rom.parsePointer();
				for(int i=0; i<413; i++) {
					int moveStart = dataStart + 0x24 * i;
					rom.seek(moveStart);
					String moveName = rom.readString(rom.parsePointer());
					getChildren().add(new MoveDataTreeItem(moveName, moveStart));
				}
				loaded = true;
			} catch (IOException | InvalidPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
