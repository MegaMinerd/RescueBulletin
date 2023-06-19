package minerd.relic.fxml.script;

import javafx.scene.control.Label;
import minerd.relic.file.Pointer;

public class MapHeaderController {
	public Label offset0, offset1, offset2, data0, data1, data2;

	public void load(Pointer headerPointer, int sceneNum, Pointer scenePointer, Pointer waypointPointer) {
		offset0.setText(Integer.toHexString(headerPointer.getOffset()));
		offset1.setText(Integer.toHexString((headerPointer.getOffset()+4)));
		offset2.setText(Integer.toHexString((headerPointer.getOffset()+8)));
		data0.setText(sceneNum+"");
		data1.setText(Integer.toHexString(scenePointer.getOffset()));
		data2.setText(Integer.toHexString(waypointPointer.getOffset()));
	}

}
