package io.github.llmagentbuilder.spring.chatagent;

import io.github.llmagentbuilder.core.ChatAgent;
import io.github.llmagentbuilder.core.ChatAgentRequest;
import io.github.llmagentbuilder.core.ChatAgentResponse;

public class ChatAgentService {

  private final ChatAgent chatAgent;

  public ChatAgentService(ChatAgent chatAgent) {
    this.chatAgent = chatAgent;
  }

  public ChatAgentResponse chat(ChatAgentRequest request) {
    return chatAgent.call(request);
  }
}
