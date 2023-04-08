package minerd.relic.data;

import java.io.IOException;

import javafx.scene.control.Control;

public abstract class GameData {

	public abstract Control load() throws IOException ;

	public abstract String getName();

}
