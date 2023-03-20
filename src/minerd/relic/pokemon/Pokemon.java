package minerd.relic.pokemon;

import java.io.IOException;

import minerd.relic.InvalidPointerException;
import minerd.relic.RomManipulator;
import minerd.relic.graphics.Chunk;

public class Pokemon {
	String name, category;
	int dexID, entityID, alphaID, parentID, palette, area;
	
	//General tab
	int bodySize, shadow, exp, recruit, movement, type1, type2, ability1, ability2;
	//Base stats tab
	int baseHp, baseAtk, baseSpa, baseDef, baseSpd, weight, size, speed;
	//Evolution tab
	int evolveFrom, evolveParam, evolveType, evolveAddition;
	//Other tab
	int regen, sleepiness, unk30, unk31, unk32;
	boolean canWalk, toolbox;
	//Sprites tab
	Chunk[] sprites;
	
	public Pokemon() {
		try {
			name = RomManipulator.readStringAndReturn(RomManipulator.parsePointer());
			category = RomManipulator.readStringAndReturn(RomManipulator.parsePointer());
			palette = RomManipulator.readUnsignedByte();
			bodySize = RomManipulator.readByte();
			RomManipulator.skip(2);
			speed = RomManipulator.readInt();
			//Face bitfield. Not used until image support is added.
			RomManipulator.skip(2);//temp
			RomManipulator.skip(1);
			type1 = RomManipulator.readUnsignedByte();
			type2 = RomManipulator.readUnsignedByte();
			movement = RomManipulator.readUnsignedByte();
			area = RomManipulator.readUnsignedByte();
			ability1 = RomManipulator.readUnsignedByte();
			ability2 = RomManipulator.readUnsignedByte();
			shadow = RomManipulator.readUnsignedByte();
			RomManipulator.skip(1);
			regen = RomManipulator.readUnsignedByte();
			canWalk = RomManipulator.readUnsignedByte()!=0;
			sleepiness = RomManipulator.readUnsignedByte();
			baseHp = RomManipulator.readShort();
			exp = RomManipulator.readInt();
			baseAtk = RomManipulator.readShort();
			baseSpa = RomManipulator.readShort();
			baseDef = RomManipulator.readShort();
			baseSpd = RomManipulator.readShort();
			weight = RomManipulator.readShort();
			size = RomManipulator.readShort();
			unk30 = RomManipulator.readUnsignedByte();
			unk31 = RomManipulator.readUnsignedByte();
			unk32 = RomManipulator.readUnsignedByte();
			toolbox = RomManipulator.readUnsignedByte()!=0;
			evolveFrom = RomManipulator.readShort();
			evolveType = RomManipulator.readShort();
			evolveParam = RomManipulator.readShort();
			evolveAddition = RomManipulator.readShort();
			dexID = RomManipulator.readShort();
			entityID = RomManipulator.readShort();
			recruit = RomManipulator.readShort();
			alphaID = RomManipulator.readShort();
			parentID = RomManipulator.readShort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setSprites(Chunk[] sprites) {
		this.sprites = sprites;
	}
}
