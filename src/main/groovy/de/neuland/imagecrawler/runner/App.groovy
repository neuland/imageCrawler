package de.neuland.imagecrawler.runner

import de.neuland.imagecrawler.config.IgnoreConfig

import java.text.DecimalFormat

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import de.neuland.imagecrawler.checker.ImageChecker
import de.neuland.imagecrawler.config.RunConfig
import de.neuland.imagecrawler.crawler.FolderCrawler
import de.neuland.imagecrawler.report.Reporter
import de.neuland.imagecrawler.cache.KnownImgsCache

class App {
    public static final String CACHE_FILE_DEFAULT = 'known_img.cache'
    public static final String IGNORE_PATH_DEFAULT = 'ignore'
    public static final String REPORT_FILE_NAME_DEFAULT = 'out'

    public static final String HELP_1_ARGS_KEY = 'help'
    public static final String HELP_2_ARGS_KEY = 'h'
    public static final String CONFIGURATION_FILE_ARGS_KEY = 'c'
    public static final String CACHE_FILE_ARGS_KEY = 'cache'
    public static final String IGNORE_PATH_ARGS_KEY = 'ignorePath'
    public static final String REPORT_FILE_NAME_ARGS_KEY = 'r'

    private static Logger logger = LogManager.getLogger(App.class)
    private static boolean useInternalIgnore = false;


    static void main(String... args) {
        if(needsHelp(args as List)){
           showHelp()
           return;
        }

        final String cacheFile = findCacheFile(getParam(CACHE_FILE_ARGS_KEY, args as List))
        KnownImgsCache.createInstance(cacheFile)
        final String ignorePath = findIgnorePath(getParam(IGNORE_PATH_ARGS_KEY, args as List))
        IgnoreConfig.createInstance(ignorePath)
        useInternalIgnore = findUseInternalIgnore(getParam('useInternalIgnore', args as List)) // TODO : just a WA
        final ImageChecker imageChecker = new ImageChecker()
        final FolderCrawler crawler = new FolderCrawler()
        def reportData = [files: [],
            reportDate: new Date(),
            ratio: 0.00,
            fileCounter: 0,
            failedImageCounter: 0,
            imageCounter: 0]
        final String configFile = getParam(CONFIGURATION_FILE_ARGS_KEY, args as List)
        def configuration = loadConfig(configFile)
        if(configuration == null){
            logger.error("Can not load configuration - FATAL ERROR")
            return;
        }
        List<RunConfig> runConfigs = configuration.runConfig
        runConfigs.each { runConfig ->
            logger.info("processing baseFolder $runConfig.baseFolder")

            def dir = new File(runConfig.baseFolder)
            if(!dir.exists() || !dir.isDirectory()){return} // should be continue in groovy

            crawler.crawle(dir, runConfig.localeConfigs).each{ fileResult ->
                logger.info("$fileResult.file.absoluteFile")
                reportData.fileCounter++

                def reportDataFile = [file:fileResult.file, images:[]]
                fileResult.images.grep { image -> imageChecker.shouldCheck(image) }.each { image ->
                    def foundImage = imageChecker.exists(new URL(image))
                    if( foundImage ) {
                        logger.debug("\t$foundImage -> $image")
                    } else {
                        reportData.failedImageCounter++
                        logger.info("\t$foundImage -> $image")
                    }

                    reportDataFile.images << [image:image, found:foundImage]
                    reportData.imageCounter++
                }

                if( !reportDataFile.images.isEmpty() ) {
                    reportData.files << reportDataFile
                }
            }
        }

        if( reportData.imageCounter > 0 ) {
            reportData.ratio = (reportData.failedImageCounter/reportData.imageCounter)
        }
        def formattedRatio = new DecimalFormat('###.##%').format(reportData.ratio)
        logger.warn("""
                Parsed $reportData.fileCounter files and processed $reportData.imageCounter images.
                Found $reportData.failedImageCounter missing image references.
                This is a missing hit ration of $formattedRatio.""")
        String reportFileParameter = getParam(REPORT_FILE_NAME_ARGS_KEY, args as List)
        reportFileParameter = reportFileParameter!=null?reportFileParameter:REPORT_FILE_NAME_DEFAULT
        File htmlReportFile = new File(reportFileParameter+'.html')
        FileWriter htmlFileWriter = new FileWriter(htmlReportFile)
        new Reporter().createReport(htmlFileWriter, reportData, 'html_report.ftl')
        File xmlReportFile = new File(reportFileParameter+'.xml')
        FileWriter xmlFileWriter = new FileWriter(xmlReportFile)
        new Reporter().createReport(xmlFileWriter, reportData, 'xml_report.ftl')
        KnownImgsCache.getInstance().save()
        logger.debug("Settings : \n useInternalIgnore : "+useInternalIgnore+"\n ignorePath : "+ignorePath+"\n cacheFile : "+cacheFile)
    }


    public static boolean isUseInternalIgnore(){
        return useInternalIgnore
    }

    static boolean needsHelp(List<String> params){
        def filtered = params.grep {
            (it == "-$HELP_1_ARGS_KEY"|| it == "-$HELP_2_ARGS_KEY")
        }
        assert filtered.size() <= 1
        return !filtered.isEmpty()
    }

    static void showHelp(){
        StringBuffer sb = new StringBuffer()
        sb.append('Arguments :')
        sb.append(System.lineSeparator());
        sb.append("-$HELP_1_ARGS_KEY / -$HELP_2_ARGS_KEY : \"shows the help\"")
        sb.append(System.lineSeparator());
        sb.append("-$CONFIGURATION_FILE_ARGS_KEY=configurationFile - the configuration file")
        sb.append(System.lineSeparator());
        sb.append("-$CACHE_FILE_ARGS_KEY=cacheFile - the cache file (will be created if not exists - default : $CACHE_FILE_DEFAULT)")
        sb.append(System.lineSeparator());
        sb.append("-$IGNORE_PATH_ARGS_KEY=ignorePath - the path to the ignore dir (will be created if not exists - default : $IGNORE_PATH_DEFAULT)")
        sb.append(System.lineSeparator());
        sb.append("-$REPORT_FILE_NAME_ARGS_KEY=reportFile - the name of the reportfiles (default : $REPORT_FILE_NAME_DEFAULT)")
        logger.info(sb.toString())
    }

    static def loadConfig(String configFile) {
        try {
            def configFileURL
            if( configFile == null ) {
                configFileURL = getResource( '/runConfig.config' )
            } else {
                configFileURL = new File( configFile )
            }

            assert configFileURL != null

            return new ConfigSlurper().parse(configFileURL.text)
        }catch (Exception e){
            logger.error(e.message)
            return null
        }

    }

    static String getParam(String paramName, List<String> params) {
        def filtered = params.grep {
            it.startsWith("-$paramName=")
        }

        assert filtered.size() <= 1
        if( filtered.isEmpty() ) {
            return null
        } else {
            String param = filtered.first()
            return param.substring(paramName.length()+2, param.length())
        }
    }

    static String findCacheFile(String input){
        return (isEmpty(input) || fileIsDir(input)) ? CACHE_FILE_DEFAULT : input
    }

    static final String findIgnorePath(String input){
        return (isEmpty(input) || fileIsNoDir(input)) ? IGNORE_PATH_DEFAULT : input
    }

    static boolean findUseInternalIgnore(String input){
        return (isEmpty(input) || !input.equals('true')) ? false : true
    }

    static boolean isEmpty(String input){
        return (input == null || input.trim().length() == 0)
    }

    static boolean fileIsDir(String input) {
        File f = new File(input);
        return (f.exists() && f.isDirectory());
    }

    static boolean fileIsNoDir(String input) {
        File f = new File(input);
        return (f.exists()) ? !f.isDirectory() : false;
    }

}