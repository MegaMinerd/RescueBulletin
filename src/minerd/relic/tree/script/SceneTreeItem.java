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
	SceneController controller;

	public SceneTreeItem(String name, int index, int number, int offset) {
		super(name, "", FolderTreeItem.class, number, false);
		this.pointer = Pointer.fromInt(offset);
	}

	@Override
	public Node select() {
		AnchorPane folderPane = null;
		try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/script/scene.fxml"));
			folderPane = loader.load();
			controller = loader.getController();

			controller.load(pointer, number);
		} catch(IOException e){
			e.printStackTrace();
		}
		return folderPane;
	}

	//User expanded the folder
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		if(!loaded) {
			getChildren().remove(0);
			String[] ptrStrs = controller.scriptPointer.getText().split("\n");
			String[] numStrs = controller.actorNum.getText().split("\n");
			for(int i=0; i<number; i++) {
				FolderTreeItem call = new FolderTreeItem("Call " + i, "", null, 0, false);
				boolean hasChildren = false;
				if(!ptrStrs[i*5].equals("null")) {
					call.getChildren().add(new CharacterFolderTreeItem(Pointer.fromInt(Integer.parseInt(ptrStrs[i*5], 16) + 0x08000000), Integer.parseInt(numStrs[i*5], 16)));
					hasChildren = true;
				}
				if(!ptrStrs[i*5 + 1].equals("null")){
					call.getChildren().add(new ScriptFolderTreeItem("Unknown 1", "", Integer.parseInt(numStrs[i*5 + 1]), Integer.parseInt(ptrStrs[i*5 + 1], 16)));
					hasChildren = true;
				}
				if(!ptrStrs[i*5 + 2].equals("null")){
					call.getChildren().add(new CameraTreeItem(Pointer.fromInt(Integer.parseInt(ptrStrs[i*5 + 2], 16) + 0x08000000)));
					hasChildren = true;
				}
				if(!ptrStrs[i*5 + 3].equals("null")){
					call.getChildren().add(new ScriptFolderTreeItem("Objects", "", Integer.parseInt(numStrs[i*5 + 3]), Integer.parseInt(ptrStrs[i*5 + 3], 16)));
					hasChildren = true;
				}
				if(!ptrStrs[i*5 + 4].equals("null")){
					call.getChildren().add(new ScriptFolderTreeItem("Main", "The behavior map itself, such as music.", Integer.parseInt(numStrs[i*5 + 4]), Integer.parseInt(ptrStrs[i*5 + 4], 16)));
					hasChildren = true;
				}
				//Counting children is buggy. Must check a boolean
				if(hasChildren) {
					getChildren().add(call);
				}
			}
			loaded = true;
		}
	}
}