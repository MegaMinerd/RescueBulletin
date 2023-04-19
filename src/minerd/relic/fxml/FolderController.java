package minerd.relic.fxml;

import javafx.scene.control.Label;

public class FolderController{
	public Label name, info;

	public void load(String n, String i) {
		name.setText(n);
		info.setText(i);
	}
}
