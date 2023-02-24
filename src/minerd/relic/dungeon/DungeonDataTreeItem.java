package minerd.relic.dungeon;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import minerd.relic.RomManipulator;
import minerd.relic.tree.DataTreeItem;

public class DungeonDataTreeItem extends DataTreeItem{
	int dataPointer;

	public DungeonDataTreeItem(String text) {
		super(text);
	}

	public DungeonDataTreeItem(String itemName, int start) {
		this(itemName);
		dataPointer = start;
	}
	
	@Override
	public Node select(){
		AnchorPane dunDataPane = null;
		try {
			//Prepare the rom to be parsed by the controller 
			RomManipulator.seek(dataPointer);
			dunDataPane = (AnchorPane)FXMLLoader.load(getClass().getResource("dungeon.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dunDataPane;
	}
}
