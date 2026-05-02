package minerd.relic.file;

import java.io.IOException;
import java.nio.ByteBuffer;

import minerd.relic.file.SiroFile.SiroLayout;
import minerd.relic.file.SiroSegment.DataType;

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
	private static SiroSegment populateFromTable(BufferedDataHandler buffer, int offset, int childSize, int childNum, Pointer table, DataType type) throws IOException {
		SiroSegment parent = new SiroSegment(table);
		buffer.seek(table.relativeTo(offset));
		for(int i = 0; i<childNum; i++){
			byte[] data = new byte[childSize];
			int dataOffset = table.getOffset() + 4*i;
			buffer.seek((new Pointer(dataOffset, true)).relativeTo(offset));
			Pointer dataPtr = buffer.parsePointer();
			buffer.seek(dataPtr.relativeTo(offset));
			buffer.read(data);
			parent.addChild(i + "", new SiroSegment(dataPtr, new BufferedDataHandler(ByteBuffer.wrap(data)), type));
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
	//TODO: Resulting offsets are not absolute
	private static SiroSegment populateFromList(BufferedDataHandler buffer, int offset, int childSize, int childNum, Pointer list, DataType type) throws IOException {
		SiroSegment parent = new SiroSegment(list);
		buffer.seek(list.relativeTo(offset));
		for(int i = 0; i<childNum; i++){
			byte[] data = new byte[childSize];
			int dataOffset = list.getOffset() + childSize*i;
			buffer.seek((new Pointer(dataOffset, true)).relativeTo(offset));
			int dataPtr = buffer.getFilePointer() + offset;
			buffer.read(data);
			parent.addChild(i + "", new SiroSegment(dataPtr, new BufferedDataHandler(ByteBuffer.wrap(data)), type));
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
	private static SiroSegment populateFromTable(BufferedDataHandler buffer, int offset, int childNum, Pointer table, DataType type) throws IOException {
		SiroSegment parent = new SiroSegment(table);
		buffer.seek(table.relativeTo(offset));
		for(int i = 0; i<childNum; i++){
			int dataOffset = table.getOffset() + 4*i;
			buffer.seek((new Pointer(dataOffset, true)).relativeTo(offset));
			Pointer dataPtr = buffer.parsePointer();
			try {
				Pointer endPtr = i==childNum - 1 ? table : buffer.parsePointer();
				byte[] data = new byte[endPtr.getOffset() - dataPtr.getOffset()];
				buffer.seek(dataPtr.relativeTo(offset));
				buffer.read(data);
				parent.addChild(i + "", new SiroSegment(dataPtr, new BufferedDataHandler(ByteBuffer.wrap(data)), type));
			}catch(InvalidPointerException e) {
				//Perhaps this file was followed by pksdir0
				break;
			}
		}
		return parent;
	}

	/**
	 * Reads a list of strings without a pointer table
	 * 
	 * @param buffer   The data buffer read from
	 * @param offset   The offset of the SIRO file
	 * @param start    The start of the string data
	 * @param end      The end of the string data
	 * @param isPadded Whether each string is padded to align to 4 bytes
	 **/
	private static SiroSegment readStringList(BufferedDataHandler buffer, int offset, int start, int end, boolean isPadded) throws IOException {
		SiroSegment strings = new SiroSegment(start + offset);
		buffer.seek(start);
		while(buffer.getFilePointer()<end){
			int off = buffer.getFilePointer() + offset;
			String str = buffer.readString();
			SiroSegment seg = new SiroSegment(off, DataType.STRING);
			seg.setData(new BufferedDataHandler(ByteBuffer.wrap(str.getBytes())));
			strings.addChild(off + "", seg);
			if(isPadded)
				buffer.skip((4 - (buffer.getFilePointer()%4))%4);
		}
		return strings;
	}
	
	public static SiroFile buildBasicSiro(BufferedDataHandler buffer, int offset, String typeName) throws IOException {
		DataType type = DataType.valueOf(typeName);
		
		//Parse header
		buffer.seek(4);
		Pointer dataPtr = buffer.parsePointer();

		buffer.seek(dataPtr.relativeTo(offset));
		byte[] data = new byte[buffer.length()-0x10];
		buffer.seek(dataPtr.relativeTo(offset));
		buffer.read(data);
		SiroSegment head = new SiroSegment(offset);
		head.addChild("data", new SiroSegment(offset+0x10, new BufferedDataHandler(ByteBuffer.wrap(data)), type));
		
		return new SiroFile(offset, head, SiroLayout.BASIC);
	}
	
	public static SiroFile buildVarTableSiro(BufferedDataHandler buffer, int offset, String typeName) throws IOException {
		DataType type = DataType.valueOf(typeName);
		
		//Parse header
		buffer.seek(4);
		Pointer tablePtr = buffer.parsePointer();

		SiroSegment head = populateFromTable(buffer, offset, (buffer.length() - tablePtr.relativeTo(offset).getOffset())/4, tablePtr, type);
		
		return new SiroFile(offset, head, SiroLayout.VARIABLE_LENGTH_TABLE);
	}

	//0306570 to 030F66B
	public static SiroFile buildItemSiro(BufferedDataHandler buffer, int offset) throws IOException {
		SiroSegment head = new SiroSegment(offset);
		buffer.seek(4);
		int dataStart = buffer.parsePointer().relativeTo(offset).getOffset();
		buffer.skip(8);
		//Read description section of the file
		SiroSegment descs = readStringList(buffer, offset, 16, dataStart, false);
		head.addChild("descs", descs);
		SiroSegment items = new SiroSegment(dataStart + offset);
		int index = 0;
		//Detect end of data by testing if the next 4 bytes match pointer format.
		//There should be a 0x08 or 0x09 iff it is a pointer, since the following data is strings.
		//If there is a pointer, read data for an item. Otherwise, move on to the names section.
		while(true){
			byte[] data = new byte[0x20];
			buffer.skip(3);
			int test = buffer.readByte();
			buffer.seek(buffer.getFilePointer() - 4);
			if(test==8 || test==9){
				int off = buffer.getFilePointer();
				buffer.read(data);
				items.addChild(index + "", new SiroSegment(off + offset, new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.ITEM));
				index++;
			} else{
				break;
			}
		}
		head.addChild("items", items);
		//Read names section of the file
		SiroSegment names = readStringList(buffer, offset, buffer.getFilePointer(), buffer.length(), true);
		head.addChild("names", names);
		return new SiroFile(offset, head, SiroLayout.ITEM);
	}

	//357B88 to 360BF4
	public static SiroFile buildPokemonSiro(BufferedDataHandler buffer, int offset) throws IOException {
		SiroSegment head = new SiroSegment(offset);
		buffer.seek(16);
		SiroSegment pokemon = new SiroSegment(offset + 16);
		int index = 0;
		//Detect end of data by testing if the next 4 bytes match pointer format.
		//There should be a 0x08 or 0x09 iff it is a pointer, since the following data is strings.
		//If there is a pointer, read data for a pokemon. Otherwise, move on to the names section.
		while(true){
			byte[] data = new byte[0x48];
			buffer.skip(3);
			int test = buffer.readByte();
			buffer.seek(buffer.getFilePointer() - 4);
			if(test==8 || test==9){
				int off = buffer.getFilePointer();
				buffer.read(data);
				pokemon.addChild(index + "", new SiroSegment(off + offset, new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.POKEMON));
				index++;
			} else{
				break;
			}
		}
		head.addChild("pokemon", pokemon);
		//Read strings section of the file
		//TODO: figure out how to handle duplicates
		SiroSegment strings = readStringList(buffer, offset, buffer.getFilePointer(), buffer.length(), true);
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
			//Start of levelup moves
			Pointer p1 = buffer.parsePointer();
			//End of levelup moves
			//Start of tm moves
			Pointer p2 = buffer.parsePointer();
			//End of tm moves
			//For last entry, will be a pointer to a move data entry, but that still shows the end of this entry.
			Pointer p3 = buffer.parsePointer();
			int off1 = p1==null ? 0 : p1.relativeTo(offset).getOffset();
			int off2 = p2==null ? 0 : p2.relativeTo(offset).getOffset();
			int off3 = p3==null ? 0 : p3.relativeTo(offset).getOffset();
			SiroSegment learnset = new SiroSegment(off1, null, DataType.UNKNOWN);
			if(p1==null){
				learnset.addChild("lv", null);
			} else{
				buffer.seek(off1);
				data = new byte[off2 - off1];
				buffer.read(data);
				learnset.addChild("lv", new SiroSegment(off1, new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.LEARNSET));
			}
			if(p2==null){
				learnset.addChild("tm", null);
			} else{
				buffer.seek(off2);
				data = new byte[off3 - off2];
				buffer.read(data);
				learnset.addChild("tm", new SiroSegment(off2, new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.LEARNSET));
				learnsets.addChild(i + "", learnset);
			}
		}
		head.addChild("learnsets", learnsets);

		//Parse moves
		buffer.seek(movePtr.relativeTo(offset));
		SiroSegment moves = new SiroSegment(movePtr.getOffset());
		for(int i = 0; i<413; i++){
			data = new byte[0x24];
			int off = buffer.getFilePointer();
			buffer.read(data);
			moves.addChild(i + "", new SiroSegment(off + offset, new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.MOVE));
		}
		head.addChild("moves", moves);

		//Parse strings
		SiroSegment strings = readStringList(buffer, offset, buffer.getFilePointer(), learnsetPtr.relativeTo(offset).getOffset(), true);
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

		head.addChild("main", populateFromTable(buffer, offset, 0x40, mainPtr, DataType.DUNGEON_MAIN));
		head.addChild("layout", populateFromList(buffer, offset, 0x1C, 0x06E4, layoutPtr, DataType.FLOOR_LAYOUT));
		head.addChild("loot", populateFromTable(buffer, offset, 0xB2, lootPtr, DataType.LOOT_TABLE));
		head.addChild("spawn", populateFromTable(buffer, offset, 0x0347, spawnPtr, DataType.SPAWN_TABLE));
		head.addChild("trap", populateFromTable(buffer, offset, 0x94, trapPtr, DataType.TRAP_LIST));

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

		byte[] data = new byte[(palettePtr==null ? footerPtr.getOffset() : palettePtr.getOffset()) - tilePtr.getOffset()];
		buffer.seek(tilePtr.relativeTo(offset));
		buffer.read(data);
		head.addChild("tile", new SiroSegment(tilePtr, new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.GRAPHICS));

		if(palettePtr!=null) {
			data = new byte[footerPtr.getOffset() - palettePtr.getOffset()];
			buffer.seek(palettePtr.relativeTo(offset));
			buffer.read(data);
			head.addChild("palette", new SiroSegment(palettePtr, new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.PALETTE));
		}

		return new SiroFile(offset, head, SiroLayout.GRAPHIC_LIST);
	}

	public static SiroFile buildGraphicTableSiro(BufferedDataHandler buffer, int offset, String childSizeStr, String childNumStr) throws IOException {
		int childSize = Integer.parseInt(childSizeStr, 16);
		int childNum = Integer.parseInt(childNumStr, 16);
		
		SiroSegment head = new SiroSegment(offset);
		//Parse header
		buffer.seek(4);
		Pointer footerPtr = buffer.parsePointer();

		//Parse footer
		buffer.seek(footerPtr.relativeTo(offset));
		Pointer tilePtr = buffer.parsePointer();
		Pointer palettePtr = buffer.parsePointer();

		head.addChild("tile", populateFromTable(buffer, offset, childSize, childNum, tilePtr, DataType.GRAPHICS));

		if(palettePtr!=null) {
			byte[] data = new byte[footerPtr.getOffset() - palettePtr.getOffset()];
			buffer.seek(palettePtr.relativeTo(offset));
			buffer.read(data);
			head.addChild("palette", new SiroSegment(tilePtr, new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.PALETTE));
		}

		return new SiroFile(offset, head, SiroLayout.GRAPHIC_TABLE);
	}

	public static BufferedDataHandler buildPaletteTableSiro(BufferedDataHandler buffer, int offset) throws IOException {
		SiroSegment head = new SiroSegment(offset);
		//Parse header
		buffer.seek(4);
		Pointer footerPtr = buffer.parsePointer();
		int footer = footerPtr.relativeTo(offset).getOffset();

		//Parse footer
		buffer.seek(footer);

		head.addChild("palette", populateFromTable(buffer, offset, (buffer.length()-footer)/4, footerPtr, DataType.PALETTE));
	

		return new SiroFile(offset, head, SiroLayout.PALETTE_TABLE);	
	}

	//1E76170 to 1E77297
	//Nearly identical to pokemon sprite format
	//REDO
	@Deprecated
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

	public static SiroFile buildGlyphTableSiro(BufferedDataHandler buffer, int offset) throws IOException {
		SiroSegment head = new SiroSegment(offset);
		buffer.seek(4);
		buffer.seek(buffer.parsePointer().relativeTo(offset));
		int count = buffer.readInt();
		Pointer tablePtr = buffer.parsePointer();
		buffer.seek(tablePtr.relativeTo(offset));

		for(int i=0; i<count; i++) {
			byte[] meta = new byte[12];
			buffer.seek(tablePtr.relativeTo(offset).getOffset() + i * 12);
			Pointer metaPtr = buffer.parsePointer();
			buffer.seek(buffer.getFilePointer()-4);
			buffer.read(meta);
			SiroSegment metaSeg = new SiroSegment(buffer.getFilePointer()+offset-12, new BufferedDataHandler(ByteBuffer.wrap(meta)),DataType.METADATA);
			buffer.seek(metaPtr.relativeTo(offset));
			byte[] data = new byte[72];
			buffer.read(data);
			SiroSegment dataSeg = new SiroSegment(buffer.getFilePointer()+offset-72, new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.GLYPH);
			metaSeg.addChild("data", dataSeg);
			head.addChild("" + i, metaSeg);
		}
		
		return new SiroFile(offset, head, SiroLayout.GLYPH_TABLE);
	}

	//I have no idea what this file is supposed to be. It's almost identical to the glyph tables but has different metadata and irregular entry length.
	public static BufferedDataHandler buildBanfontTableSiro(BufferedDataHandler buffer, int offset) throws IOException {
		SiroSegment head = new SiroSegment(offset);
		buffer.seek(4);
		buffer.seek(buffer.parsePointer().relativeTo(offset));
		Pointer tablePtr = buffer.parsePointer();
		int count = buffer.readInt();
		buffer.seek(tablePtr.relativeTo(offset));

		for(int i=0; i<count; i++) {
			//Read metadata
			buffer.seek(tablePtr.relativeTo(offset).getOffset() + i * 8);
			Pointer dataPtr = buffer.parsePointer();
			buffer.seek(buffer.getFilePointer()-4);
			byte[] meta = new byte[8];
			buffer.read(meta);
			SiroSegment metaSeg = new SiroSegment(buffer.getFilePointer()+offset-8, new BufferedDataHandler(ByteBuffer.wrap(meta)),DataType.METADATA);
			
			//Read data
			byte[] data = new byte[(i==(count-1) ? tablePtr.getOffset() : buffer.parsePointer().getOffset())-dataPtr.getOffset()];
			buffer.seek(dataPtr.relativeTo(offset));
			buffer.read(data);
			SiroSegment dataSeg = new SiroSegment(buffer.getFilePointer()+offset-data.length, new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.UNKNOWN);
			metaSeg.addChild("data", dataSeg);
			head.addChild("" + i, metaSeg);
		}
		
		return new SiroFile(offset, head, SiroLayout.BANFONT_TABLE);
	}

	//Simple Sprite                 Composite Sprite
	//Header pointing to footer     Header pointing to footer
	//A: Frame data                 A: Frame data                       Metadata about a frame of animation; ends with extra u32 in Simple; followed by FF row
	//                                                                  u16: FF/ID, u16: always 0000?, u8: ?, u8: ?, u8: ?, u8: ?, u8: ?, u8: ?
	//B: Animation data             B: Animation data                   Animation data
	//C: Tile Data                  C: Tile Data                        Main graphic tile data
	//D: Palette                    D: -                                Single palette
	//E: A*                         E: A*                               A pointer to each entry of the frame data
	//F: -                          F: Body part displacement data		Instructions on where the component is in the frame. Used for sprites that aren't square
	//G: B*                         G: B*                               8 pointers to an entry in animation data, 1 per facing direction
	//H: G*                         H: G*						        A pointer to each entry of the direction data
	//I: -                          I: C*                               A pointer to each entry of the tile data
	//Footer:						Footer:
	//E*                            E*
	//H*                            H*
	//H count                       H count
	//null                          I*
	//null                          F*
	//C*                            -
	//D*                            -
	public static BufferedDataHandler buildSpriteSiro(BufferedDataHandler buffer, int offset, String version) throws IOException {
		SiroSegment head = new SiroSegment(offset);
		
		//Parse header and footer
		buffer.seek(4);
		Pointer footerPtr = buffer.parsePointer();
		buffer.seek(footerPtr.relativeTo(offset));
		Pointer frameTblPtr = buffer.parsePointer();
		Pointer animDirTblPtr = buffer.parsePointer();
		int spriteDirCount = buffer.readInt();
		if(version.equals("Simple"))
			buffer.skip(8);
		//This will be the 6th value for simple or the 4th value for composite, but always the tile data. (extra table in composite to be dealt with later)
		Pointer spritePtr = buffer.parsePointer();
		//Read a 7th value if it is simple
		Pointer palPtr = version.equals("Simple") ? buffer.parsePointer() : null;
		//If this is composite, the previous line did not advance in the data, therefore, it is pointing at the 5th value in the footer
		Pointer layoutPtr = version.equals("Simple") ? null : buffer.parsePointer();
		
		//Parse E, A
		buffer.seek(frameTblPtr.relativeTo(offset));
		int frameCount = version.equals("Simple") ? (footerPtr.getOffset() - frameTblPtr.getOffset() - spriteDirCount * 36)/4 : (layoutPtr.getOffset() - frameTblPtr.getOffset())/4;;
		SiroSegment frameSeg = populateFromTable(buffer, offset, 20, frameCount, frameTblPtr, DataType.METADATA);
		if(version.equals("Simple")) {
			byte[] num = new byte[4];
			buffer.read(num);
			frameSeg.addChild("unkInt", new SiroSegment(buffer.getFilePointer()-4+offset, new BufferedDataHandler(ByteBuffer.wrap(num))));
		}
		head.addChild("frames", frameSeg);
		
		//Parse H, G, B
		SiroSegment sequenceSeg = new SiroSegment(animDirTblPtr.getOffset(), DataType.GENERIC);
		for(int h=0; h<spriteDirCount; h++) {
			buffer.seek(animDirTblPtr.relativeTo(offset).getOffset() + h*4);
			Pointer animDirPtr = buffer.parsePointer();
			SiroSegment animSeg = new SiroSegment(animDirPtr.getOffset(), DataType.GENERIC);
			for(int g=0; g<8; g++) {
				buffer.seek(animDirPtr.relativeTo(offset).getOffset() + g*4);
				Pointer animPtr = buffer.parsePointer();
				buffer.seek(animPtr.relativeTo(offset));
				byte[] data = new byte[12];
				buffer.read(data);
				frameSeg.addChild("unkInt", new SiroSegment(buffer.getFilePointer()-4+offset, new BufferedDataHandler(ByteBuffer.wrap(data))));
				SiroSegment dataSeg = new SiroSegment(buffer.getFilePointer()-12+offset, new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.METADATA);
				animSeg.addChild("dir" + g, dataSeg);
			}
			sequenceSeg.addChild("anim" + h, animSeg);
		}
		head.addChild("sequences", sequenceSeg);
		
		if(version.equals("Simple")) {
			//Parse C
			byte[] data = new byte[palPtr.getOffset()-spritePtr.getOffset()];
			buffer.seek(spritePtr.relativeTo(offset));
			buffer.read(data);
			SiroSegment tileSeg = new SiroSegment(spritePtr.getOffset(), new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.GRAPHICS);
			head.addChild("tiles", tileSeg);
			
			//Parse D
			data = new byte[64];
			buffer.seek(palPtr.relativeTo(offset));
			buffer.read(data);
			SiroSegment palSeg = new SiroSegment(palPtr.getOffset(), new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.PALETTE);
			head.addChild("palette", palSeg);
		} else {
			//Parse I, C
			int spriteCount = (footerPtr.getOffset()-spritePtr.getOffset())/4;
			SiroSegment spriteSeg = new SiroSegment(spritePtr.getOffset(), DataType.GENERIC);
			for(int i=0; i<spriteCount; i++) {
				buffer.seek(spritePtr.relativeTo(offset).getOffset() + i*4);
				Pointer metaPtr = buffer.parsePointer();
				buffer.seek(metaPtr.relativeTo(offset));
				Pointer dataPtr = buffer.parsePointer();
				int metaSize;
				if(dataPtr==null) {
					//This is a composite image
					
					//Skip the rest of this entry
					buffer.skip(4);
					metaSize=8;
					while(true) {
						Pointer tempPtr = buffer.parsePointer();
						int tempInt = buffer.readInt();
						//There is another meta entry, so count it.
						metaSize+=8;
						if(tempPtr!=null) {
							if(dataPtr==null) 
								//The first section of graphics is found, note it.
								dataPtr = tempPtr;
						} else if(tempInt==0)
							//Terminated by null entry
							break;
					}
				} else {
					//This is a simple image
					metaSize=16;
				}
				byte[] meta = new byte[metaSize];
				buffer.seek(buffer.getFilePointer()-4);
				buffer.read(meta);
				SiroSegment metaSeg = new SiroSegment(metaPtr.getOffset(), new BufferedDataHandler(ByteBuffer.wrap(meta)), DataType.METADATA);
				if(dataPtr!=null) {
					//Moltres, Gligar, and Milotic have metadata segments that don't point to any graphics.
					byte[] data = new byte[metaPtr.getOffset() - dataPtr.getOffset()];
					buffer.seek(dataPtr.relativeTo(offset));
					buffer.read(data);
					SiroSegment dataSeg = new SiroSegment(dataPtr.getOffset(), new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.GRAPHICS);
					metaSeg.addChild("data", dataSeg);
				}
				spriteSeg.addChild("sprite"+i, metaSeg);
			}
			head.addChild("sprites", spriteSeg);
			
			//Parse F
			byte[] data = new byte[head.getDescendant("sequences/anim0").getOffset() - layoutPtr.getOffset()];
			buffer.seek(layoutPtr.relativeTo(offset));
			buffer.read(data);
			SiroSegment layoutSeg = new SiroSegment(layoutPtr.getOffset(), new BufferedDataHandler(ByteBuffer.wrap(data)), DataType.METADATA);
			head.addChild("arrangements", layoutSeg);
		}
		
		return new SiroFile(offset, head, version.equals("Simple") ? SiroLayout.SIMPLE_SPRITE : SiroLayout.COMPOSITE_SPRITE);
	}
}