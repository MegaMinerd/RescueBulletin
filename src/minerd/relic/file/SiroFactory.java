package minerd.relic.file;

import java.io.IOException;
import java.nio.ByteBuffer;

import minerd.relic.file.SiroFile.SiroLayout;

public class SiroFactory {
	/**
	 * Reads data of fixed entry length from a pointer table
	 * 
	 * @param buffer    The data buffer read from
	 * @param parent    The SiroSegment that will store this data
	 * @param childSize The length of each entry
	 * @param childNum  The number of entries
	 * @param Pointer   A pointer to the table
	 **/
	@Deprecated
	private static void populateFromTable(BufferedDataHandler buffer, SiroSegment parent, int childSize, int childNum, Pointer table) throws IOException {
		for(int i = 0; i<childNum; i++){
			byte[] data = new byte[childSize];
			int offset = table.getOffset() + 4*i;
			buffer.seek(offset);
			buffer.read(data);
			parent.addChild(i + "", new SiroSegment(offset, new BufferedDataHandler(ByteBuffer.wrap(data))));
		}
	}

	/**
	 * Reads data of fixed entry length from a pointer table
	 * 
	 * @param buffer    The data buffer read from
	 * @param offset    The offset of the SIRO file
	 * @param childSize The length of each entry
	 * @param childNum  The number of entries
	 * @param Pointer   A pointer to the table
	 **/
	private static SiroSegment populateFromTable(BufferedDataHandler buffer, int offset, int childSize, int childNum, Pointer table) throws IOException {
		SiroSegment parent = new SiroSegment(table);
		buffer.seek(table.relativeTo(offset));
		for(int i = 0; i<childNum; i++){
			byte[] data = new byte[childSize];
			int dataOffset = table.getOffset() + 4*i;
			buffer.seek((new Pointer(dataOffset, true)).relativeTo(offset));
			Pointer dataPtr = buffer.parsePointer();
			buffer.seek(dataPtr.relativeTo(offset));
			buffer.read(data);
			parent.addChild(i + "", new SiroSegment(dataPtr, new BufferedDataHandler(ByteBuffer.wrap(data))));
		}
		return parent;
	}

	/**
	 * Reads data of fixed entry length without a pointer table
	 * 
	 * @param buffer    The data buffer read from
	 * @param offset    The offset of the SIRO file
	 * @param childSize The length of each entry
	 * @param childNum  The number of entries
	 * @param Pointer   A pointer to the list
	 **/
	private static SiroSegment populateFromList(BufferedDataHandler buffer, int offset, int childSize, int childNum, Pointer list) throws IOException {
		SiroSegment parent = new SiroSegment(list);
		buffer.seek(list.relativeTo(offset));
		for(int i = 0; i<childNum; i++){
			byte[] data = new byte[childSize];
			int dataOffset = list.getOffset() + 4*i;
			buffer.seek((new Pointer(dataOffset, true)).relativeTo(offset));
			Pointer dataPtr = buffer.parsePointer();
			buffer.seek(dataPtr.relativeTo(offset));
			buffer.read(data);
			parent.addChild(i + "", new SiroSegment(dataPtr, new BufferedDataHandler(ByteBuffer.wrap(data))));
		}
		return parent;
	}

	/**
	 * Reads data of variable entry length from a pointer table
	 * 
	 * @param buffer   The data buffer read from
	 * @param offset   The offset of the SIRO file
	 * @param childNum The number of entries
	 * @param Pointer  A pointer to the table, assumed to be immediately after the data
	 **/
	private static SiroSegment populateFromTable(BufferedDataHandler buffer, int offset, int childNum, Pointer table) throws IOException {
		SiroSegment parent = new SiroSegment(table);
		buffer.seek(table.relativeTo(offset));
		for(int i = 0; i<childNum; i++){
			int dataOffset = table.getOffset() + 4*i;
			buffer.seek((new Pointer(dataOffset, true)).relativeTo(offset));
			Pointer dataPtr = buffer.parsePointer();
			Pointer endPtr = i==childNum - 1 ? table : buffer.parsePointer();
			byte[] data = new byte[endPtr.getOffset() - dataPtr.getOffset()];
			buffer.seek(dataPtr.relativeTo(offset));
			buffer.read(data);
			parent.addChild(i + "", new SiroSegment(dataPtr, new BufferedDataHandler(ByteBuffer.wrap(data))));
		}
		return parent;
	}

	private static SiroSegment readStringList(BufferedDataHandler buffer, int offset, int start, int end, boolean isPadded) throws IOException {
		SiroSegment strings = new SiroSegment(start);
		buffer.seek(start);
		while(buffer.getFilePointer()<end){
			int off = buffer.getFilePointer() + offset;
			String str = buffer.readString();
			SiroSegment seg = new SiroSegment(off);
			seg.setData(new BufferedDataHandler(ByteBuffer.wrap(str.getBytes())));
			strings.addChild(off + "", seg);
			if(isPadded)
				buffer.skip((4 - (buffer.getFilePointer()%4))%4);
		}
		return strings;
	}

	//0306570 to 030F66B
	public static SiroFile buildItemSiro(BufferedDataHandler buffer, int offset) throws IOException {
		SiroSegment head = new SiroSegment(offset);
		buffer.seek(4);
		int dataStart = buffer.parsePointer().relativeTo(offset).getOffset();
		buffer.skip(8);
		SiroSegment descs = readStringList(buffer, offset + 16, 16, dataStart, false);
		head.addChild("descs", descs);
		SiroSegment items = new SiroSegment(dataStart);
		int index = 0;
		byte[] data = new byte[0x20];
		while(true){
			buffer.skip(3);
			int test = buffer.readByte();
			buffer.seek(buffer.getFilePointer() - 4);
			if(test==8 || test==9){
				int off = buffer.getFilePointer();
				buffer.read(data);
				items.addChild(index + "", new SiroSegment(off, new BufferedDataHandler(ByteBuffer.wrap(data))));
				index++;
			} else{
				break;
			}
		}
		head.addChild("items", items);
		SiroSegment names = readStringList(buffer, offset + buffer.getFilePointer(), buffer.getFilePointer(), buffer.length(), true);
		head.addChild("names", names);
		return new SiroFile(offset, head, SiroLayout.ITEM);
	}

	//357B88 to 360BF4
	public static SiroFile buildPokemonSiro(BufferedDataHandler buffer, int offset) throws IOException {
		SiroSegment head = new SiroSegment(offset);
		buffer.seek(16);
		SiroSegment pokemon = new SiroSegment(offset + 16);
		int index = 0;
		byte[] data = new byte[0x48];
		while(true){
			buffer.skip(3);
			int test = buffer.readByte();
			buffer.seek(buffer.getFilePointer() - 4);
			if(test==8 || test==9){
				int off = buffer.getFilePointer();
				buffer.read(data);
				pokemon.addChild(index + "", new SiroSegment(off, new BufferedDataHandler(ByteBuffer.wrap(data))));
				index++;
			} else{
				break;
			}
		}
		head.addChild("pokemon", pokemon);
		SiroSegment strings = readStringList(buffer, offset + buffer.getFilePointer(), buffer.getFilePointer(), buffer.length(), true);
		head.addChild("strings", strings);
		return new SiroFile(offset, head, SiroLayout.POKEMON);
	}

	//360BF4 to 37333F
	public static SiroFile buildMoveSiro(BufferedDataHandler buffer, int offset) throws IOException {
		SiroSegment head = new SiroSegment(offset);
		//Parse header
		buffer.seek(4);
		Pointer footerPtr = buffer.parsePointer();

		//Parse footer
		buffer.seek(footerPtr.relativeTo(offset));
		Pointer movePtr = buffer.parsePointer();
		Pointer learnsetPtr = buffer.parsePointer();

		//Parse learnsets
		byte[] data;
		SiroSegment learnsets = new SiroSegment(learnsetPtr.getOffset());
		for(int i = 0; i<424; i++){
			buffer.seek(learnsetPtr.relativeTo(offset).getOffset() + 8*i);
			int off1 = buffer.parsePointer().getOffset();
			int off2 = buffer.parsePointer().getOffset();
			int off3 = buffer.parsePointer().getOffset();
			SiroSegment learnset = new SiroSegment(off1, null);
			buffer.seek(off1);
			data = new byte[off2 - off1];
			buffer.read(data);
			learnset.addChild("lv", new SiroSegment(off1, new BufferedDataHandler(ByteBuffer.wrap(data))));
			buffer.seek(off2);
			data = new byte[off3 - off2];
			buffer.read(data);
			learnset.addChild("tm", new SiroSegment(off2, new BufferedDataHandler(ByteBuffer.wrap(data))));
			learnsets.addChild(i + "", learnset);
		}
		head.addChild("learnsets", learnsets);

		//Parse moves
		buffer.seek(movePtr.relativeTo(offset));
		SiroSegment moves = new SiroSegment(movePtr.getOffset());
		data = new byte[0x48];
		for(int i = 0; i<413; i++){
			int off = buffer.getFilePointer();
			buffer.read(data);
			moves.addChild(i + "", new SiroSegment(off, new BufferedDataHandler(ByteBuffer.wrap(data))));
		}
		head.addChild("moves", moves);

		//Parse strings
		SiroSegment strings = readStringList(buffer, offset + buffer.getFilePointer(), buffer.getFilePointer(), learnsetPtr.getOffset(), true);
		head.addChild("strings", strings);

		return new SiroFile(offset, head, SiroLayout.MOVE);
	}

	public static SiroFile buildDungeonSiro(BufferedDataHandler buffer, int offset) throws IOException {
		SiroSegment head = new SiroSegment(offset);
		//Parse header
		buffer.seek(4);
		Pointer footerPtr = buffer.parsePointer();

		//Parse footer
		buffer.seek(footerPtr.relativeTo(offset));
		Pointer mainPtr = buffer.parsePointer();
		Pointer layoutPtr = buffer.parsePointer();
		Pointer lootPtr = buffer.parsePointer();
		Pointer spawnPtr = buffer.parsePointer();
		Pointer trapPtr = buffer.parsePointer();

		head.addChild("main", populateFromTable(buffer, offset, 0x10, 0x0727, mainPtr));
		head.addChild("layout", populateFromList(buffer, offset, 0x1C, 0x06E4, layoutPtr));
		head.addChild("loot", populateFromTable(buffer, offset, 0xB2, lootPtr));
		head.addChild("spawn", populateFromTable(buffer, offset, 0x0347, spawnPtr));
		head.addChild("trap", populateFromTable(buffer, offset, 0x94, trapPtr));

		return new SiroFile(offset, head, SiroLayout.DUNGEON);
	}

	public static SiroFile buildGraphicListSiro(BufferedDataHandler buffer, int offset) throws IOException {
		SiroSegment head = new SiroSegment(offset);
		//Parse header
		buffer.seek(4);
		Pointer footerPtr = buffer.parsePointer();

		//Parse footer
		buffer.seek(footerPtr.relativeTo(offset));
		Pointer tilePtr = buffer.parsePointer();
		Pointer palettePtr = buffer.parsePointer();

		byte[] data = new byte[palettePtr.getOffset() - tilePtr.getOffset()];
		buffer.seek(tilePtr.relativeTo(offset));
		buffer.read(data);
		head.addChild("tile", new SiroSegment(tilePtr, new BufferedDataHandler(ByteBuffer.wrap(data))));

		data = new byte[footerPtr.getOffset() - palettePtr.getOffset()];
		buffer.seek(palettePtr.relativeTo(offset));
		buffer.read(data);
		head.addChild("palette", new SiroSegment(tilePtr, new BufferedDataHandler(ByteBuffer.wrap(data))));

		return new SiroFile(offset, head, SiroLayout.GRAPHIC_LIST);
	}
	
	public static SiroFile buildGraphicTableSiro(BufferedDataHandler buffer, int offset, int childSize, int childNum) throws IOException {
		SiroSegment head = new SiroSegment(offset);
		//Parse header
		buffer.seek(4);
		Pointer footerPtr = buffer.parsePointer();

		//Parse footer
		buffer.seek(footerPtr.relativeTo(offset));
		Pointer tilePtr = buffer.parsePointer();
		Pointer palettePtr = buffer.parsePointer();

		byte[] data = new byte[palettePtr.getOffset() - tilePtr.getOffset()];
		buffer.seek(tilePtr.relativeTo(offset));
		buffer.read(data);
		head.addChild("tile", populateFromTable(buffer, offset, childSize, childNum, tilePtr));

		data = new byte[footerPtr.getOffset() - palettePtr.getOffset()];
		buffer.seek(palettePtr.relativeTo(offset));
		buffer.read(data);
		head.addChild("palette", new SiroSegment(tilePtr, new BufferedDataHandler(ByteBuffer.wrap(data))));

		return new SiroFile(offset, head, SiroLayout.GRAPHIC_TABLE);
	}

	//1E76170 to 1E77297
	//Nearly identical to pokemon sprite format
	//REDO
	public static SiroFile buildItemSpriteSiro(BufferedDataHandler buffer, int offset) throws IOException {
		SiroSegment head = new SiroSegment(offset);
		buffer.seek(4);
		buffer.seek(buffer.parsePointer().relativeTo(offset)); //1E77284
		Pointer p1 = buffer.parsePointer(); //1E771A0, vestige of frame data pointer table
		Pointer p2 = buffer.parsePointer(); //1E77220, vestige of facing directions
		buffer.skip(4); //Always 1
		Pointer p3 = buffer.parsePointer(); //1E77224, sprite pointer table

		SiroSegment fdata = new SiroSegment(p1);
		//1E76180-1E7634C
		populateFromTable(buffer, fdata, 0x14, 0x18, p1.relativeTo(offset));
		head.addChild("fdata", fdata);

		SiroSegment anim = new SiroSegment(p2);
		buffer.seek(p2.getOffset() - offset); //1E77200
		//1E76360-1E76408
		populateFromTable(buffer, anim, 0x18, 0x8, buffer.parsePointer().relativeTo(offset));
		head.addChild("anim", anim);

		SiroSegment frame = new SiroSegment(p3);
		//1E764A0-1E77190
		populateFromTable(buffer, anim, 0x10, 0x18, p3.relativeTo(offset));
		head.addChild("frame", frame);

		for(SiroSegment child : frame.getChildren().values()){
			BufferedDataHandler data = child.getData();
			data.seek(0);
			buffer.seek(data.parsePointer().relativeTo(offset));
			byte[] img = new byte[0x80];
			int off = buffer.getFilePointer();
			buffer.read(img);
			child.addChild("image", new SiroSegment(off, new BufferedDataHandler(ByteBuffer.wrap(img))));
		}

		return null;//new SiroFile(offset, head);
	}

	//Header pointing to footer
	//A: Frame data
	//B: Animation data
	//C: Tile data
	//D: A*
	//E: Body part displacement data
	//F: B*
	//G: F* (facing directions)
	//H: C*
	//Footer:
	//D*
	//G*
	//int F.length/8
	//H*
	//E*
	//TODO: public static SpriteSiro(){}
}