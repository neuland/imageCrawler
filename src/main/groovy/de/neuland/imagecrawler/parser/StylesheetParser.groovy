package de.neuland.imagecrawler.parser

import java.util.regex.Pattern

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


class StylesheetParser implements Parser {
    private final Logger logger = LogManager.getLogger(StylesheetParser.class);
    private final Pattern URL_PATTERN = ~/url\(['"]?([^\'")]*)['"]?\)/

    @Override
    Set<String> parseFile(File f) {
        def result = []

        def matcher = URL_PATTERN.matcher(f.text)
        matcher.every {
            result << matcher.group(1)
        }
        return result
    }

    @Override
    boolean isParserFor(File f) {
        def fileName = f.getName().toLowerCase()
        return fileName.endsWith(".css") && !fileName.contains("generated")
    }
}
