package io.github.llmagentbuilder.spring.autoconfigure.chatagent.chatagent;

import io.github.llmagentbuilder.core.Agent;
import io.github.llmagentbuilder.core.AgentFactory;
import io.github.llmagentbuilder.core.ChatAgent;
import io.github.llmagentbuilder.core.Planner;
import io.github.llmagentbuilder.core.chatmemory.ChatMemoryStore;
import io.github.llmagentbuilder.core.chatmemory.InMemoryChatMemoryStore;
import io.github.llmagentbuilder.core.planner.planner.reactjson.ReActJsonPlannerFactory;
import io.github.llmagentbuilder.core.tool.AgentToolFunctionCallbackContext;
import io.github.llmagentbuilder.core.tool.AgentToolsProvider;
import io.github.llmagentbuilder.core.tool.AutoDiscoveredAgentToolsProvider;
import io.github.llmagentbuilder.core.tool.CompositeAgentToolsProvider;
import io.github.llmagentbuilder.spring.spring.SpringAgentToolsProvider;
import io.github.llmagentbuilder.spring.spring.chatagent.ChatAgentService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import java.util.List;
import java.util.Optional;
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
@ConditionalOnProperty(prefix = "io.github.llmagentbuilder.chatagent", name = "enabled", matchIfMissing = true)
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
    @ConditionalOnProperty(prefix = "io.github.llmagentbuilder.chatagent.memory", name = "enabled", matchIfMissing = true)
    @ConditionalOnMissingBean
    public ChatMemoryStore chatMemoryStore() {
      return new InMemoryChatMemoryStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public Planner planner(ChatClient chatClient,
        Optional<ChatMemoryStore> chatMemoryStore,
        AgentToolsProvider agentToolsProvider,
        Optional<ObservationRegistry> observationRegistry,
        Optional<MeterRegistry> meterRegistry) {
      return ReActJsonPlannerFactory.INSTANCE.create(
          chatClient,
          agentToolsProvider,
          properties.getReActJson().getSystemInstructions(),
          chatMemoryStore.orElse(null),
          properties.tracingEnabled() ? observationRegistry.orElse(null)
              : null,
          properties.metricsEnabled() ? meterRegistry.orElse(null)
              : null
      );
    }

    @Bean
    @ConditionalOnProperty(prefix = "io.github.llmagentbuilder.chatagent.tracing", name = "enabled", matchIfMissing = true)
    @ConditionalOnBean(Planner.class)
    @ConditionalOnMissingBean
    public ObservationRegistry observationRegistry() {
      return ObservationRegistry.create();
    }

    @Bean
    @ConditionalOnProperty(prefix = "io.github.llmagentbuilder.chatagent.metrics", name = "enabled", matchIfMissing = true)
    @ConditionalOnBean(Planner.class)
    @ConditionalOnMissingBean
    public MeterRegistry meterRegistry() {
      return new SimpleMeterRegistry();
    }

    @Bean
    @ConditionalOnBean(Planner.class)
    public ChatAgent chatAgent(Planner planner,
        AgentToolsProvider agentToolsProvider,
        Optional<ObservationRegistry> observationRegistry) {
      return AgentFactory.INSTANCE.createChatAgent(
          planner,
          properties.getName(),
          properties.getDescription(),
          properties.getUsageInstruction(),
          agentToolsProvider,
          properties.getId(),
          properties.tracingEnabled() ? observationRegistry.orElse(null)
              : null);
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
        Optional<ObservationRegistry> observationRegistry,
        ApplicationContext context) {
      var manager = new AgentToolFunctionCallbackContext(agentToolsProvider,
          properties.tracingEnabled() ? observationRegistry.orElse(null)
              : null);
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
