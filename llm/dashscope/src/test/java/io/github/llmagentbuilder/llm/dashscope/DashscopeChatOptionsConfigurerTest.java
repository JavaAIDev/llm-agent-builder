package io.github.llmagentbuilder.llm.dashscope;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.alexcheng1982.springai.dashscope.DashscopeChatOptions;
import io.github.alexcheng1982.springai.dashscope.api.DashscopeModelName;
import io.github.llmagentbuilder.core.planner.ChatOptionsConfigurer.ChatOptionsConfig;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DashscopeChatOptionsConfigurerTest {

  @Test
  void testConfigure() {
    var configurer = new DashscopeChatOptionsConfigurer();
    var defaultOptions = DashscopeChatOptions.builder().withModel(
        DashscopeModelName.QWEN_MAX).build();
    var config = new ChatOptionsConfig(Set.of("add"), List.of("stop"));
    assertTrue(configurer.supports(defaultOptions));
    var updated = (DashscopeChatOptions) configurer.configure(defaultOptions,
        config);
    assertNotNull(updated.getStops());
    assertTrue(updated.getStops().contains("stop"));
    assertNotNull(updated.getFunctions());
    assertTrue(updated.getFunctions().contains("add"));
  }
}