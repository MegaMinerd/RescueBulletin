package minerd.relic.tree;

import java.awt.Component;

public class FolderTreeItem extends DataTreeItem {
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
			//this.add(new DataLeafNode(""));
		}
	}
	
	public Component select() {
		//TODO: In the final version, this will build a full panel upon construction and return it here. 
		return null;//new Label(info);
	}
	
	public void expand() {
		
	}
}
