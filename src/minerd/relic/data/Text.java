package minerd.relic.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

public class Text {
	public static ArrayList<String> pokemon = new ArrayList<String>();
	private static HashMap<String, String[]> textLists;
	
	static {
		textLists = new HashMap<String, String[]>();
		
		String[] natures = {
		        "Hardy", 	"Docile", 	"Brave", 	"Jolly", 	"Impish", 
		        "Naive", 	"Timid", 	"Hasty", 	"Sassy", 	"Calm", 
		        "Relaxed", 	"Lonely", 	"Quirky"
		};
		textLists.put("Natures", natures);

		textLists.put("Weather", readTextList(0x0F9A54, 8));
		textLists.put("Types", readTextList(0x10AD4C, 18));
		textLists.put("Abilities", readTextList(0x10B4C8, 77));
		textLists.put("Friend Areas", readTextList(0x1139D0, 58));
		
	}
	
	public static String[] readTextList(int tablePointer, int count) {
		String[] texts = new String[count];
		try {
			RomFile rom = Rom.getAll();
			int last = rom.getFilePointer();
			rom.seek(tablePointer);
			for(int i=0; i<count; i++)
				texts[i] = rom.readStringAndReturn(rom.parsePointer());
			rom.seek(last);
		} catch(InvalidPointerException e) {
			
		} catch (IOException e) {
		}
		return texts;
	}
	
	public static String[] getTextList(String name) {
		return textLists.get(name);
	}
	
	public static String getText(String name, int index) {
		return textLists.get(name)[index];
	}
}
