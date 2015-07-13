package de.neuland.imagecrawler.crawler

class FileResult {
    private File file
    private Set<String> images = []

    @Override
    public String toString() {
        return "$file -> $images"
    }

}
