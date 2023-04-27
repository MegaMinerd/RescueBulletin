package minerd.relic.data.dungeon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.layout.Region;
import minerd.relic.data.GameData;
import minerd.relic.file.RomFile;

public class LootList extends GameData{
    ArrayList<Category> categories;
    ArrayList<Loot> entries;
    
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
        
        //Todo: use actusl size
        int catNum = 12;
        int lastValue = 0;
        categories = new ArrayList<Category>();
        iweightEntries.forEach((key, value) -> {
            if(key < catNum){
                //catWeights [key] = value;
                //categories.set(key, value-lastValue);
            }else{
                // Todo
            }
        });
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
}