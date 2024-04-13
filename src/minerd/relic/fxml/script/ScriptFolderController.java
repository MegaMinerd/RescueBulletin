package minerd.relic.fxml.script;

import java.io.IOException;

import javafx.scene.control.Label;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;

public class ScriptFolderController {
	public Label cola, colb, data;

	public ScriptFolderController load(String a, String b, Pointer pointer, int number) throws IOException {
		cola.setText(a);
		colb.setText(b);
		BufferedDataHandler rom = Rom.getAll();
		rom.seek(pointer);
		data.setText("");
		for(int i=0; i<number*2; i++) {
			data.setText(data.getText() + rom.readAsString(12, "  ") + "\n");
		} 
		return this;
	}
}
