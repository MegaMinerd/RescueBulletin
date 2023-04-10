package minerd.relic.fxml;

import java.io.IOException;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import minerd.relic.data.Pokemon;
import minerd.relic.data.Text;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

public class PokemonController{
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
	//public TableView levelupTable;
	//public TableColumn levelCol, moveCol;
	
	public void load(Pokemon pokemon) {
		pokemonNameLabel.setText(pokemon.getName());
		pokemonNameField.setText(pokemon.getName());
		category.setText(pokemon.getCategory());
		bodySize.setText(pokemon.getBodySize()+"");
		speed.setText(pokemon.getSpeed()+"");
		type1.getItems().addAll(Text.getTextList("Types"));
		type1.getSelectionModel().select(pokemon.getType1());
		type2.getItems().addAll(Text.getTextList("Types"));
		type2.getSelectionModel().select(pokemon.getType2());
		movement.getItems().addAll("Normal", "Magma, Water", "Magma, Water, Sky", "Magma, Water, Sky, Wall", "Magma", "Water");
		movement.getSelectionModel().select(pokemon.getMovement());
		area.getItems().addAll(Text.getTextList("Friend Areas"));
		area.getSelectionModel().select(pokemon.getArea());
		ability1.getItems().addAll(Text.getTextList("Abilities"));
		ability1.getSelectionModel().select(pokemon.getAbility1());
		ability2.getItems().addAll(Text.getTextList("Abilities"));
		ability2.getSelectionModel().select(pokemon.getAbility2());
		shadow.setText(pokemon.getShadow()+"");
		regen.setText(pokemon.getRegen()+"");
		canWalk.setSelected(pokemon.getCanWalk());
		sleepiness.setText(pokemon.getSleepiness()+"");
		baseHp.setText(pokemon.getBaseHp()+"");
		exp.setText(pokemon.getExp()+"");
		baseAtk.setText(pokemon.getBaseAtk()+"");
		baseSpa.setText(pokemon.getBaseSpa()+"");
		baseDef.setText(pokemon.getBaseDef()+"");
		baseSpd.setText(pokemon.getBaseSpd()+"");
		weight.setText(pokemon.getWeight()+"");
		size.setText(pokemon.getSize()+"");
		unk30.setText(pokemon.getUnk30()+"");
		unk31.setText(pokemon.getUnk31()+"");
		unk32.setText(pokemon.getUnk32()+"");
		toolbox.setSelected(pokemon.getToolbox());
		evolveFrom.setText(pokemon.getPreId()+"");
		//evolveFromName.setText(Text.pokemon.get(pokemon.getPreId()));
		evolveType.getItems().addAll("Unevolved", "Level", "IQ", "Item");
		evolveType.getSelectionModel().select(pokemon.getEvolveType());
		evolveParam.setText(pokemon.getEvolveParam()+"");
		evolveAddition.getItems().addAll("Unevolved", "Level", "IQ", "Item", "Link Cable", "Attack > Defense", "Attack < Defense", "Attack + Defense", 
										 "Sun Ribbon", "Lunar Ribbon", "Beauty Scarf", "50% Option 1", "50% Option 2");
		evolveAddition.getSelectionModel().select(pokemon.getEvolveAddition());
		dexID.setText(pokemon.getDexID()+"");
		entityID.setText(pokemon.getEntityID()+"");
		recruit.setText(pokemon.getRecruit()+"");
		alphaID.setText(pokemon.getAlphaID()+"");
		parentID.setText(pokemon.getParentID()+"");
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
