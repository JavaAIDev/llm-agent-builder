package io.github.llmagentbuilder.llm.dashscope;

import io.github.alexcheng1982.springai.dashscope.DashscopeChatOptions;
import io.github.llmagentbuilder.core.planner.ChatOptionsConfigurer;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.prompt.ChatOptions;

public class DashscopeChatOptionsConfigurer implements ChatOptionsConfigurer {

  @Override
  public boolean supports(@NotNull ChatOptions chatOptions) {
    return chatOptions instanceof DashscopeChatOptions;
  }

  @NotNull
  @Override
  public ChatOptions configure(@NotNull ChatOptions chatOptions,
      @NotNull ChatOptionsConfig config) {
    var options = (DashscopeChatOptions) chatOptions;
    if (config.getStopSequence() != null) {
      options.setStops(config.getStopSequence());
    }
    if (config.getToolNames() != null) {
      var toolNames = new HashSet<>(config.getToolNames());
      if (options.getFunctions() != null) {
        toolNames.addAll(options.getFunctions());
      }
      options.setFunctions(toolNames);
    }
    return options;
  }
}
