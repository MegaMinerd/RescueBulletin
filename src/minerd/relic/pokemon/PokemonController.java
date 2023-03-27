package minerd.relic.pokemon;

import java.io.IOException;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import minerd.relic.Lists;
import minerd.relic.RomManipulator;

public class PokemonController{
	public Pokemon pokemon;
	
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
	//public TableView levelupTable;
	//public TableColumn levelCol, moveCol;
	
	//Sprites tab
	public ImageView sprite;
	public TextField spriteIdField;
	public int spriteId, spriteCount;
	
	public void load(Pokemon pokemonIn) {
		pokemon = pokemonIn;
		pokemonNameLabel.setText(pokemon.name);
		pokemonNameField.setText(pokemon.name);
		category.setText(pokemon.category);
		bodySize.setText(pokemon.bodySize+"");
		speed.setText(pokemon.speed+"");
		type1.getItems().addAll(Lists.types);
		type1.getSelectionModel().select(pokemon.type1);
		type2.getItems().addAll(Lists.types);
		type2.getSelectionModel().select(pokemon.type2);
		movement.getItems().addAll("Normal", "Magma, Water", "Magma, Water, Sky", "Magma, Water, Sky, Wall", "Magma", "Water");
		movement.getSelectionModel().select(pokemon.movement);
		shadow.setText(pokemon.shadow+"");
		regen.setText(pokemon.regen+"");
		canWalk.setSelected(pokemon.canWalk);
		sleepiness.setText(pokemon.sleepiness+"");
		baseHp.setText(pokemon.baseHp+"");
		exp.setText(pokemon.exp+"");
		baseAtk.setText(pokemon.baseAtk+"");
		baseSpa.setText(pokemon.baseSpa+"");
		baseDef.setText(pokemon.baseDef+"");
		baseSpd.setText(pokemon.baseSpd+"");
		weight.setText(pokemon.weight+"");
		size.setText(pokemon.size+"");
		unk30.setText(pokemon.unk30+"");
		unk31.setText(pokemon.unk31+"");
		unk32.setText(pokemon.unk32+"");
		toolbox.setSelected(pokemon.toolbox);
		evolveFrom.setText(pokemon.evolveFrom+"");
		evolveFromName.setText(Lists.pokemon.get(pokemon.evolveFrom));
		evolveType.getItems().addAll("Unevolved", "Level", "IQ", "Item");
		evolveType.getSelectionModel().select(pokemon.evolveType);
		evolveParam.setText(pokemon.evolveParam+"");
		evolveAddition.getItems().addAll("Unevolved", "Level", "IQ", "Item", "Link Cable", "Attack > Defense", "Attack < Defense", "Attack + Defense", 
				"Sun Ribbon", "Lunar Ribbon", "Beauty Scarf", "50% Option 1", "50% Option 2");
		evolveAddition.getSelectionModel().select(pokemon.evolveAddition);
		dexID.setText(pokemon.dexID+"");
		entityID.setText(pokemon.entityID+"");
		recruit.setText(pokemon.recruit+"");
		alphaID.setText(pokemon.alphaID+"");
		parentID.setText(pokemon.parentID+"");
		
		spriteId = 0;
		spriteCount = pokemon.sprite.getAnimation(0).getDirection(0).getFrameCount();
		spriteIdField.setText("0");
		updateSprite();
	}
	
	int readMoveId() throws IOException{
		int[] highByte = RomManipulator.readMask(1, 7, 1);
		int moveId = highByte[1]==1 ?
			(highByte[0]<<7) | RomManipulator.readUnsignedByte()
			: highByte[0];
		return moveId;
	}
	
	public void lastSprite() {
		spriteId = Math.max(spriteId-1, 0);
		spriteIdField.setText(spriteId + "");
		updateSprite();
	}
	
	public void nextSprite() {
		spriteId = Math.min(spriteId+1, spriteCount-1);
		spriteIdField.setText(spriteId + "");
		updateSprite();
	}
	
	public void goToSprite() {
		spriteId = Math.min(Math.max(Integer.parseInt(spriteIdField.getText()), 0), spriteCount-1);
		spriteIdField.setText(spriteId + "");
		updateSprite();	
	}
	
	public void updateSprite() {
		WritableImage image = pokemon.sprite.getAnimation(0).getDirection(0).getFrame(spriteId);
		sprite.setImage(image);
		sprite.setFitWidth(image.getWidth()*2);
		sprite.setFitHeight(image.getHeight()*2);
	}
}
