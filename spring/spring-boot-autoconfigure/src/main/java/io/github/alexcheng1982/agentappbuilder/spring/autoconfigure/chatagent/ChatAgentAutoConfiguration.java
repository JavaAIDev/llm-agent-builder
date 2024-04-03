package io.github.alexcheng1982.agentappbuilder.spring.autoconfigure.chatagent;

import io.github.alexcheng1982.agentappbuilder.core.Agent;
import io.github.alexcheng1982.agentappbuilder.core.AgentFactory;
import io.github.alexcheng1982.agentappbuilder.core.ChatAgent;
import io.github.alexcheng1982.agentappbuilder.core.Planner;
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemoryStore;
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.InMemoryChatMemoryStore;
import io.github.alexcheng1982.agentappbuilder.core.planner.reactjson.ReActJsonPlanner;
import io.github.alexcheng1982.agentappbuilder.spring.AgentToolFunctionCallbackContext;
import io.github.alexcheng1982.agentappbuilder.spring.chatagent.ChatAgentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.autoconfigure.ollama.OllamaAutoConfiguration;
import org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration(before = WebMvcAutoConfiguration.class, after = {
    OllamaAutoConfiguration.class, OpenAiAutoConfiguration.class})
@ConditionalOnProperty(prefix = "io.github.alexcheng1982.agentappbuilder.chatagent", name = "enabled", matchIfMissing = true)
public class ChatAgentAutoConfiguration {

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnClass(Agent.class)
  @ConditionalOnBean(ChatClient.class)
  @ConditionalOnMissingBean(Agent.class)
  @EnableConfigurationProperties(ChatAgentProperties.class)
  public static class ChatAgentConfiguration {

    private final ChatAgentProperties properties;

    ChatAgentConfiguration(ChatAgentProperties properties) {
      this.properties = properties;
    }

    @Bean
    @ConditionalOnProperty(prefix = "io.github.alexcheng1982.agentappbuilder.chatagent.memory", name = "enabled", matchIfMissing = true)
    @ConditionalOnMissingBean
    public ChatMemoryStore chatMemoryStore() {
      return new InMemoryChatMemoryStore();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ChatMemoryStore.class)
    public Planner plannerWithMemory(ChatClient chatClient,
        ChatMemoryStore chatMemoryStore) {
      return ReActJsonPlanner.Companion.createDefault(
          chatClient,
          properties.getReActJson().getSystemInstructions(),
          chatMemoryStore
      );
    }

    @Bean
    @ConditionalOnMissingBean({Planner.class, ChatMemoryStore.class})
    public Planner plannerWithoutMemory(ChatClient chatClient) {
      return ReActJsonPlanner.Companion.createDefault(
          chatClient,
          StringUtils.trimToNull(
              properties.getReActJson().getSystemInstructions()),
          null
      );
    }

    @Bean
    @ConditionalOnBean(Planner.class)
    public ChatAgent chatAgent(Planner planner) {
      return AgentFactory.INSTANCE.createChatAgent(planner, "ChatAgent",
          "Auto-configured chat agent");
    }

    @Bean
    @ConditionalOnBean(ChatAgent.class)
    public ChatAgentService chatAgentService(ChatAgent chatAgent) {
      return new ChatAgentService(chatAgent);
    }

    @Bean
    @ConditionalOnMissingBean
    public FunctionCallbackContext springAiFunctionManager(
        ApplicationContext context) {
      var manager = new AgentToolFunctionCallbackContext();
      manager.setApplicationContext(context);
      return manager;
    }
  }
}
