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
	public TextField connectivity, hallDensity;
	public ChoiceBox<String> layoutType;
	public CheckBox hasDeadEnds;
	
	//Terrain Settings tab
	public TextField waterDensity;
	public ChoiceBox<String> weather, visibility;
	public CheckBox hasPonds, hasExtraTiles;
	
	//Items, Traps & Enemies tab
	public TextField itemDensity, trapDensity, maxCoinAmnt, pokemonDensity;
	
	//Chances tab
	public Slider shopChance, houseChance, mazeChance, stickyChance;
	
	public void loadLayout() {
		try {
			//System.out.println(Integer.toHexString(RomManipulator.getFilePointer()));
			layoutType.getSelectionModel().select(RomManipulator.readUnsignedShort());
			tileset.getSelectionModel().select(RomManipulator.readUnsignedByte());
			music.getSelectionModel().select(RomManipulator.readUnsignedByte());
			weather.getItems().addAll("Clear", "Sunny", "Sandstorm", "Cloudy", "Rainy", "Hail", "Fog", "Snow", "Random");
			weather.getSelectionModel().select(RomManipulator.readUnsignedByte());
			connectivity.setText(RomManipulator.readUnsignedByte()+"");
			pokemonDensity.setText(RomManipulator.readUnsignedByte()+"");
			RomManipulator.skip(4);
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
			hallDensity.setText(RomManipulator.readUnsignedShort()+"");
			waterDensity.setText(RomManipulator.readUnsignedByte()+"");
			visibility.getItems().addAll("Normal", "1 tile", "2 tiles");
			visibility.getSelectionModel().select(RomManipulator.readUnsignedByte());
			maxCoinAmnt.setText(RomManipulator.readUnsignedByte()*5+"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
