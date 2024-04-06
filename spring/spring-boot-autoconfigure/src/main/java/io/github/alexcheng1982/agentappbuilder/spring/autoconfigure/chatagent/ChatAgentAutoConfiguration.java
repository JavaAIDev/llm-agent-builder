package io.github.alexcheng1982.agentappbuilder.spring.autoconfigure.chatagent;

import io.github.alexcheng1982.agentappbuilder.core.Agent;
import io.github.alexcheng1982.agentappbuilder.core.AgentFactory;
import io.github.alexcheng1982.agentappbuilder.core.ChatAgent;
import io.github.alexcheng1982.agentappbuilder.core.Planner;
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemoryStore;
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.InMemoryChatMemoryStore;
import io.github.alexcheng1982.agentappbuilder.core.planner.reactjson.ReActJsonPlannerFactory;
import io.github.alexcheng1982.agentappbuilder.core.tool.AgentToolsProvider;
import io.github.alexcheng1982.agentappbuilder.core.tool.AutoDiscoveredAgentToolsProvider;
import io.github.alexcheng1982.agentappbuilder.core.tool.CompositeAgentToolsProvider;
import io.github.alexcheng1982.agentappbuilder.spring.AgentToolFunctionCallbackContext;
import io.github.alexcheng1982.agentappbuilder.spring.SpringAgentToolsProvider;
import io.github.alexcheng1982.agentappbuilder.spring.chatagent.ChatAgentService;
import io.micrometer.observation.ObservationRegistry;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.autoconfigure.ollama.OllamaAutoConfiguration;
import org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationAutoConfiguration;
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
    OllamaAutoConfiguration.class, OpenAiAutoConfiguration.class,
    ObservationAutoConfiguration.class})
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
        ChatMemoryStore chatMemoryStore,
        AgentToolsProvider agentToolsProvider,
        ObservationRegistry observationRegistry) {
      return ReActJsonPlannerFactory.INSTANCE.create(
          chatClient,
          agentToolsProvider,
          properties.getReActJson().getSystemInstructions(),
          chatMemoryStore,
          observationRegistry
      );
    }

    @Bean
    @ConditionalOnMissingBean({Planner.class, ChatMemoryStore.class})
    public Planner plannerWithoutMemory(ChatClient chatClient,
        AgentToolsProvider agentToolsProvider,
        ObservationRegistry observationRegistry) {
      return ReActJsonPlannerFactory.INSTANCE.create(
          chatClient,
          agentToolsProvider,
          StringUtils.trimToNull(
              properties.getReActJson().getSystemInstructions()),
          null,
          observationRegistry
      );
    }

    @Bean
    @ConditionalOnBean(Planner.class)
    @ConditionalOnMissingBean
    public ObservationRegistry observationRegistry() {
      return ObservationRegistry.NOOP;
    }

    @Bean
    @ConditionalOnBean(Planner.class)
    public ChatAgent chatAgent(Planner planner,
        AgentToolsProvider agentToolsProvider,
        ObservationRegistry observationRegistry) {
      return AgentFactory.INSTANCE.createChatAgent(
          planner,
          properties.getName(),
          properties.getDescription(),
          properties.getUsageInstruction(),
          agentToolsProvider,
          observationRegistry);
    }

    @Bean
    @ConditionalOnBean(ChatAgent.class)
    public ChatAgentService chatAgentService(ChatAgent chatAgent) {
      return new ChatAgentService(chatAgent);
    }

    @Bean
    @ConditionalOnMissingBean
    public FunctionCallbackContext springAiFunctionManager(
        AgentToolsProvider agentToolsProvider,
        ObservationRegistry observationRegistry,
        ApplicationContext context) {
      var manager = new AgentToolFunctionCallbackContext(agentToolsProvider,
          observationRegistry);
      manager.setApplicationContext(context);
      return manager;
    }

    @Bean
    @ConditionalOnMissingBean
    public AgentToolsProvider agentToolsProvider(ApplicationContext context) {
      var springAgentToolsProvider = new SpringAgentToolsProvider();
      springAgentToolsProvider.setApplicationContext(context);
      return new CompositeAgentToolsProvider(List.of(
          AutoDiscoveredAgentToolsProvider.INSTANCE,
          springAgentToolsProvider
      ));
    }
  }
}
