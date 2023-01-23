package minerd.relic.tree;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

public class FolderTreeItem extends DataTreeItem implements ChangeListener<Boolean>{
	private String info;
	protected boolean loaded;

	public FolderTreeItem(String text, String infoIn) {
		this(text, infoIn, -1);
	}
	
	public FolderTreeItem(String text, String infoIn, int off) {
		super(text, off);
		this.info = infoIn;
		loaded = true;
		if(off>=0) {
			loaded = false;
			//Force the tree to show this node as a folder, but don't actually load anything until it's needed.
			getChildren().add(new DataTreeItem(""));
		}
		expandedProperty().addListener(this);
	}
	
	public Node select() {
		//TODO: In the final version, this will build a full panel upon construction and return it here. 
		return null;//new Label(info);
	}

	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
	}
}
