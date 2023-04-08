package minerd.relic.tree;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import minerd.relic.data.GameData;

public class DataTreeItem<T extends GameData> extends TreeItem<String>{
	private String name;
	private Class<T> cacheClass;
	private int index;
	private int[] pointers;
	private T cache;
	
	public DataTreeItem(String text) {
		super(text);
		name=text;
	}
	
	public DataTreeItem(String text, int off) {
		this(text);
		//offset = off;
	}
	
	public DataTreeItem(String name, Class<T> cacheClassIn, int indexIn, int[] pointersIn) {
		super(name);
		this.name = name;
		this.cacheClass = cacheClassIn;
		this.index = indexIn;
		this.pointers = pointersIn;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPathName() {
		return "";
	}
	
	public Node select() throws IOException{
	    Control dataPane = null;
	    if(cache==null){
	        //Read the data from the ROM to store as cache
	    	try {
	    		cache = cacheClass.getConstructor(int.class, int[].class).newInstance(index, pointers);
	    		super.setValue(cache.getName());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
	    }
		dataPane = cache.load();
	    return dataPane;
	}
}
