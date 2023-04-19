package minerd.relic.fxml;

import java.io.IOException;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import minerd.relic.data.Floor;
import minerd.relic.data.Text;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

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

	public void load(Floor floor) {
		loadLayout(floor);
	}
	
	public void loadLayout(Floor floor) {
		layoutType.getSelectionModel().select(floor.getLayoutType());
		roomDensity.setText(floor.getRoomDensity()+"");
		tileset.getSelectionModel().select(floor.getTileset());
		music.getSelectionModel().select(floor.getMusic());
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
}
