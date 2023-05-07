package minerd.relic.data;

import java.io.IOException;

import javafx.scene.layout.Region;
import minerd.relic.file.RomFile;

public abstract class GameData {
    
    public abstract Region load() throws IOException;
    
    //TODO: change to return a ByteBuffer or RomFile instead of taking a variable
    public abstract void save(RomFile rom);
    
    public abstract String getName();
}
