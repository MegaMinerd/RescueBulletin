package minerd.relic.lists;

public class Starters {
	private int[] players, partners;

	public Starters(int[] playerArray, int[] partnerArray) {
		this.setPlayers(playerArray);
		this.setPartners(partnerArray);
	}

	public int[] getPlayers() {
		return players;
	}

	public int getPlayer(int id) {
		return players[id];
	}

	public void setPlayers(int[] players) {
		this.players = players;
	}

	public int[] getPartners() {
		return partners;
	}

	public int getPartner(int id) {
		return partners[id];
	}

	public void setPartners(int[] partners) {
		this.partners = partners;
	}
}
