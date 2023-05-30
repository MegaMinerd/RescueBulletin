package minerd.relic.fxml;

import java.io.IOException;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import minerd.relic.data.Item;
import minerd.relic.data.Text;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.graphics.ImageProcessor;

public class ItemController {
	Item item;
	public Label itemNameLabel;
	public TextField spriteId, paletteId, itemId, buyPrice, sellPrice, itemNameField, moveId, minAmnt, maxAmnt;
	public TextArea description;
	public ChoiceBox<String> itemType, actionType;
	public CheckBox ai1, ai2, ai3;
	public ImageView sprite;
	public Button apply;

	public void load(Item item) {
		this.item = item;
		itemId.setText(item.getItemId() + "");
		itemNameLabel.setText(item.getName());
		itemNameField.setText(item.getName());
		buyPrice.setText(item.getBuyPrice() + "");
		sellPrice.setText(item.getSellPrice() + "");
		itemType.getItems().addAll(Text.getTextList("Item Types"));
		itemType.getSelectionModel().select(item.getItemType());
		spriteId.setText(item.getSpriteId() + "");
		description.setText(item.getDescription());
		ai1.setSelected(item.isAi1());
		ai2.setSelected(item.isAi2());
		ai3.setSelected(item.isAi3());
		moveId.setText(item.getMoveId() + "");
		minAmnt.setText(item.getMinAmnt() + "");
		maxAmnt.setText(item.getMaxAmnt() + "");
		paletteId.setText(item.getPaletteId() + "");
		actionType.getItems().addAll("Use (Nothing)", "Hurl (Throwable)", "Throw (Rocks)", "Equip (Ribbons)",
				"Eat (Food)", "Ingest (Healing Items)", "Peel (Chestnut)", "Use (Money/Wish Stone)", "Use (Misc.)",
				"Use (TMs)", "Use (Link Box)", "Equip (Specs)", "Equip (Scarfs)", "Use (Orbs)");
		actionType.getSelectionModel().select(item.getActionType());
		try{
			sprite.setImage(ImageProcessor.getItemSprite(Integer.parseInt(spriteId.getText()),
					Integer.parseInt(paletteId.getText())));
		} catch(NumberFormatException | IOException | InvalidPointerException e){
			e.printStackTrace();
		}
	}

	public void applyChanges() {
		item.setItemId(Integer.parseInt(itemId.getText()));
		item.setName(itemNameLabel.getText());
		item.setName(itemNameField.getText());
		item.setBuyPrice(Integer.parseInt(buyPrice.getText()));
		item.setSellPrice(Integer.parseInt(sellPrice.getText()));
		item.setItemType(itemType.getSelectionModel().getSelectedIndex());
		item.setSpriteId(Integer.parseInt(spriteId.getText()));
		item.setDescription(description.getText());
		item.setAi1(ai1.isSelected());
		item.setAi2(ai2.isSelected());
		item.setAi3(ai3.isSelected());
		item.setMoveId(Integer.parseInt(moveId.getText()));
		item.setMinAmnt(Integer.parseInt(minAmnt.getText()));
		item.setMaxAmnt(Integer.parseInt(maxAmnt.getText()));
		item.setPaletteId(Integer.parseInt(paletteId.getText()));
		item.setActionType(actionType.getSelectionModel().getSelectedIndex());
	}
}
