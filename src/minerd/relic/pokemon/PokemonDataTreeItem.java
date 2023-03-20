package minerd.relic.pokemon;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import minerd.relic.InvalidPointerException;
import minerd.relic.RomManipulator;
import minerd.relic.area.AreaController;
import minerd.relic.area.FriendArea;
import minerd.relic.graphics.ImageProcessor;
import minerd.relic.tree.DataTreeItem;

public class PokemonDataTreeItem extends DataTreeItem{
	int dataPointer, spritePointer;
	Pokemon pokemon;

	public PokemonDataTreeItem(String pokemonName, int start, int spriteStart) {
		super(pokemonName);
		dataPointer = start;
		spritePointer = spriteStart;
	}
	
	@Override
	public Node select(){
		SplitPane pokemonDataPane = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("pokemon.fxml"));
			pokemonDataPane = loader.load();
			PokemonController controller = loader.getController();

			RomManipulator.seek(dataPointer);
			Pokemon pokemon = new Pokemon();
			RomManipulator.seek(spritePointer+4);
			int pointer = RomManipulator.parsePointer();
			System.out.print("\n"+Integer.toHexString(spritePointer+4));
			System.out.print("\t"+Integer.toHexString(pointer));
			pokemon.setSprites(ImageProcessor.readSpriteSiro(pointer));
			
			controller.load(pokemon);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pokemonDataPane;
	}
}
