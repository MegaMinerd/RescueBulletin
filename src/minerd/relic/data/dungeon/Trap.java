package minerd.relic.data.dungeon;

public class Trap {
	private String name;
	private int prob;

	public Trap(String name, int prob) {
		this.name = name;
		this.prob = prob;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getProb() {
		return prob;
	}

	public void setProb(int prob) {
		this.prob = prob;
	}
	
	public String getPercent() {
		return (prob/100.0) + "%";
	}
}
