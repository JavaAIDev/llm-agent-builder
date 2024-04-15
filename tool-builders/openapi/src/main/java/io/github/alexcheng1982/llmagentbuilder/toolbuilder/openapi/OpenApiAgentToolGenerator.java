package io.github.alexcheng1982.llmagentbuilder.toolbuilder.openapi;

import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.languages.JavaClientCodegen;

public class OpenApiAgentToolGenerator extends
    JavaClientCodegen implements CodegenConfig {

  public static final String AGENT_BUILDER_VERSION = "agentBuilderVersion";
  public static final String DEFAULT_AGENT_BUILDER_VERSION = "0.1.0-SNAPSHOT";

  private String agentBuilderVersion;

  public String getAgentBuilderVersion() {
    return agentBuilderVersion;
  }

  public void setAgentBuilderVersion(String agentBuilderVersion) {
    this.agentBuilderVersion = agentBuilderVersion;
  }

  /**
   * Configures a friendly name for the generator.  This will be used by the
   * generator to select the library with the -g flag.
   *
   * @return the friendly name for the generator
   */
  public String getName() {
    return "Java";
  }


  /**
   * Returns human-friendly help for the generator.  Provide the consumer with
   * help tips, parameters here
   *
   * @return A string value for the help message
   */
  public String getHelp() {
    return "Generates a openapi-agent-tool client library.";
  }

  public OpenApiAgentToolGenerator() {
    super();

    setLibrary(NATIVE);

    apiTemplateFiles.put("agentToolConfiguration.mustache",
        "AgentToolConfiguration.java");

    additionalProperties.put(AGENT_BUILDER_VERSION,
        DEFAULT_AGENT_BUILDER_VERSION);
  }

}
