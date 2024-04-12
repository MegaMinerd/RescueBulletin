package minerd.relic.tree.script;

import java.io.IOException;

import javafx.scene.Node;
import minerd.relic.data.GameData;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Rom;
import minerd.relic.fxml.script.ScriptPane;
import minerd.relic.tree.DataTreeItem;

public class ScriptTreeItem extends DataTreeItem<GameData> {
	int offset;
	ScriptPane script;

	public ScriptTreeItem(String name, int offset) {
		super(name);
		this.offset = offset;
	}

	@Override
	public Node select() {
        script = new ScriptPane();
        try{
        	BufferedDataHandler rom = Rom.getAll();
        	rom.seek(offset);
			script.load(rom);
		} catch(IOException e){
			e.printStackTrace();
		}
        return script;
       
	}
}