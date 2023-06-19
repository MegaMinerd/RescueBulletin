package minerd.relic.tree.script;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import minerd.relic.data.GameData;
import minerd.relic.data.Text;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.fxml.script.MapHeaderController;
import minerd.relic.tree.FolderTreeItem;
import minerd.relic.util.RrtOffsetList;

public class MapDataTreeItem extends FolderTreeItem<GameData> {
    int sceneNum, index;
    Pointer headerPointer, scenePointer, waypointPointer;
    
    public MapDataTreeItem(String name, int index){
        super(name, "", FolderTreeItem.class, 0);
        this.index = index;
        
        try{
            RomFile rom = Rom.getAll();
            rom.seek(RrtOffsetList.mapScriptOffset);
            rom.skip(4*index);
            this.headerPointer = rom.parsePointer();
            rom.seek(headerPointer);
            sceneNum = rom.readInt();
            scenePointer = rom.parsePointer();
            waypointPointer = rom.parsePointer();
        } catch (IOException e){
            
        }
    }
    
    @Override
    //TODO: Gui that shows waypoints
    public Node select() {
        AnchorPane folderPane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/script/maphead.fxml"));
            folderPane = loader.load();
            MapHeaderController controller = loader.getController();
            
            controller.load(headerPointer, sceneNum, scenePointer, waypointPointer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderPane;
    }
    
    	@Override
    public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
	    if(!loaded) {
	        getChildren().remove(0);
	        for(int i=0; i<sceneNum; i++) {
	            getChildren().add(new SceneTreeItem(
	                String.format("%s scene %d (0x%x)", Text.getText("Map", index), i, scenePointer.getOffset()+(8*i)), i, scenePointer.getOffset()+(8*i)));
	        }
	        loaded = true;
	    }
	}
}