package minerd.relic.tree.script;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import minerd.relic.data.Script;
import minerd.relic.file.Pointer;
import minerd.relic.fxml.script.TriggerZoneController;
import minerd.relic.tree.DataTreeItem;

public class TriggerZoneTreeItem extends DataTreeItem<Script> {
	Pointer pointer;
	int number;
	boolean loaded;
	TriggerZoneController controller;
	
	public TriggerZoneTreeItem(int index, Pointer pointer) {
		super("Trigger Zone " + index);
		this.pointer = pointer;
		loaded = false;
	}
    
    @Override
    public Node select() {
        AnchorPane folderPane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/script/triggerzone.fxml"));
            folderPane = loader.load();
            TriggerZoneController cont = loader.getController();
            
            cont.load(pointer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderPane;
    }
}
