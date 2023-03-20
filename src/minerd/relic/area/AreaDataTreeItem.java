package minerd.relic.area;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import minerd.relic.RomManipulator;
import minerd.relic.tree.DataTreeItem;

public class AreaDataTreeItem extends DataTreeItem{
	int dataPointer;

	public AreaDataTreeItem(String areaName, int areaStart) {
		super(areaName);
		dataPointer = areaStart;
	}
	
	@Override
	public Node select(){
		SplitPane areaDataPane = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("area.fxml"));
			areaDataPane = loader.load();
			AreaController controller = loader.getController();
			
			RomManipulator.seek(dataPointer);
			FriendArea area = new FriendArea(getName());
			
			controller.load(area);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return areaDataPane;
	}
}
