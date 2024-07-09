package minerd.relic.tree;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import minerd.relic.data.Cache;
import minerd.relic.data.GameData;
import minerd.relic.data.dungeon.Dungeon;
import minerd.relic.data.dungeon.Floor;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Rom;
import minerd.relic.util.RrtOffsetList;

public class DungeonDataTreeItem extends FolderTreeItem<Floor> {
	private int index, floorIndex;

	public DungeonDataTreeItem(String dunName, int index) {
		super(dunName, "", Floor.class, 1, false);
		this.index = index;
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
			try{
				getChildren().remove(0);
				BufferedDataHandler rom = Rom.getAll();
				rom.seek(RrtOffsetList.floorOffset);
				rom.skip(index * 4);
				rom.seek(rom.parsePointer());
				rom.skip(16);

				for(int i = 0; i<99; i++){
					long temp = rom.read(8) + rom.read(8);
					if(temp == 0)
						break;
					getChildren().add(new FloorDataTreeItem("Floor " + (i+1), i, index, rom.getFilePointer()-16));
				}
				loaded = true;
			} catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}