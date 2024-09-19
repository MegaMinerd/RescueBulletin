package minerd.relic.data;

import java.io.IOException;
import java.nio.ByteBuffer;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.SiroFile;
import minerd.relic.fxml.PokemonController;
import minerd.relic.util.CompressionHandler;
import minerd.relic.util.RrtOffsetList;

public class Pokemon extends GameData {
	private String name, category;
	private int palette, bodySize, speed, type1, type2, movement, area, ability1, ability2;
	private int shadow, regen, sleepiness, baseHp, exp, baseAtk, baseSpa, baseDef, baseSpd, weight, size;
	private int unk30, unk31, unk32, preId, evolveType, evolveParam, evolveAddition;
	private int pokemonId, dexID, entityID, recruit, alphaID, parentID, faces;
	private boolean canWalk, toolbox;
	private Learnset learnset;
	private Levelmap lvmp;

	public Pokemon(int index) {
		pokemonId = index;
		try{
			SiroFile data = (SiroFile) Rom.getInstance().getSystemSbin().getSubfile("monspara");
			BufferedDataHandler entry = data.getSegment("pokemon/" + index).getData();
			name = data.getSegment("strings/" + entry.parsePointer().getOffset()).getData().readString();
			category = data.getSegment("strings/" + entry.parsePointer().getOffset()).getData().readString();
			palette = entry.readUnsignedByte();
			bodySize = entry.readByte();
			entry.skip(2);
			speed = entry.readInt();
			faces = entry.readUnsignedShort();
			entry.skip(1);
			type1 = entry.readUnsignedByte();
			type2 = entry.readUnsignedByte();
			movement = entry.readUnsignedByte();
			area = entry.readUnsignedByte();
			ability1 = entry.readUnsignedByte();
			ability2 = entry.readUnsignedByte();
			shadow = entry.readUnsignedByte();
			entry.skip(1);
			regen = entry.readUnsignedByte();
			canWalk = entry.readUnsignedByte()!=0;
			sleepiness = entry.readUnsignedByte();
			baseHp = entry.readShort();
			exp = entry.readInt();
			baseAtk = entry.readShort();
			baseSpa = entry.readShort();
			baseDef = entry.readShort();
			baseSpd = entry.readShort();
			weight = entry.readShort();
			size = entry.readShort();
			unk30 = entry.readUnsignedByte();
			unk31 = entry.readUnsignedByte();
			unk32 = entry.readUnsignedByte();
			toolbox = entry.readUnsignedByte()!=0;
			preId = entry.readShort();
			evolveType = entry.readShort();
			evolveParam = entry.readShort();
			evolveAddition = entry.readShort();
			dexID = entry.readShort();
			entityID = entry.readShort();
			recruit = entry.readShort();
			alphaID = entry.readShort();
			parentID = entry.readShort();
			Cache.add("Pokemon", index, this);

			//I don't know why
			if(entityID>226)
				entityID -= 2;

			learnset = (Learnset) Cache.get("Learnset", index);
			if(learnset==null){
				learnset = new Learnset(index);
				Cache.add("Learnset", index, learnset);
			}

			//TODO
			entry.seek(RrtOffsetList.levelmapOffset);
			entry.skip(entityID*0x8 + 0x4);
			entry.seek(entry.parsePointer());
			entry.skip(16);
			int start = entry.getFilePointer();
			entry.skip(5);
			byte temp[] = new byte[entry.readShort()];
			entry.getBuffer().get(start, temp);
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

	public void save() {
		try{

			//I don't know why
			if(entityID>226)
				entityID += 2;

			SiroFile data = (SiroFile) Rom.getInstance().getSystemSbin().getSubfile("monspara");
			BufferedDataHandler entry = data.getSegment("pokemon/" + pokemonId).getData();
			entry.skip(8);
			//entry.writeString(name, entry.parsePointer());
			//entry.writeString(category, entry.parsePointer());
			entry.skip(8);
			entry.writeUnsignedByte(palette);
			entry.writeByte((byte) bodySize);
			entry.skip(2);
			entry.writeInt(speed);
			entry.writeUnsignedShort(faces);
			entry.skip(1);
			entry.writeUnsignedByte(type1);
			entry.writeUnsignedByte(type2);
			entry.writeUnsignedByte(movement);
			entry.writeUnsignedByte(area);
			entry.writeUnsignedByte(ability1);
			entry.writeUnsignedByte(ability2);
			entry.writeUnsignedByte(shadow);
			entry.skip(1);
			entry.writeUnsignedByte(regen);
			entry.writeBoolean(canWalk);
			entry.writeUnsignedByte(sleepiness);
			entry.writeShort((short) baseHp);
			entry.writeInt(exp);
			entry.writeShort((short) baseAtk);
			entry.writeShort((short) baseSpa);
			entry.writeShort((short) baseDef);
			entry.writeShort((short) baseSpd);
			entry.writeShort((short) weight);
			entry.writeShort((short) size);
			entry.writeUnsignedByte(unk30);
			entry.writeUnsignedByte(unk31);
			entry.writeUnsignedByte(unk32);
			entry.writeBoolean(toolbox);
			entry.writeShort((short) preId);
			entry.writeShort((short) evolveType);
			entry.writeShort((short) evolveParam);
			entry.writeShort((short) evolveAddition);
			entry.writeShort((short) dexID);
			entry.writeShort((short) entityID);
			entry.writeShort((short) recruit);
			entry.writeShort((short) alphaID);
			entry.writeShort((short) parentID);
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
