package minerd.relic.tree.script;

import java.io.IOException;
import java.util.ArrayList;

import javafx.beans.value.ObservableValue;
import minerd.relic.data.GameData;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;
import minerd.relic.tree.FolderTreeItem;
import minerd.relic.util.RrtOffsetList;

//Does not actually hold GameData, but the changed() method requires a type for some reason
public class MapFolderTreeItem extends FolderTreeItem<GameData> {
	private boolean loaded = false;
	
	public MapFolderTreeItem() {
		super("Script Scenes",  "This section will let you edit overworld scenes in the game."
							  + "\nWarning! This section is not complete. The current layout displays raw data to help with research."
							  + "\nIf you would like to help contact MegaMinerd on discord or PokeCommunity.", MapDataTreeItem.class, 174, false);
	}

	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded){
			getChildren().remove(0);
			ArrayList<Integer> seen = new ArrayList<Integer>();

			try{
				BufferedDataHandler rom = Rom.getInstance().getAll();
				for(int i = 0; true; i++){
					rom.seek(RrtOffsetList.mapScriptOffset);
					rom.skip(4*i);
					Pointer p = rom.parsePointer();
					if(!seen.contains(p.getOffset())){
						getChildren().add(new MapDataTreeItem("Map " + seen.size(), i));
						seen.add(p.getOffset());
						if(seen.size()==134)
							break;
					}
					// getChildren().add(new MapDataTreeItem(Text.getText("Map", i), i));
				}
				loaded = true;
			} catch(IOException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}