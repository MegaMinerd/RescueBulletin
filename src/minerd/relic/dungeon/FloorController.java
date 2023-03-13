package minerd.relic.dungeon;

import java.io.IOException;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import minerd.relic.RomManipulator;

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
	
	public void loadLayout() {
		try {
			//System.out.println(Integer.toHexString(RomManipulator.getFilePointer()));
			layoutType.getSelectionModel().select(RomManipulator.readUnsignedByte());
			roomDensity.setText(RomManipulator.readUnsignedByte()+"");
			tileset.getSelectionModel().select(RomManipulator.readUnsignedByte());
			music.getSelectionModel().select(RomManipulator.readUnsignedByte());
			weather.getItems().addAll("Clear", "Sunny", "Sandstorm", "Cloudy", "Rainy", "Hail", "Fog", "Snow", "Random");
			weather.getSelectionModel().select(RomManipulator.readUnsignedByte());
			connectivity.setText(RomManipulator.readUnsignedByte()+"");
			pokemonDensity.setText(RomManipulator.readUnsignedByte()+"");
			shopChance.setValue(RomManipulator.readUnsignedByte()/100.0);
			houseChance.setValue(RomManipulator.readUnsignedByte()/100.0);
			mazeChance.setValue(RomManipulator.readUnsignedByte()/100.0);
			stickyChance.setValue(RomManipulator.readUnsignedByte()/100.0);
			hasDeadEnds.setSelected(RomManipulator.readUnsignedByte()!=0);
			hasPonds.setSelected(RomManipulator.readUnsignedByte()!=0);
			hasExtraTiles.setSelected(RomManipulator.readUnsignedByte()!=0);
			RomManipulator.skip(1);
			itemDensity.setText(RomManipulator.readUnsignedByte()+"");
			trapDensity.setText(RomManipulator.readUnsignedByte()+"");
			floorNum.setText("Floor " + RomManipulator.readUnsignedByte());
			fixedRoom.getSelectionModel().select(RomManipulator.readUnsignedByte());
			hallDensity.setText(RomManipulator.readUnsignedByte()+"");
			terrainRooms.setText(RomManipulator.readUnsignedByte()+"");
			waterDensity.setText(RomManipulator.readUnsignedByte()+"");
			visibility.getItems().addAll("Normal", "1 tile", "2 tiles");
			visibility.getSelectionModel().select(RomManipulator.readUnsignedByte());
			maxCoinAmnt.setText(RomManipulator.readUnsignedByte()*5+"");
			buriedDensity.setText(RomManipulator.readUnsignedByte()+"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
