package minerd.relic.area;

import java.io.IOException;

import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

public class FriendArea{
	private String name;
	private int population, condition;
	private long price;
	
	public FriendArea(String name) throws IOException {
		this.name = name;
		RomFile rom = Rom.getAll();
		setPopulation(rom.readUnsignedShort());
		setCondition(rom.readUnsignedShort());
		setPrice(rom.readUnsignedInt());
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
