package minerd.relic.file;

import java.util.HashMap;

public class FileSystem {
    private HashMap<String, SbinFile> sbinCache;
    private HashMap<String, SiroFile> siroCache;
    
    private static FileSystem instance = new FileSystem();
    
    private FileSystem(){
        this.sbinCache = new HashMap<String, SbinFile>();
        this.siroCache = new HashMap<String, SiroFile>();
    }
    
//    public static SbinFile getSbin(String name){
//        if(!instance.sbinCache.containsKey(name))
//            instance.sbinCache.put(name, new SbinFile(name, ProjectConfig.get(name+"SbinStart"), ProjectConfig.get(name+"SbinEnd")));
//        return instance.sbinCache.get(name);
//    }
    
    public static SiroFile getSiro(String name){
        //if(!instance.siroCache.containsKey(name)){
        //    String[] parts = name.split("/", 2);
        //    instance.siroCache.put(name, getSbin(parts[0]).getSubfile(parts[1]));
        //}
        return instance.siroCache.get(name);
    }
}