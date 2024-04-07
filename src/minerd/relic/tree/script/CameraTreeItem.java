package minerd.relic.tree.script;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import minerd.relic.data.Script;
import minerd.relic.file.Pointer;
import minerd.relic.fxml.script.CameraController;
import minerd.relic.fxml.script.SceneFolderController;
import minerd.relic.tree.DataTreeItem;

public class CameraTreeItem extends DataTreeItem<Script> {
	Pointer pointer;
	int number;
	boolean loaded;
	SceneFolderController controller;
	
	public CameraTreeItem(Pointer pointer) {
		super("Camera");
		this.pointer = pointer;
		loaded = false;
	}
    
    @Override
    public Node select() {
        AnchorPane folderPane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/script/camera.fxml"));
            folderPane = loader.load();
            CameraController cont = loader.getController();
            
            cont.load(pointer, number);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderPane;
    }
}
