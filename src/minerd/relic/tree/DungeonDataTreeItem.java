package minerd.relic.tree;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import minerd.relic.data.Cache;
import minerd.relic.data.GameData;
import minerd.relic.data.dungeon.Dungeon;
import minerd.relic.data.dungeon.Floor;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

public class DungeonDataTreeItem extends FolderTreeItem<Floor>{
	private int index, floorStart;

	public DungeonDataTreeItem(String dunName, int index, int floors, int start, int floorStart) {
		super(dunName, "", Floor.class, 0-floors, start);
		this.index = index;
		this.floorStart = floorStart;
		
		if(index>63)
			getChildren().remove(0);
	}
	
	@Override
	public Node select(){
		Region dataPane = null;
	    GameData data = Cache.get("Dungeon", index);
	    if(data==null){
	        //Read the data from the ROM to store as cache
	    	data = new Dungeon(index, offsets);
    		Cache.add("Dungeon", index, data);
	    }
	    try {
	    	dataPane = data.load();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
		return dataPane;
	}
	
	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded) {
			getChildren().remove(0);
			try {
				RomFile rom = Rom.getAll();
				
				//Get a bunch of pointers from the SIRO file
				rom.seek(floorStart);
				rom.skip(4);
				rom.seek(rom.parsePointer());
				Pointer floorsPointer = rom.parsePointer();
				floorsPointer.move(4*index);
				Pointer layoutsPointer = rom.parsePointer();
				Pointer treasurePointer = rom.parsePointer();
				Pointer encountersPointer = rom.parsePointer();
				Pointer trapsPointer = rom.parsePointer();
				
				//Populate floor list
				rom.seek(floorsPointer);
				Pointer floorPointer = rom.parsePointer();
				for(int i=1; i<number; i++) {
					getChildren().add(new DataTreeItem<Floor>("Floor " + i, Floor.class, i, floorPointer.getOffset()+16*i, layoutsPointer.getOffset(), treasurePointer.getOffset(), encountersPointer.getOffset(), trapsPointer.getOffset()));
				}
				loaded = true;
			} catch (IOException | InvalidPointerException e) {
				e.printStackTrace();
			}
		}
	}
}