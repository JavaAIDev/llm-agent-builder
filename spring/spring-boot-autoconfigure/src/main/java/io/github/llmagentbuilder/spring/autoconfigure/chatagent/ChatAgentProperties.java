package io.github.llmagentbuilder.spring.autoconfigure.chatagent;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = ChatAgentProperties.CONFIG_PREFIX)
public class ChatAgentProperties {

  public static final String CONFIG_PREFIX = "io.github.llmagentbuilder.chatagent";

  private boolean enabled = true;

  private String id = null;

  private String name = "ChatAgent";

  private String description = "A conversational chat agent";

  private String usageInstruction = "Ask me anything";

  @NestedConfigurationProperty
  private ReActJson reActJson = new ReActJson();

  @NestedConfigurationProperty
  private Memory memory = new Memory();

  @NestedConfigurationProperty
  private Tracing tracing = new Tracing();

  @NestedConfigurationProperty
  private Metrics metrics = new Metrics();

  @NestedConfigurationProperty
  private Tools tools = new Tools();

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUsageInstruction() {
    return usageInstruction;
  }

  public void setUsageInstruction(String usageInstruction) {
    this.usageInstruction = usageInstruction;
  }

  public ReActJson getReActJson() {
    return reActJson;
  }

  public void setReActJson(ReActJson reActJson) {
    this.reActJson = reActJson;
  }

  public Memory getMemory() {
    return memory;
  }

  public void setMemory(
      Memory memory) {
    this.memory = memory;
  }

  public Tracing getTracing() {
    return tracing;
  }

  public void setTracing(
      Tracing tracing) {
    this.tracing = tracing;
  }

  public Metrics getMetrics() {
    return metrics;
  }

  public void setMetrics(
      Metrics metrics) {
    this.metrics = metrics;
  }

  public Tools getTools() {
    return tools;
  }

  public void setTools(
      Tools tools) {
    this.tools = tools;
  }

  public boolean tracingEnabled() {
    return tracing == null || tracing.isEnabled();
  }

  public boolean metricsEnabled() {
    return metrics == null || metrics.isEnabled();
  }

  public static class ReActJson {

    private String systemInstructions;

    public String getSystemInstructions() {
      return systemInstructions;
    }

    public void setSystemInstructions(String systemInstructions) {
      this.systemInstructions = systemInstructions;
    }
  }

  public static class Memory {

    private boolean enabled = true;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }

  public static class Tracing {

    private boolean enabled = true;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }

  public static class Metrics {

    private boolean enabled = true;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }

  public static class Tools {

    private Map<String, Map<String, Object>> config;

    public Map<String, Map<String, Object>> getConfig() {
      return config;
    }

    public void setConfig(
        Map<String, Map<String, Object>> config) {
      this.config = config;
    }
  }
}
