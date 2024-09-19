package minerd.relic.data;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.SiroFile;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.fxml.MoveController;
import minerd.relic.util.RrtOffsetList;

public class Move extends GameData {
	private String name, description, useMessage;
	// TODO: update getters/setters
	private int[] actualValues, aiValues;
	private int moveId, power, type, basePP, weight, accuracy1, accuracy2, condChance, hitNum, upgrades, crit;
	private boolean magicCoat, snatachable, usesMouth, cantHitFrozen, ignoresTaunted;

	public Move(int index) {
		moveId = index;
		try{
			SiroFile data = (SiroFile) Rom.getInstance().getSystemSbin().getSubfile("wazapara");
			BufferedDataHandler entry = data.getSegment("moves/" + index).getData();
			name = data.getSegment("strings/" + entry.parsePointer().getOffset()).getData().readString();
			power = entry.readShort();
			type = entry.readUnsignedByte();
			entry.skip(1);
			actualValues = entry.readMask(2, 4, 4);
			aiValues = entry.readMask(2, 4, 4, 4);
			basePP = entry.readUnsignedByte();
			weight = entry.readUnsignedByte();
			accuracy1 = entry.readByte();
			accuracy2 = entry.readByte();
			condChance = entry.readByte();
			hitNum = entry.readUnsignedByte();
			upgrades = entry.readByte();
			crit = entry.readByte();
			magicCoat = entry.readByte()!=0;
			snatachable = entry.readByte()!=0;
			usesMouth = entry.readByte()!=0;
			cantHitFrozen = entry.readByte()!=0;
			ignoresTaunted = entry.readByte()!=0;
			entry.skip(3);
			description = data.getSegment("strings/" + entry.parsePointer().getOffset()).getData().readString();
			useMessage = data.getSegment("strings/" + entry.parsePointer().getOffset()).getData().readString();
			Cache.add("Move", index, this);
		} catch(IOException | InvalidPointerException e){
			e.printStackTrace();
		}
	}

	@Override
	public Region load() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/move.fxml"));
		SplitPane dataPane = loader.load();
		MoveController controller = loader.getController();

		controller.load(this);
		return dataPane;
	}

	public void save() {
		try{
			SiroFile data = (SiroFile) Rom.getInstance().getSystemSbin().getSubfile("wazapara");
			BufferedDataHandler entry = data.getSegment("moves/" + moveId).getData();
			//entry.writeString(name, entry.parsePointer());
			entry.skip(4);
			entry.writeShort((short) power);
			entry.writeUnsignedByte(type);
			entry.skip(1);
			entry.writeMask(actualValues, 2, 4, 4);
			entry.writeMask(aiValues, 2, 4, 4, 4);
			entry.writeUnsignedByte(basePP);
			entry.writeUnsignedByte(weight);
			entry.writeByte((byte) accuracy1);
			entry.writeByte((byte) accuracy2);
			entry.writeByte((byte) condChance);
			entry.writeUnsignedByte(hitNum);
			entry.writeByte((byte) upgrades);
			entry.writeByte((byte) crit);
			entry.writeByte((byte) (magicCoat ? 1 : 0));
			entry.writeByte((byte) (snatachable ? 1 : 0));
			entry.writeByte((byte) (usesMouth ? 1 : 0));
			entry.writeByte((byte) (cantHitFrozen ? 1 : 0));
			entry.writeByte((byte) (ignoresTaunted ? 1 : 0));
			entry.skip(3);
			//entry.writeString(description.replace("\n", "#n"), entry.parsePointer());
			//entry.writeString(useMessage, entry.parsePointer());
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUseMessage() {
		return useMessage;
	}

	public void setUseMessage(String useMessage) {
		this.useMessage = useMessage;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getActualTarget() {
		return actualValues[0];
	}

	public void setActualTarget(int actualTarget) {
		this.actualValues[0] = actualTarget;
	}

	public int getActualRange() {
		return actualValues[1];
	}

	public void setActualRange(int actualRange) {
		this.actualValues[1] = actualRange;
	}

	public int getAiTarget() {
		return aiValues[0];
	}

	public void setAiTarget(int aiTarget) {
		this.aiValues[0] = aiTarget;
	}

	public int getAiRange() {
		return aiValues[1];
	}

	public void setAiRange(int aiRange) {
		this.aiValues[1] = aiRange;
	}

	public int getCondition() {
		return aiValues[2];
	}

	public void setCondition(int condition) {
		this.aiValues[2] = condition;
	}

	public int getBasePP() {
		return basePP;
	}

	public void setBasePP(int basePP) {
		this.basePP = basePP;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getAccuracy1() {
		return accuracy1;
	}

	public void setAccuracy1(int accuracy1) {
		this.accuracy1 = accuracy1;
	}

	public int getAccuracy2() {
		return accuracy2;
	}

	public void setAccuracy2(int accuracy2) {
		this.accuracy2 = accuracy2;
	}

	public int getCondChance() {
		return condChance;
	}

	public void setCondChance(int condChance) {
		this.condChance = condChance;
	}

	public int getHitNum() {
		return hitNum;
	}

	public void setHitNum(int hitNum) {
		this.hitNum = hitNum;
	}

	public int getUpgrades() {
		return upgrades;
	}

	public void setUpgrades(int upgrades) {
		this.upgrades = upgrades;
	}

	public int getCrit() {
		return crit;
	}

	public void setCrit(int crit) {
		this.crit = crit;
	}

	public boolean getMagicCoat() {
		return magicCoat;
	}

	public void setMagicCoat(boolean magicCoat) {
		this.magicCoat = magicCoat;
	}

	public boolean getSnatachable() {
		return snatachable;
	}

	public void setSnatachable(boolean snatachable) {
		this.snatachable = snatachable;
	}

	public boolean getUsesMouth() {
		return usesMouth;
	}

	public void setUsesMouth(boolean usesMouth) {
		this.usesMouth = usesMouth;
	}

	public boolean getCantHitFrozen() {
		return cantHitFrozen;
	}

	public void setCantHitFrozen(boolean cantHitFrozen) {
		this.cantHitFrozen = cantHitFrozen;
	}

	public boolean getIgnoresTaunted() {
		return ignoresTaunted;
	}

	public void setIgnoresTaunted(boolean ignoresTaunted) {
		this.ignoresTaunted = ignoresTaunted;
	}

}
