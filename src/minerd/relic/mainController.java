package minerd.relic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import minerd.relic.area.AreaFolderTreeItem;
import minerd.relic.dungeon.DungeonFolderTreeItem;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.item.ItemFolderTreeItem;
import minerd.relic.lists.ListsFolderTreeItem;
import minerd.relic.move.MoveFolderTreeItem;
import minerd.relic.pokemon.PokemonFolderTreeItem;
import minerd.relic.tree.BackgroundFolderTreeItem;
import minerd.relic.tree.DataTreeItem;
import minerd.relic.tree.FixedRoomFolderTreeItem;
import minerd.relic.tree.FolderTreeItem;
import minerd.relic.tree.GraphicFolderTreeItem;
import minerd.relic.tree.MapFolderTreeItem;
import minerd.relic.tree.SceneFolderTreeItem;
import minerd.relic.tree.SpriteFolderTreeItem;
import minerd.relic.tree.TilesetFolderTreeItem;

public class mainController implements Initializable{
	FileChooser fc;
	
	public ScrollPane treePane;
	public AnchorPane editorPane;
	public TreeView<String> dataTree;
	TreeItem<String> root;
	
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
		if(Rom.getAll()!=null) {
			reloadTree();
			//reload views and such
		}
	}
	
	public void reloadTree() throws IOException {
		root = new FolderTreeItem(Rom.getFilename(), "Select something to edit in the ROM from the tree on the left.");
		dataTree.setRoot(root);
		//TODO: load these offsets from a config for various builds
		root.getChildren().add(new ListsFolderTreeItem(0));
		root.getChildren().add(new SceneFolderTreeItem(-1));
		root.getChildren().add(new PokemonFolderTreeItem(0x00357B88));
		root.getChildren().add(new ItemFolderTreeItem(0x00306570));
		root.getChildren().add(new MoveFolderTreeItem(0x0360BF4));
		root.getChildren().add(new MapFolderTreeItem(-1));
		root.getChildren().add(new SpriteFolderTreeItem(-1));
		root.getChildren().add(new BackgroundFolderTreeItem(-1));
		root.getChildren().add(new AreaFolderTreeItem(0x0010AA90, 0x001139D0));
		root.getChildren().add(new DungeonFolderTreeItem(0x00109D30, 0x00111A28, 0x01077A8));
		root.getChildren().add(new FixedRoomFolderTreeItem(-1));
		root.getChildren().add(new TilesetFolderTreeItem(-1));
		root.getChildren().add(new GraphicFolderTreeItem(-1));
	}
	
	public void openRom() {
		File file = fc.showOpenDialog(dataTree.getScene().getWindow());
		if(file != null) {
			try {
				Rom.load(file);
				reloadAll();
			}catch (IOException fnfe) {
			}
		}
	}
	
	public void selectTreeItem(DataTreeItem item){
		ObservableList<Node> children = editorPane.getChildren();
		while(children.size()>0)
			children.remove(0);
		Node itemData = item.select();
		if(itemData!=null)
			children.add(itemData);
	}
}
