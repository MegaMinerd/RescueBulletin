	package minerd.relic.tree;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import minerd.relic.data.Cache;
import minerd.relic.data.GameData;
import minerd.relic.data.Text;
import minerd.relic.fxml.FolderController;

public class FolderTreeItem<T extends GameData> extends DataTreeItem<T> implements ChangeListener<Boolean>{
	private String name, info;
	protected boolean loaded;
	private Class<T> cacheClass;
	protected int number;

	public FolderTreeItem(String text, String infoIn) {
		this(text, infoIn, Object.class, 0, false);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public FolderTreeItem(String text, String infoIn, Class cacheClassIn, int numberIn) {
		this(text, infoIn, cacheClassIn, numberIn, true);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public FolderTreeItem(String text, String infoIn, Class cacheClassIn, int numberIn, boolean shouldAlloc) {
		super(text); 
		this.info = infoIn;
		this.cacheClass = cacheClassIn;
		this.number = numberIn;
		this.name = text;
		loaded = true;
		
		if(shouldAlloc)
			Cache.alloc(cacheClassIn.getSimpleName(), number);
		
		loaded = false;
		//Force the tree to show this node as a folder, but don't actually load anything until it's needed.
		getChildren().add(new DataTreeItem<T>(""));
		
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
	            getChildren().add(new DataTreeItem<T>(Text.getText(name, i), cacheClass, i));
	        }
	        loaded = true;
	    }
	}
}