package de.neuland.imagecrawler.cache

import de.neuland.imagecrawler.exception.NoInstanceFoundException


class KnownImgsCache {
    
	private static KnownImgsCache instance
    private def knownImgs = [:]
    private final String cacheFile

	static KnownImgsCache getInstance() {
        if(instance == null) throw new NoInstanceFoundException("Please call \"createInstance\" before you calling \"getInstance\"")
		return instance
	}

    static void createInstance(String cacheFile){
        if (instance != null && !instance.cacheFile.equals(cacheFile)) throw new IllegalArgumentException('different cacheFile')
        if (instance == null) instance = new KnownImgsCache(cacheFile)
    }

	private KnownImgsCache(String cacheFile){
 		knownImgs = []
        this.cacheFile = cacheFile
 		File f = new File( cacheFile )
		if(f.exists()){
			f.text.eachLine {
            	knownImgs << it
            }
		}
	}

    public def getStatus(def d){
        if(knownImgs.contains("1_" + d)) {
        	return true
        } else if(knownImgs.contains("0_" + d)) {
        	return false
        }
        return null
    }

    public void put(def d, boolean result){
    	if(result){
    		knownImgs.add("1_" + d)
		}else{
			knownImgs.add("0_" + d)
		}
    }

    public void overwriteLine(def d){
        if(knownImgs.contains("0_"+d)){
            int index = knownImgs.indexOf("0_"+d);
            knownImgs.set(index, "1_"+d);
        }
    }

    public void save(){
    	File f = new File( cacheFile )
		if(!f.exists()){
			f.createNewFile() 
		}	
    	f.withWriter { out ->
 		   	knownImgs.each() {
        		out.writeLine(it)
    		}
		}
    }
}
