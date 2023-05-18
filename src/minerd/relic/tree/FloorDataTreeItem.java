package minerd.relic.tree;

import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import minerd.relic.data.Cache;
import minerd.relic.data.dungeon.Floor;

public class FloorDataTreeItem extends DataTreeItem<Floor> {
	private int relIndex, dungeonIndex;

	public FloorDataTreeItem(String text) {
		super(text);
	}

	public FloorDataTreeItem(String name, int absIndex, int relIndex, int dungeonIndex) {
		super(name, Floor.class, absIndex);
		this.relIndex = relIndex;
		this.dungeonIndex = dungeonIndex;
	}

	public Node select() throws IOException {
		Region dataPane = null;
		// Possibly wasteful of cache space
		Floor data = (Floor) Cache.get("Floor", index);
		if(data==null){
			// Read the data from the ROM to store as cache
			try{
				data = new Floor(relIndex, dungeonIndex);
				Cache.add("Floor", index, data);
			} catch(IllegalArgumentException | SecurityException e){
				e.printStackTrace();
			}
		}
		dataPane = data.load();
		return dataPane;
	}
}