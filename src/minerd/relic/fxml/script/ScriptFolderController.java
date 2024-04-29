package minerd.relic.fxml.script;

import java.io.IOException;

import javafx.scene.control.Label;
import javafx.scene.layout.RowConstraints;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;

public class ScriptFolderController {
	public Label cola, colb, data, s2, s3, s4;
	public RowConstraints headerLine2;

	public ScriptFolderController load(String a, String b, Pointer pointer, int number, boolean hasLine2) throws IOException {
		if(!hasLine2) {
			headerLine2.setPrefHeight(0);
			headerLine2.setMaxHeight(0);
			s2.setText("");
			s3.setText("");
			s4.setText("");
		}
		cola.setText(a);
		colb.setText(b);
		BufferedDataHandler rom = Rom.getAll();
		rom.seek(pointer);
		data.setText("");
		for(int i=0; i<number*(hasLine2?2:1); i++) {
			data.setText(data.getText() + rom.readAsString(12, "  ") + "\n");
		} 
		return this;
	}
}
