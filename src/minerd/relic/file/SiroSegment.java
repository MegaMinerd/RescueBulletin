package minerd.relic.file;

import java.util.HashMap;

public class SiroSegment {
    private int offset;
    private BufferedDataHandler data;
    private HashMap<String, SiroSegment> children;
    
    public SiroSegment(Pointer p){
        this(p.getOffset());
    }
    
    public SiroSegment(int offsetIn){
        this.offset = offsetIn;
        this.children = new HashMap<String, SiroSegment>();
    }
    
    public SiroSegment(Pointer p, BufferedDataHandler dataIn){
        this(p.getOffset(), dataIn);
    }
    
    public SiroSegment(int offsetIn, BufferedDataHandler dataIn){
        this.offset = offsetIn;
        this.children = new HashMap<String, SiroSegment>();
        this.data = dataIn;
    }
    
    public void addChild(String name, SiroSegment child){
        children.put(name, child);
    }
    
    public HashMap<String, SiroSegment> getChildren() {
        return children;
    }
    
    public SiroSegment getChild(String name) {
        return children.get(name);
    }
    
    public SiroSegment getDescendant(String path){
        String[] parts = path.split("/", 2);
        return parts.length==1 ? children.get(parts[0]) : children.get(parts[0]).getDescendant(parts[1]);
    }
    
    public BufferedDataHandler getData(){
        return data;
    }
    
    public void load(BufferedDataHandler rom) {}
}