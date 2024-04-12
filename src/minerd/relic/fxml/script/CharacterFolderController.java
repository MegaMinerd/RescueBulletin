package minerd.relic.fxml.script;

import java.io.IOException;

import javafx.scene.control.Label;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;

public class CharacterFolderController {
	public Label data;

	public CharacterFolderController load(Pointer pointer, int number) throws IOException {
		BufferedDataHandler rom = Rom.getAll();
		rom.seek(pointer);
		data.setText("");
		for(int i=0; i<number*2; i++) {
			data.setText(data.getText() + rom.readAsString(12, "  ") + "\n");
		} 
		return this;
	}
}
