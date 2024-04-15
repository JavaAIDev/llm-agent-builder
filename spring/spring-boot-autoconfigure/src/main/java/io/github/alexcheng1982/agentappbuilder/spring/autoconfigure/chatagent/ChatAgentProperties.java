package io.github.alexcheng1982.agentappbuilder.spring.autoconfigure.chatagent;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "io.github.alexcheng1982.agentappbuilder.chatagent")
public class ChatAgentProperties {

  private boolean enabled = true;

  private String id = null;

  private String name = "ChatAgent";

  private String description = "A conversational chat agent";

  private String usageInstruction = "Ask me anything";

  private ReActJson reActJson = new ReActJson();

  private Memory memory = new Memory();

  private Tracing tracing = new Tracing();

  private Metrics metrics = new Metrics();

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
}
