package minerd.relic.file;
import java.io.IOException;
import java.util.HashMap;

public class SbinTest{
    public static SiroSegment test(){
        try{
            RomLoader.load(new java.io.File("D:/Games/emu/PMD/PMD_Red/PMD-Red-Original.gba"));
            SbinFile sbinFile1 = Rom.getInstance().getSystemSbin();
            //SiroFile temp = SiroFactory.buildItemSiro(sbinFile1.getSubfile("itempara"), 0x0306570);
            //this.offset = temp.getOffset();
            //this.head = temp.getSegment();
            HashMap<String, SiroSegment> descs = ((SiroFile)sbinFile1.getSubfile("itempara")).getSegment("items").getChildren();
            for(String s : descs.keySet())
                System.out.println(s);
            return ((SiroFile)sbinFile1.getSubfile("itempara")).getSegment("descs");
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
