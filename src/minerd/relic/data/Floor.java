package minerd.relic.data;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.fxml.FloorController;

public class Floor extends GameData {
	private int layoutType, roomDensity, tileset, music, weather, connectivity, pokemonDensity, shopChance, houseChance, mazeChance, stickyChance;
	private int itemDensity, trapDensity, floorNum, fixedRoom, hallDensity, terrainRooms, waterDensity, visibility, maxCoinAmnt, buriedDensity;
	private boolean hasDeadEnds, hasPonds, hasExtraTiles;
	
	public Floor(int index, int[] offsets) {
		try {
			RomFile rom = Rom.getAll();
			rom.seek(offsets[0]);
			System.out.println(Integer.toHexString(rom.getFilePointer()));
			int layoutId = rom.readUnsignedShort();
			int pokemonTableId = rom.readUnsignedShort();
			int trapListId = rom.readUnsignedShort();
			int itemTableId = rom.readUnsignedShort();
			int shopTableId = rom.readUnsignedShort();
			int houseTableId = rom.readUnsignedShort();
			int buriedTableId = rom.readUnsignedShort();
			
			rom.seek(offsets[1]+index*0x1C);
			loadLayout(rom, layoutId);
		} catch (IOException e) {
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
	
	private void loadLayout(RomFile rom, int index) throws IOException {
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
		hasDeadEnds = rom.readUnsignedByte()!=0;
		hasPonds = rom.readUnsignedByte()!=0;
		hasExtraTiles = rom.readUnsignedByte()!=0;
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
}
