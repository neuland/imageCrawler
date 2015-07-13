package de.neuland.imagecrawler.crawler

import de.neuland.imagecrawler.config.ImageReplacements
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import de.neuland.imagecrawler.config.IgnoreConfig
import de.neuland.imagecrawler.config.LocaleConfig
import de.neuland.imagecrawler.parser.FreemarkerParser
import de.neuland.imagecrawler.parser.Parser
import de.neuland.imagecrawler.parser.StylesheetParser

import groovy.io.FileType

class FolderCrawler {
    private Logger logger = LogManager.getLogger(FolderCrawler.class);
    private Set<Parser> parser = [
        new StylesheetParser(),
        new FreemarkerParser()
    ]

    Collection<FileResult> crawle(File folder, List<LocaleConfig> localeConfigs) {
        Collection<FileResult> result = []
        folder.traverse([type:FileType.FILES], {
            FileResult fileResult = parseFile(it, localeConfigs)
            if(fileResult != null && !fileResult.images.empty )
                result << fileResult
        })

        return result
    }

    private FileResult parseFile(File file, List<LocaleConfig> localeConfigs) {
        if(!shouldParse(file, localeConfigs)) {
            return null
        }

        FileResult fileResult = new FileResult(file: file)
        parser.each { parser ->
            if( parser.isParserFor(file) ) {
                (parser.parseFile(file).each { image->
                    fileResult.images.addAll(substitute(file, image, localeConfigs))
                })
            }
        }
        return fileResult
    }

    private boolean shouldParse(File file, List<LocaleConfig> localeConfigs) {
        if( localeConfigs == null || localeConfigs.isEmpty() ) {
            return !IgnoreConfig.getInstance().shouldFileBeIgnored(file.getAbsolutePath() );
        }

        if( file.getPath().contains("del_")) {
            return false;
        }

        def fileLocale = extractFileLocale(file.getName())
        def folderLocale = extractFolderLocale(file.getPath())

        def ignoreFile = false
        def fileLocaleResult = false
        def folderLocaleResult = false
        localeConfigs.each {
            if( IgnoreConfig.getInstance().shouldFileBeIgnored(it.lang, file.getAbsolutePath() ) ) {
                ignoreFile = true
            }
            if( fileLocale == null || fileLocale == it.lang ) {
                fileLocaleResult = true
            }
            if( folderLocale == null || folderLocale == it.lang ) {
                folderLocaleResult = true
            }
        }

        return fileLocaleResult && folderLocaleResult && !ignoreFile
    }

    private boolean shouldParseWithLocale(File file, def localeConfig) {
        return shouldParse(file, [localeConfig])
    }

    private String extractFileLocale(String fileName) {
        def matcher = fileName =~ /_([a-z]{2})\.ftl/
        if(matcher.size() > 0 ) {
            return matcher[0][1]
        } else {
            return null
        }
    }

    private String extractFolderLocale(String path) {
        def matcher = path =~ "[/\\\\]([a-z]{2})[/\\\\]"

        if(matcher.size() > 0 ) {
            return matcher[0][1]
        } else {
            return null
        }
    }

    private Set<String> substitute(File file, String source, List<LocaleConfig> localeConfigs) {
        def result = []

        if( localeConfigs == null || localeConfigs.isEmpty() ) {
            def parsedString = source;

            parsedString = prependProtocoll(parsedString)

            parsedString = isUrlParsedCompletly(parsedString)
            if( parsedString != null ) {
                result << parsedString
            }
        } else {
            localeConfigs.grep { shouldParseWithLocale(file, it) }.each { localeConfig ->

                def parsedString = source.replaceAll("\\{|\\}", "#")
                ImageReplacements[] imageReplacements = localeConfig.imageReplacements

                if(imageReplacements != null){
                    imageReplacements.each { imageReplacement ->
                        parsedString = parsedString.replaceAll('\\$#'+imageReplacement.key+'#', imageReplacement.value)
                        logger.debug "\treplaced all $imageReplacement.key by $imageReplacement.value"
                    }
                }else{
                    logger.debug "\timageReplacements was null => nothing was replaced"
                }

                parsedString = prependProtocoll(parsedString)

                parsedString = isUrlParsedCompletly(parsedString)
                if( parsedString != null ) {
                    result << parsedString.replaceAll(' ', '%20')
                }
            }
        }

        return result
    }

    private String prependProtocoll(String parsedString) {
        if( parsedString != null && parsedString.startsWith("//") ) {
            parsedString = "http:" + parsedString
        }
        return parsedString
    }

    private String isUrlParsedCompletly(String imageUrl) {
        if( imageUrl.contains("#") ) {
            logger.warn "\tunable to parse image: $imageUrl"
            return null
        } else if( imageUrl.contains("<@") ) {
            logger.warn "\tunable to parse image: $imageUrl"
            return null
        } else if( !imageUrl.startsWith("http") ) {
            logger.debug "\tskipping image, provied from shop: $imageUrl"
            return null
        } else {
            return imageUrl
        }
    }
}
