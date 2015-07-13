package de.neuland.imagecrawler.crawler

import de.neuland.imagecrawler.cache.KnownImgsCache
import de.neuland.imagecrawler.config.IgnoreConfig
import de.neuland.imagecrawler.config.ImageReplacements
import org.junit.Before

import static org.junit.Assert.*

import org.junit.Test

import de.neuland.imagecrawler.config.LocaleConfig
import de.neuland.imagecrawler.constants.TestConstants

class FolderCrawlerTest {

    private FolderCrawler crawler = new FolderCrawler()

    final private def localeConfigs = [
        [
            lang : "de",
            imageReplacements : [
                    [key : ".lang", value : "de"] as ImageReplacements,
                    [key : "imageServerUrl", value : "http://media.example.de"] as ImageReplacements,
                    [key : "mailHelper.imagepath", value : "http://media.example.de/example/img/systemmails"] as ImageReplacements,
                    [key : "rc.contextPath", value : "/example"] as ImageReplacements
            ] as List<ImageReplacements>
        ] as LocaleConfig
    ] as List<LocaleConfig>
    final private File file = new File("template.ftl")

    @Before
    public void before(){
        IgnoreConfig.createInstance(TestConstants.IGNORE_PATH)
        KnownImgsCache.createInstance(TestConstants.CACHE_FILE)
    }

    @Test
    public void shouldParseKnownFiles() {
        def result = crawler.crawle(new File("src/test/resources"), null)
        assertEquals(7, result.size)
    }

    @Test
    public void shouldSubstitute() {
        assertEquals([
            "http://media.example.de/example/de"] as Set,
        crawler.substitute(file, '${imageServerUrl}${rc.contextPath}/${.lang}', localeConfigs))
    }

    @Test
    public void shouldSubstituteMail() {
        assertEquals([
            "http://media.example.de/example/img/systemmails"] as Set,
        crawler.substitute(file, '${mailHelper.imagepath}', localeConfigs))
    }

    @Test
    public void shouldPrependProtocol() {
        assertEquals([
            "http://media.example.de/de"] as Set,
        crawler.substitute(file, '//media.example.de/de', localeConfigs))
    }

    @Test
    public void shouldPrependProtocolWithNoLocle() {
        assertEquals([
            "http://media.example.de/de"] as Set,
        crawler.substitute(file, '//media.example.de/de', null))
    }

    @Test
    public void shouldIgnoreImagesFromShop() {
        assertEquals([] as Set,
        crawler.substitute(file, '/example/myImage.jpg', localeConfigs))
    }

    @Test
    public void shouldIgnoreUnknownSubstitutes() {
        assertEquals([] as Set,
        crawler.substitute(file, '${SOMETHING}', localeConfigs))
    }

    @Test
    public void shouldIgnoreMacros() {
        assertEquals([] as Set,
        crawler.substitute(file, '<@SOMETHING />', localeConfigs))
    }

    @Test
    public void shouldIgnoreImagesWithoutServerUrl() {
        assertEquals([] as Set,
        crawler.substitute(file, 'myImage.jpg', localeConfigs))
    }

    @Test
    public void shouldParseAllFilesIfNoLocaleIsConfigured() {
        def file = new File("template_pl.ftl");
        assertTrue(crawler.shouldParse(file, null))
        assertTrue(crawler.shouldParse(file, [] as List<LocaleConfig>))
    }

    @Test
    public void shouldIgnoreNonCofiguredLocaleFiles() {
        def file = new File("template_pl.ftl");
        assertFalse(crawler.shouldParse(file, localeConfigs))
    }

    @Test
    public void shouldIgnoreNonCofiguredLocaleFolder() {
        def file = new File("/pl/template.ftl");
        assertFalse(crawler.shouldParse(file, localeConfigs))
    }

    @Test
    public void shouldIgnoreFilesMarkedForDeletion() {
        def file = new File("/somewhere/del_test.ftl");
        assertFalse(crawler.shouldParse(file, localeConfigs))
    }

    @Test
    public void shouldIgnoreFoldersMarkedForDeletion() {
        def file = new File("/del_somewhere/test.ftl");
        assertFalse(crawler.shouldParse(file, localeConfigs))
    }

    @Test
    public void shouldExtractLocaleFromFilename() {
        assertEquals("pl", crawler.extractFileLocale("test_pl.ftl"))
    }

    @Test
    public void shouldExtractLocaleFromLongFilename() {
        assertEquals("fr", crawler.extractFileLocale("localize-manually\\vst_details_beauty_services_fr.ftl"))
    }
    
    @Test
    public void shouldExtractLocaleFromDefaultFilename() {
        assertEquals(null, crawler.extractFileLocale("test.ftl"))
    }

    @Test
    public void shouldExtractLocaleFromFolderWindows() {
        assertEquals("pl", crawler.extractFolderLocale("\\pl\\test.ftl"))
    }

    @Test
    public void shouldExtractLocaleFromFolderLinux() {
        assertEquals("pl", crawler.extractFolderLocale("/pl/test.ftl"))
    }
    
    @Test
    public void shouldSubstituteWithMultipleLocales() {
        def mutliLocaleConfigs = [
            [
                lang : "de",
                imageReplacements : [
                        [key : ".lang", value : "de"] as ImageReplacements,
                        [key : "imageServerUrl", value : "http://media.example.de"] as ImageReplacements,
                        [key : "mailHelper.imagepath", value : "http://media.example.de/example/img/systemmails"] as ImageReplacements,
                        [key : "rc.contextPath", value : "/example"] as ImageReplacements
                ] as List<ImageReplacements>
            ] as LocaleConfig,
            [
                lang : "fr",
                imageReplacements : [
                        [key : ".lang", value : "fr"] as ImageReplacements,
                        [key : "imageServerUrl", value : "http://media.example.fr"] as ImageReplacements,
                        [key : "mailHelper.imagepath", value : "http://media.example.fr/example/img/systemmails"] as ImageReplacements,
                        [key : "rc.contextPath", value : "/example"] as ImageReplacements
                ] as List<ImageReplacements>
            ] as LocaleConfig
        ] as List<LocaleConfig>

        assertEquals([
            "http://media.example.de/example/de", "http://media.example.fr/example/fr"] as Set,
        crawler.substitute(file, '${imageServerUrl}${rc.contextPath}/${.lang}', mutliLocaleConfigs))
    }

    @Test
    public void shouldOnlySubstituteLocaleFilesWithMultipleLocalesOnce() {
        def mutliLocaleConfigs = [
                [
                        lang : "de",
                        imageReplacements : [
                                [key : ".lang", value : "de"] as ImageReplacements,
                                [key : "imageServerUrl", value : "http://media.example.de"] as ImageReplacements,
                                [key : "mailHelper.imagepath", value : "http://media.example.de/example/img/systemmails"] as ImageReplacements,
                                [key : "rc.contextPath", value : "/example"] as ImageReplacements
                        ] as List<ImageReplacements>
                ] as LocaleConfig,
                [
                        lang : "fr",
                        imageReplacements : [
                                [key : ".lang", value : "fr"] as ImageReplacements,
                                [key : "imageServerUrl", value : "http://media.example.fr"] as ImageReplacements,
                                [key : "mailHelper.imagepath", value : "http://media.example.fr/example/img/systemmails"] as ImageReplacements,
                                [key : "rc.contextPath", value : "/example"] as ImageReplacements
                        ] as List<ImageReplacements>
                ] as LocaleConfig
        ] as List<LocaleConfig>
        File file = new File("test_fr.ftl")

        assertEquals([
            "http://media.example.fr/example/fr"] as Set,
        crawler.substitute(file, '${imageServerUrl}${rc.contextPath}/${.lang}', mutliLocaleConfigs))
    }

    @Test
    public void shouldParseDefaultLocalFilesIfNoSpecificFileExists() {
        def localeConfigPl =  [ lang : "pl" ] as LocaleConfig

        def result = crawler.shouldParseWithLocale(new File("src/test/resources/locale/file.ftl"), localeConfigPl)
        assertTrue(crawler.shouldParse(file, localeConfigs))
    }
}


