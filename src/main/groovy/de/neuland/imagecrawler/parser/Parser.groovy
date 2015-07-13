package de.neuland.imagecrawler.parser

interface Parser {
    public Set<String> parseFile(File f)
    public boolean isParserFor(File f)
}
