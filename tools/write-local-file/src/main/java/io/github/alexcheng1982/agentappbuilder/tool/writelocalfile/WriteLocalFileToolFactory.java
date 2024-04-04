package io.github.alexcheng1982.agentappbuilder.tool.writelocalfile;

import io.github.alexcheng1982.agentappbuilder.core.tool.BaseConfigurableAgentToolFactory;
import org.jetbrains.annotations.NotNull;

public class WriteLocalFileToolFactory extends
    BaseConfigurableAgentToolFactory<WriteLocalFileTool, WriteLocalFileConfig> {

  public WriteLocalFileToolFactory() {
    super(() -> new WriteLocalFileConfig(null));
  }

  @NotNull
  @Override
  public WriteLocalFileTool create(WriteLocalFileConfig writeLocalFileConfig) {
    return new WriteLocalFileTool(writeLocalFileConfig);
  }
}
