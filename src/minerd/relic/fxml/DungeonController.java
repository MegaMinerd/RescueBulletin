package minerd.relic.fxml;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import minerd.relic.data.Dungeon;

public class DungeonController{
	public TextField rescuesAllowed, maxItemCount, maxPartySize, turnLimit, randWalkChance;
	public ChoiceBox<String> stairDir;
	public CheckBox evoOnKo, canRecruit, resetLevel, resetMoney, leaderSwitchable, hasBreakpoint, requireSave;
	public CheckBox needFly, needDive, needWaterfall, needSurf, needWaterType;

	public void load(Dungeon dungeon) {
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
}
