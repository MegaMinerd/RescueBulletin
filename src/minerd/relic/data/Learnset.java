package minerd.relic.data;

import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.layout.Region;
import minerd.relic.file.InvalidPointerException;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;
import minerd.relic.util.RrtOffsetList;

public class Learnset extends GameData {
	private ArrayList<LevelMove> lvMoves;
	private ArrayList<TmMove> tmMoves;

	public Learnset(int index) throws IOException, InvalidPointerException {
		RomFile rom = Rom.getAll();
		rom.seek(RrtOffsetList.learnsetOffset);
		rom.skip(index*0x8);
		rom.seek(rom.parsePointer());
		lvMoves = new ArrayList<LevelMove>();
		while(rom.peek()!=0){
			lvMoves.add(new LevelMove(readMoveId(rom), rom.readUnsignedByte()));
		}
		// The while saw the null terminator, but now it needs to be skipped.
		rom.skip(1);
		tmMoves = new ArrayList<TmMove>();
		while(rom.peek()!=0){
			tmMoves.add(new TmMove(readMoveId(rom)));
		}
	}

	private int readMoveId(RomFile rom) throws IOException {
		int[] highByte = rom.readMask(1, 7, 1);
		return highByte[1]==1 ? (highByte[0] << 7) | rom.readUnsignedByte() : highByte[0];
	}

	public Region load() throws IOException {
		return null;
	}

	public void save(RomFile rom) {
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