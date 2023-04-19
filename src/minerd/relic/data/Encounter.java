package minerd.relic.data;

public class Encounter {
	private int id, level, floorProb, houseProb;

	public Encounter(int id, int level, int floorProb, int houseProb) {
		this.id = id;
		this.level = level;
		this.floorProb = floorProb;
		this.houseProb = houseProb;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getFloorProb() {
		return floorProb;
	}

	public void setFloorProb(int floorProb) {
		this.floorProb = floorProb;
	}

	public int getHouseProb() {
		return houseProb;
	}

	public void setHouseProb(int houseProb) {
		this.houseProb = houseProb;
	}

	public String getSpecies() {
		return Text.getText("Pokemon", id);
	}
	
	public String getFloorPercent() {
		return (floorProb/100.0)+"%";
	}
	
	public String getHousePercent() {
		return (houseProb/100.0)+"%";
	}
}
