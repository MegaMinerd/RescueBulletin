package minerd.relic.data;

import java.io.IOException;

import javafx.scene.layout.Region;

public abstract class GameData {

	public abstract Region load() throws IOException ;

	public abstract String getName();

}
