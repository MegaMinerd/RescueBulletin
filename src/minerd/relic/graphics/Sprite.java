package minerd.relic.graphics;

public class Sprite {
	private DirectionalAnimation[] animations;
	
	public Sprite(DirectionalAnimation[] anims) {
		this.animations = anims;
	}

	public DirectionalAnimation getAnimation(int index) {
		return animations[index];
	}
}
