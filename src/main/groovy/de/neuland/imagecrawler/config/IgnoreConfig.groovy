package de.neuland.imagecrawler.config

import de.neuland.imagecrawler.exception.NoInstanceFoundException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.regex.Matcher
import de.neuland.imagecrawler.runner.App

class IgnoreConfig {
    private static Logger logger = LogManager.getLogger(IgnoreConfig.class)

    private static IgnoreConfig instance
    private def ignoreFiles = [:]
    private def ignoreImages
    private String ignorePath

    private IgnoreConfig(String ignorePath){
        this.ignorePath = ignorePath
    }

    public static IgnoreConfig getInstance(String ignorePath) {
        if(instance == null) throw new NoInstanceFoundException("Please call \"createInstance\" before you calling \"getInstance\"")
        return instance
    }

    static void createInstance(String ignorePath){
        if (instance != null && !instance.ignorePath.equals(ignorePath)) throw new IllegalArgumentException('different cacheFile')
        if (instance == null) instance = new IgnoreConfig(ignorePath)
    }

    public boolean shouldFileBeIgnored(String file) {
        shouldFileBeIgnored(null, file)
    }

    public boolean shouldFileBeIgnored(String locale, String file) {
        if( ignoreFiles[locale] == null ) {
            parseFiles(locale)
        }

        boolean result = false;
        ignoreFiles[locale].each {
            Matcher m = file =~ it
            if( m.matches() ) {
                result = true;
            }
        }
        return result
    }

    public boolean shouldImageBeIgnored(String image) {
        if( ignoreImages == null ) {
            parseImages()
        }

        boolean result = false;
        ignoreImages.each {
            Matcher m = image =~ it
            if( m.matches() ) {
                result = true;
            }
        }
        return result
    }

    private void parseFiles(String locale) {
        ignoreFiles[locale] = []
        parseFile(locale, File.separator+'files.txt' )
        if(locale != null){
            parseFile(locale, File.separator+'files_'+locale+'.txt' )
        }
    }

    private void parseFile(String locale, String fileName) {
        // TODO : just a WA
        if(App.isUseInternalIgnore()){
            URL url = this.getClass().getResource( "/ignore"+fileName )
            if( url != null ) {
                url.eachLine {
                    ignoreFiles[locale] << it
                }
            }
        }else{
            String fName = ignorePath+fileName;
            try {
                createFileIfNotExists(fName);
                File file = new File(fName);
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    ignoreFiles[locale] << line
                }
                br.close();
            }catch (Exception e){
                logger.error("__________ERROR__________\nfName : $fName\n: " + e.getMessage())
            }
        }
    }

    private void parseImages() {
        ignoreImages = []
        if(App.isUseInternalIgnore()){
            URL url = this.getClass().getResource( "/ignore/images.txt" )
            if( url != null ) {
                url.eachLine {
                    ignoreImages << it
                }
            }
        }else{
            addLinesToIgnoreImages(ignorePath+File.separator+'images.txt')
        }
    }

    private addLinesToIgnoreImages(String fName){
        try {
            createFileIfNotExists(fName);
            File file = new File(fName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                ignoreImages << line
            }
            br.close();
        }catch (Exception e){
            logger.error("__________ERROR__________\nfName : $fName\n: " + e.getMessage())
        }
    }

    private createFileIfNotExists(String fName){
        try {
            File f = new File(fName);
            if(!f.getParentFile().exists()){
                f.getParentFile().mkdirs();
            }
            if(!f.exists()) {
                f.createNewFile();
                new FileOutputStream(f, false);
            }
        }catch (FileNotFoundException e){
            logger.error("__________ERROR__________\nfName : $fName\n: " + e.getMessage())
        }
    }

}
