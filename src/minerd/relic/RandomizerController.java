package minerd.relic;

import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import minerd.relic.data.Cache;
import minerd.relic.data.Learnset;
import minerd.relic.data.Learnset.LevelMove;
import minerd.relic.data.Learnset.TmMove;
import minerd.relic.data.Move;
import minerd.relic.data.Pokemon;
import minerd.relic.data.Starters;
import minerd.relic.data.dungeon.Encounter;
import minerd.relic.data.dungeon.EncounterList;
import minerd.relic.data.dungeon.Floor;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.util.RrtOffsetList;

public class RandomizerController {
	public CheckBox player, partner, learnset, pokeType, abilities, moveType;
	public CheckBox dunName, tilesets, music, layout, weather;
	public CheckBox dunPoke, ground, shop, house, buried;
	public Button apply;

	public void randomize() {
		if(!Rom.isLoaded()){
			System.out.println("No Rom to randomize!");
			return;
		}

		try{
			RomFile rom = Rom.getAll();

			apply.setDisable(true);

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
						pokemon.setAbility1((int) (Math.random()*77.0 + 1));
						if(pokemon.getAbility1()==0x35)
							pokemon.setAbility1((int) (Math.random()*77.0 + 1));
						pokemon.setAbility2((int) (Math.random()*78.0));
						if(pokemon.getAbility2()==0x35)
							pokemon.setAbility2((int) (Math.random()*78.0));
						pokemon.save(rom);
					}
				}
			}

			if(learnset.isSelected()){
				// Todo: temporary quick and dirty implentation until the SIR0 util is done
				Learnset[] learnsets = new Learnset[424];
				for(int i = 1; i<424; i++){
					learnsets[i] = new Learnset(i);
					ArrayList<LevelMove> lvMoves = learnsets[i].getLvMoves();
					for(LevelMove move : lvMoves){
						move.setMoveId((int) (Math.random()*354.0 + 1));
					}
					ArrayList<TmMove> tmMoves = learnsets[i].getTmMoves();
					for(TmMove move : tmMoves){
						// Todo: pick a random element from a list of valid tms
					}
					learnsets[i].setLvMoves(lvMoves);
					// learnsets[i].setTmMoves(tmMoves);
				}
				Pointer[] pointers = new Pointer[846];
				rom.seek(0x360C06);
				for(int i = 1; i<424; i++){
					byte[] lvMoves = learnsets[i].saveLvMoves();
					byte[] tmMoves = learnsets[i].saveTmMoves();
					if((rom.getFilePointer()<0x003679A0)
							&& ((rom.getFilePointer() + lvMoves.length + tmMoves.length)>RrtOffsetList.moveOffset)){
						while(rom.getFilePointer()<RrtOffsetList.moveOffset)
							rom.writeByte((byte) 0);
						rom.seek(0x0373340);
					}
					pointers[2*i - 2] = Pointer.fromInt(rom.getFilePointer() + 0x08000000);
					rom.write(lvMoves);
					rom.writeByte((byte) 0);
					pointers[2*i - 1] = Pointer.fromInt(rom.getFilePointer() + 0x08000000);
					rom.write(tmMoves);
					rom.writeByte((byte) 0);
				}
				rom.seek(0x0372600);
				for(int i = 0; i<846; i++)
					rom.writePointer(pointers[i]);
			}

			if(moveType.isSelected()){
				Cache.alloc("Move", 413);
				for(int i = 1; i<355; i++){
					Move move = new Move(i);
					move.setType((int) (Math.random()*17.0 + 1));
					move.save(rom);
				}
			}

			//TODO: arrange the other sections this way; put in actual random ranges here
			if(tilesets.isSelected() || music.isSelected() || layout.isSelected() || weather.isSelected() ||
					dunPoke.isSelected() || ground.isSelected() || shop.isSelected() || house.isSelected() || buried.isSelected()){
				Cache.alloc("Floor", 1764);
				Cache.alloc("EncounterList", 839);
				Cache.alloc("LootList", 178);
				Cache.alloc("TrapList", 148);
				
				int[] dunTracks = { 1,  2,  3,  4,  5,  6,  7,  8,  9,  10,
									11, 12, 13, 18, 19, 20, 21, 22, 23, 24,
									25, 26, 40, 41, 66, 67, 68, 69, 73, 74 };
				
				for(int i = 1; i<1764; i++){
					Floor floor = new Floor(i, 0);
					if(tilesets.isSelected()){
						floor.setTileset((int) (Math.random()*63 + 1));
					}
					if(music.isSelected()){
						floor.setMusic(dunTracks[(int) (Math.random()*30)]);
					}
					if(layout.isSelected()){
						//TODO
					}
					if(weather.isSelected()){
						//TODO
					}
					if(ground.isSelected()){
						floor.setFloorTableId((int) (Math.random()*178));
					}
					if(shop.isSelected()){
						floor.setShopTableId((int) (Math.random()*178));
					}
					if(house.isSelected()){
						floor.setHouseTableId((int) (Math.random()*178));
					}
					if(buried.isSelected()){
						floor.setBuriedTableId((int) (Math.random()*178));
					}
					Cache.add("Floor", i, floor);
					floor.save(rom);
				}
			}
			if(dunPoke.isSelected()){
				for(int i = 0; i<839; i++){
					rom.seek(RrtOffsetList.encountersOffset + 4*i);
					rom.seek(rom.parsePointer());
					EncounterList list = new EncounterList(rom);
					for(Encounter mon : list.getEntries()) {
						mon.setId((int) (Math.random()*414.0));
					}
					rom.seek(RrtOffsetList.encountersOffset + 4*i);
					rom.seek(rom.parsePointer());
					list.save(rom);
				}
			}

			Rom.saveAll(rom);

			((Stage) apply.getScene().getWindow()).close();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}