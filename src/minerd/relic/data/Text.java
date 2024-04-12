package minerd.relic.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import javafx.scene.control.Control;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Pointer;
import minerd.relic.file.Rom;
import minerd.relic.file.BufferedDataHandler;

public class Text extends GameData {
	public static ArrayList<String> pokemon = new ArrayList<String>();
	private static HashMap<String, String[]> textLists;

	static{
		textLists = new HashMap<String, String[]>();

		String[] natures = {
		        "Hardy", 	"Docile", 	"Brave", 	"Jolly", 	"Impish", 
		        "Naive", 	"Timid", 	"Hasty", 	"Sassy", 	"Calm", 
		        "Relaxed", 	"Lonely", 	"Quirky"
		};
		textLists.put("Natures", natures);
		
		String[] itemTypes = {
				"Throwable", "Rock",  	"Berry/Seed", 	"Apple/Gummi", 	"Hold Item", 
				"TM", 		 "Money",	"Unused", 		"Misc.", 		"Orb", 
				"Link Box",  "Used TM"
		};
		textLists.put("Item Types", itemTypes);
		
		String[] actors = {
				"Player",		"Unknown 01",	"Unknown 02",	"Unknown 03",	"Unknown 04",	"Unknown 05",	"Unknown 06",	"Unknown 07",
				"Unknown 08",	"Unknown 09",	"Teammate 2",	"Teammate 3",	"Unknown 0C",	"Unknown 0D",	"Inhabitant 0",	"Inhabitant 1",
				"Inhabitant 2",	"Inhabitant 3",	"Inhabitant 4",	"Inhabitant 5",	"Inhabitant 6",	"Inhabitant 7",	"Inhabitant 8",	"Inhabitant 9",
				"Inhabitant A",	"Inhabitant B",	"Inhabitant C",	"Inhabitant D",	"Inhabitant E",	"Inhabitant F",	"Client 0",		"Client 1",
				"Unknown 20",	"Unknown 21",	"Partner",		"Unknown 23",	"Random",		"Wartortle",	"Blastoise 0)",	"Meowth",
				"Lickitung",	"Jirachi 0",	"Hypno",		"Noctowl",		"Metapod 0",	"Jirachi 1",	"Wigglytuff 0",	"Pelipper 0",
				"Pelipper 1",	"Pelipper 2",	"Pelipper 3",	"Azumarill",	"Unknown 34",	"Butterfree",	"Caterpie",		"Pelipper 4",	
				"Pelipper 5",	"Pelipper 6",	"Pelipper 7",	"Pelipper 8",	"Magnemite 0",	"Magnemite 1",	"Magnemite 2", 	"Magneton",	
				"Magnemite 3",	"Magnemite 4",	"Dugtrio 0",	"Dugtrio 1",	"Diglett",		"Skarmory",		"Green Kecleon","Purple Kecleon",	
				"Persian",		"Wigglytuff 1",	"Gulpin 0",		"Kangaskhan",	"Gulpin 1",		"Lombre",		"Jumpluff 0",	"Bellsprout",	
				"Snubbull",		"Granbull",		"Gardevoir",	"Absol",		"Makuhita",		"Shiftry",		"Nuzleaf 0",	"Nuzleaf 1",	
				"Alakazam",		"Charizard",	"Tyranitar",	"Gengar",		"Ekans",		"Medicham",		"Metapod 1",	"Jumpluff 1",	
				"Zapdos",		"Xatu",			"Wishcash",		"Ninetales",	"Decoy",		"Moltres",		"Articuno",		"Groudon",
				"Blastoise 1",	"Octillery",	"Golem",		"Blastoise 2",	"Rayquaza",		"Wynaut",		"Wobbuffet",	"Mankey 0",	
				"Mankey 1",		"Mankey 2",		"Mankey 3",		"Spinda",		"Entei",		"Raikou",		"Suicune",		"Ho oh",	
				"Mewtwo",		"Latios",		"Latias",		"Jirachi 2",	"Smeargle 0",	"Smeargle 1",	"Smeargle 2",	"MunchlaX",
				"Mew",			"Regirock",		"Regice",		"Registeel",	"Kyogre",		"Lugia",		"Deoxys",		"Raichu",	
				"Golbat",		"Rhydon",		"Mr Mime",		"Scyther",		"Pinsir",		"Meganium",		"Aipom",		"Phanpy",	
				"Merchant 0",	"Merchant 1",	"Unknown 92",	"Donphan"
		};
		textLists.put("Actors", actors);
				
		

		textLists.put("Traps", readTextTable(0x0F91F0, 20));
		textLists.put("Weather", readTextTable(0x0F9A54, 8));
		textLists.put("Types", readTextTable(0x10AD4C, 18));
		textLists.put("Abilities", readTextTable(0x10B4C8, 77));
		textLists.put("Dungeons", readTextTable(0x111A28, 98, 4));
		textLists.put("Friend Areas", readTextTable(0x1139D0, 58));
		textLists.put("Items", readTextTable(0x30CC28, 240, 28));
		textLists.put("Pokemon", readTextTable(0x357B98, 424, 68));
		textLists.put("Categories", readTextTable(0x357B9C, 424, 68));
		textLists.put("Moves", readTextTable(0x3679A0, 413, 32));
		textLists.put("Tracks", readTextTable(0x1E80054, 940, 4));

		try{
			String[] dunTracks = new String[75];
			BufferedDataHandler rom = Rom.getAll();
			rom.seek(0xF5668);
			for(int i = 0; i<75; i++){
				dunTracks[i] = getText("Tracks", rom.readUnsignedShort());
			}
			textLists.put("Dungeon Music", dunTracks);
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public static String[] readTextTable(int tablePointer, int count) {
		return readTextTable(tablePointer, count, 0);
	}

	// Used when there's a pointer table
	public static String[] readTextTable(int tablePointer, int count, int gap) {
		String[] texts = new String[count];
		try{
			BufferedDataHandler rom = Rom.getAll();
			rom.seek(tablePointer);
			for(int i = 0; i<count; i++){
				Pointer p = rom.parsePointer();
				texts[i] = p==null ? "null string" : rom.readString(p);
				rom.skip(gap);
			}
		} catch(InvalidPointerException e){

		} catch(IOException e){
		}
		return texts;
	}

	// Used when there's no pointer table
	public static String[][] readTextList(int listPointer, int count, int interlaces) {
		String[][] texts = new String[interlaces][count];
		try{
			BufferedDataHandler rom = Rom.getAll();
			rom.seek(listPointer);
			Stack<String> textStack = new Stack<String>();
			for(int i = 0; i<count*interlaces; i++){
				textStack.push(rom.readString());
				System.out.println(Integer.toHexString(rom.getFilePointer()));
				while(rom.peek()==0)
					rom.skip(1);
			}
			for(int i = 0; i<count; i++)
				for(int j = 0; j<interlaces; j++)
					texts[j][i] = textStack.pop();
		} catch(IOException e){
		}
		return texts;
	}

	public static String[] getTextList(String name) {
		return textLists.get(name);
	}

	public static String getText(String name, int index) {
		return textLists.get(name)[index];
	}

	@Override
	public String getName() {
		return "Text";
	}

	@Override
	public Control load() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(BufferedDataHandler rom) {
		// TODO Auto-generated method stub

	}
}