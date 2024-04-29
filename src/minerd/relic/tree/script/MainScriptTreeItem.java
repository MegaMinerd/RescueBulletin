package minerd.relic.tree.script;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import minerd.relic.data.Script;
import minerd.relic.file.Pointer;
import minerd.relic.fxml.script.MainScriptController;
import minerd.relic.tree.DataTreeItem;

public class MainScriptTreeItem extends DataTreeItem<Script> {
	Pointer pointer;
	int number;
	boolean loaded;
	MainScriptController controller;
	
	public MainScriptTreeItem(Pointer pointer) {
		super("Main Script");
		this.pointer = pointer;
		loaded = false;
	}
    
    @Override
    public Node select() {
        AnchorPane folderPane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/script/mainscript.fxml"));
            folderPane = loader.load();
            MainScriptController cont = loader.getController();
            
            cont.load(pointer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderPane;
    }
}
