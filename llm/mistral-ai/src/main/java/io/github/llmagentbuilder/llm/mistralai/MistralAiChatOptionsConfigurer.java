package io.github.llmagentbuilder.llm.mistralai;

import io.github.llmagentbuilder.core.planner.ChatOptionsConfigurer;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.model.ModelOptionsUtils;

public class MistralAiChatOptionsConfigurer implements ChatOptionsConfigurer {

  @Override
  public boolean supports(@NotNull ChatOptions chatOptions) {
    return chatOptions instanceof MistralAiChatOptions;
  }

  @NotNull
  @Override
  public ChatOptions configure(@NotNull ChatOptions chatOptions,
      @NotNull ChatOptionsConfig config) {
    var options = (MistralAiChatOptions) chatOptions;
    var updatedOptions = new MistralAiChatOptions();
    updatedOptions = ModelOptionsUtils.merge(options, updatedOptions,
        MistralAiChatOptions.class);
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
