package minerd.relic.file;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SirFile extends RomFile{
    private Pointer footer;
    
    public SirFile(ByteBuffer bufferIn) {
        super(bufferIn);
        try{
            seek(4);
            this.footer = parsePointer();
        } catch (IOException | InvalidPointerException e){
            
        }
    }
    
    public ByteBuffer getSection(int size, int ... path) throws IOException, IndexOutOfBoundsException, InvalidPointerException{
        seek(footer);
        for(int sectId : path){
            skip(4*sectId);
            seek(parsePointer());
        }
        byte[] data = new byte[size];
        //subBuffer.put(0, buffer, getFilePointer(), size);
        return buffer.slice(getFilePointer(), size);
    }
}