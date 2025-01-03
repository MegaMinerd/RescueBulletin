package minerd.relic.file;

import java.io.IOException;

import minerd.relic.file.SiroFile.SiroLayout;

public class SiroPacker {

	/**
	 * Reads a list of strings without a pointer table
	 * 
	 * @param offset     The offset of the string list.
	 * @param strs       The segment with the strings as children.
	 * @param data       The segment that should have pointers updated. Must have sequential children names if present.
	 * @param isReversed Whether the first entry is last in the list.
	 * @param hasRepeats Whether entries may point to the same string. TODO
	 * @param ptroff     The offsets in the data entry that should have pointers updated.
	 **/
	private static BufferedDataHandler writeStringList(int offset, SiroSegment strs, SiroSegment data, boolean isReversed, boolean hasRepeats, int ... ptroff) throws IOException {
		int totalLength = 0;
		//First pass to tally size
		for(int i = 0; i<data.getChildren().size(); i++){
			BufferedDataHandler entry = data.getChild("" + i).getData();
			for(int j = 0; j<ptroff.length; j++){
				entry.seek(ptroff[j]);
				BufferedDataHandler str = strs.getChild("" + entry.parsePointer().getOffset()).getData();
				str.seek(0);
				totalLength += Math.ceil((str.readString().length() + 1)/4.0)*4;
			}
		}

		BufferedDataHandler out = new BufferedDataHandler(totalLength);
		out.seek(0);
		//Second pass to write strings
		for(int i = 0; i<data.getChildren().size(); i++){
			BufferedDataHandler entry = data.getChild("" + (isReversed ? data.getChildren().size() - i - 1 : i)).getData();
			for(int j = 0; j<ptroff.length; j++){
				entry.seek(ptroff[j]);
				String strname = "" + entry.parsePointer().getOffset();
				SiroSegment strseg = strs.getChild(strname);
				BufferedDataHandler str = strseg.getData();
				//Update strs. Removing the old copy is unsafe if hasRepeats.
				if(!hasRepeats)
					strs.removeChild(strname);
				strs.addChild("" + offset + out.getFilePointer(), strseg);
				//Update entry pointer
				entry.seek(ptroff[j]);
				entry.writePointer(new Pointer(offset + out.getFilePointer(), true));
				//Write string to list
				str.seek(0);
				out.writeString(str.readString());
				while(out.getFilePointer()%4!=0){
					out.writeByte((byte) 0);
				}
			}
		}

		return out;
	}

	public static BufferedDataHandler pack(SiroFile siroFile, SiroLayout layout) throws IOException {
		switch(layout){
			case BASIC:
				return SiroPacker.packBasicSiro(siroFile);
			case ITEM:
				return SiroPacker.packItemSiro(siroFile);
			case POKEMON:
				return SiroPacker.packPokemonSiro(siroFile);
			case MOVE:
				return SiroPacker.packMoveSiro(siroFile);
			case DUNGEON:
				return SiroPacker.packDungeonSiro(siroFile);
			case GRAPHIC_LIST:
				return SiroPacker.packGraphicListSiro(siroFile);
			case GRAPHIC_TABLE:
				return SiroPacker.packGraphicTableSiro(siroFile);
			default:
				return null;
		}
	}

	private static BufferedDataHandler packBasicSiro(SiroFile siroFile) throws IOException {
		BufferedDataHandler data = siroFile.getSegment("data").getData();
		BufferedDataHandler out = new BufferedDataHandler(data.length() + 0x10);
		out.seek(0);
		out.writeString("SIRO");
		out.writePointer(new Pointer(siroFile.getOffset() + 0x10, true));
		out.write(data);
		return out;
	}

	private static BufferedDataHandler packItemSiro(SiroFile siroFile) throws IOException {
		SiroSegment itemsseg = siroFile.getSegment("items");
		int absoff = siroFile.getOffset() + 0x10;

		Pointer descptr = new Pointer(absoff, true);
		SiroSegment descsseg = siroFile.getSegment("descs");
		BufferedDataHandler descs = writeStringList(absoff, descsseg, itemsseg, false, false, 0x10);
		absoff += descs.length() + itemsseg.getChildren().size()*0x20;
		

		Pointer namesptr = new Pointer(absoff, true);
		SiroSegment namesseg = siroFile.getSegment("names");
		BufferedDataHandler names = writeStringList(absoff, namesseg, itemsseg, true, false, 0x10);
		absoff += names.length();

		BufferedDataHandler out = new BufferedDataHandler(absoff - siroFile.getOffset() + 0x08);
		//Write header
		out.seek(0);
		out.writeString("SIRO");
		out.writePointer(new Pointer(absoff, true));
		out.seek(0x10);
		//Write data
		out.write(descs);
		for(int i = 0; i<itemsseg.getChildren().size(); i++){
			out.write(itemsseg.getChild("" + i).getData());
		}
		out.write(names);
		//Write footer
		out.writePointer(descptr);
		out.writePointer(namesptr);

		return out;
	}

	//TODO SIRO pointers
	private static BufferedDataHandler packPokemonSiro(SiroFile siroFile) throws IOException {
		SiroSegment pokemonseg = siroFile.getSegment("pokemon");
		SiroSegment stringsseg = siroFile.getSegment("strings");
		int totalLength = siroFile.getOffset() + 0x10 + pokemonseg.getChildren().size()*0x48;
		BufferedDataHandler strs = writeStringList(totalLength, stringsseg, pokemonseg, true, true, 0x0, 0x4);
		totalLength += strs.length();
		
		BufferedDataHandler out = new BufferedDataHandler(totalLength);
		for(int i = 0; i<pokemonseg.getChildren().size(); i++){
			out.write(pokemonseg.getChild("" + i).getData());
		}
		out.write(strs);
		
		return out;
	}

	private static BufferedDataHandler packMoveSiro(SiroFile siroFile) {
		//TODO Auto-generated method stub
		return null;
	}

	private static BufferedDataHandler packDungeonSiro(SiroFile siroFile) {
		//TODO Auto-generated method stub
		return null;
	}

	private static BufferedDataHandler packGraphicListSiro(SiroFile siroFile) throws IOException {
		SiroSegment tileseg = siroFile.getSegment("tile");
		SiroSegment palseg = siroFile.getSegment("palette");
		int absoff = siroFile.getOffset() + 0x10;
		
		BufferedDataHandler out = new BufferedDataHandler(tileseg.getData().length() + palseg.getData().length() + 0x18);
		//Write header
		out.seek(0);
		out.writeString("SIRO");
		out.writePointer(new Pointer(absoff + tileseg.getData().length() + palseg.getData().length(), true));
		out.seek(0x10);
		//Write data
		out.write(tileseg.getData());
		out.write(palseg.getData());
		//Write footer
		out.writePointer(new Pointer(absoff, true));
		out.writePointer(new Pointer(absoff + tileseg.getData().length(), true));
		
		return out;
	}

	private static BufferedDataHandler packGraphicTableSiro(SiroFile siroFile) {
		//TODO Auto-generated method stub
		return null;
	}

}
