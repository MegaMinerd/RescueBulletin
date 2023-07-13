package minerd.relic.fxml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import minerd.relic.graphics.ImageProcessor;

public class PortraitImportController implements Initializable{
	FileChooser fc;
	File imgFile;
	
	public Button openButton, saveButton;
	public TextField offset;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		fc = new FileChooser();
		fc.setTitle("Open Portrait Sheet");
		fc.getExtensionFilters().add(new ExtensionFilter("Images", "*.png"));
	}

	public void openFile() {
		File file = fc.showOpenDialog(null);
		if(file!=null){
			imgFile = file;
		}
	}
	
	public void save() {
		try{
			ImageProcessor.importPortraits(imgFile, Integer.parseInt(offset.getText(), 16), 0x1FFF, true);
		} catch(NumberFormatException | IOException e){
			e.printStackTrace();
		}
	}
}