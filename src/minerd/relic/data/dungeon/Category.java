package minerd.relic.data.dungeon;

import minerd.relic.data.Text;

public class Category {
	private int id, weight;
	
    public Category(int id, int weight) {
    	this.id = id;
    	this.weight = weight;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return Text.getText("Item Types", id);
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getChance() {
		return (weight/100.0)+"%";
	}
}