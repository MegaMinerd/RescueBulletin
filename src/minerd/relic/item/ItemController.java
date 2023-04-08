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
import javafx.scene.image.ImageView;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.graphics.ImageProcessor;

public class ItemController implements Initializable {
	public Label itemNameLabel;
	public TextField spriteID, paletteID, itemID, buyPrice, sellPrice, itemNameField, moveId, minAmnt, maxAmnt;
	public TextArea description;
	public ChoiceBox<String> itemType, actionType;
	public CheckBox ai1, ai2, ai3;
	public ImageView sprite;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			RomFile rom = Rom.getAll();
			//int dataPointer = RomManipulator.getFilePointer();
			String name = rom.readStringAndReturn(rom.parsePointer());
			itemNameLabel.setText(name);
			itemNameField.setText(name);
			buyPrice.setText(rom.readInt() + "");
			sellPrice.setText(rom.readInt() + "");
			itemType.getItems().addAll("Throwable", "Rock", "Berry/Seed", "Apple/Gummi", "Hold Item", "TM", "Money",
					"Unused", "Misc.", "Orb", "Link Box", "Used TM");
			itemType.getSelectionModel().select(rom.readUnsignedByte());
			spriteID.setText(rom.readUnsignedByte() + "");
			rom.skip(2);
			description.setText(rom.readStringAndReturn(rom.parsePointer()).replace("#n", "\n"));
			ai1.setSelected(rom.readByte() != 0);
			ai2.setSelected(rom.readByte() != 0);
			ai3.setSelected(rom.readByte() != 0);
			rom.skip(1);
			moveId.setText(rom.readShort() + "");
			//Item order. Not shown here.
			rom.skip(1);
			minAmnt.setText(rom.readUnsignedByte() + "");
			maxAmnt.setText(rom.readUnsignedByte() + "");
			paletteID.setText(rom.readUnsignedByte() + "");
			actionType.getItems().addAll("Use (Nothing)", "Hurl (Throwable)", "Throw (Rocks)", "Equip (Ribbons)",
					"Eat (Food)", "Ingest (Healing Items)", "Peel (Chestnut)", "Use (Money/Wish Stone)", "Use (Misc.)",
					"Use (TMs)", "Use (Link Box)", "Equip (Specs)", "Equip (Scarfs)", "Use (Orbs)");
			actionType.getSelectionModel().select(rom.readUnsignedByte());
			sprite.setImage(ImageProcessor.getItemSprite(Integer.parseInt(spriteID.getText()), Integer.parseInt(paletteID.getText())));
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
