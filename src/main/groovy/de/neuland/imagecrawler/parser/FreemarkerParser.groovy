package de.neuland.imagecrawler.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class FreemarkerParser implements Parser {
    @Override
    public Set<String> parseFile(File f) {
        def result = []
        Document doc = Jsoup.parse(f, null)

        result.addAll extract(doc, "img", "src")
        result.addAll extract(doc, "input[type=image]", "src")

        return result
    }

    @Override
    public boolean isParserFor(File f) {
        return f.getName().toLowerCase().endsWith(".ftl");
    }

    private def extract(Document doc, selector, attr) {
        return doc.select(selector).contents.collect {
            it.attr(attr)
        }
    }
}
