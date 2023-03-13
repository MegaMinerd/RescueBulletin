package minerd.relic.dungeon;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import minerd.relic.InvalidPointerException;
import minerd.relic.RomManipulator;
import minerd.relic.tree.FolderTreeItem;

public class DungeonDataTreeItem extends FolderTreeItem{
	//floorNum will actually be a pointer until loaded
	int dataPointer, floorNum;

	public DungeonDataTreeItem(String itemName, int id, int start, int floors) {
		//A bit unorthodox way to use this constructor
		//This class will overwrite what these params are used for
		super(itemName, "", id);
		this.dataPointer = start;
		this.floorNum = floors;
		if(id>63)
			getChildren().remove(0);
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
	
	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded) {
			getChildren().remove(0);
			try {
				//Get actual floor number from pointer
				RomManipulator.seek(floorNum+offset);
				floorNum = RomManipulator.readUnsignedByte();
				
				//Get a bunch of pointers from the SIRO file
				RomManipulator.seek(0x004A2BF4);
				RomManipulator.skip(4);
				RomManipulator.seek(RomManipulator.parsePointer());
				int floorsPointer = RomManipulator.parsePointer();
				floorsPointer += 4*offset;
				int layoutsPointer = RomManipulator.parsePointer();
				int treasurePointer = RomManipulator.parsePointer();
				int encountersPointer = RomManipulator.parsePointer();
				int trapsPointer = RomManipulator.parsePointer();
				
				//Populate floor list
				RomManipulator.seek(floorsPointer);
				int floorPointer = RomManipulator.parsePointer();
				//System.out.println(Integer.toHexString(RomManipulator.getFilePointer()));
				for(int i=1; i<floorNum; i++) {
					getChildren().add(new FloorDataTreeItem("Floor " + i, floorPointer+16*i, layoutsPointer, treasurePointer, encountersPointer, trapsPointer));
				}
				loaded = true;
			} catch (IOException | InvalidPointerException e) {
				e.printStackTrace();
			}
		}
	}
}
