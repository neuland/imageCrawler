<#list files as fileResult>
        <#list fileResult.images as imageResult>
                <#if !imageResult.found>
                    ${imageResult.image}<br/>
                </#if>
        </#list>
</#list>