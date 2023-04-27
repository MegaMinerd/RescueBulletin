package minerd.relic.fxml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import minerd.relic.data.Text;
import minerd.relic.data.dungeon.Category;
import minerd.relic.data.dungeon.Encounter;
import minerd.relic.data.dungeon.Floor;
import minerd.relic.data.dungeon.Loot;
import minerd.relic.data.dungeon.LootList;
import minerd.relic.data.dungeon.Trap;

public class FloorController{
	public Label floorNum;
	public ChoiceBox<String> tileset, music, fixedRoom;
	
	//Structure tab
	public TextField roomDensity, connectivity, hallDensity;
	public ChoiceBox<String> layoutType;
	public CheckBox hasDeadEnds;
	
	//Terrain Settings tab
	public TextField terrainRooms, waterDensity;
	public ChoiceBox<String> weather, visibility;
	public CheckBox hasPonds, hasExtraTiles;
	
	//Items, Traps & Enemies tab
	public TextField itemDensity, trapDensity, maxCoinAmnt, pokemonDensity, buriedDensity;
	
	//Chances tab
	public Slider shopChance, houseChance, mazeChance, stickyChance;
	
	//Pokemon spawns tab
	public TableView<Encounter> encounterTable;
	public TableColumn<Encounter, Integer> encounterId, encounterLevel;
	public TableColumn<Encounter, String> encounterName, encounterFloorChance, encounterHouseChance;
	
	//Trap spawns tab
	public TableView<Trap> trapTable;
	public TableColumn<Trap, String> trapName, trapChance;
	
	//Item spawns tab
	public TableView<Category> floorCategoryTable, shopCategoryTable, houseCategoryTable, buriedCategoryTable;
	public TableView<Loot> floorItemTable, shopItemTable, houseItemTable, buriedItemTable;
	
	public TableColumn<Category, Integer> floorCategoryId, floorCategoryWeight, shopCategoryId, shopCategoryWeight;
	public TableColumn<Category, Integer> houseCategoryId, houseCategoryWeight, buriedCategoryId, buriedCategoryWeight;
	public TableColumn<Category, String> floorCategoryName, floorCategoryChance, shopCategoryName, shopCategoryChance;
	public TableColumn<Category, String> houseCategoryName, houseCategoryChance, buriedCategoryName, buriedCategoryChance;
	
	public TableColumn<Loot, Integer> floorItemId, floorItemCat, floorItemWeight;
	public TableColumn<Loot, String> floorItemName, floorItemChance;

	public void load(Floor floor) {
		loadLayout(floor);
		loadEncounters(floor); 
		loadTraps(floor); 
		loadLoot(floor.getFloorLoot(), floorCategoryId,  floorCategoryName,  floorCategoryWeight,  floorCategoryChance,  floorCategoryTable);
		loadLoot(floor.getShopLoot(), shopCategoryId,   shopCategoryName,   shopCategoryWeight,   shopCategoryChance,   shopCategoryTable);
		loadLoot(floor.getHouseLoot(), houseCategoryId,  houseCategoryName,  houseCategoryWeight,  houseCategoryChance,  houseCategoryTable);
		loadLoot(floor.getBuriedLoot(), buriedCategoryId, buriedCategoryName, buriedCategoryWeight, buriedCategoryChance, buriedCategoryTable);
	}

	public void loadLayout(Floor floor) {
		layoutType.getSelectionModel().select(floor.getLayoutType());
		roomDensity.setText(floor.getRoomDensity()+"");
		tileset.getSelectionModel().select(floor.getTileset());
		music.getItems().addAll(Text.getTextList("Dungeon Music"));
		music.getSelectionModel().select(floor.getMusic());
		//System.out.println(floor.getMusic());
		weather.getItems().addAll(Text.getTextList("Weather"));
		weather.getSelectionModel().select(floor.getWeather());
		connectivity.setText(floor.getConnectivity()+"");
		pokemonDensity.setText(floor.getPokemonDensity()+"");
		shopChance.setValue(floor.getShopChance()/100.0);
		houseChance.setValue(floor.getHouseChance()/100.0);
		mazeChance.setValue(floor.getMazeChance()/100.0);
		stickyChance.setValue(floor.getStickyChance()/100.0);
		hasDeadEnds.setSelected(floor.getHasDeadEnds());
		hasPonds.setSelected(floor.geHasPonds());
		hasExtraTiles.setSelected(floor.getHasExtraTiles());
		itemDensity.setText(floor.getItemDensity()+"");
		trapDensity.setText(floor.getTrapDensity()+"");
		floorNum.setText(floor.getName());
		fixedRoom.getSelectionModel().select(floor.getFixedRoom());
		hallDensity.setText(floor.getHallDensity()+"");
		terrainRooms.setText(floor.getTerrainRooms()+"");
		waterDensity.setText(floor.getWaterDensity()+"");
		visibility.getItems().addAll("Normal", "1 tile", "2 tiles");
		visibility.getSelectionModel().select(floor.getVisibility());
		maxCoinAmnt.setText(floor.getMaxCoinAmnt()*5+"");
		buriedDensity.setText(floor.getBuriedDensity()+"");
	}
	
	public void loadEncounters(Floor floor) {
		ObservableList<Encounter> encounterList  = FXCollections.observableArrayList();
		encounterList.addAll(floor.getEncounters().getEntries());
		
		encounterId.setCellValueFactory(new PropertyValueFactory<Encounter, Integer>("id"));
		encounterName.setCellValueFactory(new PropertyValueFactory<Encounter, String>("species"));
		encounterLevel.setCellValueFactory(new PropertyValueFactory<Encounter, Integer>("level"));
		encounterFloorChance.setCellValueFactory(new PropertyValueFactory<Encounter, String>("floorPercent"));
		encounterHouseChance.setCellValueFactory(new PropertyValueFactory<Encounter, String>("housePercent"));
		
		encounterTable.setItems(encounterList);
	}
	
	public void loadTraps(Floor floor) {
		ObservableList<Trap> trapList  = FXCollections.observableArrayList();
		trapList.addAll(floor.getTraps().getEntries());
		
		trapName.setCellValueFactory(new PropertyValueFactory<Trap, String>("name"));
		trapChance.setCellValueFactory(new PropertyValueFactory<Trap, String>("percent"));
		
		trapTable.setItems(trapList);
	}
	
	public void loadLoot(LootList lootList, TableColumn<Category, Integer> categoryId, TableColumn<Category, String> categoryName, 
			TableColumn<Category, Integer> categoryWeight, TableColumn<Category, String> categoryChance, TableView<Category> categoryTable) {
		ObservableList<Category> observeList  = FXCollections.observableArrayList();
		observeList.addAll(lootList.getCategories());
		
		categoryId.setCellValueFactory(new PropertyValueFactory<Category, Integer>("id"));
		categoryName.setCellValueFactory(new PropertyValueFactory<Category, String>("name"));
		categoryWeight.setCellValueFactory(new PropertyValueFactory<Category, Integer>("weight"));
		categoryChance.setCellValueFactory(new PropertyValueFactory<Category, String>("chance"));
		
		categoryTable.setItems(observeList);
	}
}
