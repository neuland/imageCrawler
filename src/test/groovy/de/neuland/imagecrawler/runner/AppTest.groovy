package de.neuland.imagecrawler.runner;

import static org.junit.Assert.*

import org.junit.Test

class AppTest {

    @Test
    public void shouldGetParameter() {
        assertNull App.getParam("f", [])
        assertEquals "value", App.getParam("f", ["-f=value"])
        assertEquals "value", App.getParam("f", ["-f=value", "-b=No"])
    }

    // TODO : just a WA
    @Test
    public void shouldFindUseInternalIgnore() {
        String input = App.getParam("useInternalIgnore", ["-useInternalIgnore=true"])
        assertEquals("true",input)
        boolean r = App.findUseInternalIgnore(input)
        assertTrue(r)
    }

    @Test
    public void shouldNeedsHelp() {
        assertTrue(App.needsHelp(["-h"]))
        assertTrue(App.needsHelp(["-help"]))
    }

    @Test
    public void shouldNotNeedsHelp(){
        assertFalse(App.needsHelp(["-hallo"]))
        assertFalse(App.needsHelp(["-helpME"]))
    }
}