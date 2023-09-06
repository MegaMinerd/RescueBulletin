package minerd.relic.tree.script;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import minerd.relic.file.Pointer;
import minerd.relic.fxml.script.MapSceneController;
import minerd.relic.tree.FolderTreeItem;

public class SceneFolderTreeItem extends FolderTreeItem {
	Pointer pointer;
	int number;
	
	public SceneFolderTreeItem(Pointer pointer, int number) {
		super("Scenes", "", SceneTreeItem.class, number);
		this.pointer = pointer;
		this.number = number;
	}
    
    @Override
    public Node select() {
        AnchorPane folderPane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/script/mapscene.fxml"));
            folderPane = loader.load();
            MapSceneController controller = loader.getController();
            
            controller.load(pointer, number);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderPane;
    }

}
