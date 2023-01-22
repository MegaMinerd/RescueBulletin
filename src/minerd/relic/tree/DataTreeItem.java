package minerd.relic.tree;

import java.awt.Component;

import javafx.scene.control.TreeItem;

public class DataTreeItem extends TreeItem{
	private String name;
	protected int offset;
	
	public DataTreeItem(String text) {
		super(text);
		name=text;
	}

	public DataTreeItem(String text, int off) {
		this(text);
		offset = off;
	}
	
	public String getName() {
		return "";//this.isRoot() ? "root" : name;
	}
	
	public String getPathName() {
		return "";//this.isRoot() ? "root" : (((DataTreeItem)parent).getPath() + "/" + name);
	}
	
	public Component select() {
		return null;
	}
}
