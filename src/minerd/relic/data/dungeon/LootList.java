package minerd.relic.data.dungeon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.layout.Region;
import minerd.relic.data.GameData;
import minerd.relic.file.RomFile;

public class LootList extends GameData{
    private ArrayList<Category> categories;
    private ArrayList<Loot> loot;
    
    public LootList(RomFile rom) throws IOException{
        HashMap<Integer, Integer> iweightEntries = new HashMap<Integer, Integer>();
        int itemId = 0;
        int maxId = 400; //depends on game
        while(itemId < maxId){
            int data = rom.readUnsignedShort();
            if(data<30000){
                iweightEntries.put(itemId, data);
                itemId++;
            }else
                itemId += data-30000;
        }
        
        //Todo: use actual size
        int catNum = 12;
        int lastValue = 0;
        categories = new ArrayList<Category>();
        for(int key : iweightEntries.keySet())  {
        	int value = iweightEntries.get(key);
            if(key < catNum){
                Category cat = new Category(key, value-lastValue);
                categories.add(cat);
                lastValue= value;
            }else{
                // Todo
            }
        }
    }
    
    public Region load() throws IOException{
        return null;
    }
    
    public RomFile save() throws IOException{
        return null;
    }
    
    public String getName(){
        return "Loot List";
    }

	public ArrayList<Category> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<Category> categories) {
		this.categories = categories;
	}

	public ArrayList<Loot> getLoot() {
		return loot;
	}

	public void setLoot(ArrayList<Loot> loot) {
		this.loot = loot;
	}
}