package io.github.llmagentbuilder.llm.openai;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.llmagentbuilder.core.planner.ChatOptionsConfigurer.ChatOptionsConfig;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.OpenAiChatOptions;

class OpenAiChatOptionsConfigurerTest {

  @Test
  void testConfigure() {
    var configurer = new OpenAiChatOptionsConfigurer();
    var defaultOptions = OpenAiChatOptions.builder().withModel("test").build();
    var config = new ChatOptionsConfig(Set.of("add"), List.of("stop"));
    assertTrue(configurer.supports(defaultOptions));
    var updated = (OpenAiChatOptions) configurer.configure(defaultOptions,
        config);
    assertNotNull(updated.getStop());
    assertTrue(updated.getStop().contains("stop"));
    assertNotNull(updated.getFunctions());
    assertTrue(updated.getFunctions().contains("add"));
    assertTrue(CollectionUtils.isEmpty(defaultOptions.getStop()));
    assertTrue(CollectionUtils.isEmpty(defaultOptions.getFunctions()));
  }
}