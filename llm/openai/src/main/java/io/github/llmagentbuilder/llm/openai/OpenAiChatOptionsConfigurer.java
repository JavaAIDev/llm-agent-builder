package io.github.llmagentbuilder.llm.openai;

import io.github.llmagentbuilder.core.planner.ChatOptionsConfigurer;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;

public class OpenAiChatOptionsConfigurer implements ChatOptionsConfigurer {

  @Override
  public boolean supports(@NotNull ChatOptions chatOptions) {
    return chatOptions instanceof OpenAiChatOptions;
  }

  @NotNull
  @Override
  public ChatOptions configure(@NotNull ChatOptions chatOptions,
      @NotNull ChatOptionsConfig config) {
    var options = (OpenAiChatOptions) chatOptions;
    if (config.getStopSequence() != null) {
      options.setStop(config.getStopSequence());
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
