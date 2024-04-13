package minerd.relic.tree.script;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import minerd.relic.data.GameData;
import minerd.relic.data.Text;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;
import minerd.relic.fxml.script.ScriptFolderController;
import minerd.relic.tree.FolderTreeItem;

public class ScriptFolderTreeItem<T extends GameData> extends FolderTreeItem<T> {
	String a, b;
	Pointer pointer;
	int number;
	boolean hasNames;
	ScriptFolderController controller;
	
	public ScriptFolderTreeItem(String name, String a, String b, Pointer pointer, int number, boolean hasNames) {
		super(name, "", ScriptTreeItem.class, 0);
		this.pointer = pointer;
		this.number = number;
		this.a = a;
		this.b = b;
		this.hasNames = hasNames;
	}
    
    @Override
    public Node select() {
        AnchorPane folderPane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/script/scriptfolder.fxml"));
            folderPane = loader.load();
            ScriptFolderController cont = loader.getController();
            
            controller = cont.load(a, b, pointer, number);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderPane;
    }

	//User expanded the folder
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
	    if(!loaded) {
	        getChildren().remove(0);

			BufferedDataHandler rom;
			try{
				rom = Rom.getAll();
				rom.seek(pointer);
				for(int i = 0; i<number; i++){
					String name = hasNames ? Text.getText("Actors", rom.readByte() & 0xFF) : "Object " + i;
					FolderTreeItem actor = new FolderTreeItem(name, "");
					rom.skip(hasNames ? 7 : 8);
					for(int j = 0; j<4; j++){
						Pointer ptr = rom.parsePointer();
						if(ptr!=null)
							actor.getChildren().add(new ScriptTreeItem("Script " + j, ptr.getOffset()));
					}
					getChildren().add(actor);
				}
			} catch(IOException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        loaded = true;
	    }
	}
}
