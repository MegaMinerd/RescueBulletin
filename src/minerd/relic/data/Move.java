package minerd.relic.data;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.fxml.MoveController;

public class Move extends GameData {
	private String name, description, useMessage;
	private int power, type, actualTarget, actualRange, aiTarget, aiRange, condition;
	private int basePP, weight, accuracy1, accuracy2, condChance, hitNum, upgrades, crit;
	private boolean magicCoat, snatachable, usesMouth, cantHitFrozen, ignoresTaunted;
	
	public Move(int index, int[] offsets) {
		try {
			RomFile rom = Rom.getAll();
			rom.seek(offsets[0]);
			rom.skip(index*0x24);
			name = rom.readStringAndReturn(rom.parsePointer());
			power = rom.readShort();
			type = rom.readUnsignedByte();
			rom.skip(1);
			short actualValues = rom.readShort();
			actualTarget = actualValues&0x0F;
			actualRange = (actualValues&0xF0)>>4;
			short aiValues = rom.readShort();
			aiTarget = aiValues&0x0F;
			aiRange = (aiValues&0xF0)>>4;
			condition = (aiValues&0xF00)>>8;
			basePP = rom.readUnsignedByte();
			weight = rom.readUnsignedByte();
			accuracy2 = rom.readByte();
			accuracy1 = rom.readByte();
			condChance = rom.readByte();
			hitNum = rom.readUnsignedByte();
			upgrades = rom.readByte();
			crit = rom.readByte();
			magicCoat = rom.readByte()!=0;
			snatachable = rom.readByte()!=0;
			usesMouth = rom.readByte()!=0;
			cantHitFrozen = rom.readByte()!=0;
			ignoresTaunted = rom.readByte()!=0;
			rom.skip(3);
			description = rom.readStringAndReturn(rom.parsePointer()).replace("#n", "\n");
			useMessage = rom.readStringAndReturn(rom.parsePointer());
		} catch (IOException | InvalidPointerException e) {
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
		return actualTarget;
	}

	public void setActualTarget(int actualTarget) {
		this.actualTarget = actualTarget;
	}

	public int getActualRange() {
		return actualRange;
	}

	public void setActualRange(int actualRange) {
		this.actualRange = actualRange;
	}

	public int getAiTarget() {
		return aiTarget;
	}

	public void setAiTarget(int aiTarget) {
		this.aiTarget = aiTarget;
	}

	public int getAiRange() {
		return aiRange;
	}

	public void setAiRange(int aiRange) {
		this.aiRange = aiRange;
	}

	public int getCondition() {
		return condition;
	}

	public void setCondition(int condition) {
		this.condition = condition;
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
