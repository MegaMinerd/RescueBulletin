package minerd.relic.item;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import minerd.relic.InvalidPointerException;
import minerd.relic.RomManipulator;

public class ItemController implements Initializable {
	public Label itemNameLabel;
	public TextField spriteID, paletteID, itemID, buyPrice, sellPrice, itemNameField, description;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			String name = RomManipulator.readStringAndReturn(RomManipulator.parsePointer());
			itemNameLabel.setText(name);
			itemNameField.setText(name);
			buyPrice.setText(RomManipulator.readInt()+"");
			sellPrice.setText(RomManipulator.readInt()+"");
			RomManipulator.skip(1);//temp
			spriteID.setText(RomManipulator.readUnsignedByte()+"");
			RomManipulator.skip(2);
			description.setText(RomManipulator.readStringAndReturn(RomManipulator.parsePointer()));
			RomManipulator.skip(3);//temp
			RomManipulator.skip(1);
			RomManipulator.skip(5);//temp
			paletteID.setText(RomManipulator.readUnsignedByte()+"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
