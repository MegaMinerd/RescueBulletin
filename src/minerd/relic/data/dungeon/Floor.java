package minerd.relic.data.dungeon;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import minerd.relic.data.Cache;
import minerd.relic.data.GameData;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Rom;
import minerd.relic.file.SiroFile;
import minerd.relic.fxml.FloorController;
import minerd.relic.util.RrtOffsetList;

public class Floor extends GameData {
	private int layoutId, pokemonTableId, trapListId, itemTableId, shopTableId, houseTableId, buriedTableId;
	private int absIndex, relIndex, dungeonIndex, layoutType, roomDensity, tileset, music, weather, connectivity,
			pokemonDensity, shopChance, houseChance, mazeChance, stickyChance, itemDensity, trapDensity, floorNum,
			fixedRoom, hallDensity, terrainRooms, waterDensity, visibility, maxCoinAmnt, buriedDensity;
	private boolean hasDeadEnds, hasPonds, hasExtraTiles;
	private EncounterList encounters;
	private TrapList traps;
	private LootList floorLoot, shopLoot, houseLoot, buriedLoot;

	public Floor(int absIndex, int relIndex, int dunIndex) {
		this.absIndex = absIndex;
		this.relIndex = relIndex;
		this.dungeonIndex = dunIndex;
		try{
			SiroFile mapparam = (SiroFile) Rom.getInstance().getDungeonSbin().getSubfile("mapparam");
			BufferedDataHandler dunMain = mapparam.getSegment("main/" + dunIndex).getData();
			dunMain.seek(16*relIndex+16);
			layoutId = dunMain.readUnsignedShort();
			pokemonTableId = dunMain.readUnsignedShort();
			trapListId = dunMain.readUnsignedShort();
			itemTableId = dunMain.readUnsignedShort();
			shopTableId = dunMain.readUnsignedShort();
			houseTableId = dunMain.readUnsignedShort();
			buriedTableId = dunMain.readUnsignedShort();

			loadLayout(mapparam.getSegment("layout/" + layoutId).getData());

			encounters = new EncounterList(mapparam.getSegment("spawn/" + pokemonTableId).getData());
			traps = new TrapList(mapparam.getSegment("trap/" + trapListId).getData());
			floorLoot = new LootList(mapparam.getSegment("loot/" + itemTableId).getData());
			shopLoot = new LootList(mapparam.getSegment("loot/" + shopTableId).getData());
			houseLoot = new LootList(mapparam.getSegment("loot/" + houseTableId).getData());
			buriedLoot = new LootList(mapparam.getSegment("loot/" + buriedTableId).getData());
			
			Cache.add("Floor", absIndex, this);
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public Region load() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/floor.fxml"));
		SplitPane dataPane = loader.load();
		FloorController controller = loader.getController();

		controller.load(this);
		return dataPane;
	}

	private void loadLayout(BufferedDataHandler rom) throws IOException {
		layoutType = rom.readUnsignedByte();
		roomDensity = rom.readUnsignedByte();
		tileset = rom.readUnsignedByte();
		music = rom.readUnsignedByte();
		weather = rom.readUnsignedByte();
		connectivity = rom.readUnsignedByte();
		pokemonDensity = rom.readUnsignedByte();
		shopChance = rom.readUnsignedByte();
		houseChance = rom.readUnsignedByte();
		mazeChance = rom.readUnsignedByte();
		stickyChance = rom.readUnsignedByte();
		hasDeadEnds = rom.readBoolean();
		hasPonds = rom.readBoolean();
		hasExtraTiles = rom.readBoolean();
		rom.skip(1);
		itemDensity = rom.readUnsignedByte();
		trapDensity = rom.readUnsignedByte();
		floorNum = rom.readUnsignedByte();
		fixedRoom = rom.readUnsignedByte();
		hallDensity = rom.readUnsignedByte();
		terrainRooms = rom.readUnsignedByte();
		waterDensity = rom.readUnsignedByte();
		visibility = rom.readUnsignedByte();
		maxCoinAmnt = rom.readUnsignedByte();
		buriedDensity = rom.readUnsignedByte();
	}

	@Override
	public void save() {
		try{
			SiroFile mapparam = (SiroFile) Rom.getInstance().getDungeonSbin().getSubfile("mapparam");
			BufferedDataHandler dunMain = mapparam.getSegment("main/" + dungeonIndex).getData();
			dunMain.seek(16*relIndex+16);
			dunMain.writeUnsignedShort(layoutId);
			dunMain.writeUnsignedShort(pokemonTableId);
			dunMain.writeUnsignedShort(trapListId);
			dunMain.writeUnsignedShort(itemTableId);
			dunMain.writeUnsignedShort(shopTableId);
			dunMain.writeUnsignedShort(houseTableId);
			dunMain.writeUnsignedShort(buriedTableId);

			saveLayout(mapparam.getSegment("layout/" + layoutId).getData());

			encounters.save();
			traps.save();
			floorLoot.save();
			shopLoot.save();
			houseLoot.save();
			buriedLoot.save();
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	private void saveLayout(BufferedDataHandler layout) throws IOException {
		layout.writeUnsignedByte(layoutType);
		layout.writeUnsignedByte(roomDensity);
		layout.writeUnsignedByte(tileset);
		layout.writeUnsignedByte(music);
		layout.writeUnsignedByte(weather);
		layout.writeUnsignedByte(connectivity);
		layout.writeUnsignedByte(pokemonDensity);
		layout.writeUnsignedByte(shopChance);
		layout.writeUnsignedByte(houseChance);
		layout.writeUnsignedByte(mazeChance);
		layout.writeUnsignedByte(stickyChance);
		layout.writeBoolean(hasDeadEnds);
		layout.writeBoolean(hasPonds);
		layout.writeBoolean(hasExtraTiles);
		layout.skip(1);
		layout.writeUnsignedByte(itemDensity);
		layout.writeUnsignedByte(trapDensity);
		layout.writeUnsignedByte(floorNum);
		layout.writeUnsignedByte(fixedRoom);
		layout.writeUnsignedByte(hallDensity);
		layout.writeUnsignedByte(terrainRooms);
		layout.writeUnsignedByte(waterDensity);
		layout.writeUnsignedByte(visibility);
		layout.writeUnsignedByte(maxCoinAmnt);
		layout.writeUnsignedByte(buriedDensity);
	}

	public int getDungeonIndex() {
		return dungeonIndex;
	}

	public void setDungeonIndex(int dungeonIndex) {
		this.dungeonIndex = dungeonIndex;
	}

	public int getFloorIndex() {
		return relIndex;
	}

	public void setFloorIndex(int floorIndex) {
		this.relIndex = floorIndex;
	}

	@Override
	public String getName() {
		return "Floor " + floorNum;
	}

	public int getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(int layoutType) {
		this.layoutType = layoutType;
	}

	public int getRoomDensity() {
		return roomDensity;
	}

	public void setRoomDensity(int roomDensity) {
		this.roomDensity = roomDensity;
	}

	public int getTileset() {
		return tileset;
	}

	public void setTileset(int tileset) {
		this.tileset = tileset;
	}

	public int getMusic() {
		return music;
	}

	public void setMusic(int music) {
		this.music = music;
	}

	public int getWeather() {
		return weather;
	}

	public void setWeather(int weather) {
		this.weather = weather;
	}

	public int getConnectivity() {
		return connectivity;
	}

	public void setConnectivity(int connectivity) {
		this.connectivity = connectivity;
	}

	public int getPokemonDensity() {
		return pokemonDensity;
	}

	public void setPokemonDensity(int pokemonDensity) {
		this.pokemonDensity = pokemonDensity;
	}

	public int getShopChance() {
		return shopChance;
	}

	public void setShopChance(int shopChance) {
		this.shopChance = shopChance;
	}

	public int getHouseChance() {
		return houseChance;
	}

	public void setHouseChance(int houseChance) {
		this.houseChance = houseChance;
	}

	public int getMazeChance() {
		return mazeChance;
	}

	public void setMazeChance(int mazeChance) {
		this.mazeChance = mazeChance;
	}

	public int getStickyChance() {
		return stickyChance;
	}

	public void setStickyChance(int stickyChance) {
		this.stickyChance = stickyChance;
	}

	public int getItemDensity() {
		return itemDensity;
	}

	public void setItemDensity(int itemDensity) {
		this.itemDensity = itemDensity;
	}

	public int getTrapDensity() {
		return trapDensity;
	}

	public void setTrapDensity(int trapDensity) {
		this.trapDensity = trapDensity;
	}

	public int getFixedRoom() {
		return fixedRoom;
	}

	public void setFixedRoom(int fixedRoom) {
		this.fixedRoom = fixedRoom;
	}

	public int getHallDensity() {
		return hallDensity;
	}

	public void setHallDensity(int hallDensity) {
		this.hallDensity = hallDensity;
	}

	public int getTerrainRooms() {
		return terrainRooms;
	}

	public void setTerrainRooms(int terrainRooms) {
		this.terrainRooms = terrainRooms;
	}

	public int getWaterDensity() {
		return waterDensity;
	}

	public void setWaterDensity(int waterDensity) {
		this.waterDensity = waterDensity;
	}

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public int getMaxCoinAmnt() {
		return maxCoinAmnt;
	}

	public void setMaxCoinAmnt(int maxCoinAmnt) {
		this.maxCoinAmnt = maxCoinAmnt;
	}

	public int getBuriedDensity() {
		return buriedDensity;
	}

	public void setBuriedDensity(int buriedDensity) {
		this.buriedDensity = buriedDensity;
	}

	public boolean getHasDeadEnds() {
		return hasDeadEnds;
	}

	public void setHasDeadEnds(boolean hasDeadEnds) {
		this.hasDeadEnds = hasDeadEnds;
	}

	public boolean geHasPonds() {
		return hasPonds;
	}

	public void setHasPonds(boolean hasPonds) {
		this.hasPonds = hasPonds;
	}

	public boolean getHasExtraTiles() {
		return hasExtraTiles;
	}

	public void setHasExtraTiles(boolean hasExtraTiles) {
		this.hasExtraTiles = hasExtraTiles;
	}

	public EncounterList getEncounters() {
		return encounters;
	}

	public void setEncounters(EncounterList encounters) {
		this.encounters = encounters;
	}

	public TrapList getTraps() {
		return traps;
	}

	public void setTraps(TrapList traps) {
		this.traps = traps;
	}

	public LootList getFloorLoot() {
		return floorLoot;
	}

	public void setFloorLoot(LootList floorLoot) {
		this.floorLoot = floorLoot;
	}
	
	public int getFloorTableId() {
		return itemTableId;
	}
	
	public void setFloorTableId(int id) {
		itemTableId = id;
	}

	public LootList getShopLoot() {
		return shopLoot;
	}

	public void setShopLoot(LootList shopLoot) {
		this.shopLoot = shopLoot;
	}

	public int getShopTableId() {
		return shopTableId;
	}
	
	public void setShopTableId(int id) {
		shopTableId = id;
	}

	public LootList getHouseLoot() {
		return houseLoot;
	}

	public void setHouseLoot(LootList houseLoot) {
		this.houseLoot = houseLoot;
	}

	public int getHouseTableId() {
		return houseTableId;
	}
	
	public void setHouseTableId(int id) {
		houseTableId = id;
	}

	public LootList getBuriedLoot() {
		return buriedLoot;
	}

	public void setBuriedLoot(LootList buriedLoot) {
		this.buriedLoot = buriedLoot;
	}

	public int getBuriedTableId() {
		return buriedTableId;
	}
	
	public void setBuriedTableId(int id) {
		buriedTableId = id;
	}

	public int getEncounterTableId() {
		return pokemonTableId;
	}
	
	public int getTrapTableId() {
		return trapListId;
	}
}