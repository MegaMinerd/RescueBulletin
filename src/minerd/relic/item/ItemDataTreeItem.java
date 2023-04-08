package minerd.relic.item;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.tree.DataTreeItem;

public class ItemDataTreeItem extends DataTreeItem{
	int dataPointer;

	public ItemDataTreeItem(String text) {
		super(text);
	}

	public ItemDataTreeItem(String itemName, int start) {
		this(itemName);
		dataPointer = start;
	}
	
	@Override
	public Node select(){
		SplitPane itemDataPane = null;
		try {
			RomFile rom = Rom.getAll();
			//Prepare the rom to be parsed by the controller 
			rom.seek(dataPointer);
			itemDataPane = (SplitPane)FXMLLoader.load(getClass().getResource("item.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemDataPane;
	}
}
