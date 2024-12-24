package minerd.relic.file;

import java.io.IOException;
import java.nio.ByteBuffer;

import minerd.relic.file.SiroFile.SiroLayout;

public class SiroPacker {

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
	
//	public static SiroFile buildBasicSiro(BufferedDataHandler buffer, int offset) throws IOException {
//		//Parse header
//		buffer.seek(4);
//		Pointer dataPtr = buffer.parsePointer();
//
//		buffer.seek(dataPtr.relativeTo(offset));
//		byte[] data = new byte[buffer.length()-0x10];
//		buffer.seek(dataPtr.relativeTo(offset));
//		buffer.read(data);
//		SiroSegment head = new SiroSegment(offset);
//		head.addChild("data", new SiroSegment(offset+0x10, new BufferedDataHandler(ByteBuffer.wrap(data))));
//		
//		return new SiroFile(offset, head, SiroLayout.BASIC);
//	}

	private static BufferedDataHandler packItemSiro(SiroFile siroFile) {
		// TODO Auto-generated method stub
		return null;
	}

	private static BufferedDataHandler packPokemonSiro(SiroFile siroFile) {
		// TODO Auto-generated method stub
		return null;
	}

	private static BufferedDataHandler packMoveSiro(SiroFile siroFile) {
		// TODO Auto-generated method stub
		return null;
	}

	private static BufferedDataHandler packDungeonSiro(SiroFile siroFile) {
		// TODO Auto-generated method stub
		return null;
	}

	private static BufferedDataHandler packGraphicListSiro(SiroFile siroFile) {
		// TODO Auto-generated method stub
		return null;
	}

	private static BufferedDataHandler packGraphicTableSiro(SiroFile siroFile) {
		// TODO Auto-generated method stub
		return null;
	}

}
