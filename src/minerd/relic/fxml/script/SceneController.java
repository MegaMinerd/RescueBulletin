package minerd.relic.fxml.script;

import java.io.IOException;

import javafx.scene.control.Label;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;

public class SceneController {
	public Label offset, actorNum, scriptPointer, scriptType;

	public void load(Pointer pointer, int number) throws IOException {
		BufferedDataHandler rom = Rom.getAll(); 
		rom.seek(pointer);
		for(int i=0; i<number; i++) {
			offset.setText(offset.getText() + Integer.toHexString(rom.getFilePointer()) + "\n\n\n\n\n");
			for(int j=0; j<5; j++) {
				actorNum.setText(actorNum.getText() + Integer.toHexString(rom.readInt()) + "\n");
				Pointer ptr =rom.parsePointer();
				scriptPointer.setText(scriptPointer.getText() + (ptr==null ? "null" : Integer.toHexString(ptr.getOffset()))+ "\n");
			}
			scriptType.setText(scriptType.getText() + "Call " + (i) + " character data\n");
			scriptType.setText(scriptType.getText() + "Unknown\n");
			scriptType.setText(scriptType.getText() + "Call " + (i) + " camera data\n");
			scriptType.setText(scriptType.getText() + "Call " + (i) + " object data\n");
			scriptType.setText(scriptType.getText() + "Call " + (i) + " main data\n");
		}
	}
}
