package minerd.relic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import minerd.relic.data.Cache;
import minerd.relic.data.FriendArea;
import minerd.relic.data.Item;
import minerd.relic.data.Move;
import minerd.relic.data.Pokemon;
import minerd.relic.file.RedRom;
import minerd.relic.file.Rom;
import minerd.relic.tree.DataTreeItem;
import minerd.relic.tree.DungeonFolderTreeItem;
import minerd.relic.tree.FolderTreeItem;
import minerd.relic.tree.ListsFolderTreeItem;
import minerd.relic.tree.script.MapFolderTreeItem;

public class mainController implements Initializable {
	FileChooser fc;

	public ScrollPane treePane;
	public AnchorPane editorPane;
	public TreeView<String> dataTree;
	TreeItem<String> root;

	@Override
	@SuppressWarnings({ "rawtypes" , "unchecked" })
	public void initialize(URL location, ResourceBundle resources) {
		fc = new FileChooser();
		fc.setTitle("Open Resource File");
		fc.getExtensionFilters().add(new ExtensionFilter("ROM Files (.gba)", "*.gba"));

		root = new FolderTreeItem("No ROM", "Open a rom file from the menu");
		dataTree.setRoot(root);
		dataTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>(){
			@Override
			public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
				selectTreeItem((DataTreeItem) newValue);
			}
		});
	}

	public void reloadAll() throws IOException {
		if(Rom.getInstance()!=null){
			reloadTree();
			// reload views and such
		}
	}

	@SuppressWarnings({ "rawtypes" , "unchecked" })
	public void reloadTree() throws IOException {
		root = new FolderTreeItem(Rom.getInstance().getFilename(), "Select something to edit in the ROM from the tree on the left.");
		dataTree.setRoot(root);
		root.getChildren().add(new ListsFolderTreeItem());
		root.getChildren().add(new MapFolderTreeItem());
		root.getChildren().add(new FolderTreeItem<Pokemon>("Pokemon", "This section lets you edit data for Pokemon in the game.", Pokemon.class, 424));
		Cache.alloc("Learnset", 424);
		root.getChildren().add(new FolderTreeItem<Item>("Items", "This section lets you edit data for items in the game.", Item.class, 240));
		root.getChildren().add(new FolderTreeItem<Move>("Moves", "This section lets you edit settings related to moves.", Move.class, 413));
		//root.getChildren().add(new FolderTreeItem<Map>("Map Backgrounds", "This section lets you edit map backgrounds.", Map.class, -1));
		//root.getChildren().add(new FolderTreeItem<Sprite>("Object Sprites", "This section lets you import and export object sprites.", Sprite.class, -1));
		//root.getChildren().add(new FolderTreeItem<Background>("Backgrounds", "This section lets you edit backgrounds.", Background.class, -1));
		root.getChildren().add(new FolderTreeItem<FriendArea>("Friend Areas", "This section lets you edit friend areas in the game.", FriendArea.class, 58));
		root.getChildren().add(new DungeonFolderTreeItem());
		//root.getChildren().add(new FolderTreeItem<FixedRoom>("Fixed Rooms", "This section lets you edit fixed rooms.", FixedRoom.class, -1));
		//root.getChildren().add(new FolderTreeItem<Tileset>("Dungeon Tilesets", "This section lets you edit the graphics of dungeon tiles.", Tileset.class, -1));
		//root.getChildren().add(new FolderTreeItem<Graphic>("Misc. Graphics", "This section lets you edit miscellaneous graphics.", Graphic.class, -1));
	}

	public void openRom() {
		File file = fc.showOpenDialog(dataTree.getScene().getWindow());
		if(file!=null){
			try{
				Rom.load(new RedRom(file));
				reloadAll();
			} catch(IOException fnfe){
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void selectTreeItem(DataTreeItem item) {
		ObservableList<Node> children = editorPane.getChildren();
		while(children.size()>0)
			children.remove(0);
		Node itemData;
		try{
			itemData = item.select();
			if(itemData!=null)
				children.add(itemData);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void save() {
		try{
			Cache.saveAll();
		} catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Saved");
	}

	public void randomize() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/rando.fxml"));
		Parent rando;
		try{
			rando = (Parent) loader.load();
			Stage stage = new Stage();
			stage.setTitle("Randomize");
			stage.setScene(new Scene(rando));

			stage.show();
		} catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void importPortraits() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/portraits.fxml"));
		Parent importer;
		try{
			importer = (Parent) loader.load();
			Stage stage = new Stage();
			stage.setTitle("Import Portraits");
			stage.setScene(new Scene(importer));

			stage.show();
		} catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void importLevelmap() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/levelmaps.fxml"));
		Parent importer;
		try{
			importer = (Parent) loader.load();
			Stage stage = new Stage();
			stage.setTitle("Import Level Map");
			stage.setScene(new Scene(importer));

			stage.show();
		} catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}