package minerd.relic.dungeon;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

public class DungeonController implements Initializable {
	public TextField rescuesAllowed, maxItemCount, maxPartySize, turnLimit, randWalkChance;
	public ChoiceBox<String> stairDir;
	public CheckBox evoOnKo, canRecruit, resetLevel, resetMoney, leaderSwitchable, hasBreakpoint, requireSave;
	public CheckBox needFly, needDive, needWaterfall, needSurf, needWaterType;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			RomFile rom = Rom.getAll();
			stairDir.getItems().addAll("Down", "Up");
			stairDir.getSelectionModel().select(rom.readUnsignedByte());
			evoOnKo.setSelected(rom.readUnsignedByte()!=0);
			canRecruit.setSelected(rom.readUnsignedByte()!=0);
			rescuesAllowed.setText(rom.readByte()+"");
			maxItemCount.setText(rom.readUnsignedByte()+"");
			maxPartySize.setText(rom.readUnsignedByte()+"");
			resetLevel.setSelected(rom.readUnsignedByte()!=0);
			resetMoney.setSelected(rom.readUnsignedByte()==0);
			leaderSwitchable.setSelected(rom.readUnsignedByte()!=0);
			hasBreakpoint.setSelected(rom.readUnsignedByte()!=0);
			requireSave.setSelected(rom.readUnsignedByte()==0);
			int[] hmMask = rom.readMask(1, 1, 1, 1, 1, 1);
			needFly.setSelected(hmMask[0]!=0);
			needDive.setSelected(hmMask[1]!=0);
			needWaterfall.setSelected(hmMask[2]!=0);
			needSurf.setSelected(hmMask[3]!=0);
			needWaterType.setSelected(hmMask[4]!=0);
			turnLimit.setText(rom.readUnsignedShort()+"");
			randWalkChance.setText(rom.readUnsignedShort()+"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
