package minerd.relic.dungeon;

import java.io.IOException;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
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
	
	public void loadLayout() {
		try {
			RomFile rom = Rom.getAll();
			//System.out.println(Integer.toHexString(RomManipulator.getFilePointer()));
			layoutType.getSelectionModel().select(rom.readUnsignedByte());
			roomDensity.setText(rom.readUnsignedByte()+"");
			tileset.getSelectionModel().select(rom.readUnsignedByte());
			music.getSelectionModel().select(rom.readUnsignedByte());
			weather.getItems().addAll(Text.getTextList("Weather"));
			weather.getSelectionModel().select(rom.readUnsignedByte());
			connectivity.setText(rom.readUnsignedByte()+"");
			pokemonDensity.setText(rom.readUnsignedByte()+"");
			shopChance.setValue(rom.readUnsignedByte()/100.0);
			houseChance.setValue(rom.readUnsignedByte()/100.0);
			mazeChance.setValue(rom.readUnsignedByte()/100.0);
			stickyChance.setValue(rom.readUnsignedByte()/100.0);
			hasDeadEnds.setSelected(rom.readUnsignedByte()!=0);
			hasPonds.setSelected(rom.readUnsignedByte()!=0);
			hasExtraTiles.setSelected(rom.readUnsignedByte()!=0);
			rom.skip(1);
			itemDensity.setText(rom.readUnsignedByte()+"");
			trapDensity.setText(rom.readUnsignedByte()+"");
			floorNum.setText("Floor " + rom.readUnsignedByte());
			fixedRoom.getSelectionModel().select(rom.readUnsignedByte());
			hallDensity.setText(rom.readUnsignedByte()+"");
			terrainRooms.setText(rom.readUnsignedByte()+"");
			waterDensity.setText(rom.readUnsignedByte()+"");
			visibility.getItems().addAll("Normal", "1 tile", "2 tiles");
			visibility.getSelectionModel().select(rom.readUnsignedByte());
			maxCoinAmnt.setText(rom.readUnsignedByte()*5+"");
			buriedDensity.setText(rom.readUnsignedByte()+"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
