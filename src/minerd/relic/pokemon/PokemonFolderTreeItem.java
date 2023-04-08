package minerd.relic.pokemon;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import minerd.relic.data.Text;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.tree.FolderTreeItem;

public class PokemonFolderTreeItem extends FolderTreeItem {

	public PokemonFolderTreeItem(int offset) {
		super("Pokemon", "This section lets you edit data for Pokemon in the game.", offset);
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
				for(int i=0; i<424; i++) {
					int pokemonStart = dataStart + 0x48 * i;
					rom.seek(pokemonStart);
					String pokemonName = rom.readString(rom.parsePointer());
					getChildren().add(new PokemonDataTreeItem(pokemonName, pokemonStart));
					Text.pokemon.add(pokemonName);
				}
				loaded = true;
			} catch (IOException | InvalidPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
