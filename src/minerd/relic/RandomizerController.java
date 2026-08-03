package minerd.relic;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import minerd.relic.data.Cache;
import minerd.relic.data.Item;
import minerd.relic.data.Learnset;
import minerd.relic.data.Learnset.LevelMove;
import minerd.relic.data.Learnset.TmMove;
import minerd.relic.data.Move;
import minerd.relic.data.Pokemon;
import minerd.relic.data.Starters;
import minerd.relic.data.Text;
import minerd.relic.data.dungeon.Floor;
import minerd.relic.file.Rom;

public class RandomizerController implements Initializable {
	//Pokemon tab
	public CheckBox typeBox, abilityBox, mobilityBox, levelupBox, tmsetBox, damageBox, scaleBox;
	public ChoiceBox<String> biasChoice;
	public VBox moveWhitelist, abilityWhitelist;
	public CheckBox[] moveWhitelistBoxes, abilityWhitelistBoxes;
	
	//Pokemon tab
	public CheckBox layoutBox, roomBox, pathBox, deadendBox, enemiesBox, trapBox, itemBox, pondBox;
	public CheckBox shopBox, houseBox, stickyBox, moneyBox, weatherBox, tileBox, musicBox;
	public ChoiceBox<String> enemyChoice, trapChoice, floorChoice, houseChoice, shopChoice, buriedChoice;
	public VBox pokemonWhitelist;
	public CheckBox[] pokemonWhitelistBoxes;
	
	//Other tab
	public CheckBox playerBox, partnerBox, storyBox, bossBox, tmBox, priceBox, movetypeBox;
	
	
	//old
	public CheckBox player, partner;
	public CheckBox dunName, tilesets, music, layout, weather;
	public CheckBox dunPoke, ground, shop, house, buried;
	public Button apply;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		biasChoice.getItems().addAll("None", "STAB", "STAB or Normal");
		biasChoice.getSelectionModel().select(0);
		
		String[] options = { "Vanilla", "Shuffle", "Random", "Both" };
		enemyChoice.getItems().addAll(options);
		enemyChoice.getSelectionModel().select(0);
		trapChoice.getItems().addAll(options);
		trapChoice.getSelectionModel().select(0);
		floorChoice.getItems().addAll(options);
		floorChoice.getSelectionModel().select(0);
		houseChoice.getItems().addAll(options);
		houseChoice.getSelectionModel().select(0);
		shopChoice.getItems().addAll(options);
		shopChoice.getSelectionModel().select(0);
		buriedChoice.getItems().addAll(options);
		buriedChoice.getSelectionModel().select(0);
	
		
		int num = Text.getTextList("Moves").length;
		moveWhitelistBoxes = new CheckBox[num];
		int end=9999;
		for(int i=1; i<num; i++) {
			String name = Text.getText("Moves", i);
			moveWhitelistBoxes[i] = new CheckBox(name);
			if(name.equals("attack"))
				end=i;
			if(i<end && !(name.equals("Struggle") || name.equals("Bide") || name.equals("Avalanche") || name.equals("Revenge") || name.equals("Teeter Dance")))
				moveWhitelistBoxes[i].selectedProperty().set(true);
			moveWhitelist.getChildren().add(moveWhitelistBoxes[i]);
		}
		
		
		num = Text.getTextList("Abilities").length;
		abilityWhitelistBoxes = new CheckBox[num];
		for(int i=1; i<num; i++) {
			String name = Text.getText("Abilities", i);
			abilityWhitelistBoxes[i] = new CheckBox(name);
			if(!name.equals("Wonder Guard"))
				abilityWhitelistBoxes[i].selectedProperty().set(true);
			abilityWhitelist.getChildren().add(abilityWhitelistBoxes[i]);
		}
		
		num = Text.getTextList("Pokemon").length;
		pokemonWhitelistBoxes = new CheckBox[num];
		for(int i=1; i<num; i++) {
			String name = Text.getText("Pokemon", i);
			pokemonWhitelistBoxes[i] = new CheckBox(name);
			if(!(name.equals("Shedinja") || name.equals("Munchlax") || name.equals("Decoy") || name.equals("Statue")))
				pokemonWhitelistBoxes[i].selectedProperty().set(true);
			pokemonWhitelist.getChildren().add(pokemonWhitelistBoxes[i]);
		}
	}

	public void randomize() {
		try{
			long time = System.currentTimeMillis();
			Rom rom = Rom.getInstance();
			
			//Data in system.sbin
			boolean shouldSave = false;
			
			//Pokemon data
			if(typeBox.isSelected() || abilityBox.isSelected() || mobilityBox.isSelected()){
				shouldSave = true;
				//Grab pokemon count
				float pokemonNum = (float)Text.getTextList("Pokemon").length;
				for(int i = 1; i<pokemonNum; i++){
					Pokemon pokemon = Pokemon.getPokemon(i);
					if(typeBox.isSelected()){
						//Roll first type
						pokemon.setType1((int) (Math.random()*17.0 + 1));
						
						//Roll second type until it doesn't match first type
						//Monotype is weighted by being selected upon rolling 0, -1, or -2
						do{
							pokemon.setType2((int) Math.max((Math.random()*20.0 -2), 0));
						} while(pokemon.getType1()==pokemon.getType2());
					}
					if(abilityBox.isSelected()){
						//Grab ability count
						float abilityNum = (float)Text.getTextList("Abilities").length;
						int ability;
						
						//Roll first ability until a permitted one is selected
						do {
							ability = (int) (Math.random()*(abilityNum-1));
						} while (!abilityWhitelistBoxes[ability].isSelected());
						pokemon.setAbility1(ability);
						
						//Only generate a second ability 3/4 of the time
						if((Math.random()*4.0) > 1) {
							//Roll second ability until a non-duplicate permitted one is selected
							do {
								ability = (int) (Math.random()*(abilityNum-1));
							} while (!abilityWhitelistBoxes[ability].isSelected() || pokemon.getAbility1()==ability);
							pokemon.setAbility2(ability);
						}
					}
					if(mobilityBox.isSelected()){
						pokemon.setMovement((int)(Math.random()*4.0));
					}
					pokemon.save();
				}
			}
			
			//Move data
			ArrayList<Integer>[] types = new ArrayList[18];
			for(int i = 0; i<18; i++)
				types[i] = new ArrayList();
			if(movetypeBox.isSelected() || levelupBox.isSelected()){
				shouldSave = true;
				//Grab move count
				float moveNum = (float)Text.getTextList("Moves").length;

				for(int i = 1; i<moveNum; i++){
					Move move = Move.getMove(i);
					//Randomize move type unless it is typeless
					if(movetypeBox.isSelected() && move.getType()>0){
						move.setType((int) (Math.random()*17.0 + 1));
						move.save();
					}
					//Sort the move into a list by type
					if(levelupBox.isSelected()){
						types[0].add(i);
						types[move.getType()].add(i);
					}
				}
			}
			
			//TM list
			ArrayList<Integer> tmMovesList = new ArrayList<Integer>();
			if(tmBox.isSelected() || tmsetBox.isSelected()){
				//Grab item count
				int itemNum = Text.getTextList("Items").length;
				//Grab move count
				float moveNum = (float)Text.getTextList("Moves").length;
				
				//Mark down the available TMs to randomize tmsets and prevent repeats
				for(int i=0; i<itemNum; i++) {
					Item item = Item.getItem(i);
					if(item.getItemType()==5) {
						if(tmBox.isSelected()){
							shouldSave = true;
							int moveId = 0;
							do {
								moveId = (int) (Math.random()*(moveNum-1))+1;
							} while (!moveWhitelistBoxes[moveId].isSelected() || tmMovesList.contains(moveId));
							Item tmItem = Item.getItem(i);
							tmItem.setMoveId(moveId);
							tmItem.setName(String.format("%c%c%s", 0x87, 0x4E, Move.getMove(moveId).getName()));
							tmItem.setDescription(String.format("Teaches the move #c4%s#r.%c%s", Move.getMove(moveId).getName(), 0x0D, Move.getMove(moveId).getDescription()));
						}
						tmMovesList.add(item.getMoveId());
					}
				}
			}
			
			//TMset data
			if(tmsetBox.isSelected()){
				shouldSave = true;
				//Grab pokemon count
				float pokemonNum = (float)Text.getTextList("Pokemon").length;
				for(int i = 1; i<pokemonNum; i++){
					Learnset learnset = Pokemon.getPokemon(i).getLearnset();
					ArrayList<TmMove> tmMoves = learnset.getTmMoves();
					for(TmMove move : tmMoves){
						move.setMoveId(tmMovesList.get((int)(Math.random()*tmMovesList.size())));
					}
				}
			}
			
			//Learnset data
			if(levelupBox.isSelected()){
				//Grab pokemon count
				float pokemonNum = (float)Text.getTextList("Pokemon").length;
				float moveNum = (float)Text.getTextList("Moves").length;
				for(int i = 1; i<pokemonNum; i++){
					Learnset learnset = Pokemon.getPokemon(i).getLearnset();
					
					//Randomize levelup moves
					ArrayList<LevelMove> lvMoves = learnset.getLvMoves();
					for(LevelMove move : lvMoves){
						//A value of 0 means use any move
						int list = 0;
						//Bias the type
						if(biasChoice.getSelectionModel().getSelectedIndex()==1) {
							switch((int) (Math.random()*3.0)){
								case 0:
									list = Pokemon.getPokemon(i).getType1();
									break;
								case 1:
									list = Pokemon.getPokemon(i).getType2();
									if(list==0)
										//Pokemon is monotype
										list = Pokemon.getPokemon(i).getType1();
									break;
							}
						} else if (biasChoice.getSelectionModel().getSelectedIndex()==2) {
							switch((int) (Math.random()*4.0)){
								case 0:
									list = 1;
									break;
								case 1:
									list = Pokemon.getPokemon(i).getType1();
									break;
								case 2:
									list = Pokemon.getPokemon(i).getType2();
									if(list==0)
										//Pokemon is monotype
										list = Pokemon.getPokemon(i).getType1();
									break;
							}
						}
						
						//Roll moves until one that suits the settings is selected
						int moveID = 0;
						//For performance, only roll 5 times, unless a banned move is rolled
						int rollNum = 0;
						do {
							rollNum++;
							if(list==0) {
								moveID = (int) (Math.random()*(moveNum-1))+1;
							} else {
								moveID = (int) types[list].get((int) (Math.random()*types[list].size()));
							}
							
							//Reject deselected moves
							if(!moveWhitelistBoxes[moveID-1].isSelected())
								continue;
							
							//This move is permitted and the roll cap is reached
							if(rollNum>=5)
								break;
							
							//Reject non-damaging moves with 62.5% probability, if preference is on
							//After all rerolls, assuming 60% of moves do damage, about 20% of learned moves will be status
							if(damageBox.isSelected() && Math.random() > 0.625 && Move.getMove(moveID).getPower()==0)
								continue;
							
							//Reject moves of the wrong power, if scaling is on
							if(scaleBox.isSelected()) {
								int power = Move.getMove(moveID).getPower();
								if(move.getLevel()==0 && (power<15 || power>60))
									continue;
								if(move.getLevel()!=0 && move.getLevel()<16 && (power<30 || power>90))
									continue;
							}
								
							break;
						} while(true);
						
						move.setMoveId(moveID);
					}
				}
			}
			
			//if(shouldSave)
			//TODO: Save system.sbin to the ROM
			shouldSave = false;

			
			
			//Begin old code (will gradually convert to new version
			
			
			int[] legendaries = {144, 145, 146, 150, 151, 270, 271, 272, 276, 277,
								 278, 407, 408, 409, 410, 411, 412, 413, 414, 415,
								 416};

			if(playerBox.isSelected() || partnerBox.isSelected()){
				Cache.alloc("Starters", 1);
				Starters starters = new Starters(0);
				if(playerBox.isSelected()){
					int[] players = new int[26];
					for(int i = 0; i<26; i++){
						players[i] = (int) (Math.random()*415.0+1);
						//If a legendary was rolled, reroll once
						if(Arrays.binarySearch(legendaries, players[i])>=0 && Math.random()>0.5f)
							players[i] = (int) (Math.random()*415.0+1);
					}
					starters.setPlayers(players);
				}
				if(partnerBox.isSelected()){
					int[] partners = new int[10];
					for(int i = 0; i<10; i++){
						partners[i] = (int) (Math.random()*415.0+1);
						//If a legendary was rolled, reroll once
						if(Arrays.binarySearch(legendaries, partners[i])>=0 && Math.random()>0.5f)
							partners[i] = (int) (Math.random()*415.0+1);
					}
					starters.setPartners(partners);
				}
				starters.save();
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
					Floor floor = new Floor(i, 0, 0);
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
					floor.save();
				}
			}
		    
		    int[] bosses = {  9,  21,  38,  63,  71,  79,  80,  96, 111, 127,
		                    160, 162, 277, 286, 294, 298, 338, 394, 411, 427,
		                    442, 480, 726, 838};
		
//			if(dunPoke.isSelected()){
//				for(int i = 0; i<839; i++){
//					if(Arrays.binarySearch(bosses, i)>=0){
//						//Randomizing this WILL cause a crash
//						continue;
//					}
//					rom.seek(RrtOffsetList.encountersOffset + 4*i);
//					rom.seek(rom.parsePointer());
//					EncounterList list = new EncounterList(rom);
//					//Can't be foreach. The last 2 must be skipped to avoid a crash.
//					for(int j=0; j<list.getEntries().size()-2; j++){
//						Encounter mon = list.getEntries().get(j);
//						mon.setId((int) (Math.random()*415.0+1));
//						if(Arrays.binarySearch(legendaries, mon.getId())>=0 && Math.random()>0.5f)
//							mon.setId((int) (Math.random()*415.0+1));
//					}
//					rom.seek(RrtOffsetList.encountersOffset + 4*i);
//					rom.seek(rom.parsePointer());
//					list.save(rom);
//				}
//			}

			Rom.getInstance().saveAll(rom.getAll());

			((Stage) apply.getScene().getWindow()).close();

			System.out.println("Done in " + (System.currentTimeMillis() - time) + "ms");
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}