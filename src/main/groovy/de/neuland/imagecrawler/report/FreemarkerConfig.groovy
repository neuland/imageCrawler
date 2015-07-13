package de.neuland.imagecrawler.report;

import freemarker.template.Configuration
import freemarker.template.DefaultObjectWrapper
import freemarker.template.TemplateExceptionHandler
import freemarker.template.Version

public class FreemarkerConfig {
    static Configuration cfg

    static {
        cfg = new Configuration()

        // Specify the data source where the template files come from.
        cfg.setClassForTemplateLoading(FreemarkerConfig.class, "/templates")

        // Specify how templates will see the data-model. This is an advanced topic...
        // for now just use this:
        cfg.setObjectWrapper(new DefaultObjectWrapper())

        // Set your preferred charset template files are stored in. UTF-8 is
        // a good choice in most applications:
        cfg.setDefaultEncoding("UTF-8")

        // Sets how errors will appear. Here we assume we are developing HTML pages.
        // For production systems TemplateExceptionHandler.RETHROW_HANDLER is better.
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER)

        // At least in new projects, specify that you want the fixes that aren't
        // 100% backward compatible too (these are very low-risk changes as far as the
        // 1st and 2nd version number remains):
        cfg.setIncompatibleImprovements(new Version(2, 3, 20))  // FreeMarker 2.3.20
    }

    public static Configuration getInstance() {
        return cfg
    }
}
