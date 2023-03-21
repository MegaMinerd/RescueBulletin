package minerd.relic.graphics;

public class DirectionalAnimation {
	private Animation[] directions;

	public DirectionalAnimation(Animation[] anims) {
		this.directions = anims;
	}

	public Animation getDirection(int index) {
		return directions[index];
	}
}
