package minerd.relic.tree;

import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import minerd.relic.data.Cache;
import minerd.relic.data.dungeon.Floor;

public class FloorDataTreeItem extends DataTreeItem<Floor> {
	private int dungeonIndex;

	public FloorDataTreeItem(String text) {
		super(text);
	}

	public FloorDataTreeItem(String name, int index, int dungeonIndex) {
		super(name, Floor.class, index);
		this.dungeonIndex = dungeonIndex;
	}

	public Node select() throws IOException {
		Region dataPane = null;
		//Possibly wasteful of 
		Floor data = (Floor) Cache.get("Floor", dungeonIndex*100 + index);
		if(data==null){
			// Read the data from the ROM to store as cache
			try{
				data = new Floor(index, dungeonIndex);
				Cache.add("Floor", dungeonIndex*100 + index, data);
			} catch(IllegalArgumentException | SecurityException e){
				e.printStackTrace();
			}
		}
		dataPane = data.load();
		return dataPane;
	}
}
