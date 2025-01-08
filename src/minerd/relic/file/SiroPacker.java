package minerd.relic.file;

import java.io.IOException;
import java.util.HashMap;

import minerd.relic.file.SiroFile.SiroLayout;

public class SiroPacker {

	/**
	 * Reads a list of strings without a pointer table
	 * 
	 * @param offset     The offset of the string list.
	 * @param strs       The segment with the strings as children.
	 * @param data       The segment that should have pointers updated. Must have sequential children names.
	 * @param isReversed Whether the first entry is last in the list.
	 * @param hasRepeats Whether entries may point to the same string.
	 * @param ptroff     The offsets in the data entry that should have pointers updated.
	 **/
	private static BufferedDataHandler writeStringList(int offset, SiroSegment strs, SiroSegment data, boolean isReversed, boolean hasRepeats, int ... ptroff) throws IOException {
		int totalLength = 0;
		//First pass to tally size and list offsets
		String stroffs = "";

		for(int i = 0; i<data.getChildren().size(); i++){
			BufferedDataHandler entry = data.getChild("" + i).getData();
			for(int j = 0; j<ptroff.length; j++){
				//Grab offset and string
				entry.seek(ptroff[j]);
				String stroff = "" + entry.parsePointer().getOffset();
				BufferedDataHandler strdata = strs.getChild(stroff).getData();
				strdata.seek(0);

				//Tally size and build list
				if(!hasRepeats || !stroffs.contains("," + stroff)){
					stroffs = String.format("%s,%s", stroffs, stroff);
					totalLength += Math.ceil((strdata.readString().length() + 1)/4.0)*4;
				}
			}
		}

		//Second pass to write strings
		BufferedDataHandler out = new BufferedDataHandler(totalLength);
		out.seek(0);
		String[] strofflist = stroffs.split(",");
		HashMap<String, Pointer> renames = new HashMap<String, Pointer>();
		for(int i = 0; i<strofflist.length; i++){
			//Get string
			String strname = strofflist[isReversed ? strofflist.length - i - 1 : i];
			renames.put(strname, new Pointer(offset + out.getFilePointer(), true));
			SiroSegment strseg = strs.getChild(strname);
			BufferedDataHandler str = strseg.getData();
			//Write string to list
			str.seek(0);
			out.writeString(str.readString());
			//Update strs
			strs.removeChild(strname);
			strs.addChild("" + renames.get(strname).getOffset(), strseg);
		}

		//Third pass to update data
		for(int i = 0; i<data.getChildren().size(); i++){
			BufferedDataHandler entry = data.getChild("" + i).getData();
			for(int j = 0; j<ptroff.length; j++){
				//Get old pointer
				entry.seek(ptroff[j]);
				Pointer ptr = entry.parsePointer();
				//Update entry pointer
				entry.seek(ptroff[j]);
				entry.writePointer(renames.get("" + ptr.getOffset()));
			}
		}

		return out;
	}

	public static BufferedDataHandler pack(SiroFile siroFile, SiroLayout layout) throws IOException {
		return pack(siroFile, layout, siroFile.getOffset());
	}

	//TODO: switch to this when saving siro subfiles
	public static BufferedDataHandler pack(SiroFile siroFile, SiroLayout layout, int offset) throws IOException {
		switch(layout){
			case BASIC:
				return SiroPacker.packBasicSiro(siroFile, offset);
			case ITEM:
				return SiroPacker.packItemSiro(siroFile, offset);
			case POKEMON:
				return SiroPacker.packPokemonSiro(siroFile, offset);
			case MOVE:
				return SiroPacker.packMoveSiro(siroFile, offset);
			case DUNGEON:
				return SiroPacker.packDungeonSiro(siroFile, offset);
			case GRAPHIC_LIST:
				return SiroPacker.packGraphicListSiro(siroFile, offset);
			case GRAPHIC_TABLE:
				return SiroPacker.packGraphicTableSiro(siroFile, offset);
			default:
				return null;
		}
	}

	private static BufferedDataHandler packBasicSiro(SiroFile siroFile, int offset) throws IOException {
		BufferedDataHandler data = siroFile.getSegment("data").getData();
		BufferedDataHandler out = new BufferedDataHandler(data.length() + 0x10);
		out.seek(0);
		out.writeString("SIRO");
		out.writePointer(new Pointer(offset + 0x10, true));
		out.write(data);
		return out;
	}

	private static BufferedDataHandler packItemSiro(SiroFile siroFile, int offset) throws IOException {
		SiroSegment itemsseg = siroFile.getSegment("items");
		int absoff = offset + 0x10;

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

	private static BufferedDataHandler packPokemonSiro(SiroFile siroFile, int offset) throws IOException {
		SiroSegment pokemonseg = siroFile.getSegment("pokemon");
		SiroSegment stringsseg = siroFile.getSegment("strings");
		int totalLength = offset + 0x10 + pokemonseg.getChildren().size()*0x48;

		BufferedDataHandler strs = writeStringList(totalLength, stringsseg, pokemonseg, true, true, 0x0, 0x4);
		totalLength += strs.length();

		//Write header
		BufferedDataHandler out = new BufferedDataHandler(totalLength);
		out.seek(0);
		out.writeString("SIRO", false);
		out.writePointer(new Pointer(offset + 0x10, true));
		out.seek(0x10);
		//Write data
		for(int i = 0; i<pokemonseg.getChildren().size(); i++){
			out.write(pokemonseg.getChild("" + i).getData());
		}
		out.write(strs);

		return out;
	}

	private static BufferedDataHandler packMoveSiro(SiroFile siroFile, int offset) {
		//TODO Auto-generated method stub
		return null;
	}

	private static BufferedDataHandler packDungeonSiro(SiroFile siroFile, int offset) {
		//TODO Auto-generated method stub
		return null;
	}

	private static BufferedDataHandler packGraphicListSiro(SiroFile siroFile, int offset) throws IOException {
		SiroSegment tileseg = siroFile.getSegment("tile");
		SiroSegment palseg = siroFile.getSegment("palette");
		int absoff = offset + 0x10;

		BufferedDataHandler out = new BufferedDataHandler(tileseg.getData().length() + palseg.getData().length() + 0x18);
		//Write header
		out.seek(0);
		out.writeString("SIRO", false);
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

	private static BufferedDataHandler packGraphicTableSiro(SiroFile siroFile, int offset) {
		//TODO Auto-generated method stub
		return null;
	}

}
