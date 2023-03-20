package minerd.relic.tree;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class FolderTreeItem extends DataTreeItem implements ChangeListener<Boolean>{
	private String name, info;
	protected boolean loaded;

	public FolderTreeItem(String text, String infoIn) {
		this(text, infoIn, -1);
	}
	
	public FolderTreeItem(String text, String infoIn, int off) {
		super(text, off);
		this.info = infoIn;
		this.name = text;
		loaded = true;
		if(off>=0) {
			loaded = false;
			//Force the tree to show this node as a folder, but don't actually load anything until it's needed.
			getChildren().add(new DataTreeItem(""));
		}
		expandedProperty().addListener(this);
	}
	
	public Node select() {
		AnchorPane folderPane = null;
		//try {
			//folderPane = (AnchorPane)FXMLLoader.load(getClass().getResource("folder.fxml"));
			//Todo: figure out what to actually do here
			//folderPane.getController().populate(name, info);
		//} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}
		return folderPane;
	}

	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
	}
}
