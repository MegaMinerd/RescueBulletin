package minerd.relic.data;

import java.io.IOException;
import java.nio.ByteBuffer;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.fxml.PokemonController;
import minerd.relic.util.CompressionHandler;
import minerd.relic.util.RrtOffsetList;

public class Pokemon extends GameData {
	private String name, category;
	private int offset, palette, bodySize, speed, type1, type2, movement, area, ability1, ability2;
	private int shadow, regen, sleepiness, baseHp, exp, baseAtk, baseSpa, baseDef, baseSpd, weight, size;
	private int unk30, unk31, unk32, preId, evolveType, evolveParam, evolveAddition;
	private int dexID, entityID, recruit, alphaID, parentID, faces;
	private boolean canWalk, toolbox;
	private Learnset learnset;
	private Levelmap lvmp;

	public Pokemon(int index) {
		try{
			BufferedDataHandler rom = Rom.getAll();
			rom.seek(RrtOffsetList.pokemonOffset);
			rom.skip(16 + index*0x48);
			offset = rom.getFilePointer();
			name = rom.readString(rom.parsePointer());
			category = rom.readString(rom.parsePointer());
			palette = rom.readUnsignedByte();
			bodySize = rom.readByte();
			rom.skip(2);
			speed = rom.readInt();
			faces = rom.readUnsignedShort();
			rom.skip(1);
			type1 = rom.readUnsignedByte();
			type2 = rom.readUnsignedByte();
			movement = rom.readUnsignedByte();
			area = rom.readUnsignedByte();
			ability1 = rom.readUnsignedByte();
			ability2 = rom.readUnsignedByte();
			shadow = rom.readUnsignedByte();
			rom.skip(1);
			regen = rom.readUnsignedByte();
			canWalk = rom.readUnsignedByte()!=0;
			sleepiness = rom.readUnsignedByte();
			baseHp = rom.readShort();
			exp = rom.readInt();
			baseAtk = rom.readShort();
			baseSpa = rom.readShort();
			baseDef = rom.readShort();
			baseSpd = rom.readShort();
			weight = rom.readShort();
			size = rom.readShort();
			unk30 = rom.readUnsignedByte();
			unk31 = rom.readUnsignedByte();
			unk32 = rom.readUnsignedByte();
			toolbox = rom.readUnsignedByte()!=0;
			preId = rom.readShort();
			evolveType = rom.readShort();
			evolveParam = rom.readShort();
			evolveAddition = rom.readShort();
			dexID = rom.readShort();
			entityID = rom.readShort();
			recruit = rom.readShort();
			alphaID = rom.readShort();
			parentID = rom.readShort();
			Cache.add("Pokemon", index, this);
			
			learnset = (Learnset)Cache.get("Learnset", index);
			if(learnset==null) {
				learnset = new Learnset(index);
				Cache.add("Learnset", index, learnset);
			}
			

			rom.seek(RrtOffsetList.levelmapOffset);
			rom.skip(index*0x8+0x4);
			rom.seek(rom.parsePointer());
			rom.skip(16);
			int start = rom.getFilePointer();
			rom.skip(5);
			byte temp[] = new byte[rom.readShort()];
			rom.getBuffer().get(start, temp);
			lvmp = new Levelmap(CompressionHandler.decompress(new BufferedDataHandler(ByteBuffer.wrap(temp)), false), this);
		} catch(IOException | InvalidPointerException e){
			e.printStackTrace();
		}
	}

	public Region load() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/pokemon.fxml"));
		SplitPane dataPane = loader.load();
		PokemonController controller = loader.getController();

		controller.load(this);
		return dataPane;
	}

	public void save(BufferedDataHandler rom) {
		try{
			rom.seek(offset);
			//rom.writeString(name, rom.parsePointer());
			//rom.writeString(category, rom.parsePointer());
			rom.skip(8);;
			rom.writeUnsignedByte(palette);
			rom.writeByte((byte) bodySize);
			rom.skip(2);
			rom.writeInt(speed);
			rom.writeUnsignedShort(faces);
			rom.skip(1);
			rom.writeUnsignedByte(type1);
			rom.writeUnsignedByte(type2);
			rom.writeUnsignedByte(movement);
			rom.writeUnsignedByte(area);
			rom.writeUnsignedByte(ability1);
			rom.writeUnsignedByte(ability2);
			rom.writeUnsignedByte(shadow);
			rom.skip(1);
			rom.writeUnsignedByte(regen);
			rom.writeBoolean(canWalk);
			rom.writeUnsignedByte(sleepiness);
			rom.writeShort((short) baseHp);
			rom.writeInt(exp);
			rom.writeShort((short) baseAtk);
			rom.writeShort((short) baseSpa);
			rom.writeShort((short) baseDef);
			rom.writeShort((short) baseSpd);
			rom.writeShort((short) weight);
			rom.writeShort((short) size);
			rom.writeUnsignedByte(unk30);
			rom.writeUnsignedByte(unk31);
			rom.writeUnsignedByte(unk32);
			rom.writeBoolean(toolbox);
			rom.writeShort((short) preId);
			rom.writeShort((short) evolveType);
			rom.writeShort((short) evolveParam);
			rom.writeShort((short) evolveAddition);
			rom.writeShort((short) dexID);
			rom.writeShort((short) entityID);
			rom.writeShort((short) recruit);
			rom.writeShort((short) alphaID);
			rom.writeShort((short) parentID);
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getBodySize() {
		return bodySize;
	}

	public void setBodySize(int bodySize) {
		this.bodySize = bodySize;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getType1() {
		return type1;
	}

	public void setType1(int type1) {
		this.type1 = type1;
	}

	public int getType2() {
		return type2;
	}

	public void setType2(int type2) {
		this.type2 = type2;
	}

	public int getMovement() {
		return movement;
	}

	public void setMovement(int movement) {
		this.movement = movement;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public int getAbility1() {
		return ability1;
	}

	public void setAbility1(int ability1) {
		this.ability1 = ability1;
	}

	public int getAbility2() {
		return ability2;
	}

	public void setAbility2(int ability2) {
		this.ability2 = ability2;
	}

	public int getShadow() {
		return shadow;
	}

	public void setShadow(int shadow) {
		this.shadow = shadow;
	}

	public int getRegen() {
		return regen;
	}

	public void setRegen(int regen) {
		this.regen = regen;
	}

	public int getSleepiness() {
		return sleepiness;
	}

	public void setSleepiness(int sleepiness) {
		this.sleepiness = sleepiness;
	}

	public int getBaseHp() {
		return baseHp;
	}

	public void setBaseHp(int baseHp) {
		this.baseHp = baseHp;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getBaseAtk() {
		return baseAtk;
	}

	public void setBaseAtk(int baseAtk) {
		this.baseAtk = baseAtk;
	}

	public int getBaseSpa() {
		return baseSpa;
	}

	public void setBaseSpa(int baseSpa) {
		this.baseSpa = baseSpa;
	}

	public int getBaseDef() {
		return baseDef;
	}

	public void setBaseDef(int baseDef) {
		this.baseDef = baseDef;
	}

	public int getBaseSpd() {
		return baseSpd;
	}

	public void setBaseSpd(int baseSpd) {
		this.baseSpd = baseSpd;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getUnk30() {
		return unk30;
	}

	public void setUnk30(int unk30) {
		this.unk30 = unk30;
	}

	public int getUnk31() {
		return unk31;
	}

	public void setUnk31(int unk31) {
		this.unk31 = unk31;
	}

	public int getUnk32() {
		return unk32;
	}

	public void setUnk32(int unk32) {
		this.unk32 = unk32;
	}

	public int getPreId() {
		return preId;
	}

	public void setPreId(int preId) {
		this.preId = preId;
	}

	public int getEvolveType() {
		return evolveType;
	}

	public void setEvolveType(int evolveType) {
		this.evolveType = evolveType;
	}

	public int getEvolveParam() {
		return evolveParam;
	}

	public void setEvolveParam(int evolveParam) {
		this.evolveParam = evolveParam;
	}

	public int getEvolveAddition() {
		return evolveAddition;
	}

	public void setEvolveAddition(int evolveAddition) {
		this.evolveAddition = evolveAddition;
	}

	public int getDexID() {
		return dexID;
	}

	public void setDexID(int dexID) {
		this.dexID = dexID;
	}

	public int getEntityID() {
		return entityID;
	}

	public void setEntityID(int entityID) {
		this.entityID = entityID;
	}

	public int getRecruit() {
		return recruit;
	}

	public void setRecruit(int recruit) {
		this.recruit = recruit;
	}

	public int getAlphaID() {
		return alphaID;
	}

	public void setAlphaID(int alphaID) {
		this.alphaID = alphaID;
	}

	public int getParentID() {
		return parentID;
	}

	public void setParentID(int parentID) {
		this.parentID = parentID;
	}

	public boolean getCanWalk() {
		return canWalk;
	}

	public void setCanWalk(boolean canWalk) {
		this.canWalk = canWalk;
	}

	public boolean getToolbox() {
		return toolbox;
	}

	public void setToolbox(boolean toolbox) {
		this.toolbox = toolbox;
	}

	public Learnset getLearnset() {
		return learnset;
	}

	public void setLearnset(Learnset learnset) {
		this.learnset = learnset;
	}

	public Levelmap getLvmp() {
		return lvmp;
	}
}
