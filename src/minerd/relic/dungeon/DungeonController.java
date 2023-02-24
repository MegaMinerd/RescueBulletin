package minerd.relic.dungeon;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import minerd.relic.RomManipulator;

public class DungeonController implements Initializable {
	public TextField rescuesAllowed, maxItemCount, maxPartySize, turnLimit, randWalkChance;
	public ChoiceBox<String> stairDir;
	public CheckBox evoOnKo, canRecruit, resetLevel, resetMoney, leaderSwitchable, hasBreakpoint, requireSave;
	public CheckBox needFly, needDive, needWaterfall, needSurf, needWaterType;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			stairDir.getItems().addAll("Down", "Up");
			stairDir.getSelectionModel().select(RomManipulator.readUnsignedByte());
			evoOnKo.setSelected(RomManipulator.readUnsignedByte()!=0);
			canRecruit.setSelected(RomManipulator.readUnsignedByte()!=0);
			rescuesAllowed.setText(RomManipulator.readByte()+"");
			maxItemCount.setText(RomManipulator.readUnsignedByte()+"");
			maxPartySize.setText(RomManipulator.readUnsignedByte()+"");
			resetLevel.setSelected(RomManipulator.readUnsignedByte()!=0);
			resetMoney.setSelected(RomManipulator.readUnsignedByte()==0);
			leaderSwitchable.setSelected(RomManipulator.readUnsignedByte()!=0);
			hasBreakpoint.setSelected(RomManipulator.readUnsignedByte()!=0);
			requireSave.setSelected(RomManipulator.readUnsignedByte()==0);
			int[] hmMask = RomManipulator.readMask(1, 1, 1, 1, 1, 1);
			needFly.setSelected(hmMask[0]!=0);
			needDive.setSelected(hmMask[1]!=0);
			needWaterfall.setSelected(hmMask[2]!=0);
			needSurf.setSelected(hmMask[3]!=0);
			needWaterType.setSelected(hmMask[4]!=0);
			turnLimit.setText(RomManipulator.readUnsignedShort()+"");
			randWalkChance.setText(RomManipulator.readUnsignedShort()+"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
