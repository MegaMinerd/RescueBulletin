package minerd.relic.dungeon;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import minerd.relic.RomManipulator;
import minerd.relic.tree.DataTreeItem;

public class FloorDataTreeItem extends DataTreeItem{
	int mainDataPointer, layoutPointer, treasurePointer, encountersPointer, trapsPointer;

	public FloorDataTreeItem(String text) {
		super(text);
	}

	public FloorDataTreeItem(String itemName, int mainData, int layout, int treasure, int encounters, int traps) {
		this(itemName);
		mainDataPointer = mainData;
		layoutPointer = layout;
		treasurePointer = treasure;
		encountersPointer = encounters;
		trapsPointer = traps;
	}
	
	@Override
	public Node select(){
		SplitPane floorDataPane = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("floor.fxml"));
			floorDataPane = loader.load();
			FloorController controller = loader.getController();
			
			RomManipulator.seek(mainDataPointer);
			int[] ids = new int[7];
			for(int i=0; i<7; i++) {
				ids[i] = RomManipulator.readUnsignedShort();
			}
			
			//System.out.println(Integer.toHexString(mainDataPointer));
			RomManipulator.seek(layoutPointer + 28*ids[0]);
			controller.loadLayout();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return floorDataPane;
	}
}
