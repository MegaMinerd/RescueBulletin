package minerd.relic.tree.script;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import minerd.relic.data.GameData;
import minerd.relic.file.Pointer;
import minerd.relic.fxml.script.CharacterFolderController;
import minerd.relic.fxml.script.SceneFolderController;
import minerd.relic.tree.FolderTreeItem;

public class CharacterFolderTreeItem<T extends GameData> extends FolderTreeItem<T> {
	Pointer pointer;
	int number;
	SceneFolderController controller;
	
	public CharacterFolderTreeItem(Pointer pointer, int number) {
		super("Characters", "Anything that moves on screen", ScriptTreeItem.class, 0);
		this.pointer = pointer;
		this.number = number;
	}
    
    @Override
    public Node select() {
        AnchorPane folderPane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/script/characterfolder.fxml"));
            folderPane = loader.load();
            CharacterFolderController cont = loader.getController();
            
            //controller = 
            cont.load(pointer, number);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderPane;
    }

	//User expanded the folder
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
	    if(!loaded) {
	        getChildren().remove(0);
	        //String[] ptrStrs = controller.callPointer.getText().split("\n");
	        //String[] numStrs = controller.callNum.getText().split("\n");
	        //for(int i=0; i<number; i++) {
	        //    getChildren().add(new SceneTreeItem("Scene " + i, i, Integer.parseInt(numStrs[i]), Integer.parseInt(ptrStrs[i], 16) | 0x08000000));
	        //}
	        loaded = true;
	    }
	}
}
