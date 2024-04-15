package io.github.alexcheng1982.agentappbuilder.tool.writelocalfile;

import io.github.alexcheng1982.agentappbuilder.core.tool.EnvironmentVariableConfigurableAgentToolFactory;
import org.jetbrains.annotations.NotNull;

public class WriteLocalFileToolFactory extends
    EnvironmentVariableConfigurableAgentToolFactory<WriteLocalFileTool, WriteLocalFileConfig> {

  public WriteLocalFileToolFactory() {
    super(WriteLocalFileConfig.class, "writeLocalFile_");
  }

  @NotNull
  @Override
  public WriteLocalFileTool create(WriteLocalFileConfig writeLocalFileConfig) {
    return new WriteLocalFileTool(writeLocalFileConfig);
  }
}
