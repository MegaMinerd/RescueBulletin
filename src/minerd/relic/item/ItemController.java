package minerd.relic.item;

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

public class ItemController implements Initializable {
	public Label itemNameLabel;
	public TextField spriteID, paletteID, itemID, buyPrice, sellPrice, itemNameField, moveId, minAmnt, maxAmnt;
	public TextArea description;
	public ChoiceBox<String> itemType, actionType;
	public CheckBox ai1, ai2, ai3;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			//int dataPointer = RomManipulator.getFilePointer();
			String name = RomManipulator.readStringAndReturn(RomManipulator.parsePointer());
			itemNameLabel.setText(name);
			itemNameField.setText(name);
			buyPrice.setText(RomManipulator.readInt() + "");
			sellPrice.setText(RomManipulator.readInt() + "");
			itemType.getItems().addAll("Throwable", "Rock", "Berry/Seed", "Apple/Gummi", "Hold Item", "TM", "Money",
					"Unused", "Misc.", "Orb", "Link Box", "Used TM");
			itemType.getSelectionModel().select(RomManipulator.readUnsignedByte());
			spriteID.setText(RomManipulator.readUnsignedByte() + "");
			RomManipulator.skip(2);
			description.setText(RomManipulator.readStringAndReturn(RomManipulator.parsePointer()).replace("#n", "\n"));
			ai1.setSelected(RomManipulator.readByte() != 0);
			ai2.setSelected(RomManipulator.readByte() != 0);
			ai3.setSelected(RomManipulator.readByte() != 0);
			RomManipulator.skip(1);
			moveId.setText(RomManipulator.readShort() + "");
			//Item order. Not shown here.
			RomManipulator.skip(1);
			minAmnt.setText(RomManipulator.readUnsignedByte() + "");
			maxAmnt.setText(RomManipulator.readUnsignedByte() + "");
			paletteID.setText(RomManipulator.readUnsignedByte() + "");
			actionType.getItems().addAll("Use (Nothing)", "Hurl (Throwable)", "Throw (Rocks)", "Equip (Ribbons)",
					"Eat (Food)", "Ingest (Healing Items)", "Peel (Chestnut)", "Use (Money/Wish Stone)", "Use (Misc.)",
					"Use (TMs)", "Use (Link Box)", "Equip (Specs)", "Equip (Scarfs)", "Use (Orbs)");
			actionType.getSelectionModel().select(RomManipulator.readUnsignedByte());
			//buffer.skip(0x1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
