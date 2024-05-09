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
    var updatedOptions = options.createCopy();
    if (config.getStopSequence() != null) {
      updatedOptions.setStops(config.getStopSequence());
    }
    if (config.getFunctions() != null) {
      var toolNames = new HashSet<>(config.getFunctions());
      if (options.getFunctions() != null) {
        toolNames.addAll(options.getFunctions());
      }
      updatedOptions.setFunctions(toolNames);
    }
    return updatedOptions;
  }
}
