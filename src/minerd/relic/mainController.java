package minerd.relic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import minerd.relic.tree.BackgroundFolderTreeItem;
import minerd.relic.tree.DataTreeItem;
import minerd.relic.tree.DungeonFolderTreeItem;
import minerd.relic.tree.FixedRoomFolderTreeItem;
import minerd.relic.tree.FolderTreeItem;
import minerd.relic.tree.GraphicFolderTreeItem;
import minerd.relic.tree.ItemFolderTreeItem;
import minerd.relic.tree.MapFolderTreeItem;
import minerd.relic.tree.PokemonFolderTreeItem;
import minerd.relic.tree.SceneFolderTreeItem;
import minerd.relic.tree.SpriteFolderTreeItem;
import minerd.relic.tree.TilesetFolderTreeItem;

public class mainController implements Initializable{
	FileChooser fc;
	
	public ScrollPane treePane;
	public TreeView<String> dataTree;
	TreeItem<String> root;

	RomManipulator rom;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fc = new FileChooser();
		fc.setTitle("Open Resource File");
		fc.getExtensionFilters().add(new ExtensionFilter("ROM Files (.gba)", "*.gba"));
		
		root = new FolderTreeItem("No ROM", "");
		dataTree.setRoot(root);
		dataTree.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<TreeItem<String>>() {
			@Override
	        public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
				selectTreeItem((DataTreeItem)newValue);
	        }
	    });
	}
	
	public void reloadAll() throws IOException {
		if(rom!=null) {
			reloadTree();
			//reload views and such`
		}
	}
	
	public void reloadTree() throws IOException {
		root = new FolderTreeItem(RomManipulator.getFilename(), "Select something to edit in the ROM from the tree on the left.");
		dataTree.setRoot(root);
		//TODO: load these offsets from a config for various builds
		root.getChildren().add(new SceneFolderTreeItem(0));
		root.getChildren().add(new PokemonFolderTreeItem(0));
		root.getChildren().add(new ItemFolderTreeItem(0x00306570));
		root.getChildren().add(new MapFolderTreeItem(0));
		root.getChildren().add(new SpriteFolderTreeItem(0));
		root.getChildren().add(new BackgroundFolderTreeItem(0));
		root.getChildren().add(new DungeonFolderTreeItem(0));
		root.getChildren().add(new FixedRoomFolderTreeItem(0));
		root.getChildren().add(new TilesetFolderTreeItem(0));
		root.getChildren().add(new GraphicFolderTreeItem(0));
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
	
	public void selectTreeItem(DataTreeItem item){
		//TODO: change right content
	}
}
