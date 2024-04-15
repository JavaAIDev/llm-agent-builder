package io.github.alexcheng1982.llmagentbuilder.spring.chatagent;

import io.github.alexcheng1982.llmagentbuilder.core.ChatAgent;
import io.github.alexcheng1982.llmagentbuilder.core.ChatAgentRequest;
import io.github.alexcheng1982.llmagentbuilder.core.ChatAgentResponse;

public class ChatAgentService {

  private final ChatAgent chatAgent;

  public ChatAgentService(ChatAgent chatAgent) {
    this.chatAgent = chatAgent;
  }

  public ChatAgentResponse chat(ChatAgentRequest request) {
    return chatAgent.call(request);
  }
}
