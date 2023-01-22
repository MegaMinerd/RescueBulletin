package minerd.relic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import minerd.relic.tree.FolderTreeItem;

public class mainController implements Initializable{
	FileChooser fc;
	
	public TreeView<String> dataTree;
	TreeItem<String> root;

	RomManipulator rom;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fc = new FileChooser();
		fc.setTitle("Open Resource File");
		
		root = new FolderTreeItem("No ROM", "");
		dataTree.setRoot(root);
	}
	
	public void reloadAll() throws IOException {
		if(rom!=null) {
			reloadTree();
			//reload views and such
		}
	}
	
	public void reloadTree() throws IOException {
		root = new FolderTreeItem(RomManipulator.getFilename(), "Select something to edit in the ROM from the tree on the left.");
		dataTree.setRoot(root);
	}
	
	public void openRom() {
		File file = fc.showOpenDialog(dataTree.getScene().getWindow());
		if(file != null) {
			try {
				rom = new RomManipulator(file);
				reloadAll();
			}catch (IOException fnfe) {
			}
		}
	}
}
