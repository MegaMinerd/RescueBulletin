package minerd.relic;

import java.io.IOException;

import javafx.scene.control.CheckBox;
import minerd.relic.data.Cache;
import minerd.relic.data.Starters;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

public class RandomizerController {
	public CheckBox player, partner;
	
	public void randomize() {
		if(!Rom.isLoaded()) {
			System.out.println("No Rom to randomize!");
			return;
		}
		
		try {
			RomFile rom = Rom.getAll();
			if(player.isSelected() || partner.isSelected()) {
				Cache.alloc("Starters", 1);
				int[] off = {0x00F278E, 0x000F4264};
				Starters starters = new Starters(0, off);
				if(player.isSelected()) {
					int[] players = new int[26];
					for(int i=0; i<26; i++)
						players[i] = (int)(Math.random()*414.0);
					starters.setPlayers(players);
				}
				if(partner.isSelected()) {
					int[] partners = new int[10];
					for(int i=0; i<10; i++)
						partners[i] = (int)(Math.random()*414.0);
					starters.setPartners(partners);
				}
				starters.save(rom);
			}
			Rom.saveAll(rom);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}