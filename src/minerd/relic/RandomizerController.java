package minerd.relic;

import java.io.IOException;

import javafx.scene.control.CheckBox;
import minerd.relic.data.Cache;
import minerd.relic.data.Move;
import minerd.relic.data.Pokemon;
import minerd.relic.data.Starters;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

public class RandomizerController {
	public CheckBox player, partner, pokeType, abilities, moveType;

	public void randomize() {
		if(!Rom.isLoaded()){
			System.out.println("No Rom to randomize!");
			return;
		}

		try{
			RomFile rom = Rom.getAll();
			if(player.isSelected() || partner.isSelected()){
				Cache.alloc("Starters", 1);
				int[] off = { 0x00F278E, 0x000F4264 };
				Starters starters = new Starters(0);
				if(player.isSelected()){
					int[] players = new int[26];
					for(int i = 0; i<26; i++)
						players[i] = (int) (Math.random()*414.0);
					starters.setPlayers(players);
				}
				if(partner.isSelected()){
					int[] partners = new int[10];
					for(int i = 0; i<10; i++)
						partners[i] = (int) (Math.random()*414.0);
					starters.setPartners(partners);
				}
				starters.save(rom);
			}

			if(pokeType.isSelected() || abilities.isSelected()){
				Cache.alloc("Pokemon", 413);
				for(int i = 1; i<413; i++){
					Pokemon pokemon = new Pokemon(i);
					Cache.add("Pokemon", i, pokemon);
				}
				
				if(pokeType.isSelected()){
					for(int i = 1; i<413; i++){
						Pokemon pokemon = (Pokemon) Cache.get("Pokemon", i);
						pokemon.setType1((int) (Math.random()*17.0 + 1));
						pokemon.setType2((int) (Math.random()*18.0));
						pokemon.save(rom);
					}
				}

				if(abilities.isSelected()){
					for(int i = 1; i<413; i++){
						Pokemon pokemon = (Pokemon) Cache.get("Pokemon", i);
						pokemon.setAbility1((int) (Math.random()*78.0));
						if(pokemon.getAbility1()==0x35)
							pokemon.setAbility1((int) (Math.random()*78.0));
						pokemon.setAbility2((int) (Math.random()*77.0 + 1));
						if(pokemon.getAbility2()==0x35)
							pokemon.setAbility2((int) (Math.random()*77.0 + 1));
						pokemon.save(rom);
					}
				}
			}

			if(moveType.isSelected()){
				Cache.alloc("Move", 413);
				for(int i = 1; i<355; i++){
					Move move = new Move(i);
					move.setType((int) (Math.random()*17.0 + 1));
					move.save(rom);
				}
			}

			Rom.saveAll(rom);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}