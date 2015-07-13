package de.neuland.imagecrawler.parser

import static org.junit.Assert.*

import org.junit.Test

class StylesheetParserTest {
    Parser parser = new StylesheetParser()

    @Test
    public void shouldParseUrl() {
        def result = parser.parseFile(new File("src/test/resources/stylesheet/background.css"))
        assertEquals(["http://groovy.codehaus.org/images/groovy-logo-medium.png"] as Set, result)
    }

    @Test
    public void shouldParseCompressedFiles() {
        def result = parser.parseFile(new File("src/test/resources/stylesheet/compressed.css"))
        assertEquals(["http://groovy.codehaus.org/images/groovy-logo-medium.png"] as Set, result)
    }

    @Test
    public void shouldParseCommentedUrl() {
        def result = parser.parseFile(new File("src/test/resources/stylesheet/comment.css"))
        assertEquals(["http://groovy.codehaus.org/images/groovy-logo-medium.png"] as Set, result)
    }

    @Test
    public void shouldParseQuatedUrl() {
        def result = parser.parseFile(new File("src/test/resources/stylesheet/background-quated.css"))
        assertEquals(["http://groovy.codehaus.org/images/groovy-logo-medium.png"] as Set, result)
    }

    @Test
    public void shouldParseDoublequatedUrl() {
        def result = parser.parseFile(new File("src/test/resources/stylesheet/background-doublequated.css"))
        assertEquals(["http://groovy.codehaus.org/images/groovy-logo-medium.png"] as Set, result)
    }
}
