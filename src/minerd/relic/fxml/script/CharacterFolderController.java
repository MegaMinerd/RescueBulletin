package minerd.relic.fxml.script;

import java.io.IOException;

import javafx.scene.control.Label;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;

public class CharacterFolderController {
	public Label data;

	public void load(Pointer pointer, int number) throws IOException {
		BufferedDataHandler rom = Rom.getAll(); 
		rom.seek(pointer);
		data.setText("");
		for(int i=0; i<number*2; i++) {
			for(int j=0; j<12; j++) {
				String next = Integer.toHexString(rom.readByte()&0xFF).toUpperCase();
				next = next.length()==1 ? "0" + next + "  " :  next + "  ";
				data.setText(data.getText() + next);
			}
			data.setText(data.getText() + "\n");
		} 
	}
}
