package minerd.relic.tree.script;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import minerd.relic.data.GameData;
import minerd.relic.file.Pointer;
import minerd.relic.fxml.script.SceneController;
import minerd.relic.tree.FolderTreeItem;

public class SceneTreeItem extends FolderTreeItem<GameData> {
	Pointer pointer;

	public SceneTreeItem(String name, int index, int number, int offset) {
		super(name, "", FolderTreeItem.class, number, false);
		this.pointer = Pointer.fromInt(offset);
	}
    
    @Override
    public Node select() {
        AnchorPane folderPane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/script/scene.fxml"));
            folderPane = loader.load();
            SceneController controller = loader.getController();
            
            controller.load(pointer, number);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderPane;
    }

	//User expanded the folder
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
	    if(!loaded) {
	        getChildren().remove(0);
	        for(int i=0; i<number; i++) {
	            //getChildren().add(new SceneTreeItem("Scene " + i, i, Integer.parseInt(ptrStrs[i], 16) ));
	        }
	        loaded = true;
	    }
	}
}