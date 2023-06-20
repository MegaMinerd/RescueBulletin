package minerd.relic.file;


public class SiroFile{
    private SiroSegment head;
    private int offset;
    
    /**
     * @param head The head element of a tree defining the data structure.
     * @param offset The location of this file in the full rom
     */
    public SiroFile(int offset, SiroSegment head) {
        this.offset = offset;
        this.head = head;
    }
    
    public SiroSegment getSegment(String path) {
        return head.getDescendant(path);
    }
}