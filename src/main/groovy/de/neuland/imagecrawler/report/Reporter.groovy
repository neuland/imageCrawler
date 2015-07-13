package de.neuland.imagecrawler.report

import freemarker.template.Configuration
import freemarker.template.Template

class Reporter {
    public void createReport(Writer out, def reportData, def template) {
        Configuration cfg = FreemarkerConfig.instance
        Template htmlReportTemplate = cfg.getTemplate(template)
        htmlReportTemplate.process(reportData, out)
    }
}
