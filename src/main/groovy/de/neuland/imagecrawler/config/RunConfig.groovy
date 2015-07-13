package de.neuland.imagecrawler.config

class RunConfig {
    String baseFolder
    LocaleConfig[] localeConfigs

    @Override
    public String toString() {
        return "RunConfig{" +
                "baseFolder='" + baseFolder + '\'' +
                ", localeConfigs=" + arrayToString(localeConfigs) +
                '}';
    }

    public static String arrayToString(Object[] o){
        return (o == null) ? "array was null" : Arrays.toString(o)
    }
}

class LocaleConfig {
    String lang
    ImageReplacements[] imageReplacements

    @Override
    public String toString() {
        return "LocaleConfig{" +
                "lang='" + lang + '\'' +
                ", imageReplacements=" + RunConfig.arrayToString(imageReplacements) +
                '}';
    }
}

class ImageReplacements {
    String key
    String value

    @Override
    public String toString() {
        return "ImageReplacements{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
