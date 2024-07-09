package minerd.relic.tree;

import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import minerd.relic.data.Cache;
import minerd.relic.data.dungeon.Floor;

public class FloorDataTreeItem extends DataTreeItem<Floor> {
	private int relIndex, dungeonIndex, offset;

	public FloorDataTreeItem(String text) {
		super(text);
	}

	public FloorDataTreeItem(String name, int relIndex, int dungeonIndex, int offset) {
		super(name, Floor.class, relIndex);
		this.relIndex = relIndex;
		this.dungeonIndex = dungeonIndex;
		this.offset = offset;
	}

	public Node select() throws IOException {
		Region dataPane = null;
		// Read the data from the ROM to store as cache
		try{
			Floor data = new Floor(relIndex, dungeonIndex, offset);
			dataPane = data.load();
		} catch(IllegalArgumentException | SecurityException e){
			e.printStackTrace();
		}
		return dataPane;
	}
}