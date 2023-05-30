package minerd.relic.fxml;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import minerd.relic.data.dungeon.Dungeon;

public class DungeonController{
	Dungeon dungeon;
	public TextField rescuesAllowed, maxItemCount, maxPartySize, turnLimit, randWalkChance;
	public ChoiceBox<String> stairDir;
	public CheckBox evoOnKo, canRecruit, resetLevel, resetMoney, leaderSwitchable, hasBreakpoint, requireSave;
	public CheckBox needFly, needDive, needWaterfall, needSurf, needWaterType;
	public Button apply;

	public void load(Dungeon dungeon) {
		this.dungeon = dungeon;
		stairDir.getItems().addAll("Down", "Up");
		stairDir.getSelectionModel().select(dungeon.areStairsUp()?1:0);
		evoOnKo.setSelected(dungeon.getEvoOnKo());
		canRecruit.setSelected(dungeon.isRecruitable());
		rescuesAllowed.setText(dungeon.getRescuesAllowed()+"");
		maxItemCount.setText(dungeon.getMaxItemCount()+"");
		maxPartySize.setText(dungeon.getMaxPartySize()+"");
		resetLevel.setSelected(dungeon.isLevelReset());
		resetMoney.setSelected(dungeon.isMoneyReset());
		leaderSwitchable.setSelected(dungeon.isLeaderSwitchable());
		hasBreakpoint.setSelected(dungeon.getHasBreakpoint());
		requireSave.setSelected(dungeon.isSaveRequired());
		needFly.setSelected(dungeon.isFlyNeeded());
		needDive.setSelected(dungeon.isDiveNeeded());
		needWaterfall.setSelected(dungeon.isWaterfallNeeded());
		needSurf.setSelected(dungeon.isSurfNeeded());
		needWaterType.setSelected(dungeon.isWaterTypeNeeded());
		turnLimit.setText(dungeon.getTurnLimit()+"");
		randWalkChance.setText(dungeon.getRandWalkChance()+"");
	}
	
	public void applyChanges() {
		dungeon.setStairsUp(stairDir.getSelectionModel().getSelectedIndex()==1);
		dungeon.setEvoOnKo(evoOnKo.isSelected());
		dungeon.setRecruitable(canRecruit.isSelected());
		dungeon.setRescuesAllowed(Integer.parseInt(rescuesAllowed.getText()));
		dungeon.setMaxItemCount(Integer.parseInt(maxItemCount.getText()));
		dungeon.setMaxPartySize (Integer.parseInt(maxPartySize.getText()));
		dungeon.setResetLevel(resetLevel.isSelected());
		dungeon.setResetMoney(resetMoney.isSelected());
		dungeon.setLeaderSwitchable(leaderSwitchable.isSelected());
		dungeon.setHasBreakpoint(hasBreakpoint.isSelected());
		dungeon.setSaveRequired(requireSave.isSelected());
		dungeon.setFlyNeeded(needFly.isSelected());
		dungeon.setDiveNeeded(needDive.isSelected());
		dungeon.setWaterfallNeeded(needWaterfall.isSelected());
		dungeon.setSurfNeeded(needSurf.isSelected());
		dungeon.setWaterTypeNeeded(needWaterType.isSelected());
		dungeon.setTurnLimit(Integer.parseInt(turnLimit.getText()));
		dungeon.setRandWalkChance(Integer.parseInt(randWalkChance.getText()));
	}
}
