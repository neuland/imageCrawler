<?xml version="1.0" encoding="UTF-8" standalone="no"?>


<#escape x as x?html>
<imageCrawlerReport>
    <time>${reportDate?datetime?string["yyyy-MM-dd'T'HH:mm:ss"]}</time>
    <fileCount>${fileCounter?c}</fileCount>
    <imageCount>${imageCounter?c}</imageCount>
    <failedImageCounter>${failedImageCounter?c}</failedImageCounter>
    <ratio>${ratio?c}</ratio>
    <#list files as fileResult>
    <fileResult>
        <fileName>${fileResult.file.absoluteFile}</fileName>
        <#list fileResult.images as imageResult>
        <imageResult>
            <found>${imageResult.found?c}</found>
            <image>${imageResult.image}</image>
        </imageResult>
        </#list>
    </fileResult>
    </#list>
</imageCrawlerReport>
</#escape>
