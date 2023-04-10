package minerd.relic.data;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.fxml.StartersController;

public class Starters extends GameData{
	private int[] players, partners;

	public Starters(int index, int[] pointers) {
		try {
			RomFile rom = Rom.getAll();

			rom.seek(pointers[0]);
			players = new int[26];
			for(int i=0; i<26; i++)
				players[i] = rom.readUnsignedShort();
			
			rom.seek(pointers[1]);
			partners = new int[10];
			for(int i=0; i<10; i++)
				partners[i] = rom.readUnsignedShort();
			System.out.println("loaded");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Region load() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/starters.fxml"));
		AnchorPane dataPane = loader.load();
	    StartersController controller = loader.getController();
	    
	    controller.load(this);
	    return dataPane;
	}

	@Override
	public String getName() {
		return "Starters";
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