package minerd.relic.pokemon;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import minerd.relic.InvalidPointerException;
import minerd.relic.Lists;
import minerd.relic.RomManipulator;
import minerd.relic.tree.FolderTreeItem;

public class PokemonFolderTreeItem extends FolderTreeItem {
	int spritesPointer;

	public PokemonFolderTreeItem(int offset, int sprites) {
		super("Pokemon", "This section lets you edit data for Pokemon in the game.", offset);
		spritesPointer = sprites;
	}
	
	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded) {
			getChildren().remove(0);
			try {
				RomManipulator.seek(offset);
				RomManipulator.skip(4);
				int dataStart = RomManipulator.parsePointer();
				for(int i=0; i<424; i++) {
					int pokemonStart = dataStart + 0x48 * i;
					int spriteStart = spritesPointer + 0x8 * (i-1);
					RomManipulator.seek(pokemonStart);
					String pokemonName = RomManipulator.readString(RomManipulator.parsePointer());
					getChildren().add(new PokemonDataTreeItem(pokemonName, pokemonStart, spriteStart));
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
