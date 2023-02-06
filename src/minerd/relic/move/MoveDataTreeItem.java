package minerd.relic.move;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import minerd.relic.RomManipulator;
import minerd.relic.tree.DataTreeItem;

public class MoveDataTreeItem extends DataTreeItem {
	int dataPointer;

	public MoveDataTreeItem(String text) {
		super(text);
	}

	public MoveDataTreeItem(String moveName, int start) {
		this(moveName);
		dataPointer = start;
	}
	
	@Override
	public Node select(){
		SplitPane moveDataPane = null;
		try {
			//Prepare the rom to be parsed by the controller 
			RomManipulator.seek(dataPointer);
			moveDataPane = (SplitPane)FXMLLoader.load(getClass().getResource("move.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return moveDataPane;
	}
}
