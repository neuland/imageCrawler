package de.neuland.imagecrawler.checker

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import de.neuland.imagecrawler.config.IgnoreConfig
import de.neuland.imagecrawler.cache.KnownImgsCache

class ImageChecker {
    private Logger logger = LogManager.getLogger(ImageChecker.class)

    public boolean shouldCheck(String image) {
        !IgnoreConfig.getInstance().shouldImageBeIgnored(image)
    }

    public boolean exists(URL url) {
        assert url != null
        final def result

        def resultFromCache = KnownImgsCache.getInstance().getStatus(url)
        if(resultFromCache == null || !resultFromCache) {
            result = existsWithoutCache(url)
            if(resultFromCache == null){
                KnownImgsCache.getInstance().put(url, result)
            }else if(!resultFromCache && result){
                KnownImgsCache.getInstance().overwriteLine(url) // Wird nur ueberschrieben wenn Es nun vorhanden ist.
            }                
        } else {
            result = resultFromCache
        }

        result
    }

    private boolean existsWithoutCache(URL url) {
        try {
            HttpURLConnection connection = url.openConnection();
            connection.setRequestMethod("HEAD");

            return connection.getResponseCode() == 200
        } catch (Exception e) {
            logger.error ("Unable to check URL=$url", e)
        }
    }
}
