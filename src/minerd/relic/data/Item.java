package minerd.relic.data;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.fxml.ItemController;
import minerd.relic.util.RrtOffsetList;

public class Item extends GameData {
	private String name, description;
	private int itemId, offset, buyPrice, sellPrice, itemType, spriteId, moveId, order, minAmnt, maxAmnt, paletteId, actionType;
	private boolean ai1, ai2, ai3;

	public Item(int index) {
		itemId = index;
		try{
			RomFile rom = Rom.getAll();
			rom.seek(RrtOffsetList.itemOffset + 4);
			rom.seek(rom.parsePointer());
			rom.skip(index*0x20);
			offset = rom.getFilePointer();
			name = rom.readString(rom.parsePointer());
			buyPrice = rom.readInt();
			sellPrice = rom.readInt();
			itemType = rom.readUnsignedByte();
			spriteId = rom.readUnsignedByte();
			rom.skip(2);
			description = rom.readString(rom.parsePointer()).replace("#n", "\n");
			ai1 = rom.readByte()!=0;
			ai2 = rom.readByte()!=0;
			ai3 = rom.readByte()!=0;
			rom.skip(1);
			moveId = rom.readShort();
			order = rom.readUnsignedByte();
			minAmnt = rom.readUnsignedByte();
			maxAmnt = rom.readUnsignedByte();
			paletteId = rom.readUnsignedByte();
			actionType = rom.readUnsignedByte();
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

	public void save(RomFile rom) {
		try{
			rom.seek(offset);
			rom.writeString(name, rom.parsePointer());
			rom.writeInt(buyPrice);
			rom.writeInt(sellPrice);
			rom.writeUnsignedByte(itemType);
			rom.writeUnsignedByte(spriteId);
			rom.skip(2);
			rom.writeString(description.replace("\n", "#n"), rom.parsePointer());
			rom.writeByte((byte) (ai1 ? 1 : 0));
			rom.writeByte((byte) (ai2 ? 1 : 0));
			rom.writeByte((byte) (ai3 ? 1 : 0));
			rom.skip(1);
			rom.writeShort((short) moveId);
			rom.writeUnsignedByte(order);
			rom.writeUnsignedByte(minAmnt);
			rom.writeUnsignedByte(maxAmnt);
			rom.writeUnsignedByte(paletteId);
			rom.writeUnsignedByte(actionType);
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
