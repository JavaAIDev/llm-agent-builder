package io.github.alexcheng1982.agentappbuilder.tool.extractwebpagecontent

import org.junit.jupiter.api.Test

class WebPageExtractionToolTest {
    @Test
    fun testExtraction() {
        val tool = WebPageExtractionToolFactory().create()
        val content =
            tool.apply(WebPageExtractionRequest("https://www.msa.gov.cn/page/article.do?articleId=DA9B2615-9EC2-47A6-ACF5-AA9B574B1216&channelId=86DE2FFF-FF2C-47F9-8359-FD1F20D6508F")).content
        println(content)
    }
}