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
import minerd.relic.data.Item;
import minerd.relic.data.Move;
import minerd.relic.data.Pokemon;
import minerd.relic.file.Rom;
import minerd.relic.tree.DataTreeItem;
import minerd.relic.tree.FolderTreeItem;
import minerd.relic.tree.ListsFolderTreeItem;

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
		
		root = new FolderTreeItem("No ROM", "Open a rom file from the menu");
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
		root.getChildren().add(new ListsFolderTreeItem());
		//root.getChildren().add(new SceneFolderTreeItem(-1));
		root.getChildren().add(new FolderTreeItem<Pokemon>("Pokemon", "This section lets you edit data for Pokemon in the game.", Pokemon.class, 424, 0x00357B88));
		root.getChildren().add(new FolderTreeItem<Item>("Items", "This section lets you edit data for items in the game.", Item.class, 240, 0x00306570));
		root.getChildren().add(new FolderTreeItem<Move>("Moves", "This section lets you edit settings related to moves.", Move.class, 413, 0x0360BF4));
		//root.getChildren().add(new MapFolderTreeItem(-1));
		//root.getChildren().add(new SpriteFolderTreeItem(-1));
		//root.getChildren().add(new BackgroundFolderTreeItem(-1));
		//root.getChildren().add(new AreaFolderTreeItem(0x0010AA90, 0x001139D0));
		//root.getChildren().add(new DungeonFolderTreeItem(0x00109D30, 0x00111A28, 0x01077A8));
		//root.getChildren().add(new FixedRoomFolderTreeItem(-1));
		//root.getChildren().add(new TilesetFolderTreeItem(-1));
		//root.getChildren().add(new GraphicFolderTreeItem(-1));
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
		Node itemData;
		try {
			itemData = item.select();
			if(itemData!=null)
				children.add(itemData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
