package minerd.relic.fxml.script;

import java.io.IOException;

import javafx.scene.control.Label;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;

public class TriggerZoneController {
	public Label offset, pointer, data, desc;
	public ScriptPane script;

	public void load(Pointer ptr) throws IOException {
		BufferedDataHandler rom = Rom.getAll(); 
		rom.seek(ptr);

		offset.setText(Integer.toHexString(rom.getFilePointer()));
		data.setText(data.getText() + rom.readAsString(12, "  "));
		
		rom.seek(rom.getFilePointer()-4);
		rom.seek(rom.parsePointer());
		script.load(rom);
	}
}
