package minerd.relic.pokemon;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.tree.DataTreeItem;

public class PokemonDataTreeItem extends DataTreeItem{
	int dataPointer;

	public PokemonDataTreeItem(String text) {
		super(text);
	}

	public PokemonDataTreeItem(String pokemonName, int start) {
		this(pokemonName);
		dataPointer = start;
	}
	
	@Override
	public Node select(){
		SplitPane pokemonDataPane = null;
		try {
			RomFile rom = Rom.getAll();
			//Prepare the rom to be parsed by the controller 
			rom.seek(dataPointer);
			pokemonDataPane = (SplitPane)FXMLLoader.load(getClass().getResource("pokemon.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pokemonDataPane;
	}
}
