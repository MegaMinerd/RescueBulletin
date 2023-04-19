package minerd.relic.data;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.fxml.DungeonController;

public class Dungeon extends GameData {
	private String name;
	private int rescuesAllowed, maxItemCount, maxPartySize, turnLimit, randWalkChance; 
	private boolean stairsUp, evoOnKo, recruitable, resetLevel, resetMoney, leaderSwitchable, hasBreakpoint, saveRequired;
	private boolean flyNeeded, diveNeeded, waterfallNeeded, surfNeeded, waterTypeNeeded;
	
	public Dungeon(int index, int[] offsets) {
		try {
			RomFile rom = Rom.getAll();
			rom.seek(offsets[0]);
			rom.skip(index*0x10);
			name = Text.getText("Dungeons", index);
			stairsUp = rom.readUnsignedByte()!=0;
			evoOnKo = rom.readUnsignedByte()!=0;
			recruitable = rom.readUnsignedByte()!=0;
			rescuesAllowed = rom.readByte();
			maxItemCount = rom.readUnsignedByte();
			maxPartySize = rom.readUnsignedByte();
			resetLevel = rom.readUnsignedByte()!=0;
			resetMoney = rom.readUnsignedByte()==0;
			leaderSwitchable = rom.readUnsignedByte()!=0;
			hasBreakpoint = rom.readUnsignedByte()!=0;
			saveRequired = rom.readUnsignedByte()==0;
			int[] hmMask = rom.readMask(1, 1, 1, 1, 1, 1);
			flyNeeded = hmMask[0]!=0;
			diveNeeded = hmMask[1]!=0;
			waterfallNeeded = hmMask[2]!=0;
			surfNeeded = hmMask[3]!=0;
			waterTypeNeeded = hmMask[4]!=0;
			turnLimit = rom.readUnsignedShort();
			randWalkChance = rom.readUnsignedShort();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Region load() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/dungeon.fxml"));
		AnchorPane dataPane = loader.load();
		DungeonController controller = loader.getController();
	    
	    controller.load(this);
	    return dataPane;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getRescuesAllowed() {
		return rescuesAllowed;
	}

	public void setRescuesAllowed(int rescuesAllowed) {
		this.rescuesAllowed = rescuesAllowed;
	}

	public int getMaxItemCount() {
		return maxItemCount;
	}

	public void setMaxItemCount(int maxItemCount) {
		this.maxItemCount = maxItemCount;
	}

	public int getMaxPartySize() {
		return maxPartySize;
	}

	public void setMaxPartySize(int maxPartySize) {
		this.maxPartySize = maxPartySize;
	}

	public int getTurnLimit() {
		return turnLimit;
	}

	public void setTurnLimit(int turnLimit) {
		this.turnLimit = turnLimit;
	}

	public int getRandWalkChance() {
		return randWalkChance;
	}

	public void setRandWalkChance(int randWalkChance) {
		this.randWalkChance = randWalkChance;
	}

	public boolean areStairsUp() {
		return stairsUp;
	}

	public void setStairsUp(boolean stairsUp) {
		this.stairsUp = stairsUp;
	}

	public boolean getEvoOnKo() {
		return evoOnKo;
	}

	public void setEvoOnKo(boolean evoOnKo) {
		this.evoOnKo = evoOnKo;
	}

	public boolean isRecruitable() {
		return recruitable;
	}

	public void setRecruitable(boolean recruitable) {
		this.recruitable = recruitable;
	}

	public boolean isLevelReset() {
		return resetLevel;
	}

	public void setResetLevel(boolean resetLevel) {
		this.resetLevel = resetLevel;
	}

	public boolean isMoneyReset() {
		return resetMoney;
	}

	public void setResetMoney(boolean resetMoney) {
		this.resetMoney = resetMoney;
	}

	public boolean isLeaderSwitchable() {
		return leaderSwitchable;
	}

	public void setLeaderSwitchable(boolean leaderSwitchable) {
		this.leaderSwitchable = leaderSwitchable;
	}

	public boolean getHasBreakpoint() {
		return hasBreakpoint;
	}

	public void setHasBreakpoint(boolean hasBreakpoint) {
		this.hasBreakpoint = hasBreakpoint;
	}

	public boolean isSaveRequired() {
		return saveRequired;
	}

	public void setSaveRequired(boolean saveRequired) {
		this.saveRequired = saveRequired;
	}

	public boolean isFlyNeeded() {
		return flyNeeded;
	}

	public void setFlyNeeded(boolean flyNeeded) {
		this.flyNeeded = flyNeeded;
	}

	public boolean isDiveNeeded() {
		return diveNeeded;
	}

	public void setDiveNeeded(boolean diveNeeded) {
		this.diveNeeded = diveNeeded;
	}

	public boolean isWaterfallNeeded() {
		return waterfallNeeded;
	}

	public void setWaterfallNeeded(boolean waterfallNeeded) {
		this.waterfallNeeded = waterfallNeeded;
	}

	public boolean isSurfNeeded() {
		return surfNeeded;
	}

	public void setSurfNeeded(boolean surfNeeded) {
		this.surfNeeded = surfNeeded;
	}

	public boolean isWaterTypeNeeded() {
		return waterTypeNeeded;
	}

	public void setWaterTypeNeeded(boolean waterTypeNeeded) {
		this.waterTypeNeeded = waterTypeNeeded;
	}

}
