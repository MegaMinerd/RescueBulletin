package minerd.relic.move;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import minerd.relic.InvalidPointerException;
import minerd.relic.Lists;
import minerd.relic.RomManipulator;
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
				RomManipulator.seek(offset);
				RomManipulator.skip(4);
				RomManipulator.seek(RomManipulator.parsePointer());
				int dataStart = RomManipulator.parsePointer();
				for(int i=0; i<413; i++) {
					int moveStart = dataStart + 0x24 * i;
					RomManipulator.seek(moveStart);
					String pokemonName = RomManipulator.readString(RomManipulator.parsePointer());
					getChildren().add(new MoveDataTreeItem(pokemonName, moveStart));
					Lists.pokemon.add(pokemonName);
				}
				loaded = true;
			} catch (IOException | InvalidPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
