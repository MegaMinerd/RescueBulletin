package minerd.relic.fxml.script;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.util.CodeConverter;

public class ScriptPane extends AnchorPane{
	@FXML
	public Label offset, data, desc;
	
	public ScriptPane() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/script/ScriptPane.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try{
			loader.load();
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public void load(BufferedDataHandler rom) throws IOException {
		byte[] code = new byte[16];
		while(true) {
			offset.setText(offset.getText() + Integer.toHexString(rom.getFilePointer()) + "\n\n");

			data.setText(data.getText() + rom.readAsString(16) + "\n\n");
			rom.seek(rom.getFilePointer()-16);
			rom.read(code);
			desc.setText(desc.getText() + CodeConverter.interpretCommand(code) + "\n");
			if((code[0]&0xFF)>=0xE8 && (code[0]&0xFF)<=0xF1 && rom.peek()!=0xF4)
				break;
		}
	}
}
