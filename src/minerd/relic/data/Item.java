package minerd.relic.data;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.SiroFile;
import minerd.relic.fxml.ItemController;

public class Item extends GameData {
	private String name, description;
	private int itemId, buyPrice, sellPrice, itemType, spriteId, moveId, order, minAmnt, maxAmnt, paletteId, actionType;
	private boolean ai1, ai2, ai3;

	public Item(int index) {
		itemId = index;
		try{
			SiroFile data = (SiroFile) Rom.getInstance().getSystemSbin().getSubfile("itempara");
			BufferedDataHandler entry = data.getSegment("items/" + index).getData();
			name = data.getSegment("names/" + entry.parsePointer().getOffset()).getData().readString();
			buyPrice = entry.readInt();
			sellPrice = entry.readInt();
			itemType = entry.readUnsignedByte();
			spriteId = entry.readUnsignedByte();
			entry.skip(2);
			description = data.getSegment("descs/" + entry.parsePointer().getOffset()).getData().readString().replace("#n", "\n");
			ai1 = entry.readByte()!=0;
			ai2 = entry.readByte()!=0;
			ai3 = entry.readByte()!=0;
			entry.skip(1);
			moveId = entry.readShort();
			order = entry.readUnsignedByte();
			minAmnt = entry.readUnsignedByte();
			maxAmnt = entry.readUnsignedByte();
			paletteId = entry.readUnsignedByte();
			actionType = entry.readUnsignedByte();
			Cache.add("Item", index, this);
		} catch(IOException | InvalidPointerException e){
			e.printStackTrace();
		}
	}

	@Override
	public Region load() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/minerd/relic/fxml/item.fxml"));
		SplitPane dataPane = loader.load();
		ItemController controller = loader.getController();

		controller.load(this);
		return dataPane;
	}

	public void save() {
		try{
			SiroFile data = (SiroFile) Rom.getInstance().getSystemSbin().getSubfile("itempara");
			BufferedDataHandler entry = data.getSegment("items/" + itemId).getData();
			data.getSegment("names/" + entry.parsePointer().getOffset()).getData().writeString(name);
			entry.writeInt(buyPrice);
			entry.writeInt(sellPrice);
			entry.writeUnsignedByte(itemType);
			entry.writeUnsignedByte(spriteId);
			entry.skip(2);
			data.getSegment("descs/" + entry.parsePointer().getOffset()).getData().writeString(description.replace("\n", "#n"));
			entry.writeByte((byte) (ai1 ? 1 : 0));
			entry.writeByte((byte) (ai2 ? 1 : 0));
			entry.writeByte((byte) (ai3 ? 1 : 0));
			entry.skip(1);
			entry.writeShort((short) moveId);
			entry.writeUnsignedByte(order);
			entry.writeUnsignedByte(minAmnt);
			entry.writeUnsignedByte(maxAmnt);
			entry.writeUnsignedByte(paletteId);
			entry.writeUnsignedByte(actionType);
		} catch(IOException | InvalidPointerException e){
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(int buyPrice) {
		this.buyPrice = buyPrice;
	}

	public int getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(int sellPrice) {
		this.sellPrice = sellPrice;
	}

	public int getItemType() {
		return itemType;
	}

	public void setItemType(int itemType) {
		this.itemType = itemType;
	}

	public int getSpriteId() {
		return spriteId;
	}

	public void setSpriteId(int spriteID) {
		this.spriteId = spriteID;
	}

	public int getMoveId() {
		return moveId;
	}

	public void setMoveId(int moveId) {
		this.moveId = moveId;
	}

	public int getMinAmnt() {
		return minAmnt;
	}

	public void setMinAmnt(int minAmnt) {
		this.minAmnt = minAmnt;
	}

	public int getMaxAmnt() {
		return maxAmnt;
	}

	public void setMaxAmnt(int maxAmnt) {
		this.maxAmnt = maxAmnt;
	}

	public int getPaletteId() {
		return paletteId;
	}

	public void setPaletteId(int paletteID) {
		this.paletteId = paletteID;
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	public boolean isAi1() {
		return ai1;
	}

	public void setAi1(boolean ai1) {
		this.ai1 = ai1;
	}

	public boolean isAi2() {
		return ai2;
	}

	public void setAi2(boolean ai2) {
		this.ai2 = ai2;
	}

	public boolean isAi3() {
		return ai3;
	}

	public void setAi3(boolean ai3) {
		this.ai3 = ai3;
	}

}
