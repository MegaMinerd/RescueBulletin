package minerd.relic.data;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.fxml.AreaController;

public class FriendArea extends GameData {
	private String name;
	private int population, condition;
	private long price;
	
	public FriendArea(int index, int[] offsets) throws IOException {
		RomFile rom = Rom.getAll();
		rom.seek(offsets[0]);
		rom.skip(index*0x8);
		name = Text.getText("Friend Areas", index);
		setPopulation(rom.readUnsignedShort());
		setCondition(rom.readUnsignedShort());
		setPrice(rom.readUnsignedInt());
	}

	@Override
	public Region load() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/area.fxml"));
		SplitPane dataPane = loader.load();
	    AreaController controller = loader.getController();
	    
	    controller.load(this);
	    return dataPane;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	public int getCondition() {
		return condition;
	}

	public void setCondition(int condition) {
		this.condition = condition;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}
}
