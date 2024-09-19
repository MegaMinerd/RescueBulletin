package minerd.relic.fxml.script;

import java.io.IOException;

import javafx.scene.control.Label;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;

public class CameraController {
	public Label offset, data, desc;
	public ScriptPane script;

	public void load(Pointer pointer, int number) throws IOException {
		BufferedDataHandler rom = Rom.getInstance().getAll();
		rom.seek(pointer);
		
		offset.setText(Integer.toHexString(rom.getFilePointer()));
		data.setText(data.getText() + rom.readAsString(12));
		
		rom.seek(rom.getFilePointer()-4);
		rom.seek(rom.parsePointer());
		script.load(rom);
	}
}
