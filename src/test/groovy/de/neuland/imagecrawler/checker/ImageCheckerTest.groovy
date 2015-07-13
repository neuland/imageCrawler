package de.neuland.imagecrawler.checker

import de.neuland.imagecrawler.cache.KnownImgsCache
import de.neuland.imagecrawler.config.IgnoreConfig

import static org.junit.Assert.*
import de.neuland.imagecrawler.constants.TestConstants
import org.gmock.GMockController
import org.gmock.WithGMock
import org.junit.Before
import org.junit.Test


@WithGMock
class ImageCheckerTest {
    private ImageChecker checker;
    private GMockController gmc
    private URL url
    private HttpURLConnection connection

    @Before
    public void setup() {
        IgnoreConfig.createInstance(TestConstants.IGNORE_PATH)
        KnownImgsCache.createInstance(TestConstants.CACHE_FILE)
        checker = new ImageChecker()

        gmc = new GMockController()
        connection = gmc.mock(HttpURLConnection)
        connection.setRequestMethod("HEAD")
        url = gmc.mock(URL)
        url.openConnection().returns(connection).once()
        url.toExternalForm().returns("http://groovy.codehaus.org/images/groovy-logo-medium.png").stub()
    }

    @Test
    public void shouldExists() {
        connection.getResponseCode().returns(200)
        gmc.play {
            assertTrue checker.existsWithoutCache(url)
        }
    }

    @Test
    public void shouldNotExists() {
        connection.getResponseCode().returns(404)
        gmc.play {
            assertFalse checker.existsWithoutCache(url)
        }
    }
}