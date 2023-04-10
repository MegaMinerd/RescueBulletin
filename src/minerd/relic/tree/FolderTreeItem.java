	package minerd.relic.tree;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import minerd.relic.data.GameData;
import minerd.relic.data.Text;
import minerd.relic.fxml.FolderController;
import minerd.relic.fxml.PokemonController;

public class FolderTreeItem<T extends GameData> extends DataTreeItem<T> implements ChangeListener<Boolean>{
	private String name, info;
	protected boolean loaded;
	private Class<T> cacheClass;
	private int number;
	protected int[] pointers;

	public FolderTreeItem(String text, String infoIn) {
		this(text, infoIn, Object.class, -1);
	}
	
	public FolderTreeItem(String text, String infoIn, int off) {
		super(text, off);
	}
	
	public FolderTreeItem(String text, String infoIn, Class cacheClassIn, int numberIn, int ... pointersIn) {
		super(text);
		this.info = infoIn;
		this.cacheClass = cacheClassIn;
		this.number = numberIn;
		this.pointers = pointersIn;
		this.name = text;
		loaded = true;
		
		if(pointers.length==0 || pointers[0]>=0) {
			loaded = false;
			//Force the tree to show this node as a folder, but don't actually load anything until it's needed.
			getChildren().add(new DataTreeItem<T>(""));
		}
		expandedProperty().addListener(this);
	}
	
	//User clicked on the item
	public Node select() {
		AnchorPane folderPane = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/folder.fxml"));
			folderPane = loader.load();
		    FolderController controller = loader.getController();
		    
		    controller.load(name, info);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return folderPane;
	}

	//User expanded the folder
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
	    if(!loaded) {
	        getChildren().remove(0);
	        for(int i=0; i<number; i++) {
	            getChildren().add(new DataTreeItem<T>(Text.getText(name, i), cacheClass, i, pointers));
	        }
	        loaded = true;
	    }
	}
}
