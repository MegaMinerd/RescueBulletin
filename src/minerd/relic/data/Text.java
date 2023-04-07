package minerd.relic.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import minerd.relic.InvalidPointerException;
import minerd.relic.RomManipulator;

public class Text {
	public static ArrayList<String> pokemon = new ArrayList<String>();
	private static HashMap<String, String[]> textLists;
	
	static {
		textLists = new HashMap<String, String[]>();
		textLists.put("Types", readTextList(0x10AD4C, 18));
		
		String[] natures = {
		        "Hardy", 	"Docile", 	"Brave", 	"Jolly", 	"Impish", 
		        "Naive", 	"Timid", 	"Hasty", 	"Sassy", 	"Calm", 
		        "Relaxed", 	"Lonely", 	"Quirky"
		};
		
		textLists.put("Natures", natures);
		textLists.put("Friend Areas", readTextList(0x1139D0, 58));
		textLists.put("Abilities", readTextList(0x10B4C8, 77));
		
	}
	
	public static String[] readTextList(int tablePointer, int count) {
		String[] texts = new String[count];
		try {
			int last = RomManipulator.getFilePointer();
			RomManipulator.seek(tablePointer);
			for(int i=0; i<count; i++)
				texts[i] = RomManipulator.readStringAndReturn(RomManipulator.parsePointer());
			RomManipulator.seek(last);
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
