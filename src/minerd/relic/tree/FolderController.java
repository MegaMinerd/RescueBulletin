package minerd.relic.tree;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class FolderController implements Initializable {
	public Label name, info;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	public void populate(String n, String i) {
		name.setText(n);
		info.setText(i);
	}
}
