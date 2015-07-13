package de.neuland.imagecrawler.parser;
import static org.junit.Assert.*

import org.junit.Test

class FreemarkerParserTest {
    Parser parser = new FreemarkerParser()

    @Test
    public void shouldParseImageTag() {
        def result = parser.parseFile(new File("src/test/resources/freemarker/img.ftl"))
        assertEquals(["http://groovy.codehaus.org/images/groovy-logo-medium.png"] as Set, result)
    }

    @Test
    public void shouldParseInputTag() {
        def result = parser.parseFile(new File("src/test/resources/freemarker/input.ftl"))
        assertEquals(["http://groovy.codehaus.org/images/groovy-logo-medium.png"] as Set, result)
    }
}
