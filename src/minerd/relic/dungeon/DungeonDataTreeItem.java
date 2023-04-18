package minerd.relic.dungeon;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import minerd.relic.data.GameData;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.tree.FolderTreeItem;

public class DungeonDataTreeItem extends FolderTreeItem<GameData>{
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
			RomFile rom = Rom.getAll();
			rom.seek(dataPointer);
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
				RomFile rom = Rom.getAll();
				
				//Get actual floor number from pointer
				rom.seek(floorNum+pointers[0]);
				floorNum = rom.readUnsignedByte();
				
				//Get a bunch of pointers from the SIRO file
				rom.seek(0x004A2BF4);
				rom.skip(4);
				rom.seek(rom.parsePointer());
				Pointer floorsPointer = rom.parsePointer();
				floorsPointer.move(4*pointers[0]);
				Pointer layoutsPointer = rom.parsePointer();
				Pointer treasurePointer = rom.parsePointer();
				Pointer encountersPointer = rom.parsePointer();
				Pointer trapsPointer = rom.parsePointer();
				
				//Populate floor list
				rom.seek(floorsPointer);
				Pointer floorPointer = rom.parsePointer();
				//System.out.println(Integer.toHexString(RomManipulator.getFilePointer()));
				for(int i=1; i<floorNum; i++) {
					//getChildren().add(new FloorDataTreeItem("Floor " + i, floorPointer.getOffset()+16*i, layoutsPointer, treasurePointer, encountersPointer, trapsPointer));
				}
				loaded = true;
			} catch (IOException | InvalidPointerException e) {
				e.printStackTrace();
			}
		}
	}
}
