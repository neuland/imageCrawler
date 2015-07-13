package de.neuland.imagecrawler.config;

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import de.neuland.imagecrawler.constants.TestConstants

class IgnoreConfigTest {
    private IgnoreConfig ignoreConfig
    @Before
    public void before(){
        ignoreConfig = new IgnoreConfig(TestConstants.IGNORE_PATH)
    }

    @Test
    public void shouldIgnoreFile() {
        ignoreConfig.ignoreFiles = ["de": [".*test.*"]]
        assertTrue ignoreConfig.shouldFileBeIgnored("de", "test.ftl")
    }
}
