	package de.neuland.imagecrawler.report

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class ReporterTest {
    private Reporter reporter = new Reporter()
    private StringWriter writer
    private def testData = [
            reportDate: new Date(),
            fileCounter: 12151,
            imageCounter: 154554,
            failedImageCounter: 54,
            ratio: 10.70,
            files: [
            [ file : new File("/test/template.ftl"),
            images:[
                [image:"found.gif", found:true],
                [image:"notFound.gif", found:false]
                ]
            ]
        ]]

    @Before
    public void setup() {
        writer = new StringWriter()
    }

    @Test
    public void runHtmlReport() {
        runReport('html_report.ftl')
    }

    @Test
    public void runXmlReport() {
        runReport('xml_report.ftl')
    }

    private void runReport(def reportType){
        reporter.createReport(writer, testData, reportType)
        println "____________________" + reportType + "____________________"
        println writer.buffer
        assertTrue writer.buffer.contains("found.gif")
        assertTrue writer.buffer.contains("template.ftl")
        assertTrue writer.buffer.contains("notFound.gif")
    }
}
