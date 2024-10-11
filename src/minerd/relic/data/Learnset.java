package minerd.relic.data;

import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.layout.Region;
import minerd.relic.file.BufferedDataHandler;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.SiroFile;

public class Learnset extends GameData {
	private ArrayList<LevelMove> lvMoves;
	private ArrayList<TmMove> tmMoves;

	public Learnset(int index) throws IOException, InvalidPointerException {
		SiroFile data = (SiroFile) Rom.getInstance().getSystemSbin().getSubfile("wazapara");
		BufferedDataHandler lv = data.getSegment("learnsets/" + index + "/lv").getData();
		lvMoves = new ArrayList<LevelMove>();
		while(lv.getFilePointer()<lv.length()){
			lvMoves.add(new LevelMove(readMoveId(lv), lv.readUnsignedByte()));
			if(lv.peek() == 0) break;
		}
		BufferedDataHandler tm = data.getSegment("learnsets/" + index + "/tm").getData();
		tmMoves = new ArrayList<TmMove>();
		while(tm.getFilePointer()<tm.length()){
			tmMoves.add(new TmMove(readMoveId(tm)));
			if(tm.peek() == 0) break;
		}
	}

	private int readMoveId(BufferedDataHandler rom) throws IOException {
		int[] highByte = rom.readMask(1, 7, 1);
		return highByte[1]==1 ? (highByte[0] << 7) | rom.readUnsignedByte() : highByte[0];
	}

	private byte[] saveMoveId(int id) {
		if(id<0x80){
			return new byte[] { (byte) id };
		} else{
			byte highByte = (byte) (((id & 0xFF80) >> 7) | 0x80);
			byte lowByte = (byte) (id & 0x7F);
			return new byte[] { highByte, lowByte };
		}
	}

	public Region load() throws IOException {
		return null;
	}

	public void save() {
	}

	public byte[] saveLvMoves() {
		ArrayList<Byte> data = new ArrayList<Byte>();
		for(LevelMove move : lvMoves){
			byte[] id = saveMoveId(move.getMoveId());
			data.add(id[0]);
			if(id.length>1)
				data.add(id[1]);
			data.add((byte) move.getLevel());
		}
		return toPrimitive(data);
	}

	public byte[] saveTmMoves() {
		ArrayList<Byte> data = new ArrayList<Byte>();
		for(TmMove move : tmMoves){
			byte[] id = saveMoveId(move.getMoveId());
			data.add(id[0]);
			if(id.length>1)
				data.add(id[1]);
		}
		return toPrimitive(data);
	}

	private byte[] toPrimitive(ArrayList<Byte> dataIn) {
		byte[] dataOut = new byte[dataIn.size()];
		for(int i = 0; i<dataIn.size(); i++){
			dataOut[i] = dataIn.get(i);
		}
		return dataOut;
	}

	public String getName() {
		return "Learnset";
	}

	public ArrayList<LevelMove> getLvMoves() {
		return lvMoves;
	}

	public void setLvMoves(ArrayList<LevelMove> lvMoves) {
		this.lvMoves = lvMoves;
	}

	public ArrayList<TmMove> getTmMoves() {
		return tmMoves;
	}

	public void setTmMoves(ArrayList<TmMove> tmMoves) {
		this.tmMoves = tmMoves;
	}

	public class LevelMove {
		private int moveId, level;

		public LevelMove(int moveId, int level) {
			this.moveId = moveId;
			this.level = level;
		}

		public int getMoveId() {
			return moveId;
		}

		public void setMoveId(int moveId) {
			this.moveId = moveId;
		}

		public String getMoveName() {
			return Text.getText("Moves", moveId);
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

	}

	public class TmMove {
		private int moveId;

		public TmMove(int moveId) {
			this.moveId = moveId;
		}

		public int getMoveId() {
			return moveId;
		}

		public void setMoveId(int moveId) {
			this.moveId = moveId;
		}

		public String getMoveName() {
			return Text.getText("Moves", moveId);
		}
	}
}