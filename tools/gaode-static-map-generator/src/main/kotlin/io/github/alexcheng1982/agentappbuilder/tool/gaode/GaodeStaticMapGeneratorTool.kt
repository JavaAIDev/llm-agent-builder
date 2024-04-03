package io.github.alexcheng1982.agentappbuilder.tool.gaode

import io.github.alexcheng1982.agentappbuilder.core.BaseConfigurableAgentToolFactory
import io.github.alexcheng1982.agentappbuilder.core.ConfigurableAgentTool
import io.github.alexcheng1982.gaode.StaticMap
import io.github.alexcheng1982.gaode.StaticMapGenerator
import io.github.alexcheng1982.gaode.param.*
import org.slf4j.LoggerFactory

class GaodeStaticMapGeneratorTool(private val config: ToolConfig) :
    ConfigurableAgentTool<GaodeStaticMapGenerationRequest, GaodeStaticMapGenerationResponse, ToolConfig> {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun name() = "gaodeStaticMap"

    override fun description() = "Generate URL of Gaode static maps"

    override fun apply(request: GaodeStaticMapGenerationRequest): GaodeStaticMapGenerationResponse {
        logger.info("Generate static map with request {}", request)
        if (request.locations.isEmpty()) {
            return GaodeStaticMapGenerationResponse("")
        }
        val mapBuilder = StaticMap.builder()
            .key(getApiKey())
            .size(MapSize.MAXIMUM)
            .zoom(8)
            .scale(MapScaleMode.HIGH_RES)
        val hasSingleLocation = request.locations.size == 1
        if (hasSingleLocation) {
            val singleLocation = request.locations.first()
            val convertedLocations = convertLocations(
                listOf(
                    singleLocation
                )
            )
            mapBuilder.markers(
                Markers.builder()
                    .markersGroups(
                        listOf(
                            MarkersGroup.builder()
                                .locations(convertedLocations)
                                .style(
                                    MarkerStyle.builder()
                                        .size(MarkerSize.LARGE)
                                        .build()
                                )
                                .build()
                        )
                    )
                    .build()
            ).labels(
                Labels.builder()
                    .labelsGroups(
                        listOf(
                            LabelsGroup.builder()
                                .locations(convertedLocations)
                                .style(
                                    LabelStyle.builder()
                                        .fontSize(16)
                                        .content(
                                            singleLocation.name.substringBefore(
                                                " "
                                            ).take(15)
                                        )
                                        .build()
                                )
                                .build()
                        )
                    )
                    .build()
            )
        } else {
            val locations =
                request.locations + listOf(request.locations.first())
            mapBuilder.paths(
                Paths.builder()
                    .pathsGroups(
                        listOf(
                            PathsGroup.builder()
                                .locations(
                                    convertLocations(locations)
                                )
                                .build()
                        )
                    )
                    .build()
            )
        }
        val url = StaticMapGenerator.generate(
            mapBuilder.build()
        )
        return GaodeStaticMapGenerationResponse(url)
    }

    private fun convertLocations(locations: List<Location>) =
        locations.map(this::convertLocation)

    private fun convertLocation(location: Location) = GeoLocation.builder()
        .lat(location.latitude)
        .lng(location.longitude)
        .build()

    private fun getApiKey(): String {
        return config.apiKey
    }
}

data class ToolConfig(
    val apiKey: String
)

class GaodeStaticMapGeneratorToolFactory :
    BaseConfigurableAgentToolFactory<GaodeStaticMapGeneratorTool, ToolConfig>({
        ToolConfig(
            System.getenv(
                "GAODE_API_KEY"
            )
        )
    }) {
    override fun create(config: ToolConfig): GaodeStaticMapGeneratorTool {
        return GaodeStaticMapGeneratorTool(config)
    }
}