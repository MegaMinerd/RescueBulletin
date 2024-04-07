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
		BufferedDataHandler rom = Rom.getAll(); 
		rom.seek(pointer);
		
		offset.setText(Integer.toHexString(rom.getFilePointer()));
		for(int i=0; i<12; i++) {
			data.setText(data.getText() + Integer.toHexString(rom.readByte()&0xFF) + " ");
		}
		
		rom.seek(rom.getFilePointer()-4);
		System.out.println(Integer.toHexString(rom.getFilePointer()));
		rom.seek(rom.parsePointer());
		script.load(rom);
	}
}
