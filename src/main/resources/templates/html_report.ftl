<?xml version="1.0" ?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Report ImageCrawler</title>
<script src="https://code.jquery.com/jquery-2.0.3.min.js"></script>
<style>
h1, h2{
    font-size: 22px;
    margin-top: 10px;
    margin-bottom: 10px;
    font-weight: bold;
    text-align: center;
}

h2{
    font-size: 15px;
    font-weight: normal;
}

body {
    font-family: verdana, helvetica, arial, sans-serif;
    font-size: 12px;
}

.file,.images {
    font-family: "courier new";
}

li {
    list-style: none;
}

ul {
    display: table;
    padding-left: 0px;
    width: 80%;
    text-align: left;
}

li.file {
    border-width: 1px;
    border-style: solid;
    border-color: gray;
    margin-bottom: 3px;
}

.header {
    padding: 10px;
    background-color: #EDEDED;
}

li.file ul {
    color: black;
    background-color: white;
    padding-left: 20px;
}

li.missingImage {
    color: red;
}

a {
    color: black;
    text-decoration: none;
}
</style>

<script type="text/javascript">
    //<![CDATA[
    $(document).ready(function() {
        $(".file:has(.missingImage)").addClass('missingImage');
        $(".foundImage").hide();
        $(".file:not(.missingImage)").hide();

        $(".toggleFoundImages").click(function() {
            $(".foundImage").toggle();
        });
        $(".toggleFiles").click(function() {
            $(".file:not(.missingImage)").toggle();
        });
    });
    //]]>
</script>
</head>
<body>
    <h1>Report ImageCrawler - ${reportDate?datetime}</h1>
    <h2>
        Parsed ${fileCounter} files and processed ${imageCounter} images.<br />
        Found ${failedImageCounter} missing image references. <br />
        This is a missing hit ration of ${ratio?string("###.##%")}.
    </h2>
    <center>
        <a href="#" class="toggleFoundImages">[ found images (on/off)]</a> <a
            href="#" class="toggleFiles">[ files with missing images
            (on/off)]</a>
        <ul class="files">
            <#list files as fileResult>
                <li class="file"><div class="header">${fileResult.file.absoluteFile}</div>
                    <#list fileResult.images as imageResult>
                        <ul class="images">
                            <#if imageResult.found>
                                <li class="foundImage"><a
                                    href="${imageResult.image}"
                                    target="_blank">
                                        ${imageResult.image}</a>
                                </li>
                            <#else>
                                <li class="missingImage">${imageResult.image}</li>
                            </#if>
                        </ul>
                    </#list>
                </li>
            </#list>
        </ul>
        <a href="#" class="toggleFoundImages">[ found images (on/off)]</a> <a
            href="#" class="toggleFiles">[ files with missing images
            (on/off)]</a>
    </center>
</body>
</html>
