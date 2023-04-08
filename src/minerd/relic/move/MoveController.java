package minerd.relic.move;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import minerd.relic.data.Text;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

public class MoveController implements Initializable {
	public Label moveNameLabel;
	public ChoiceBox<String> type;
	//Name tab
	public TextField moveNameField, useMessage;
	public TextArea description;
	//General tab
	public TextField power, basePP, accuracy1, accuracy2, hitNum, upgrades, crit;
	//Range tab
	public TextField weight, condChance;
	public ChoiceBox<String> actualTarget, actualRange, aiTarget, aiRange, condition;
	//Other tab
	public CheckBox magicCoat, snatachable, usesMouth, cantHitFrozen, ignoresTaunted;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			RomFile rom = Rom.getAll();
			String name = rom.readStringAndReturn(rom.parsePointer());
			moveNameLabel.setText(name);
			moveNameField.setText(name);
			power.setText(rom.readShort() + "");
			type.getItems().addAll(Text.getTextList("Types"));
			type.getSelectionModel().select(rom.readUnsignedByte());
			rom.skip(1);
			String[] targets = {"Enemies", "Allies", "Everyone", "User", "Two-turn move",
					"Everyone except user", "All allies except user", "Invalid 7",
					"Invalid 8", "Invalid 9", "Invalid 10", "Invalid 11", "Invalid 12",
					"Invalid 13", "Invalid 14", "Special / Invalid"	
			};
			String[] ranges = {"In front", "In front + adjacent", "8 tiles around user", "Room",
					"Two tiles away", "Straight line", "Floor", "User", "In front; cuts corners",
					"Two tiles away; cuts corners",	"Invalid 10", "Invalid 11", "Invalid 12",
					"Invalid 13", "Invalid 14", "Special / Invalid"	
			};
			short actualValues = rom.readShort();
			actualTarget.getItems().addAll(targets);
			actualTarget.getSelectionModel().select((actualValues&0x0F));
			actualRange.getItems().addAll(ranges);
			actualRange.getSelectionModel().select(((actualValues&0xF0)>>4));
			short aiValues = rom.readShort();
			aiTarget.getItems().addAll(targets);
			aiTarget.getSelectionModel().select((aiValues&0x0F));
			aiRange.getItems().addAll(ranges);
			aiRange.getSelectionModel().select(((aiValues&0xF0)>>4));
			condition.getItems().addAll("No condition", "Based on AI Condition 1 Chance",
					"Is asleep, in a nightmare, or napping",
					"Is a ghost-type Pokemon and does not have the exposed status",
					"Current HP <= 25% or has at least one negative status condition",
					"Invalid 7", "Invalid 8", "Invalid 9", "Invalid 10", "Invalid 11", 
					"Invalid 12", "Invalid 13", "Invalid 14", "Invalid 15");
			condition.getSelectionModel().select((aiValues&0xF00)>>8);
			basePP.setText(rom.readUnsignedByte() + "");
			weight.setText(rom.readUnsignedByte() + "");
			accuracy2.setText(rom.readByte() + "");
			accuracy1.setText(rom.readByte() + "");
			condChance.setText(rom.readByte()+"");
			hitNum.setText(rom.readUnsignedByte() + "");
			upgrades.setText(rom.readByte() + "");
			crit.setText(rom.readByte() + "");
			magicCoat.setSelected(rom.readByte()!=0);
			snatachable.setSelected(rom.readByte()!=0);
			usesMouth.setSelected(rom.readByte()!=0);
			cantHitFrozen.setSelected(rom.readByte()!=0);
			ignoresTaunted.setSelected(rom.readByte()!=0);
			rom.skip(3);
			description.setText(rom.readStringAndReturn(rom.parsePointer()).replace("#n", "\n"));
			useMessage.setText(rom.readStringAndReturn(rom.parsePointer()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
