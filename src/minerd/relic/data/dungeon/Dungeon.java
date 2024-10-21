package minerd.relic.data.dungeon;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import minerd.relic.data.Cache;
import minerd.relic.data.GameData;
import minerd.relic.data.Text;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.Rom;
import minerd.relic.file.SiroFile;
import minerd.relic.fxml.DungeonController;

public class Dungeon extends GameData {
	private String name;
	private int dungeonId, rescuesAllowed, maxItemCount, maxPartySize, turnLimit, randWalkChance;
	private boolean stairsUp, evoOnKo, recruitable, resetLevel, resetMoney, leaderSwitchable, hasBreakpoint, saveRequired;
	private int[] hmMask;

	public Dungeon(int index) {
		dungeonId = index;
		try{
			BufferedDataHandler entry = Rom.getInstance().getDungeonData(index);
			name = Text.getText("Dungeons", index);
			entry.seek(0);
			stairsUp = entry.readBoolean();
			evoOnKo = entry.readBoolean();
			recruitable = entry.readBoolean();
			rescuesAllowed = entry.readByte();
			maxItemCount = entry.readUnsignedByte();
			maxPartySize = entry.readUnsignedByte();
			resetLevel = entry.readBoolean();
			resetMoney = !entry.readBoolean();
			leaderSwitchable = entry.readBoolean();
			hasBreakpoint = entry.readBoolean();
			saveRequired = !entry.readBoolean();
			hmMask = entry.readMask(1, 1, 1, 1, 1, 1);
			turnLimit = entry.readUnsignedShort();
			randWalkChance = entry.readUnsignedShort();
			Cache.add("Dungeon", index, this);
		} catch(IOException e){
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

	public void save() {
		try{
			SiroFile data = (SiroFile) Rom.getInstance().getDungeonSbin().getSubfile("mapparam");
			BufferedDataHandler entry = data.getSegment("main/" + dungeonId).getData();
			entry.seek(dungeonId);
			entry.writeBoolean(stairsUp);
			entry.writeBoolean(evoOnKo);
			entry.writeBoolean(recruitable);
			entry.writeByte((byte) rescuesAllowed);
			entry.writeUnsignedByte(maxItemCount);
			entry.writeUnsignedByte(maxPartySize);
			entry.writeBoolean(resetLevel);
			entry.writeBoolean(!resetMoney);
			entry.writeBoolean(leaderSwitchable);
			entry.writeBoolean(hasBreakpoint);
			entry.writeBoolean(!saveRequired);
			entry.writeMask(hmMask, 1, 1, 1, 1, 1, 1);
			entry.writeUnsignedShort(turnLimit);
			entry.writeUnsignedShort(randWalkChance);
		} catch(IOException e){
			e.printStackTrace();
		}
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
		return hmMask[0]!=0;
	}

	public void setFlyNeeded(boolean flyNeeded) {
		this.hmMask[0] = flyNeeded ? 1 : 0;
	}

	public boolean isDiveNeeded() {
		return hmMask[1]!=0;
	}

	public void setDiveNeeded(boolean diveNeeded) {
		this.hmMask[1] = diveNeeded ? 1 : 0;
	}

	public boolean isWaterfallNeeded() {
		return hmMask[2]!=0;
	}

	public void setWaterfallNeeded(boolean waterfallNeeded) {
		this.hmMask[2] = waterfallNeeded ? 1 : 0;
	}

	public boolean isSurfNeeded() {
		return hmMask[3]!=0;
	}

	public void setSurfNeeded(boolean surfNeeded) {
		this.hmMask[3] = surfNeeded ? 1 : 0;
	}

	public boolean isWaterTypeNeeded() {
		return hmMask[4]!=0;
	}

	public void setWaterTypeNeeded(boolean waterTypeNeeded) {
		this.hmMask[4] = waterTypeNeeded ? 1 : 0;
	}
}
