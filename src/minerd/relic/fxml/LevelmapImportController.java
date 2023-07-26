package minerd.relic.fxml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Rom;
import minerd.relic.util.CompressionHandler;

public class LevelmapImportController implements Initializable {
	FileChooser fc;
	File lvmpFile;

	public Button openButton, saveButton;
	public TextField offset;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		fc = new FileChooser();
		fc.setTitle("Open Level Map");
		fc.getExtensionFilters().add(new ExtensionFilter("Comma Separated Values", "*.csv"));
	}

	public void openFile() {
		File file = fc.showOpenDialog(null);
		if(file!=null){
			lvmpFile = file;
		}
	}

	public void save() {
		try{
			BufferedReader br = new BufferedReader(new FileReader(lvmpFile));
			String line;
			BufferedDataHandler data = new BufferedDataHandler(ByteBuffer.allocate(1200));
			data.seek(0);
			//Discard header line
			br.readLine();
			for(int i = 1; i<=100; i++){
				line = br.readLine();
				String[] values = line.split(",");
				if(values.length!=7){
					System.out.println("Invalid level map: line " + i);
					return;
				}
				data.writeInt(Integer.parseInt(values[1]));
				data.writeByte(Byte.parseByte(values[2]));
				data.skip(1);
				data.writeByte(Byte.parseByte(values[3]));
				data.writeByte(Byte.parseByte(values[4]));
				data.writeByte(Byte.parseByte(values[5]));
				data.writeByte(Byte.parseByte(values[6]));
				data.skip(2);
			}
			data.seek(0);
			BufferedDataHandler comp = CompressionHandler.compress(data, false);
			comp.seek(0);

			BufferedDataHandler rom = Rom.getAll();
			rom.seek(Integer.parseInt(offset.getText(), 16));
			rom.write(comp, 0);
			Rom.saveAll(rom);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}