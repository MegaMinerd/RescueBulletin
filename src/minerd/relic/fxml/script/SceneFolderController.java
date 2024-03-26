package minerd.relic.fxml.script;

import java.io.IOException;

import javafx.scene.control.Label;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;

public class SceneFolderController {
	public Label offset, callNum, callPointer, sceneNum;

	public SceneFolderController load(Pointer pointer, int number) throws IOException {
		BufferedDataHandler rom = Rom.getAll(); 
		rom.seek(pointer);
		for(int i=0; i<number; i++) {
			offset.setText(offset.getText() + Integer.toHexString(rom.getFilePointer()) + "\n");
			callNum.setText(callNum.getText() + Integer.toHexString(rom.readInt()) + "\n");
			callPointer.setText(callPointer.getText() + Integer.toHexString(rom.parsePointer().getOffset()) + "\n");
			sceneNum.setText(sceneNum.getText() + "Scene " + (i+1) + "\n");
		}
		return this;
	}

}
