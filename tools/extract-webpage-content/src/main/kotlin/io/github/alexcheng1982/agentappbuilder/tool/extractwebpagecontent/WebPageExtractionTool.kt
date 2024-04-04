package io.github.alexcheng1982.agentappbuilder.tool.extractwebpagecontent

import io.github.alexcheng1982.agentappbuilder.core.tool.BaseConfigurableAgentToolFactory
import io.github.alexcheng1982.agentappbuilder.core.tool.ConfigurableAgentTool
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist

data class WebPageExtractionToolConfig(val limit: Int = 1000)

class WebPageExtractionTool(private val config: WebPageExtractionToolConfig) :
    ConfigurableAgentTool<WebPageExtractionRequest, WebPageExtractionResponse, WebPageExtractionToolConfig> {
    override fun name(): String {
        return "extractWebPageContent"
    }

    override fun description(): String {
        return "extract web page content by url"
    }

    override fun apply(request: WebPageExtractionRequest): WebPageExtractionResponse {
        val doc = Jsoup.connect(request.url).get()
        doc.select("*").removeAttr("style")
        listOf(
            "script",
            "style",
            "link",
            "textarea",
            "input",
            "button"
        ).forEach {
            doc.select(it).remove()
        }
        doc.select("a").unwrap()
        val body = doc.body()
        val text = Jsoup.clean(body.html(), Safelist.none())
        return WebPageExtractionResponse(
            text.take(config.limit)
        )
    }
}

class WebPageExtractionToolFactory :
    BaseConfigurableAgentToolFactory<WebPageExtractionTool, WebPageExtractionToolConfig>(
        {
            WebPageExtractionToolConfig()
        }) {
    override fun create(config: WebPageExtractionToolConfig): WebPageExtractionTool {
        return WebPageExtractionTool(config)
    }

}