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
import minerd.relic.InvalidPointerException;
import minerd.relic.Lists;
import minerd.relic.RomManipulator;

public class PokemonController implements Initializable {
	public Label pokemonNameLabel;
	public TextField dexID, entityID, alphaID, parentID;
	
	//Name tab
	public TextField pokemonNameField, category;
	//General tab
	public TextField bodySize, shadow, exp, recruit;
	public ChoiceBox<String> movement, type1, type2;
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
			//int dataPointer = RomManipulator.getFilePointer();
			String name = RomManipulator.readStringAndReturn(RomManipulator.parsePointer());
			pokemonNameLabel.setText(name);
			pokemonNameField.setText(name);
			category.setText(RomManipulator.readStringAndReturn(RomManipulator.parsePointer()));
			//Palette ID. Not used until image support is added.
			RomManipulator.skip(1);
			bodySize.setText(RomManipulator.readByte()+"");
			RomManipulator.skip(2);
			speed.setText(RomManipulator.readInt()+"");
			//Face bitfield. Not used until image support is added.
			RomManipulator.skip(2);
			RomManipulator.skip(1);
			type1.getItems().addAll(Lists.types);
			type1.getSelectionModel().select(RomManipulator.readUnsignedByte());
			type2.getItems().addAll(Lists.types);
			type2.getSelectionModel().select(RomManipulator.readUnsignedByte());
			movement.getItems().addAll("Normal", "Magma, Water", "Magma, Water, Sky", "Magma, Water, Sky, Wall", "Magma", "Water");
			movement.getSelectionModel().select(RomManipulator.readUnsignedByte());
			// TODO enumerate the options for below
			//area.setText(RomManipulator.readUnsignedByte();
			//ability1.setText(RomManipulator.readUnsignedByte();
			//ability2.setText(RomManipulator.readUnsignedByte();
			RomManipulator.skip(3);//temp
			shadow.setText(RomManipulator.readUnsignedByte()+"");
			RomManipulator.skip(1);
			regen.setText(RomManipulator.readUnsignedByte()+"");
			canWalk.setSelected(RomManipulator.readUnsignedByte()!=0);
			sleepiness.setText(RomManipulator.readUnsignedByte()+"");
			baseHp.setText(RomManipulator.readShort()+"");
			exp.setText(RomManipulator.readInt()+"");
			baseAtk.setText(RomManipulator.readShort()+"");
			baseSpa.setText(RomManipulator.readShort()+"");
			baseDef.setText(RomManipulator.readShort()+"");
			baseSpd.setText(RomManipulator.readShort()+"");
			weight.setText(RomManipulator.readShort()+"");
			size.setText(RomManipulator.readShort()+"");
			unk30.setText(RomManipulator.readUnsignedByte()+"");
			unk31.setText(RomManipulator.readUnsignedByte()+"");
			unk32.setText(RomManipulator.readUnsignedByte()+"");
			toolbox.setSelected(RomManipulator.readUnsignedByte()!=0);
			short preId = RomManipulator.readShort();
			evolveFrom.setText(preId+"");
			evolveFromName.setText(Lists.pokemon.get(preId));
			evolveType.getItems().addAll("Unevolved", "Level", "IQ", "Item");
			evolveType.getSelectionModel().select(RomManipulator.readShort());
			evolveParam.setText(RomManipulator.readShort()+"");
			evolveAddition.getItems().addAll("Unevolved", "Level", "IQ", "Item", "Link Cable", "Attack > Defense", "Attack < Defense", "Attack + Defense", 
					"Sun Ribbon", "Lunar Ribbon", "Beauty Scarf", "50% Option 1", "50% Option 2");
			evolveAddition.getSelectionModel().select(RomManipulator.readShort());
			dexID.setText(RomManipulator.readShort()+"");
			entityID.setText(RomManipulator.readShort()+"");
			recruit.setText(RomManipulator.readShort()+"");
			alphaID.setText(RomManipulator.readShort()+"");
			parentID.setText(RomManipulator.readShort()+"");
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	int readMoveId() throws IOException{
		int[] highByte = RomManipulator.readMask(1, 7, 1);
		int moveId = highByte[1]==1 ?
			(highByte[0]<<7) | RomManipulator.readUnsignedByte()
			: highByte[0];
		return moveId;
	}
}
