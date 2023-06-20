package minerd.relic.file;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SiroFactory {
    private static void populateFromTable(BufferedDataHandler buffer, SiroSegment parent,
      int childSize, int childNum, Pointer table) throws IOException{
        for(int i=0; i<childNum; i++){
            byte[] data = new byte[childSize];
            int offset = table.getOffset()+4*i;
            buffer.seek(offset);
            buffer.read(data);
            parent.addChild(i+"", new SiroSegment(offset, new BufferedDataHandler(ByteBuffer.wrap(data))));
        }
    }
    
    public static SiroFile buildPaletteSiro(BufferedDataHandler buffer, int offset) throws IOException{
        SiroSegment head = new SiroSegment(offset);
        buffer.seek(4);
        Pointer table = buffer.parsePointer().relativeTo(offset);
        for(int i=0; i<32; i++){
            int off = table.getOffset()+4*i;
            buffer.seek(offset);
            byte[] data = new byte[buffer.peek()+4];
            buffer.read(data);
            head.addChild(i+"", new SiroSegment(off, new BufferedDataHandler(ByteBuffer.wrap(data))));
        }
        return new SiroFile(offset, head);
    }
    
    //0306570 to 030F66B
    //TODO: public static SiroFile buildItemSiro(ByteBuffer buffer, int offset){}
    
    //04F246C to 04F45A7
    public static SiroFile buildTrapTileSiro(BufferedDataHandler buffer, int offset) throws IOException{
        SiroSegment head = new SiroSegment(offset);
        buffer.seek(4);
        buffer.seek(buffer.parsePointer().relativeTo(offset)); //04F45A0
        Pointer p1 = buffer.parsePointer(); //04F247C, tiles
        Pointer p2 = buffer.parsePointer(); //04F4520, palettes
        
        //One int tile num (29 chunks * 9 tiles = 261)
        //Then tile data
        buffer.seek(p1.relativeTo(offset));
        byte[] data = new byte[4+29*9*0x20];
        buffer.read(data);
        head.addChild("tiles", new SiroSegment(p1, new BufferedDataHandler(ByteBuffer.wrap(data))));
        
        buffer.seek(p2.relativeTo(offset));
        data = new byte[0x80];
        buffer.read(data);
        head.addChild("palettes", new SiroSegment(p1, new BufferedDataHandler(ByteBuffer.wrap(data))));
        
        return new SiroFile(offset, head);
    }
    
    //1E76170 to 1E77297
    //Nearly identical to pokemon sprite format
    public static SiroFile buildItemSpriteSiro(BufferedDataHandler buffer, int offset) throws IOException{
        SiroSegment head = new SiroSegment(offset);
        buffer.seek(4);
        buffer.seek(buffer.parsePointer().relativeTo(offset)); //1E77284
        Pointer p1 = buffer.parsePointer(); //1E771A0, vestige of frame data pointer table
        Pointer p2 = buffer.parsePointer(); //1E77220, vestige of facing directions 
        buffer.skip(4);                     //Always 1
        Pointer p3 = buffer.parsePointer(); //1E77224, sprite pointer table
        
        SiroSegment fdata = new SiroSegment(p1);
        //1E76180-1E7634C
        populateFromTable(buffer, fdata, 0x14, 0x18, p1.relativeTo(offset));
        head.addChild("fdata", fdata);
        
        SiroSegment anim = new SiroSegment(p2);
        buffer.seek(p2.getOffset()-offset); //1E77200
        //1E76360-1E76408
        populateFromTable(buffer, anim, 0x18, 0x8, buffer.parsePointer().relativeTo(offset));
        head.addChild("anim", anim);
        
        SiroSegment frame = new SiroSegment(p3);
        //1E764A0-1E77190
        populateFromTable(buffer, anim, 0x10, 0x18, p3.relativeTo(offset));
        head.addChild("frame", frame);
        
        for(SiroSegment child : frame.getChildren().values()){
            BufferedDataHandler data = child.getData();
            data.seek(0);
            buffer.seek(data.parsePointer().relativeTo(offset));
            byte[] img = new byte[0x80];
            int off = buffer.getFilePointer();
            buffer.read(img);
            child.addChild("image", new SiroSegment(off, new BufferedDataHandler(ByteBuffer.wrap(img))));
        }
        
        return new SiroFile(offset, head);
    }
    
    //Header pointing to footer
    //A: Frame data
    //B: Animation data
    //C: Tile data
    //D: A* 
    //E: Body part displacement data
    //F: B*
    //G: F* (facing directions)
    //H: C*
    //Footer:
    //  D*
    //  G*
    //  int F.length/8
    //  H*
    //  E*
    //TODO: public static SpriteSiro(){}
}