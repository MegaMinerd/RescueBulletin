package minerd.relic.pokemon;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import minerd.relic.data.Text;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

public class PokemonController implements Initializable {
	public Label pokemonNameLabel;
	public TextField dexID, entityID, alphaID, parentID;
	
	//Name tab
	public TextField pokemonNameField, category;
	//General tab
	public TextField bodySize, shadow, exp, recruit;
	public ChoiceBox<String> movement, type1, type2, ability1, ability2, area;
	//Base stats tab
	public TextField baseHp, baseAtk, baseSpa, baseDef, baseSpd, weight, size, speed;
	//Evolution tab
	public Label evolveFromName, itemName;
	public TextField evolveFrom, evolveParam;
	public ChoiceBox<String> evolveType, evolveAddition;
	//Other tab
	public TextField regen, sleepiness, unk30, unk31, unk32;
	public CheckBox canWalk, toolbox;
	
	//Level up moves tab
	public TableView levelupTable;
	public TableColumn levelCol, moveCol;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			RomFile rom = Rom.getAll();
			//int dataPointer = rom.getFilePointer();
			String name = rom.readStringAndReturn(rom.parsePointer());
			pokemonNameLabel.setText(name);
			pokemonNameField.setText(name);
			category.setText(rom.readStringAndReturn(rom.parsePointer()));
			//Palette ID. Not used until image support is added.
			rom.skip(1);
			bodySize.setText(rom.readByte()+"");
			rom.skip(2);
			speed.setText(rom.readInt()+"");
			//Face bitfield. Not used until image support is added.
			rom.skip(2);
			rom.skip(1);
			type1.getItems().addAll(Text.getTextList("Types"));
			type1.getSelectionModel().select(rom.readUnsignedByte());
			type2.getItems().addAll(Text.getTextList("Types"));
			type2.getSelectionModel().select(rom.readUnsignedByte());
			movement.getItems().addAll("Normal", "Magma, Water", "Magma, Water, Sky", "Magma, Water, Sky, Wall", "Magma", "Water");
			movement.getSelectionModel().select(rom.readUnsignedByte());
			area.getItems().addAll(Text.getTextList("Friend Areas"));
			area.getSelectionModel().select(rom.readUnsignedByte());
			ability1.getItems().addAll(Text.getTextList("Abilities"));
			ability1.getSelectionModel().select(rom.readUnsignedByte());
			ability2.getItems().addAll(Text.getTextList("Abilities"));
			ability2.getSelectionModel().select(rom.readUnsignedByte());
			shadow.setText(rom.readUnsignedByte()+"");
			rom.skip(1);
			regen.setText(rom.readUnsignedByte()+"");
			canWalk.setSelected(rom.readUnsignedByte()!=0);
			sleepiness.setText(rom.readUnsignedByte()+"");
			baseHp.setText(rom.readShort()+"");
			exp.setText(rom.readInt()+"");
			baseAtk.setText(rom.readShort()+"");
			baseSpa.setText(rom.readShort()+"");
			baseDef.setText(rom.readShort()+"");
			baseSpd.setText(rom.readShort()+"");
			weight.setText(rom.readShort()+"");
			size.setText(rom.readShort()+"");
			unk30.setText(rom.readUnsignedByte()+"");
			unk31.setText(rom.readUnsignedByte()+"");
			unk32.setText(rom.readUnsignedByte()+"");
			toolbox.setSelected(rom.readUnsignedByte()!=0);
			short preId = rom.readShort();
			evolveFrom.setText(preId+"");
			evolveFromName.setText(Text.pokemon.get(preId));
			evolveType.getItems().addAll("Unevolved", "Level", "IQ", "Item");
			evolveType.getSelectionModel().select(rom.readShort());
			evolveParam.setText(rom.readShort()+"");
			evolveAddition.getItems().addAll("Unevolved", "Level", "IQ", "Item", "Link Cable", "Attack > Defense", "Attack < Defense", "Attack + Defense", 
					"Sun Ribbon", "Lunar Ribbon", "Beauty Scarf", "50% Option 1", "50% Option 2");
			evolveAddition.getSelectionModel().select(rom.readShort());
			dexID.setText(rom.readShort()+"");
			entityID.setText(rom.readShort()+"");
			recruit.setText(rom.readShort()+"");
			alphaID.setText(rom.readShort()+"");
			parentID.setText(rom.readShort()+"");
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	int readMoveId() throws IOException{
		RomFile rom = Rom.getAll();
		int[] highByte = rom.readMask(1, 7, 1);
		int moveId = highByte[1]==1 ?
			(highByte[0]<<7) | rom.readUnsignedByte()
			: highByte[0];
		return moveId;
	}
}
