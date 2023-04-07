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
import minerd.relic.InvalidPointerException;
import minerd.relic.RomManipulator;
import minerd.relic.data.Text;

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
			String name = RomManipulator.readStringAndReturn(RomManipulator.parsePointer());
			moveNameLabel.setText(name);
			moveNameField.setText(name);
			power.setText(RomManipulator.readShort() + "");
			type.getItems().addAll(Text.getTextList("Types"));
			type.getSelectionModel().select(RomManipulator.readUnsignedByte());
			RomManipulator.skip(1);
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
			short actualValues = RomManipulator.readShort();
			actualTarget.getItems().addAll(targets);
			actualTarget.getSelectionModel().select((actualValues&0x0F));
			actualRange.getItems().addAll(ranges);
			actualRange.getSelectionModel().select(((actualValues&0xF0)>>4));
			short aiValues = RomManipulator.readShort();
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
			basePP.setText(RomManipulator.readUnsignedByte() + "");
			weight.setText(RomManipulator.readUnsignedByte() + "");
			accuracy2.setText(RomManipulator.readByte() + "");
			accuracy1.setText(RomManipulator.readByte() + "");
			condChance.setText(RomManipulator.readByte()+"");
			hitNum.setText(RomManipulator.readUnsignedByte() + "");
			upgrades.setText(RomManipulator.readByte() + "");
			crit.setText(RomManipulator.readByte() + "");
			magicCoat.setSelected(RomManipulator.readByte()!=0);
			snatachable.setSelected(RomManipulator.readByte()!=0);
			usesMouth.setSelected(RomManipulator.readByte()!=0);
			cantHitFrozen.setSelected(RomManipulator.readByte()!=0);
			ignoresTaunted.setSelected(RomManipulator.readByte()!=0);
			RomManipulator.skip(3);
			description.setText(RomManipulator.readStringAndReturn(RomManipulator.parsePointer()).replace("#n", "\n"));
			useMessage.setText(RomManipulator.readStringAndReturn(RomManipulator.parsePointer()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
