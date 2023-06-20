package minerd.relic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import minerd.relic.data.Cache;
import minerd.relic.data.Learnset;
import minerd.relic.data.Learnset.LevelMove;
import minerd.relic.data.Move;
import minerd.relic.data.Pokemon;
import minerd.relic.data.Starters;
import minerd.relic.data.dungeon.Encounter;
import minerd.relic.data.dungeon.EncounterList;
import minerd.relic.data.dungeon.Floor;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;
import minerd.relic.file.BufferedDataHandler;
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
			long time = System.currentTimeMillis();
			BufferedDataHandler rom = Rom.getAll();

			apply.setDisable(true);

			int[] legendaries = {144, 145, 146, 150, 151, 270, 271, 272, 276, 277,
								 278, 407, 408, 409, 410, 411, 412, 413, 414, 415,
								 416};

			if(player.isSelected() || partner.isSelected()){
				Cache.alloc("Starters", 1);
				int[] off = { 0x00F278E, 0x000F4264 };
				Starters starters = new Starters(0);
				if(player.isSelected()){
					int[] players = new int[26];
					for(int i = 0; i<26; i++){
						players[i] = (int) (Math.random()*415.0+1);
						if(Arrays.binarySearch(legendaries, players[i])>=0 && Math.random()>0.5f)
							players[i] = (int) (Math.random()*415.0+1);
					}
					starters.setPlayers(players);
				}
				if(partner.isSelected()){
					int[] partners = new int[10];
					for(int i = 0; i<10; i++){
						partners[i] = (int) (Math.random()*415.0+1);
						if(Arrays.binarySearch(legendaries, partners[i])>=0 && Math.random()>0.5f)
							partners[i] = (int) (Math.random()*415.0+1);
					}
					starters.setPartners(partners);
				}
				starters.save(rom);
			}

			if(pokeType.isSelected() || abilities.isSelected() || learnset.isSelected()){
				Cache.alloc("Pokemon", 416);
				for(int i = 1; i<416; i++){
					Pokemon pokemon = new Pokemon(i);
					Cache.add("Pokemon", i, pokemon);
					if(pokeType.isSelected()){
						pokemon.setType1((int) (Math.random()*17.0 + 1));
						do{
							pokemon.setType2((int) (Math.random()*18.0));
						} while(pokemon.getType1()==pokemon.getType2());
					}
					if(abilities.isSelected()){
						pokemon.setAbility1((int) (Math.random()*77.0 + 1));
						//Reroll Wonder Guard once
						if(pokemon.getAbility1()==0x35)
							pokemon.setAbility1((int) (Math.random()*77.0 + 1));
						pokemon.setAbility2((int) (Math.random()*78.0));
						//Reroll Wonder Guard once
						//Duplicate abilities are rare enough to ignore for now
						if(pokemon.getAbility2()==0x35)
							pokemon.setAbility2((int) (Math.random()*78.0));
					}
					//No need to reach the save step if we only wanted to populate the cache
					//Done this way to reduce number of conditional checks
					else if(!pokeType.isSelected())
						continue;
					pokemon.save(rom);
				}
			}

			ArrayList[] types = new ArrayList[18];
			for(int i = 0; i<18; i++)
				types[i] = new ArrayList();
			if(moveType.isSelected() || learnset.isSelected()){
				Cache.alloc("Move", 413);
				//Note: there are 413 moves in the data
				//But 355 and later are orbs/special/unused
				for(int i = 1; i<355; i++){
					Move move = new Move(i);
					Cache.add("Move", i, move);
					if(moveType.isSelected()){
						// Skip Struggle
						if(i==352)
							continue;
						move.setType((int) (Math.random()*17.0 + 1));
						move.save(rom);
					}
					if(learnset.isSelected()){
						types[0].add(i);
						types[move.getType()].add(i);
					}
				}
			}

			if(learnset.isSelected()){
				//TODO: temporary quick and dirty implentation until the SIR0 util is done
				//TODO: is this the correct number?
				Learnset[] learnsets = new Learnset[416];
				for(int i = 1; i<416; i++){
					learnsets[i] = new Learnset(i);
					ArrayList<LevelMove> lvMoves = learnsets[i].getLvMoves();
					for(LevelMove move : lvMoves){
						int list = 0;
						switch((int) (Math.random()*5)){
							case 0:
								list = 1;
								break;
							case 1:
								list = ((Pokemon) Cache.get("Pokemon", i)).getType1();
								break;
							case 2:
								list = ((Pokemon) Cache.get("Pokemon", i)).getType2();
								break;
						}
						int id = (int) types[list].get((int) (Math.random()*types[list].size()));
						int power = ((Move) Cache.get("Move", id)).getPower();
						if(move.getLevel()==0 && (power<15 || power>60))
							id = (int) types[list].get((int) (Math.random()*types[list].size()));
						if(move.getLevel()==0 && (power<15 || power>60))
							id = (int) types[list].get((int) (Math.random()*types[list].size()));
						if(move.getLevel()!=0 && move.getLevel()<16 && (power<30 || power>90))
							id = (int) types[list].get((int) (Math.random()*types[list].size()));
						move.setMoveId(id);
					}
					//ArrayList<TmMove> tmMoves = learnsets[i].getTmMoves();
					//for(TmMove move : tmMoves){
					//TODO: pick a random element from a list of valid tms
					//}
					learnsets[i].setLvMoves(lvMoves);
					//learnsets[i].setTmMoves(tmMoves);
				}
				Pointer[] pointers = new Pointer[830];
				rom.seek(0x360C06);
				for(int i = 1; i<416; i++){
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
				//TODO: There are actually 848 learnsets in data. I have decided to not care for the beta
				for(int i = 0; i<830; i++)
					rom.writePointer(pointers[i]);
			}

			if(tilesets.isSelected() || music.isSelected() || layout.isSelected() || weather.isSelected()
					|| dunPoke.isSelected() || ground.isSelected() || shop.isSelected() || house.isSelected()
					|| buried.isSelected()){
				Cache.alloc("Floor", 1764);
				Cache.alloc("EncounterList", 839);
				Cache.alloc("LootList", 178);
				Cache.alloc("TrapList", 148);

				int[] dunTracks =  {   1,    2,    3,    4,    5,    6,    7,    8,    9,   10,
									  11,   12,   13,   18,   19,   20,   21,   22,   23,   24,
									  25,   26,   40,   41,   66,   67,   68,   69,   73,   74 };
				
				for(int i = 1; i<1764; i++){
					Floor floor = new Floor(i, 0);
					Cache.add("Floor", i, floor);
					if(floor.getFixedRoom()!=0){
						//Randomizing this may cause a crash
						System.out.println("Skipping layout " + i);
						continue;
					}
					System.out.println("Randomizing layout " + i);
					if(tilesets.isSelected()){
						floor.setTileset((int) (Math.random()*63 + 1));
					}
					if(music.isSelected()){
						floor.setMusic(dunTracks[(int) (Math.random()*30)]);
					}
					if(layout.isSelected()){
						floor.setLayoutType((int) (Math.random()*10 + 1));
						if(floor.getLayoutType()==2 || floor.getLayoutType()==5)
							floor.setLayoutType((int) (Math.random()*10 + 1));
					}
					if(weather.isSelected()){
						//"Clear", "Sunny", "Sandstorm", "Cloudy",
						//"Rain", "Hail", "Fog", "Snow", "Random"
						double rand = Math.random();
						int result;
						if(rand<0.4)
							result = 0;
						else if(rand<0.5)
							result = 1;
						else if(rand<0.6)
							result = 3;
						else if(rand<0.7)
							result = 4;
						else if(rand<0.8)
							result = 6;
						else if(rand<0.9)
							result = 7;
						else if(rand<0.93)
							result = 2;
						else if(rand<0.95)
							result = 5;
						else
							result = 8;
						floor.setWeather(result);
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
					floor.save(rom);
				}
			}

			if(dunPoke.isSelected()){
				for(int i = 0; i<839; i++){
					rom.seek(RrtOffsetList.encountersOffset + 4*i);
					rom.seek(rom.parsePointer());
					EncounterList list = new EncounterList(rom);
					for(Encounter mon : list.getEntries()){
						mon.setId((int) (Math.random()*415.0+1));
						if(Arrays.binarySearch(legendaries, mon.getId())>=0 && Math.random()>0.5f)
							mon.setId((int) (Math.random()*415.0+1));
					}
					rom.seek(RrtOffsetList.encountersOffset + 4*i);
					rom.seek(rom.parsePointer());
					list.save(rom);
				}
			}

			Rom.saveAll(rom);

			((Stage) apply.getScene().getWindow()).close();

			System.out.println("Done in " + (System.currentTimeMillis() - time) + "ms");
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}