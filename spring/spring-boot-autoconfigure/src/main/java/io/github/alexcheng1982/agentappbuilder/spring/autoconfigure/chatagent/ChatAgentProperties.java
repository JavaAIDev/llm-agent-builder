package io.github.alexcheng1982.agentappbuilder.spring.autoconfigure.chatagent;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "io.github.alexcheng1982.agentappbuilder.chatagent")
public class ChatAgentProperties {

  private boolean enabled = true;

  private ReActJson reActJson = new ReActJson();

  private Memory memory = new Memory();

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
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
}
