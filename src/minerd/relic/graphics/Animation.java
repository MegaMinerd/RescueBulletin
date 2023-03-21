package minerd.relic.graphics;

import javafx.scene.image.WritableImage;

public class Animation {
	private WritableImage[] frames;
	private Integer[] durations;
	private int rows, cols;
	
	public Animation(int rowNum, int colNum, int frameNum) {
		this.frames = new WritableImage[frameNum];
		this.rows = rowNum;
		this.cols = colNum;
	}

	public Animation(WritableImage[] framesIn, Integer[] durationsIn) {
		this.frames = framesIn;
		this.durations = durationsIn;
	}

	public WritableImage getFrame(int index) {
		return frames[index];
	}

	public int getFrameCount() {
		return frames.length;
	}
}
