package minerd.relic.data;

import java.io.IOException;
import java.util.HashMap;

import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

public class Cache {
    private HashMap<String, GameData[]> dataCache;
    private static Cache instance = new Cache();
    
    private Cache(){
        dataCache = new HashMap<String, GameData[]>();
        System.out.println("Creating cache");
    }
    
    private HashMap<String, GameData[]> getCache(){
        return dataCache;
    }
    
    public static void alloc(String typeName, int size) {
    	System.out.println("Allocating " + typeName + ": " + size);
    	GameData[] list = new GameData[size];
        instance.dataCache.put(typeName, list);
    }
    
    public static void add(String typeName, int index, GameData data){
    	System.out.println("Updating " + typeName + ": " + index);
    	GameData[] list = instance.getCache().get(typeName);
        list[index] = data;
    }
    
    public static GameData get(String typeName, int index){
    	System.out.println("Accessing " + typeName + ": " + index);
        return instance.getCache().get(typeName)[index];
    }
    
    public static void dump(){
        instance = new Cache();
    }
    
    public static void dump(String typeName){
        instance.getCache().remove(typeName);
    }
    
    public static void dump(String typeName, int index){
        instance.getCache().get(typeName)[index] = null;
    }

	public static void saveAll() throws IOException {
		RomFile rom = Rom.getAll();
		for(String type : instance.getCache().keySet()) {
			for(GameData data : instance.getCache().get(type)) {
				if(data!=null) {
					data.save(rom);
					System.out.println(data.getName());
				}
			}
		}
		Rom.saveAll(rom);
	}
    
    //note to self: opening scene starts at 26dfe8
}