package minerd.relic.fxml.script;

import java.io.IOException;

import javafx.scene.control.Label;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;

public class ScriptController {
	public Label offset, op, b, s, i1, i2, p, desc;

	public void load(Pointer pointer) throws IOException {
		BufferedDataHandler rom = Rom.getAll(); 
		rom.seek(pointer);
		//for(int i=0; i<number; i++) {
		//}
	}
}
