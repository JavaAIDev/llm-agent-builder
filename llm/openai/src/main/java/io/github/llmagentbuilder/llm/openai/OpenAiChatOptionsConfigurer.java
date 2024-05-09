package io.github.llmagentbuilder.llm.openai;

import io.github.llmagentbuilder.core.planner.ChatOptionsConfigurer;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.ModelOptionsUtils;
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
    var updatedOptions = new OpenAiChatOptions();
    ModelOptionsUtils.merge(options, updatedOptions, OpenAiChatOptions.class);
    if (config.getStopSequence() != null) {
      updatedOptions.setStop(config.getStopSequence());
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
