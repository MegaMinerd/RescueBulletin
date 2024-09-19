package minerd.relic.data.dungeon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javafx.scene.layout.Region;
import minerd.relic.data.Cache;
import minerd.relic.data.GameData;
import minerd.relic.data.Item;
import minerd.relic.file.BufferedDataHandler;

public class LootList extends GameData {
	private ArrayList<Category> categories;
	private ArrayList<Loot> loots;

	public LootList(BufferedDataHandler rom) throws IOException {
		HashMap<Integer, Integer> iweightEntries = new HashMap<Integer, Integer>();
		int itemId = 0;
		int maxId = 252;
		while(itemId<maxId){
			int data = rom.readUnsignedShort();
			if(data<30000){
				iweightEntries.put(itemId, data);
				itemId++;
			} else
				itemId += data - 30000;
		}

		// Todo: use actual size
		int catNum = 12;
		int lastValue = 0;
		categories = new ArrayList<Category>();
		loots = new ArrayList<Loot>();
		int[] catWeights = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		Object[] itemWeights = iweightEntries.keySet().toArray();
		Arrays.sort(itemWeights);
		for(int i=0; i<itemWeights.length; i++){
			int key = (int)itemWeights[i];
			int value = iweightEntries.get(key);
			if(key<catNum){
				Category cat = new Category(key, value - lastValue);
				categories.add(cat);
				lastValue = value;
			} else{
				Item item = (Item) Cache.get("Item", key - catNum);
				if(item==null){
					item = new Item(key - catNum);
					Cache.add("Item", key - catNum, item);
				}
				int lootCat = item.getItemType();
				Loot loot = new Loot(key - catNum, value - catWeights[lootCat]);
				loots.add(loot);
				catWeights[lootCat] = value;
			}
		}
	}

	public Region load() throws IOException {
		return null;
	}

	@Override
	public void save() {
	}

	public String getName() {
		return "Loot List";
	}

	public ArrayList<Category> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<Category> categories) {
		this.categories = categories;
	}

	public ArrayList<Loot> getLoot() {
		return loots;
	}

	public void setLoot(ArrayList<Loot> loot) {
		this.loots = loot;
	}
}