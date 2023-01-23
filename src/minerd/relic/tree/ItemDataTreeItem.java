package minerd.relic.tree;

import javafx.scene.control.TreeItem;

public class ItemDataTreeItem extends TreeItem<String>{
	int dataPointer;

	public ItemDataTreeItem(String text) {
		super(text);
	}

	public ItemDataTreeItem(String itemName, int start) {
		this(itemName);
		dataPointer = start;
	}
}
