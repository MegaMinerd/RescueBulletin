package minerd.relic.fxml;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import minerd.relic.data.Move;
import minerd.relic.data.Text;

public class MoveController{
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
	
	public void load(Move move) {
		moveNameLabel.setText(move.getName());
		moveNameField.setText(move.getName());
		power.setText(move.getPower() + "");
		type.getItems().addAll(Text.getTextList("Types"));
		type.getSelectionModel().select(move.getType());
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
		actualTarget.getItems().addAll(targets);
		actualTarget.getSelectionModel().select(move.getActualTarget());
		actualRange.getItems().addAll(ranges);
		actualRange.getSelectionModel().select(move.getActualRange());
		aiTarget.getItems().addAll(targets);
		aiTarget.getSelectionModel().select(move.getAiTarget());
		aiRange.getItems().addAll(ranges);
		aiRange.getSelectionModel().select(move.getAiRange());
		condition.getItems().addAll("No condition", "Based on AI Condition 1 Chance",
				"Is asleep, in a nightmare, or napping",
				"Is a ghost-type Pokemon and does not have the exposed status",
				"Current HP <= 25% or has at least one negative status condition",
				"Invalid 7", "Invalid 8", "Invalid 9", "Invalid 10", "Invalid 11", 
				"Invalid 12", "Invalid 13", "Invalid 14", "Invalid 15");
		condition.getSelectionModel().select(move.getCondition());
		basePP.setText(move.getBasePP() + "");
		weight.setText(move.getWeight() + "");
		accuracy2.setText(move.getAccuracy1() + "");
		accuracy1.setText(move.getAccuracy2() + "");
		condChance.setText(move.getCondChance() +"");
		hitNum.setText(move.getHitNum() + "");
		upgrades.setText(move.getUpgrades() + "");
		crit.setText(move.getCrit() + "");
		magicCoat.setSelected(move.getMagicCoat());
		snatachable.setSelected(move.getSnatachable());
		usesMouth.setSelected(move.getUsesMouth());
		cantHitFrozen.setSelected(move.getCantHitFrozen());
		ignoresTaunted.setSelected(move.getIgnoresTaunted());
		description.setText(move.getDescription());
		useMessage.setText(move.getUseMessage());
	}
}
