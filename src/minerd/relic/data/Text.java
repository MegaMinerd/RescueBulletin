package minerd.relic.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import javafx.scene.control.Control;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

public class Text extends GameData {
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

		textLists.put("Weather", readTextTable(0x0F9A54, 8));
		textLists.put("Types", readTextTable(0x10AD4C, 18));
		textLists.put("Abilities", readTextTable(0x10B4C8, 77));
		textLists.put("Friend Areas", readTextTable(0x1139D0, 58));
		
		String[][] texts = readTextList(0x35F2D8, 424, 2);
		textLists.put("Pokemon", texts[0]);
		textLists.put("Categories", texts[1]);
	}
	
	//Used when there's a pointer table
	public static String[] readTextTable(int tablePointer, int count) {
		String[] texts = new String[count];
		try {
			RomFile rom = Rom.getAll();
			rom.seek(tablePointer);
			for(int i=0; i<count; i++)
				texts[i] = rom.readStringAndReturn(rom.parsePointer());
		} catch(InvalidPointerException e) {
			
		} catch (IOException e) {
		}
		return texts;
	}
	
	//Used when there's no pointer table
	public static String[][] readTextList(int listPointer, int count, int interlaces) {
		String[][] texts = new String[interlaces][count];
		try {
			RomFile rom = Rom.getAll();
			rom.seek(listPointer);
			Stack<String> textStack = new Stack<String>(); 
			for(int i=0; i<count*interlaces; i++) {
				textStack.push(rom.readString());
				System.out.println(Integer.toHexString(rom.getFilePointer()));
				while(rom.peek()==0)
					rom.skip(1);
			}
			for(int i=0; i<count; i++)
				for(int j=0; j<interlaces; j++)
					texts[j][i] = textStack.pop();
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

	@Override
	public String getName() {
		return "Text";
	}

	@Override
	public Control load() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
