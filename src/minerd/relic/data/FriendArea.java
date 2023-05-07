package minerd.relic.data;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.fxml.AreaController;
import minerd.relic.util.RrtOffsetList;

public class FriendArea extends GameData {
	private String name;
	private int index, population, condition;
	private long price;

	public FriendArea(int index) {
		this.index = index;
		name = Text.getText("Friend Areas", index);

		// TODO: code seems inconsistent on whether GameData
		// constructors catch or throw IOExceptions
		try{
			RomFile rom = Rom.getAll();
			rom.seek(RrtOffsetList.areaOffset);
			rom.skip(index*0x8);
			population = rom.readUnsignedShort();
			condition = rom.readUnsignedShort();
			price = rom.readUnsignedInt();
		} catch(IOException e){

		}
	}

	@Override
	public Region load() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/area.fxml"));
		SplitPane dataPane = loader.load();
		AreaController controller = loader.getController();

		controller.load(this);
		return dataPane;
	}

	@Override
	public void save(RomFile rom) {
		try{
			rom.seek(RrtOffsetList.areaOffset);
			rom.skip(index*0x8);
			rom.writeUnsignedShort(population);
			rom.writeUnsignedShort(condition);
			rom.writeUnsignedInt(price);
		} catch(IOException e){

		}
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
