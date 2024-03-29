package minerd.relic.tree;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import minerd.relic.data.Cache;
import minerd.relic.data.GameData;
import minerd.relic.data.dungeon.Dungeon;
import minerd.relic.data.dungeon.Floor;

public class DungeonDataTreeItem extends FolderTreeItem<Floor> {
	private int index, floorIndex;

	public DungeonDataTreeItem(String dunName, int index, int floorIndex, int floors) {
		super(dunName, "", Floor.class, floors, false);
		this.index = index;
		this.floorIndex = floorIndex;

		if(index>63)
			getChildren().remove(0);
	}

	@Override
	public Node select() {
		Region dataPane = null;
		GameData data = Cache.get("Dungeon", index);
		if(data==null){
			// Read the data from the ROM to store as cache
			data = new Dungeon(index);
			Cache.add("Dungeon", index, data);
		}
		try{
			dataPane = data.load();
		} catch(IOException e){
			e.printStackTrace();
		}
		return dataPane;
	}

	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded){
			getChildren().remove(0);
			for(int i = 0; i<number; i++){
				getChildren().add(new FloorDataTreeItem("Floor " + (i+1), floorIndex+i, i, index));
			}
			loaded = true;
		}
	}
}